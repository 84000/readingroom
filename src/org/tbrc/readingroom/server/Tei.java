package org.tbrc.readingroom.server;

import java.util.ArrayList;

import org.tbrc.readingroom.shared.Global;
import org.tbrc.readingroom.shared.GlossItem;
import org.tbrc.readingroom.shared.PartName;
import org.tbrc.readingroom.shared.TextData;
import org.tbrc.readingroom.shared.MilestoneItem;
import org.tbrc.readingroom.shared.NoteItem;
import org.tbrc.readingroom.shared.TextPartData;

public class Tei
{
	private final String SUMMARY_BEGIN = "<div type=\"summary\">";
	private final String ACK_BEGIN = "<div type=\"acknowledgment\">";
	private final String INTRO_BEGIN = "<div type=\"introduction\">";
	private final String MILESTONE = "<milestone unit=\"chunk\"/>";

	public String teiStr = "";
	public String header = "";
	public String front = "";
	public String body = "";
	public String back = "";
	
	// Header
	public String titleStmtStr = "";
	public String pubStmtStr = "";
	public String sourceDescStr = "";
	
	// Front
	public String summaryStr = "";
	public String ackStr = "";
	public String introStr = "";
	private TextPartData summaryData = new TextPartData();
	private TextPartData ackData = new TextPartData();
	private TextPartData introData = new TextPartData();
	private TextPartData biblData = new TextPartData();
	
	// Body
	public int bodyParaCount = 0;
	public int bodyMilestoneCount = 0;
	public int bodyGlossCount = 0;
	public ArrayList<String> translationPartNames = new ArrayList<String>();
	public ArrayList<TextPartData> translationParts = new ArrayList<TextPartData>();
	private int sectionCount = 0;
	private int chapterCount = 0;
	private int colophonCount = 0;
	private int appendixSectionCount = 0;
	private int appendixChapterCount = 0;
	
	// Back
	public String notesStr = "";
	public String notesAbbrStr = "";
	public String biblStr = "";
	public String appendixStr = "";
	public ArrayList<TextPartData> appendixParts = new ArrayList<TextPartData>();
	
	public TextData textData = new TextData();
	private Converter converter = new Converter();

	
	public void parse(String textTei)
	{
		// Capture raw TEI string
		teiStr = textTei;
		
		// First, reset relevant data
		translationPartNames.clear();
		translationParts.clear();
		appendixParts.clear();
		textData.glossItems.clear();
		textData.noteItems.clear();
		bodyParaCount = 0;
		bodyMilestoneCount = 0;
		bodyGlossCount = 0;
		
		// Header
		header = XmlUtils.getBookendedString(textTei, "<teiHeader>", "</teiHeader>", true);
		// Pre-process header
		header = processGeneralElements(header);
		
		// Sections within "header" (title)
		if (header != null)
		{
			titleStmtStr = XmlUtils.getBookendedString(textTei, "<titleStmt>", "</titleStmt>", true);
			pubStmtStr = XmlUtils.getBookendedString(textTei, "<publicationStmt>", "</publicationStmt>", true);
			sourceDescStr = XmlUtils.getBookendedString(textTei, "<sourceDesc>", "</sourceDesc>", true);
			
			// Wrap HTML in CDATA
			header = wrapHtmlElementContent(header, "publisher");
			header = wrapHtmlElementContent(header, "licence");
			header = wrapHtmlElementContent(header, "author");
		}
		
		// Front
		front = XmlUtils.getBookendedString(textTei, "<front>", "</front>", true);
		// Pre-process front
		front = processGeneralElements(front);
		front = processElement(front, "title", "em");
	
		// Sections within "front" (Summary, Acknowledgement, Intro)
		if (front != null)
		{
			int begin;
			
			begin = front.indexOf(SUMMARY_BEGIN);
			if (begin >= 0)
				summaryStr = XmlUtils.getXmlNode(front, "div", begin);
			
			begin = front.indexOf(ACK_BEGIN);
			if (begin >= 0)
				ackStr = XmlUtils.getXmlNode(front, "div", begin);
			
			begin = front.indexOf(INTRO_BEGIN);
			if (begin >= 0)
				introStr = XmlUtils.getXmlNode(front, "div", begin);
		}
		
		// Body
		body = XmlUtils.getBookendedString(textTei, "<body>", "</body>", true);
		body = processGeneralElements(body);		// Pre-process body
		body = processElement(body, "title", "em");
		// Process various "parts" of the text - NOTE: Order matters - affects how parts are ordered in reader
		sectionCount = processTranslationParts(body, "section");	// Sections - can occur with (before) or without chapters
		chapterCount = processTranslationParts(body, "chapter");	// Chapters
		colophonCount = processTranslationParts(body, "colophon");	// Colophon

		// Back
		back = XmlUtils.getBookendedString(textTei, "<back>", "</back>", true);
		notesAbbrStr = XmlUtils.getBookendedString(textTei, "<list type=\"abbreviations\">", "</list>", true);
		notesAbbrStr = preProcessNotesAbbr(notesAbbrStr);
		back = processGeneralElements(back);
		back = processElement(back, "title", "em");

		// Process notes back section (separate from actual embedded notes)
		textData.notesAbbr = notesAbbrStr;

		// Process bibliography
		int biblStart = back.indexOf("<div type=\"listBibl\">");
		if (biblStart >= 0)
		{
			biblStr = XmlUtils.getXmlNode(back, "div", biblStart);
			processBibliography(biblStr);
		}
		
		// Process appendix string (if one exists)
		int appendixStart = back.indexOf("<div type=\"appendix\">");
		if (appendixStart >= 0)
		{
			appendixStr = XmlUtils.getXmlNode(back, "div", appendixStart);
			appendixSectionCount = processTranslationParts(appendixStr, "section");	// Sections - can occur with (before) or without chapters
			appendixChapterCount = processTranslationParts(appendixStr, "chapter");	// Chapters
			textData.appendixStart = Global.TRANS_START_PAGE + sectionCount + chapterCount + colophonCount;
		}

		// Now that we've broken up the XML into parts, process it for display
		process();
	}

