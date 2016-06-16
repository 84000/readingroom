package org.tbrc.readingroom.client;

import java.util.ArrayList;

import com.google.gwt.user.client.Window;
import com.google.gwt.xml.client.DOMException;
import com.google.gwt.xml.client.Document;
import com.google.gwt.xml.client.Element;
import com.google.gwt.xml.client.Node;
import com.google.gwt.xml.client.NodeList;
import com.google.gwt.xml.client.Text;
import com.google.gwt.xml.client.XMLParser;

public class Parser
{
	// Query-level items not contained in DataItems
	private static String kangyurDesc = "";
	private static String tengyurDesc = "";
	private static String kangyurLearnMore = "";
	private static String tengyurLearnMore = "";
	private static String sectionLearnMore = "";
	private static String sectionNote = "";

	public static ArrayList<DataItem> parseLobbyQueryResult(String xml, String category)
	{
		ArrayList<DataItem> lobbyItems = new ArrayList<DataItem>();

		try
		{
		    // parse the XML document into a DOM
		    Document messageDom = XMLParser.parse(xml);

		    // Extract the category ("kanjur" or "tenjur") node - we know there is only one
		    Node catNode = messageDom.getElementsByTagName(category).item(0);
		    if (catNode != null)
		    {
			    Element ec = (Element)catNode;
			    
			    // Get kanjur/tenjur description
		    	Node descNode = ec.getElementsByTagName("description").item(0);
		    	if (descNode != null)
		    	{
		    		Text descTextNode = (Text)descNode.getFirstChild();
		    		if (descTextNode != null)
		    		{
		    			if (category == "kanjur")
		    				kangyurDesc = descTextNode.getData();
		    			else // if (category == "tenjur")
		    				tengyurDesc = descTextNode.getData();
		    		}
		    	}
			    		
			    // Get kanjur/tenjur "learn more" info
		    	Node learnMoreNode = ec.getElementsByTagName("learnMore").item(0);
		    	if (learnMoreNode != null)
		    	{
		    		Text learnMoreTextNode = (Text)learnMoreNode.getFirstChild();
		    		if (learnMoreTextNode != null)
		    		{
		    			if (category == "kanjur")
		    				kangyurLearnMore = learnMoreTextNode.getData();
		    			else // if (category == "tenjur")
		    				tengyurLearnMore = learnMoreTextNode.getData();
		    		}
		    	}
			    		
		    	lobbyItems = getLobbyItems(ec);
		    }
		    else
		    {
		    	// For non-main-lobby queries (no outer "kanjur" or "tenjur" node), get any items that may exist
		    	lobbyItems = getLobbyItems((Element)messageDom);
		    }
		}
		catch (DOMException e)
		{
			Window.alert("Could not parse XML document.");
		}
		
		return lobbyItems;
	}
	