	public boolean process()
	{
		boolean processed = true;
		int noteCount = 0;
		
		//-----------------------
		// Header
		//-----------------------
		// Title Statement
		// Publication Statement
		// Source Description
		// NOTE: THESE ARE HANDLED IN CLIENT - SIMPLE XML PARSING
		
		//-----------------------
		// Front
		//-----------------------
		// Summary
		if (summaryStr.length() > 0)
		{
			// Capture title
			int s = summaryStr.indexOf("<title") + 1;
			int titleBegin = summaryStr.indexOf(">", s) + ">".length();
			int titleEnd = summaryStr.indexOf("</title>", titleBegin);
			int e = titleEnd + "</title>".length();
			if (titleBegin >= 0 && titleEnd > titleBegin)
			{
				summaryData.setTitle(summaryStr.substring(titleBegin, titleEnd));
				// Rather than isolating title as a widget, just use <cite>
				//String newContent = summaryStr.substring(0, s-1) + "<span id='title'></span>" + summaryStr.substring(e);
				String newContent = summaryStr.substring(0, s-1) + "<cite>" + summaryData.getTitle() + "</cite>" + summaryStr.substring(e);
				summaryData.setContent(newContent);
				summaryStr = summaryData.getContent();
			}
				
			// Strip head from XML
			String headStr = XmlUtils.getBookendedString(summaryStr, "<head type=\"summary\">", "</head>", false);
			summaryStr = summaryStr.replace("<head type=\"summary\">" + headStr + "</head>", "");
			summaryData.setContent(summaryStr);
		
			// Main content
			findAndProcessMilestones(summaryData, "S");
			//System.out.println("\n\nPROCESSED SUMMARY: {" + summaryData.getContent() + "}");
		}
		
		// Acknowledgments
		if (ackStr.length() > 0)
		{
			// Capture and strip head from XML
			String headStr = XmlUtils.getBookendedString(ackStr, "<head type=\"acknowledgment\">", "</head>", false);
			ackData.setTitle(headStr);
			ackStr = ackStr.replace("<head type=\"acknowledgment\">" + headStr + "</head>", "");
			ackData.setContent(ackStr);
		
			// Main content
			findAndProcessMilestones(ackData, "A");
		}
		
		// Introduction
		if (introStr.length() > 0)
		{
			// Capture and strip head from XML
			String headStr = XmlUtils.getBookendedString(introStr, "<head type=\"introduction\">", "</head>", false);
			introData.setTitle(headStr);
			introStr = introStr.replace("<head type=\"introduction\">" + headStr + "</head>", "");
			introStr = processElement(introStr, "head", "h2");
			introData.setContent(introStr);
		
			// Main content
			findAndProcessMilestones(introData, "I");
			noteCount += findAndProcessNotes(introData, Global.TEXT_INTRO_PAGE, noteCount);
		}

		//-----------------------
		// Body
		//-----------------------
		findAndProcessTransTitles(body);
		
		//for (int i = 0; i < translationParts.size(); i++)
		int transPartCount = sectionCount + chapterCount + colophonCount;
		for (int i = 0; i < transPartCount; i++)
		{
			// Process various elements in main content
			// Homages
			findAndProcessHomages(translationParts.get(i));
			
			// Milestones
			if (translationParts.get(i).partType.equalsIgnoreCase("section"))
			{
				if (chapterCount > 0)
					findAndProcessMilestones(translationParts.get(i), "P");
				else
					findAndProcessMilestones(translationParts.get(i), ("" + (i+1)));
			}
			else if (translationParts.get(i).partType.equalsIgnoreCase("colophon"))
				findAndProcessMilestones(translationParts.get(i), "C");
			else // Chapters
				findAndProcessMilestones(translationParts.get(i), ("" + (i+1-sectionCount)));

			// Notes
			int partPageNum = Global.TRANS_START_PAGE + i; // overall number (i.e., 0 = title, 1 = summary, etc.)
			int count = findAndProcessNotes(translationParts.get(i), partPageNum, noteCount);
			noteCount += count;
			
			// Refs
			String newContent = findAndProcessRefs(translationParts.get(i));
			translationParts.get(i).setContent(newContent);

			//if (i == 7)
			//	System.out.println("\n(AFTER) PROCESSED PART " + (i + 1) + " CONTENT: {" + translationParts.get(i).getContent() + "}");
		}
		
		// Process additional instances of glossary terms
		// TEST //processForwardGlossInstances();
		// TEST //System.out.println("PROCESSED PART 1, CONTENT: {" + translationParts.get(1).getContent().substring(0, 10000) + "}");
		

		//-----------------------
		// Back
		//-----------------------
		processGlossary(back);

		int appendixPartCount = appendixSectionCount + appendixChapterCount;
		for (int i = transPartCount; i < (transPartCount+appendixPartCount); i++)
		{
			// Process various elements in main content
			// Homages
			findAndProcessHomages(translationParts.get(i));
			
			// Milestones
			if (translationParts.get(i).partType.equalsIgnoreCase("section"))
			{
				if (appendixChapterCount > 0)
					findAndProcessMilestones(translationParts.get(i), "AP");
				else
					findAndProcessMilestones(translationParts.get(i), ("A" + (i-transPartCount+1)));
			}
			else // Chapters
				findAndProcessMilestones(translationParts.get(i), ("A" + (i-transPartCount+1-appendixSectionCount)));

			// Notes
			int partPageNum = Global.TRANS_START_PAGE + i; // overall number (i.e., 0 = title, 1 = summary, etc.)
			int count = findAndProcessNotes(translationParts.get(i), partPageNum, noteCount);
			noteCount += count;
			
			// Refs
			String newContent = findAndProcessRefs(translationParts.get(i));
			translationParts.get(i).setContent(newContent);
		}
		
		return processed;
	}
	
	private String preProcessNotesAbbr(String str)
	{
		String newStr = str;

		newStr = newStr.replace("<list type=\"abbreviations\">", "<table>");
		newStr = newStr.replace("</list>", "</table>");
		newStr = newStr.replace("<head>", "<tr><th class=\"noteAbbrTh\"></th><th class=\"noteAbbrTh\">");
		newStr = newStr.replace("</head>", "</th></tr>");
		/*
		newStr = newStr.replace("<head", "<strong");
		newStr = newStr.replace("</head>", "</strong><br/><br/>");
		newStr = newStr.replace("<item", "<li style='list-style-type: none;'");
		newStr = newStr.replace("</item>", "</li>");
		newStr = newStr.replace("<expan", "<span style='padding-left: 2em;'");		// Chapuza!
		newStr = newStr.replace("</expan>", "</span>");	// Chapuza!
		*/
		newStr = newStr.replace("<item", "<tr");
		newStr = newStr.replace("</item>", "</tr>");
		newStr = newStr.replace("<abbr", "<td class=\"noteAbbrTd\"");
		newStr = newStr.replace("</abbr>", "</td>");
		newStr = newStr.replace("<expan", "<td class=\"noteAbbrTd\"");
		newStr = newStr.replace("</expan>", "</td>");
		
		return newStr;
	}
	
	private String processGeneralElements(String str)
	{
		String newStr = str;

		// Remove all comments
		newStr = XmlUtils.stripComments(newStr);
		
		// Titles are italicized
		// Breaks title page! Separated out below
		//newStr = newStr.replace("<title", "<em");
		//newStr = newStr.replace("</title>", "</em>");
		
		// Clean up whitespace
		newStr = newStr.replaceAll("\\s+", " ");
		
		// Emphasized elements are also italicized
		newStr = newStr.replace("<emph", "<em");
		newStr = newStr.replace("</emph>", "</em>");

		// Foreign language terms are also italicized
		newStr = newStr.replace("<foreign", "<em");
		newStr = newStr.replace("</foreign>", "</em>");
		
		// "Distinct" phrases are also italicized
		// NOTE: Specific "homage" processing happens AFTER this replacement
		newStr = newStr.replace("<distinct", "<em");
		newStr = newStr.replace("</distinct>", "</em>");

		// Trailer statements are also italicized
		newStr = newStr.replace("<trailer", "<div id='trailer'><em");
		newStr = newStr.replace("</trailer>", "</em></div>");
		
		// For quotes, convert to <div> with specific style
		//newStr = newStr.replace("<q>", "<hr style='height:4px; visibility:hidden;' />");
		//newStr = newStr.replace("</q>", "<hr style='height:4px; visibility:hidden;' />");
		newStr = newStr.replace("<q>", "<div class='quoteSection'>");
		newStr = newStr.replace("</q>", "</div>");
		
		// Speaker elements are hidden
		newStr = newStr.replace("<speaker>", "<p style='display: none;'>");
		newStr = newStr.replace("</speaker>", "</p>");

		// Line groups are converted to description lists
		newStr = newStr.replace("<lg>", "<dl class='verse'>");
		newStr = newStr.replace("</lg>", "</dl>");
		newStr = newStr.replace("<l>", "<dt>");
		newStr = newStr.replace("</l>", "</dt>");
		
		// Lists are converted to HTML unordered lists
		newStr = newStr.replace("<list>", "<ul class='textList'>");
		newStr = newStr.replace("</list>", "</ul>");
		newStr = newStr.replace("<list rend=\"bulleted\">", "<ul class='bulletList'>");
		newStr = newStr.replace("</list>", "</ul>");
		newStr = newStr.replace("<list rend=\"abbreviations\">", "<ul class='infoPanelContent'>");
		newStr = newStr.replace("</list>", "</ul>");
		//newStr = newStr.replace("<item>", "<li class='textList'>");
		newStr = newStr.replace("<item", "<li class='textItem'");
		newStr = newStr.replace("</item>", "</li>");
		
		// Embedded graphic images
		newStr = newStr.replace("<graphic url", "<img src");
		
		// Line breaks
		newStr = newStr.replace("<lb/>", "<br/>");
		newStr = newStr.replace("<lb", "<br");
		newStr = newStr.replace("</lb>", "</br>");
		
		// Speech - put break before and after
		newStr = newStr.replace("<sp>", "<div class='speech'>");
		newStr = newStr.replace("</sp>", "</div>");
	
		// "Name" is shown as bold
		newStr = newStr.replace("<name>", "<strong>");
		newStr = newStr.replace("</name>", "</strong>");
		
		// "Label" shown as bold
		newStr = newStr.replace("<label>", "<br/><strong>");
		newStr = newStr.replace("</label>", "</strong><br/>");
	
		return newStr;
	}
	
	private String processElement(String str, String xmlStr, String htmlStr)
	{
		// This had to be separated because titles are handled differently in different sections
		String newStr = str, fromStr = "", toStr = "";

		// Titles are italicized
		fromStr = "<" + xmlStr;
		toStr = "<" + htmlStr;
		newStr = newStr.replace(fromStr, toStr);
		fromStr = "</" + xmlStr + ">";
		toStr = "</" + htmlStr + ">";
		newStr = newStr.replace(fromStr, toStr);

		return newStr;
	}
	