	private static ArrayList<DataItem> getLobbyItems(Element ec)
	{
		ArrayList<DataItem> lobbyItems = new ArrayList<DataItem>();

		// Get sections within the kangyur/tengyur
	    NodeList sectionNodeList = ec.getElementsByTagName("section");
	    if (sectionNodeList != null)
	    {
	    	for (int i = 0; i < sectionNodeList.getLength(); i++)
	    	{
	    		Node sectionNode = sectionNodeList.item(i);
	    		Element es = (Element)sectionNode;
	    		String id = es.getAttribute("id");

	    		Node nameNode = null;
	    		Text nameTextNode = null;

	    		String nameEn = "";
	    		nameNode = es.getElementsByTagName("name").item(0);
	    		if (nameNode != null)
	    		{
	    			nameTextNode = (Text)nameNode.getFirstChild();
	    			if (nameTextNode != null)
	    				nameEn = nameTextNode.getData();
	    		}

	    		String nameTi = "";
	    		nameNode = es.getElementsByTagName("name").item(2);
	    		if (nameNode != null)
	    		{
	    			nameTextNode = (Text)nameNode.getFirstChild();
	    			if (nameTextNode != null)
	    				nameTi = nameTextNode.getData();
	    		}

	    		String nameWy = "";
	    		nameNode = es.getElementsByTagName("name").item(1);
	    		if (nameNode != null)
	    		{
	    			nameTextNode = (Text)nameNode.getFirstChild();
	    			if (nameTextNode != null)
	    				nameWy = nameTextNode.getData();
	    		}

	    		String nameSa = "";
	    		nameNode = es.getElementsByTagName("name").item(3);
	    		if (nameNode != null)
	    		{
	    			nameTextNode = (Text)nameNode.getFirstChild();
	    			if (nameTextNode != null)
	    				nameSa = nameTextNode.getData();
	    		}

	    		String desc = "";
	    		Node descNode = es.getElementsByTagName("description").item(0);
	    		if (descNode != null)
	    		{
	    			Text descTextNode = (Text)descNode.getFirstChild();
	    			if (descTextNode != null)
	    				desc = descTextNode.getData();
	    		}

	    		String learnMore = "";
	    		Node learnMoreNode = es.getElementsByTagName("learnMore").item(0);
	    		if (learnMoreNode != null)
	    		{
	    			Text learnMoreTextNode = (Text)learnMoreNode.getFirstChild();
	    			if (learnMoreTextNode != null)
	    				learnMore = learnMoreTextNode.getData();
	    		}

	    		String note = "";
	    		Node noteNode = es.getElementsByTagName("note").item(0);
	    		if (noteNode != null)
	    		{
	    			Text noteTextNode = (Text)noteNode.getFirstChild();
	    			if (noteTextNode != null)
	    				note = noteTextNode.getData();
	    		}

	    		String toh = "";
	    		Node tohNode = es.getElementsByTagName("toh").item(0);
	    		if (tohNode != null)
	    		{
	    			Text tohTextNode = (Text)tohNode.getFirstChild();
	    			if (tohTextNode != null)
	    				toh = tohTextNode.getData();
	    		}

	    		// Get stats node items
	    		Node statsNode = es.getElementsByTagName("stats").item(0);
	    		Element ess = (Element)statsNode;

	    		String total = "";
	    		Node totalNode = ess.getElementsByTagName("total").item(0);
	    		if (totalNode != null)
	    		{
	    			Text totalTextNode = (Text)totalNode.getFirstChild();
	    			if (totalTextNode != null)
	    				total = totalTextNode.getData();
	    		}

	    		String translated = "";
	    		Node translatedNode = ess.getElementsByTagName("translated").item(0);
	    		if (translatedNode != null)
	    		{
	    			Text translatedTextNode = (Text)translatedNode.getFirstChild();
	    			if (translatedTextNode != null)
	    				translated = translatedTextNode.getData();
	    		}

	    		String inProgress = "";
	    		Node inProgressNode = ess.getElementsByTagName("inProgress").item(0);
	    		if (inProgressNode != null)
	    		{
	    			Text inProgressTextNode = (Text)inProgressNode.getFirstChild();
	    			if (inProgressTextNode != null)
	    				inProgress = inProgressTextNode.getData();
	    		}

	    		String notBegun = "";
	    		Node notBegunNode = ess.getElementsByTagName("notBegun").item(0);
	    		if (notBegunNode != null)
	    		{
	    			Text notBegunTextNode = (Text)notBegunNode.getFirstChild();
	    			if (notBegunTextNode != null)
	    				notBegun = notBegunTextNode.getData();
	    		}

	    		// Create and populate item
	    		DataItem lobbyItem = new DataItem();

	    		lobbyItem.setId(id);
	    		lobbyItem.setNameEn(nameEn);
	    		lobbyItem.setNameTi(nameTi);
	    		lobbyItem.setNameWy(nameWy);
	    		lobbyItem.setNameSa(nameSa);
	    		lobbyItem.setDesc(desc);
	    		lobbyItem.setLearnMore(learnMore);
	    		lobbyItem.setNote(note);
	    		lobbyItem.setToh(toh);
	    		lobbyItem.setStatsTotal(total);
	    		lobbyItem.setStatsTrans(translated);
	    		lobbyItem.setStatsInProg(inProgress);
	    		lobbyItem.setStatsNotBeg(notBegun);

	    		lobbyItems.add(lobbyItem);
	    	}
	    }
	    
	    return lobbyItems;
	}
	