	private String wrapHtmlElementContent(String xmlStr, String elementName)
	{
		int elementBegin, elementEnd, prevElementEnd = 0;
		int contentBegin, contentEnd;
		String content;
		String newXmlStr = "";
		int elementCount = 0;
		//String startTag = "<" + elementName + ">";
		String startTag = "<" + elementName;
		String endTag = "</" + elementName + ">";

		while (true)
		{
			elementBegin = xmlStr.indexOf(startTag, prevElementEnd);
			//contentBegin = elementBegin + startTag.length();
			
			if (elementBegin >= 0)
			{
				contentBegin = xmlStr.indexOf(">", elementBegin) + ">".length();
				contentEnd = xmlStr.indexOf(endTag, contentBegin);

				// Bad content end means no separate end tag = no element content
				if (contentEnd < 0)
				{
					elementEnd = contentBegin;	// The ">" found above was actually the end of the element
					newXmlStr += xmlStr.substring(prevElementEnd, elementEnd);
				}
				else
				{
					elementEnd = contentEnd + endTag.length();

					content = xmlStr.substring(contentBegin, contentEnd);

					// New content string replaces gloss item with HTML
					newXmlStr += xmlStr.substring(prevElementEnd, contentBegin);
					newXmlStr += "<![CDATA[" + content + "]]>" + endTag;
				}

				elementCount++;
				prevElementEnd = elementEnd;
			}
			else
				break;
		}
	
		if (elementCount == 0)
			newXmlStr = xmlStr;
		else
			newXmlStr += xmlStr.substring(prevElementEnd);
		
		return newXmlStr;
	}
	
	private void findAndProcessTransTitles(String str)
	{
		textData.transTitle = XmlUtils.getBookendedString(str, "<head type=\"translation\">", "</head>", false);
		textData.honTitle = XmlUtils.getBookendedString(str, "<head type=\"titleHon\">", "</head>", false);
		textData.mainTitle = XmlUtils.getBookendedString(str, "<head type=\"titleMain\">", "</head>", false);
		//System.out.println("TITLES = TRANS:" + textData.transTitle + " HON: " + textData.honTitle + ", MAIN: " + textData.mainTitle);
	}
	
	private int findAndProcessHomages(TextPartData data)
	{
		// EXAMPLE:
		/*
				<distinct type="homage">
					<ref cRef="F.1.b"/> Homage to all buddhas and bodhisattvas!
				</distinct>
		 */
		int homageCount = 0;
		String homageStr = null;
		int homageBegin, homageEnd, prevHomageEnd = 0;
		String xmlStr = data.getContent();
		String newXmlStr = "";

		while (true)
		{
			homageBegin = xmlStr.indexOf("<em type=\"homage\"", prevHomageEnd);
			homageEnd = xmlStr.indexOf("</em>", homageBegin) + "</em>".length();

			if (homageBegin >= 0 && homageEnd > homageBegin)
			{
				homageStr = xmlStr.substring(homageBegin, homageEnd);

				// New content string wraps homage in a styled div
				newXmlStr += xmlStr.substring(prevHomageEnd, homageBegin);
				newXmlStr += "<div id='homage'>" + homageStr + "</div>";

				homageCount++;
				prevHomageEnd = homageEnd;
			}
			else
				break;
		}

		if (homageCount > 0)
		{
			newXmlStr += xmlStr.substring(prevHomageEnd);
			data.setContent(newXmlStr);
		}

		return homageCount;
	}
	
	private void processGlossary(String str)
	{
		findAndProcessGlossTerms(str);
		
		// Glossary terms are shown in the introduction and main parts/chapters
		processForwardGlossInstances(introData, "I");
		for (int i = 0; i < translationParts.size(); i++)
			processForwardGlossInstances(translationParts.get(i), ("" + i));
	}
	
	private int findAndProcessGlossTerms(String xmlStr)
	{
		// EXAMPLE:
		/*
		<gloss>
			<term xml:id="UT22084-051-003-pl1">Śrāvastī</term>
			<term xml:lang="Bo-Ltn">mnyan yod</term>
			<term xml:lang="Sa-Ltn">Śrāvastī</term>
			<term type="definition">The capital city of Kosala where Buddha lived...</term>
        </gloss>
        */

		int glossCount = 0;
		String glossStr = null;
		int glossBegin, glossEnd, prevGlossEnd = 0;
		int termBegin, termEnd;
		// TEST //String xmlStr = data.getContent();
		// TEST //String newXmlStr = "";
		
		// TEST //data.glossItems.clear();
		
		while (true)
		{
			glossBegin = xmlStr.indexOf("<gloss>", prevGlossEnd);
			glossEnd = xmlStr.indexOf("</gloss>", glossBegin) + "</gloss>".length();
			//System.out.println("" + glossCount + " BEGIN: " + glossBegin + ", END: " + glossEnd);
			
			if (glossBegin >= 0 && glossEnd > glossBegin)
			{
				GlossItem glossItem = new GlossItem();
				glossStr = xmlStr.substring(glossBegin, glossEnd);
				//System.out.println("TEI PROCESS, GLOSS STR: {" + glossStr + "}");

				// ID
				termBegin = glossStr.indexOf("<term xml:id=\"");
				if (termBegin >= 0)
				{
					termBegin += "<term xml:id=\"".length();
					termEnd = glossStr.indexOf("\"", termBegin);
					if (termEnd >= 0)
					{
						glossItem.id = glossStr.substring(termBegin, termEnd);
					
						// Name
						termBegin = glossStr.indexOf(">", termEnd);
						if (termBegin >= 0)
						{
							termBegin += ">".length();
							termEnd = glossStr.indexOf("</term>", termBegin);
							if (termEnd >= 0)
								glossItem.name = glossStr.substring(termBegin, termEnd).trim();
						}
					}
				}
					
				// Name (Bo-Ltn)
				termBegin = glossStr.indexOf("<term xml:lang=\"Bo-Ltn\">");
				if (termBegin >= 0)
				{
					termBegin += "<term xml:lang=\"Bo-Ltn\">".length();
					termEnd = glossStr.indexOf("</term>", termBegin);
					if (termEnd >= 0)
					{
						glossItem.nameBoLtn = glossStr.substring(termBegin, termEnd);
						glossItem.nameBo = converter.toUnicode(glossItem.nameBoLtn);
					}
				}
				
				// Name (Sa-Ltn)
				termBegin = glossStr.indexOf("<term xml:lang=\"Sa-Ltn\">");
				if (termBegin >= 0)
				{
					termBegin += "<term xml:lang=\"Sa-Ltn\">".length();
					termEnd = glossStr.indexOf("</term>", termBegin);
					if (termEnd >= 0)
						glossItem.nameSaLtn = glossStr.substring(termBegin, termEnd);
				}

				// definition
				termBegin = glossStr.indexOf("<term type=\"definition\">");
				if (termBegin >= 0)
				{
					termBegin += "<term type=\"definition\">".length();
					termEnd = glossStr.indexOf("</term>", termBegin);
					if (termEnd >= 0)
						glossItem.def = glossStr.substring(termBegin, termEnd);
				}
				else
				{
					//glossItem.def = "No definition available";
					glossItem.def = "";
				}

				// Update appropriate data object
				// TEST //data.glossItems.add(glossItem);
				addGlossItemToMasterList(glossItem);

				// New content string replaces gloss item with HTML
				// TEST //newXmlStr += xmlStr.substring(prevGlossEnd, glossBegin);
				// TEST //newXmlStr += "<span id='" + glossItem.id + "'></span>";
				
				glossCount++;
				prevGlossEnd = glossEnd;
			}
			else
				break;
		}
		
		if (glossCount > 0)
		{
			// TEST //newXmlStr += xmlStr.substring(prevGlossEnd);
			// TEST //data.setContent(newXmlStr);
		}
	
		//System.out.println("\nPROCESSED " + glossCount + " GLOSS TERMS:");
		//for (int i = 0; i < glossCount; i++)
		//	System.out.println("   TERM: " + textData.glossItems.get(i).name);
			
		return glossCount;
	}
	
	private void processForwardGlossInstances(TextPartData translationPart, String partId)
	{
		// Loop through master list of glossary terms
		for (int termNum = 0; termNum < textData.glossItems.size(); termNum++)
		{
			GlossItem glossItem = textData.glossItems.get(termNum);
			int termBegin = 0, termEnd = 0, prevTermEnd = 0, searchPoint = 0;

			String oldContent = translationPart.getContent();
			String oldContentLower = oldContent.toLowerCase();
			String newContent = "";

			termBegin = 0; termEnd = 0; prevTermEnd = 0; searchPoint = 0;

			// First check to see if the original instance of the term is on this page
			// NOTE: No longer necessary - terms are all defined in the back matter
			//if ((termBegin = oldContent.indexOf("'" + glossItem.id + "'", searchPoint)) >= 0)
			//	searchPoint = termBegin + Global.HIGHLIGHT_SPACING_CHARS;

			// Now look for additional instances of the term
			int termCount = 0;
			String term = glossItem.name.toLowerCase();
			while ((termBegin = oldContentLower.indexOf(term, searchPoint)) >= 0)
			{
				termEnd = termBegin + term.length();

				// Validate that this is a stand-alone word (not part of another word)
				if (!XmlUtils.isIsolatedWord(term, oldContent, termBegin))
				{
					searchPoint = termEnd;
					continue;
				}

				// Create new gloss item that is identical, except for unique ID
				GlossItem newItem = glossItem.createDuplicate();
				newItem.id = newItem.id + "." + partId + "." + termCount;

				// New content string replaces gloss item with HTML
				newContent += oldContent.substring(prevTermEnd, termBegin);
				newContent += "<span id='" + newItem.id + "'></span>";

				// Add new instance to text part
				translationPart.glossItems.add(newItem);

				prevTermEnd = termEnd;
				termCount++;

				// Check if there are > HIGHLIGHT_SPACING_CHARS remaining. If not, we're done
				if (oldContent.length() > (prevTermEnd + Global.HIGHLIGHT_SPACING_CHARS))
					searchPoint = prevTermEnd + Global.HIGHLIGHT_SPACING_CHARS;
				else
					break;
			}

			// Capture modified content
			if (termCount > 0)
			{
				newContent += oldContent.substring(prevTermEnd);
				translationPart.setContent(newContent);
			}
		}
	}
	