	public static String getKangyurDesc()	{ return kangyurDesc; }
	public static String getTengyurDesc()	{ return tengyurDesc; }
	public static String getKangyurLearnMore()	{ return kangyurLearnMore; }
	public static String getTengyurLearnMore()	{ return tengyurLearnMore; }

	public static ArrayList<DataItem> parseSectionQueryResult(String xml)
	{
		ArrayList<DataItem> sectionItems = new ArrayList<DataItem>();

		try
		{
		    // parse the XML document into a DOM
		    Document messageDom = XMLParser.parse(xml);

		    // Get section data
		    sectionLearnMore = "";
    		Node learnMoreNode = messageDom.getElementsByTagName("learnMore").item(0);
    		if (learnMoreNode != null)
    		{
    			Text learnMoreTextNode = (Text)learnMoreNode.getFirstChild();
    			if (learnMoreTextNode != null)
    				sectionLearnMore = learnMoreTextNode.getData();
    		}

    		sectionNote = "";
    		Node noteNode = messageDom.getElementsByTagName("note").item(0);
    		if (noteNode != null)
    		{
    			Text noteTextNode = (Text)noteNode.getFirstChild();
    			if (noteTextNode != null)
    				sectionNote = noteTextNode.getData();
    		}
		    
		    // Get nodes within this section
		    NodeList sectionNodeList = messageDom.getElementsByTagName("node");
		    for (int i = 0; i < sectionNodeList.getLength(); i++)
		    {
		    	Node sectionNode = sectionNodeList.item(i);
			    Element es = (Element)sectionNode;
			    String id = es.getAttribute("id");
			    String type = es.getAttribute("type");
			    String numChapters = es.getAttribute("numChapters");
			    String url = es.getAttribute("url");
				    
			    Node nameNode = null;
			    Text nameTextNode = null;
				    
			    String nameEn = "";
			    nameNode = es.getElementsByTagName("name").item(0);
			    if (nameNode != null)
			    {
			    	nameTextNode = (Text)nameNode.getFirstChild();
			    	if (nameTextNode != null)
			    		nameEn = nameTextNode.getData();
			    }

			    String nameWy = "";
			    nameNode = es.getElementsByTagName("name").item(1);
			    if (nameNode != null)
			    {
			    	nameTextNode = (Text)nameNode.getFirstChild();
			    	if (nameTextNode != null)
			    		nameWy = nameTextNode.getData();
			    }

			    String nameTi = "";
			    nameNode = es.getElementsByTagName("name").item(2);
			    if (nameNode != null)
			    {
			    	nameTextNode = (Text)nameNode.getFirstChild();
			    	if (nameTextNode != null)
			    		nameTi = nameTextNode.getData();
			    }

			    String nameSa = "";
			    nameNode = es.getElementsByTagName("name").item(3);
			    if (nameNode != null)
			    {
			    	nameTextNode = (Text)nameNode.getFirstChild();
			    	if (nameTextNode != null)
			    		nameSa = nameTextNode.getData();
			    }

			    String desc = "", summary = "", titles = "";
			    Node descNode = es.getElementsByTagName("description").item(0);
			    if (descNode != null)
			    {
			    	Text descTextNode = (Text)descNode.getFirstChild();
			    	if (descTextNode != null)
			    		desc = descTextNode.getData();
			    }

			    Node summaryNode = es.getElementsByTagName("summary").item(0);
			    if (summaryNode != null)
			    {
			    	Text summaryTextNode = (Text)summaryNode.getFirstChild();
			    	if (summaryTextNode != null)
			    		summary = summaryTextNode.getData();
			    }
			    Element titlesNode = (Element)es.getElementsByTagName("titles").item(0);
			    if (titlesNode != null)
			    {
				    NodeList divNodeList = titlesNode.getElementsByTagName("div");
				    for (int ii = 0; ii < divNodeList.getLength(); ii++)
				    {
				    	Node divNode = divNodeList.item(ii);
					    if (divNode != null)
					    {
					    	Text divTextNode = (Text)divNode.getFirstChild();
					    	if (divTextNode != null)
					    		titles += divTextNode.getData() + "<br/>";	// Chapuza! Should not have formatting here
					    }
				    }
			    }
			    
			    String rid = "";
			    Node ridNode = es.getElementsByTagName("textRID").item(0);
			    if (ridNode != null)
			    {
			    	Text ridTextNode = (Text)ridNode.getFirstChild();
			    	if (ridTextNode != null)
			    		rid = ridTextNode.getData();
			    }

			    String toh = "";
	    		Node tohNode = es.getElementsByTagName("toh").item(0);
	    		if (tohNode != null)
	    		{
	    			Text tohTextNode = (Text)tohNode.getFirstChild();
	    			if (tohTextNode != null)
	    				toh = tohTextNode.getData();
	    		}
				
			    // Get stats node items
			    Node statsNode = es.getElementsByTagName("stats").item(0);
			    Element ess = (Element)statsNode;
				    
			    String total = "";
			    Node totalNode = ess.getElementsByTagName("total").item(0);
			    if (totalNode != null)
			    {
			    	Text totalTextNode = (Text)totalNode.getFirstChild();
			    	if (totalTextNode != null)
			    		total = totalTextNode.getData();
			    }
				    
			    String translated = "";
			    Node translatedNode = ess.getElementsByTagName("translated").item(0);
			    if (translatedNode != null)
			    {
			    	Text translatedTextNode = (Text)translatedNode.getFirstChild();
			    	if (translatedTextNode != null)
			    		translated = translatedTextNode.getData();
			    }
			    
			    String inProgress = "";
			    Node inProgressNode = ess.getElementsByTagName("inProgress").item(0);
			    if (inProgressNode != null)
			    {
			    	Text inProgressTextNode = (Text)inProgressNode.getFirstChild();
			    	if (inProgressTextNode != null)
			    		inProgress = inProgressTextNode.getData();
			    }
				    
			    String notBegun = "";
			    Node notBegunNode = ess.getElementsByTagName("notBegun").item(0);
			    if (notBegunNode != null)
			    {
			    	Text notBegunTextNode = (Text)notBegunNode.getFirstChild();
			    	if (notBegunTextNode != null)
			    		notBegun = notBegunTextNode.getData();
			    }

			    // Create and populate item
				DataItem sectionItem = new DataItem();

				sectionItem.setId(id);
				sectionItem.setType(type);
				sectionItem.setNumChapters(numChapters);
				sectionItem.setUrl(url);
				sectionItem.setNameEn(nameEn);
				sectionItem.setNameTi(nameTi);
				sectionItem.setNameWy(nameWy);
				sectionItem.setNameSa(nameSa);
		    	sectionItem.setDesc(desc);
		    	sectionItem.setSummary(summary);
		    	sectionItem.setTitles(titles);
				sectionItem.setRid(rid);
		    	sectionItem.setToh(toh);
		    	sectionItem.setStatsTotal(total);
			    sectionItem.setStatsTrans(translated);
			    sectionItem.setStatsInProg(inProgress);
			    sectionItem.setStatsNotBeg(notBegun);
		    
		    	sectionItems.add(sectionItem);
		    }
		}
		catch (DOMException e)
		{
			Window.alert("Could not parse XML document.");
		}
		
		return sectionItems;
	}