	/*
	private void processForwardGlossInstances(TextPartData translationPart, String partId)
	{
		// Loop through master list of glossary terms
		for (int termNum = 0; termNum < textData.glossItems.size(); termNum++)
		{
			GlossItem glossItem = textData.glossItems.get(termNum);
			int termBegin = 0, termEnd = 0, prevTermEnd = 0, searchPoint = 0;

			String oldContent = translationPart.getContent();
			String newContent = "";

			termBegin = 0; termEnd = 0; prevTermEnd = 0; searchPoint = 0;

			// First check to see if the original instance of the term is on this page
			// NOTE: No longer necessary - terms are all defined in the back matter
			//if ((termBegin = oldContent.indexOf("'" + glossItem.id + "'", searchPoint)) >= 0)
			//	searchPoint = termBegin + Global.HIGHLIGHT_SPACING_CHARS;

			// Now look for additional instances of the term
			int termCount = 0;
			String term = glossItem.name;
			while ((termBegin = oldContent.indexOf(term, searchPoint)) >= 0)
			{
				termEnd = termBegin + term.length();

				// Validate that this is a stand-alone word (not part of another word)
				if (!XmlUtils.isIsolatedWord(term, oldContent, termBegin))
				{
					searchPoint = termEnd;
					continue;
				}

				// Create new gloss item that is identical, except for unique ID
				GlossItem newItem = glossItem.createDuplicate();
				newItem.id = newItem.id + "." + partId + "." + termCount;

				// New content string replaces gloss item with HTML
				newContent += oldContent.substring(prevTermEnd, termBegin);
				newContent += "<span id='" + newItem.id + "'></span>";

				// Add new instance to text part
				translationPart.glossItems.add(newItem);

				prevTermEnd = termEnd;
				termCount++;

				// Check if there are > HIGHLIGHT_SPACING_CHARS remaining. If not, we're done
				if (oldContent.length() > (prevTermEnd + Global.HIGHLIGHT_SPACING_CHARS))
					searchPoint = prevTermEnd + Global.HIGHLIGHT_SPACING_CHARS;
				else
					break;
			}

			// Capture modified content
			if (termCount > 0)
			{
				newContent += oldContent.substring(prevTermEnd);
				translationPart.setContent(newContent);
			}
		}
	}
	*/
	
	private int findAndProcessMilestones(TextPartData data, String prefix)
	{
		// EXAMPLE:
		/*
		<milestone unit="chunk"/>
		*/

		int milestoneCount = 0;
		int milestoneBegin, milestoneEnd, prevMilestoneEnd = 0;
		int nextElementEnd;
		String xmlStr = data.getContent();
		String newXmlStr = "";

		data.milestoneItems.clear();
		
		while (true)
		{
			milestoneBegin = xmlStr.indexOf(MILESTONE, prevMilestoneEnd);
			milestoneEnd = milestoneBegin + MILESTONE.length();
			//System.out.println("" + milestoneCount + " BEGIN: " + milestoneBegin + ", END: " + milestoneEnd);
			
			if (milestoneBegin >= 0)
			{
				MilestoneItem milestoneItem = new MilestoneItem();
				milestoneItem.id = prefix + "." + (milestoneCount+1);
				
				data.milestoneItems.add(milestoneItem);

				// New content string replaces gloss item with HTML
				newXmlStr += xmlStr.substring(prevMilestoneEnd, milestoneBegin);
				newXmlStr += "<span id='" + milestoneItem.id + "'></span>\n";
				
				// Find next element and add margin/indent style to it
				nextElementEnd = xmlStr.indexOf(">", milestoneEnd);
				if (nextElementEnd >= 0)
				{
					newXmlStr += xmlStr.substring(milestoneEnd, nextElementEnd);
					if (milestoneCount == 0)
						newXmlStr += " style='margin-top: -1.5em; text-indent: 0em;'>";
					else
						newXmlStr += " style='margin-top: -1.5em;'>";
				}
				
				// If first milestone, insert a div to thwart the "span+p" CSS (paragraph indent)
				//if (milestoneCount == 0)
				//	newXmlStr += "<div id=\"bla\"></div>";

				milestoneCount++;
				
				if (nextElementEnd >= 0)
					prevMilestoneEnd = nextElementEnd + ">".length();
				else
					prevMilestoneEnd = milestoneEnd;
			}
			else
				break;
		}
	
		if (milestoneCount > 0)
		{
			newXmlStr += xmlStr.substring(prevMilestoneEnd);
			data.setContent(newXmlStr);
		}
	
		return milestoneCount;
	}
	
	private int findAndProcessNotes(TextPartData data, int partNum, int startNoteNum)
	{
		// EXAMPLE:
		/*
		<note place="end">The sūtra’s Sanskrit title <title xml:lang="Sa-Ltn">Amitāyurdhyānasūtra</title> is now believed to have
                been fabricated at a later date. Most scholars even consider the <title xml:lang="Chi-Ltn">Guan wu liang shou jing</title> to be a Chinese or
                Central Asian compilation. See Kōtatsu Fujita, “Textual Origins of the Kuan
                Wu-liang-shou ching,” in Robert E. Buswell (1990), pp. 149–173. See
                especially p. 155 and n. 48 on the question of the title. On p. 155 Fujita
                says: “It cannot be determined categorically what the Sanskrit title of the
                Kuan Wu-liang-shou ching might have been.”
        </note>
        */
		int noteCount = 0;
		String noteStr = null;
		int noteBegin, noteEnd, prevNoteEnd = 0;
		int textBegin, textEnd;
		String xmlStr = data.getContent();
		String newXmlStr = "";
		
		data.noteItems.clear();
		
		while (true)
		{
			noteBegin = xmlStr.indexOf("<note", prevNoteEnd);
			noteEnd = xmlStr.indexOf("</note>", noteBegin) + "</note>".length();
			
			if (noteBegin >= 0 && noteEnd > noteBegin)
			{
				NoteItem noteItem = new NoteItem();
				noteStr = xmlStr.substring(noteBegin, noteEnd);
				//System.out.println("TEI PROCESS, NOTE STR: {" + noteStr + "}");

				// ID
				noteItem.id = "" + (startNoteNum + (noteCount+1));

				// Text
				textBegin = noteStr.indexOf(">");
				if (textBegin >= 0)
				{
					textBegin += ">".length();
					textEnd = noteStr.length() - "</note>".length();
					noteItem.text = noteStr.substring(textBegin, textEnd);
				}
				
				// Part number
				noteItem.textPartNum = partNum;

				// Update appropriate data object
				data.noteItems.add(noteItem);
				addNoteItemToMasterList(noteItem);

				// New content string replaces gloss item with HTML
				newXmlStr += xmlStr.substring(prevNoteEnd, noteBegin);
				newXmlStr += "<span id='" + noteItem.id + "'></span>";
					
				noteCount++;
				prevNoteEnd = noteEnd;
			}
			else
				break;
		}
		
		if (noteCount > 0)
		{
			newXmlStr += xmlStr.substring(prevNoteEnd);
			data.setContent(newXmlStr);
		}
	
		return noteCount;
	}
	
	// Add set of glossary terms to master list and maintain alphabetic order
	private void addGlossItemToMasterList(GlossItem newItem)
	{
		if (textData.glossItems.size() == 0)
			textData.glossItems.add(newItem);
		else
		{
			int insertPoint;
			for (insertPoint = 0; insertPoint < textData.glossItems.size(); insertPoint++)
			{
				//if (textData.glossItems.get(insertPoint).name.toLowerCase().compareTo(newItem.name.toLowerCase()) > 0)
				//	break;
				String refString = XmlUtils.removeDiacriticalMarks(textData.glossItems.get(insertPoint).name.toLowerCase());
				String newString = XmlUtils.removeDiacriticalMarks(newItem.name.toLowerCase());
				if (refString.compareTo(newString) > 0)
					break;
			}
			textData.glossItems.add(insertPoint, newItem);
		}
	}

	// Add set of notes to master list and maintain note numbering order
	private void addNoteItemToMasterList(NoteItem newItem)
	{
		if (textData.noteItems.size() == 0)
			textData.noteItems.add(newItem);
		else
		{
			int insertPoint;
			for (insertPoint = 0; insertPoint < textData.noteItems.size(); insertPoint++)
			{
				if (Integer.parseInt(textData.noteItems.get(insertPoint).id) > Integer.parseInt(newItem.id))
					break;
			}
			textData.noteItems.add(insertPoint, newItem);
		}
	}
	
	private String findAndProcessRefs(TextPartData data)
	{
		// EXAMPLE:
		/*
			<ref cRef="F.10.b"/>
        */
		int refCount = 0;
		String refStr = null, refNumStr = "";
		int refBegin, refEnd, prevRefEnd = 0;
		int textBegin, textEnd;
		String xmlStr = data.getContent();
		String newXmlStr = "";
		
		while (true)
		{
			refBegin = xmlStr.indexOf("<ref cRef=", prevRefEnd);
			refEnd = xmlStr.indexOf(">", refBegin) + ">".length();
			
			if (refBegin >= 0 && refEnd > refBegin)
			{
				refStr = xmlStr.substring(refBegin, refEnd);

				// Text
				textBegin = refStr.indexOf("\"");
				if (textBegin >= 0)
				{
					textBegin += "\"".length();
					textEnd = refStr.indexOf("\"", textBegin);
					refNumStr = refStr.substring(textBegin, textEnd);
				}

				// New content string replaces gloss item with HTML
				newXmlStr += xmlStr.substring(prevRefEnd, refBegin);
				newXmlStr += "[" + refNumStr + "] ";
					
				refCount++;
				prevRefEnd = refEnd;
			}
			else
				break;
		}
		
		if (refCount > 0)
			newXmlStr += xmlStr.substring(prevRefEnd);
		else
			newXmlStr = xmlStr;
	
		return newXmlStr;
	}
	