	public static String getSectionLearnMore() { return sectionLearnMore; }
	public static String getSectionNote() { return sectionNote; }
	
	public static boolean parseTextQueryHeader(String xml, TextItem text)
	{
		boolean parsed = true;

		try
		{
		    // parse the XML document into a DOM
		    Document messageDom = XMLParser.parse(xml);

		    // Get header node
	    	Node hNode = messageDom.getElementsByTagName("teiHeader").item(0);
	    	if (hNode != null)
	    	{
	    		Element eh = (Element)hNode;
		    		
	    		// titles
	    		int tCount = eh.getElementsByTagName("title").getLength();
	    		for (int t = 0; t < tCount; t++)
	    		{
	    			Node tNode = eh.getElementsByTagName("title").item(t);
	    			if (tNode != null)
	    			{
	    			    Element eti = (Element)tNode;
	    			    String type = eti.getAttribute("type");
	    			    String lang = eti.getAttribute("xml:lang");
	    			    if (type == "mainTitle")
	    			    {
    				    	Text titleTextNode = (Text)tNode.getFirstChild();
    				    	if (titleTextNode != null)
    				    	{
    				    		if (lang.equalsIgnoreCase("eng"))
    				    			text.mainTitleEn = titleTextNode.getData();
    				    		else if (lang.equalsIgnoreCase("bo"))
    				    			text.mainTitleTi = titleTextNode.getData();
    				    		else if (lang.equalsIgnoreCase("bo-ltn"))
    				    			text.mainTitleWy = titleTextNode.getData();
    				    		else if (lang.equalsIgnoreCase("sa-ltn"))
    				    			text.mainTitleSa = titleTextNode.getData();
    				    	}
	    			    }
	    			    else if (type == "longTitle")
	    			    {
    				    	Text titleTextNode = (Text)tNode.getFirstChild();
    				    	if (titleTextNode != null)
    				    	{
    				    		if (lang.equalsIgnoreCase("eng"))
    				    			text.longTitleEn = titleTextNode.getData();
    				    		else if (lang.equalsIgnoreCase("bo"))
    				    			text.longTitleTi = titleTextNode.getData();
    				    		else if (lang.equalsIgnoreCase("bo-ltn"))
    				    			text.longTitleWy = titleTextNode.getData();
    				    		else if (lang.equalsIgnoreCase("sa-ltn"))
    				    			text.longTitleSa = titleTextNode.getData();
    				    	}
	    			    }
	    			}
	    		}
	    		
	    		// Author/translator
    			Node authNode = eh.getElementsByTagName("author").item(0);
    			if (authNode != null)
    			{
			    	Text authTextNode = (Text)authNode.getFirstChild();
			    	if (authTextNode != null)
			    		text.translator = authTextNode.getData();
    			}
    			
	    		// Edition
    			Node edNode = eh.getElementsByTagName("editionStmt").item(0);
    			if (edNode != null)
    			{
    				Element eed = (Element)edNode;
 
    				Node edtnNode = eed.getElementsByTagName("edition").item(0);
    				if (edtnNode != null)
    				{
    			    	Text edtnTextNode = (Text)edtnNode.getFirstChild();
    			    	if (edtnTextNode != null)
    			    		text.edition = edtnTextNode.getData().trim();
    				}
    				 
    				Node dateNode = eed.getElementsByTagName("date").item(0);
    				if (dateNode != null)
    				{
    			    	Text dateTextNode = (Text)dateNode.getFirstChild();
    			    	if (dateTextNode != null)
    			    		text.publishDate = dateTextNode.getData();
    				}
    			}
	    		
	    		// Publication
    			Node pubNode = eh.getElementsByTagName("publicationStmt").item(0);
    			if (pubNode != null)
    			{
    				Element epb = (Element)pubNode;
    				NodeList pNodeList = epb.getElementsByTagName("publisher");

    				// Publisher - 1st instance is name, graphic url
    				if (pNodeList.getLength() >= 1)
    				{
    					Text pubTextNode = (Text)pNodeList.item(0).getFirstChild();
    					if (pubTextNode != null)
    						text.publisher = pubTextNode.getData();
    				}

    				/*
    				// Publisher - 2nd instance is description
    				if (pNodeList.getLength() >= 2)
    				{
    					Text pubTextNode = (Text)pNodeList.item(1).getFirstChild();
    					if (pubTextNode != null)
    						text.publisherDesc = pubTextNode.getData();
    				}
    				*/
    				
    	    		// License
    				Node lNode = epb.getElementsByTagName("licence").item(0);
    				if (lNode != null)
    				{
    					Text licTextNode = (Text)lNode.getFirstChild();
    					if (licTextNode != null)
    						text.licenseText = licTextNode.getData();
    				}
    			}
    				    		
	    		// Bibliography
    			Node sourceNode = eh.getElementsByTagName("sourceDesc").item(0);
    			if (sourceNode != null)
    			{
					Element es = (Element)sourceNode;
    				NodeList bNodeList = es.getElementsByTagName("bibl");

    				// Bibl - 1st instance is toh, title, biblScope
    				if (bNodeList.getLength() >= 1)
    				{
    					Element eb = (Element)bNodeList.item(0);
        				
        				Node refNode = eb.getElementsByTagName("ref").item(0);
    					if (refNode != null)
    					{
        					Text refTextNode = (Text)refNode.getFirstChild();
    						if (refTextNode != null)
    							text.toh = refTextNode.getData();
    					}

        				Node titleNode = eb.getElementsByTagName("title").item(0);
    					if (titleNode != null)
    					{
        					Text titleTextNode = (Text)titleNode.getFirstChild();
    						if (titleTextNode != null)
    							text.biblTitle = titleTextNode.getData();
    					}

        				Node scopeNode = eb.getElementsByTagName("biblScope").item(0);
    					if (scopeNode != null)
    					{
        					Text scopeTextNode = (Text)scopeNode.getFirstChild();
    						if (scopeTextNode != null)
    							text.biblScope = scopeTextNode.getData();
    					}
    				}

    				// Bibl - 2nd instance is translation info
    				if (bNodeList.getLength() >= 2)
    				{
    					Text biblTextNode = (Text)bNodeList.item(1).getFirstChild();
    					if (biblTextNode != null)
    						text.translator = biblTextNode.getData();
    				}
    			}
		    }
		    else
		    {
				Window.alert("Could not parse text header XML.");
		    	parsed = false;
		    }
		}
		catch (DOMException e)
		{
			Window.alert("Exception parsing text header XML.");
			parsed = false;
		}
		
		return parsed;
	}