	private int processTranslationParts(String str, String partType)
	{
		// EXAMPLE:
		/*
		*/
		int partCount = 0;
		int partBegin, prevPartEnd = 0;
	
		//while ((partBegin = str.indexOf("<div type=\"chapter\"", prevPartEnd)) >= 0)
		while ((partBegin = str.indexOf("<div type=\"" + partType + "\"", prevPartEnd)) >= 0)
		{
			TextPartData partData = new TextPartData();
			partData.partType = partType;

			String partStr = XmlUtils.getXmlNode(str, "div", partBegin);
			
			if (partType.equalsIgnoreCase("section"))
			{
				// We only care about sections before chapters and/or, where chapters don't exist, before the colophon
				// NOTE: this prevents colophon sections being found and mistaken for main text sections
				int endPos = str.indexOf("<div type=\"chapter\">", 0);
				if (endPos == -1)
					endPos = str.indexOf("<div type=\"colophon\">", 0);
				if (endPos != -1 && endPos < partBegin)
					break;
				
				// If we're looking at a section, set title
				int titleNodeBegin, titleNodeEnd;
				int titleBegin, titleEnd;
				titleNodeBegin = str.indexOf("<head type=\"sectionTitle\">", partBegin);
				if (titleNodeBegin >= 0)
				{
					titleBegin = titleNodeBegin + "<head type=\"sectionTitle\">".length();
					titleEnd = str.indexOf("</head>", titleBegin);
					titleNodeEnd = titleEnd + "</head>".length();
					String titleNodeStr = "", titleStr = "";
					if (titleEnd >= 0)
					{
						titleNodeStr = str.substring(titleNodeBegin, titleNodeEnd);
						titleStr = str.substring(titleBegin, titleEnd);
						partData.setTitle(titleStr);
					}

					// Strip title from XML
					partStr = partStr.replace(titleNodeStr, "");
				}
				else
				{
					//partData.setTitle("The Text");
					partData.setTitle("Translation");
				}

				// Find and deal with any "subsections" in a section
				int subsecBegin, subsecEnd, prevSubsecEnd = 0;
				while ((subsecBegin = str.indexOf("<div type=\"subSection\"", prevSubsecEnd)) >= 0)
				{
					String origSubsecStr = XmlUtils.getXmlNode(str, "div", subsecBegin);
					
					String newSubsecStr = origSubsecStr.replace("<head type=\"subSectionTitle\"", "<h2");
					newSubsecStr = newSubsecStr.replace("</head>", "</h2>");

					int newStart = "<div type=\"subSection\">".length();
					int newEnd = newSubsecStr.length() - "</div>".length();

					newSubsecStr = newSubsecStr.substring(newStart, newEnd-1);
					partStr = partStr.replace(origSubsecStr, newSubsecStr);
					subsecEnd = subsecBegin + newSubsecStr.length();
					
					prevSubsecEnd = subsecEnd;
				}
			}
			else if (partType.equalsIgnoreCase("chapter"))
			{
				// If we're looking at a chapter, set name/title
				int partNameNodeBegin, partNameNodeEnd;
				int partNameBegin, partNameEnd;
				partNameNodeBegin = str.indexOf("<head type=\"chapter\">", partBegin);
				if (partNameNodeBegin >= 0)
				{
					partNameBegin = partNameNodeBegin + "<head type=\"chapter\">".length();
					partNameEnd = str.indexOf("</head>", partNameBegin);
					partNameNodeEnd = partNameEnd + "</head>".length();
					String titleNodeStr = "", titleStr = "";
					if (partNameEnd >= 0)
					{
						titleNodeStr = str.substring(partNameNodeBegin, partNameNodeEnd);
						titleStr = str.substring(partNameBegin, partNameEnd);
						partData.setTitle(titleStr);
					}

					// Strip title from XML
					partStr = partStr.replace(titleNodeStr, "");

					// Set part subtitle
					int subtitleNodeBegin, subtitleNodeEnd;
					int subtitleBegin, subtitleEnd;
					subtitleNodeBegin = str.indexOf("<head type=\"chapterTitle\">", partNameBegin);
					if (subtitleNodeBegin >= 0)
					{
						subtitleBegin = subtitleNodeBegin + "<head type=\"chapterTitle\">".length();
						subtitleEnd = str.indexOf("</head>", subtitleBegin);
						subtitleNodeEnd = subtitleEnd + "</head>".length();
						String subtitleNodeStr = "", subtitleStr = "";
						if (subtitleEnd >= 0)
						{
							subtitleNodeStr = str.substring(subtitleNodeBegin, subtitleNodeEnd);
							subtitleStr = str.substring(subtitleBegin, subtitleEnd);
							partData.setSubtitle(subtitleStr);
						}

						// Strip title from XML
						partStr = partStr.replace(subtitleNodeStr, "");
					}
				}
			}
			else if (partType.equalsIgnoreCase("colophon"))
			{
				partData.setTitle("Colophon");
				partStr = processElement(partStr, "head", "h2");
			}
			
			// Set part content
			partData.setContent(partStr);

			// Add part to list
			translationParts.add(partData);
			System.out.println("---> Part #" + partCount + ": [" + partData.getTitle() + "]");
			partCount++;
			
			// TEMP TEMP TEMP
			//if (partCount == 7 || partCount == 8)
			//	System.out.println("---> CHAPTER " + partCount + " [" + partData.getContent() + "] <---");
			// TEMP TEMP TEMP

			prevPartEnd = partBegin + partStr.length();
		}
		
		System.out.println("---> Successfully processed [" + partCount + "] parts <---");
		return partCount;
	}
	
	private void processBibliography(String str)
	{
		// EXAMPLE:
		/*
		*/
		//int sectionCount = 0, biblCount = 0;
		int biblSectionBegin, biblSectionEnd, prevBiblSectionEnd = 0;
		String biblContent = "";
	
		biblData.setTitle(XmlUtils.getBookendedString(str, "<head type=\"listBibl\">", "</head>", false));
		
		while ((biblSectionBegin = str.indexOf("<div type=\"section\">", prevBiblSectionEnd)) >= 0)
		{
			biblSectionBegin += "<div type=\"section\">".length();
			biblSectionEnd = str.indexOf("</div>", biblSectionBegin);
			
			//String biblSectionStr = XmlUtils.getXmlNode(str, "div", biblSectionBegin);
			String biblSectionStr = str.substring(biblSectionBegin, biblSectionEnd);
			
			biblSectionStr = biblSectionStr.replace("<head", "<h2");
			biblSectionStr = biblSectionStr.replace("</head>", "</h2>");

			biblSectionStr = biblSectionStr.replace("<bibl>", "<div class='bibl'>");
			biblSectionStr = biblSectionStr.replace("<bibl ", "<div class='bibl' ");
			biblSectionStr = biblSectionStr.replace("</bibl>", "</div>");

			biblContent += biblSectionStr;
			prevBiblSectionEnd = biblSectionEnd;

			//System.out.println("---> BIBL SECTION BEGIN <---");
			//System.out.println(biblSectionStr);
			//System.out.println("---> BIBL SECTION BEGIN <---");
		}
		
		biblData.setContent(biblContent);
	}
	
	public TextPartData getTextSummaryData() { return summaryData; }
	public TextPartData getTextAckData() { return ackData; }
	public TextPartData getTextIntroData() { return introData; }
	public TextPartData getTextBiblData() { return biblData; }
	
	public ArrayList<PartName> getTranslationPartNames()
	{
		ArrayList<PartName> partNames = new ArrayList<PartName>();
		
		for (int i = 0; i < translationParts.size(); i++)
		{
			PartName pn = new PartName();
			pn.title = translationParts.get(i).getTitle();
			pn.subtitle = translationParts.get(i).getSubtitle();
			partNames.add(pn);
		}
		
		return partNames;
	}
	
	public TextPartData getTransPartByName(String partName)
	{
		TextPartData partData = null;
		
		for (int i = 0; i < translationParts.size(); i++)
		{
			if (translationParts.get(i).getTitle().equalsIgnoreCase(partName))
			{
				partData = translationParts.get(i);
				break;
			}
		}
		
		return partData;
	}
	
	public TextData getTextData()
	{
		return textData;
	}
}