	/*
	public static boolean parseTextQueryFront(String xml, TextItem text)
	{
		boolean parsed = true;

		Log.info("Parsing text 'front'...");
		try
		{
		    // parse the XML document into a DOM
		    Document messageDom = XMLParser.parse(xml);

		    // Get header node
	    	Node fNode = messageDom.getElementsByTagName("front").item(0);
	    	if (fNode != null)
	    	{
	    		Element ef = (Element)fNode;
		    		
	    		// Summary
	    		NodeList fDivList = ef.getElementsByTagName("div");
	    		int fCount = fDivList.getLength();
	    		for (int i = 0; i < fCount; i++)
	    		{
	    			Element efs = (Element)fDivList.item(i);
    			    String type = efs.getAttribute("type");

    			    if (type.equalsIgnoreCase("summary"))
    			    {
    			    	Node sumNode = efs.getElementsByTagName("head").item(0);
				    	Text sumTextNode = (Text)sumNode.getFirstChild();
    			    	if (sumTextNode != null)
    			    		text.summaryHeading = sumTextNode.getData();
    			    	
    			    	Node pNode = efs.getElementsByTagName("p").item(0);
				    	Text pTextNode = (Text)pNode.getFirstChild();
    			    	if (pTextNode != null)
    			    		text.summaryText = pTextNode.getData();
    		    		Log.info("Summary length: " + text.summaryText.length());
    			    }
    			    else if (type.equalsIgnoreCase("acknowledgment"))
    			    {
    			    	Node ackNode = efs.getElementsByTagName("head").item(0);
				    	Text ackTextNode = (Text)ackNode.getFirstChild();
    			    	if (ackTextNode != null)
    			    		text.ackHeading = ackTextNode.getData();    			    	
    			    }
    			    else if (type.equalsIgnoreCase("introduction"))
    			    {
    			    	Node introNode = efs.getElementsByTagName("head").item(0);
				    	Text introTextNode = (Text)introNode.getFirstChild();
    			    	if (introTextNode != null)
    			    		text.introHeading = introTextNode.getData();
    			    }
	    		}
		    }
		    else
		    {
		    	// Handle non-TEI xml here
		    	parsed = false;
		    }
		}
		catch (DOMException e)
		{
			Window.alert("Could not parse text front XML.");
			parsed = false;
		}
		
		return parsed;
	}
	*/
}
