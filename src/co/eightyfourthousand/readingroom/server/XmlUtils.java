package co.eightyfourthousand.readingroom.server;

import java.text.Normalizer;
import java.text.Normalizer.Form;

import co.eightyfourthousand.readingroom.shared.Global;

public class XmlUtils
{
	//
	// Utility functions
	//
	
	//
	// Gets complete XML node, starting at "begin" within "str"
	//
	public static String getXmlNode(String str, String nodeType, int begin)
	{
		String fullNode = "";
		
		String startTag = "<" + nodeType;
		String endTag = "</" + nodeType + ">";

		int startTagPos = begin, prevStartTagPos = begin+1;
		int endTagPos, prevEndTagPos = begin;
		
		int endTagsNeeded = 1;	// We know we need at least the 1 endTag for our known tag

		while (endTagsNeeded > 0)
		{
			endTagPos = str.indexOf(endTag, prevEndTagPos);
			if (endTagPos < 0)
				break;
			
			endTagsNeeded--;
				
			// Check for additional start tags between original and newly-found end tag
			while (true)
			{
				startTagPos = str.indexOf(startTag, prevStartTagPos);
				if (startTagPos < 0 || startTagPos >= endTagPos)
					break;
				endTagsNeeded++;
				prevStartTagPos = startTagPos+1;
			}
			
			prevEndTagPos = endTagPos+1;
		}
		
		fullNode = str.substring(begin, prevEndTagPos + endTag.length());
		
		return fullNode;
	}
	
	//
	// Gets string (from within "str") that starts with "beginStr" and ends with "endStr"
	// Will include or not include the bounding strings based on "includeBookends"
	//
	public static String getBookendedString(String str, String beginStr, String endStr, boolean includeBookends)
	{
		String subStr = "";
		int begin = 0, end = 0;

		if (includeBookends)
		{
			begin = str.indexOf(beginStr);
			if (begin >= 0)
			{
				end = str.indexOf(endStr, begin);
				if (begin >= 0 && end > begin)
					subStr = str.substring(begin, (end + endStr.length()));
			}
		}
		else
		{
			begin = str.indexOf(beginStr);
			if (begin >= 0)
			{
				end = str.indexOf(endStr, begin);
				if (begin >= 0 && end > begin)
					subStr = str.substring((begin + beginStr.length()), end);
			}
		}
		
		return subStr;
	}
	
	/**
	 * Count the number of instances of substring within a string.
	 *
	 * @param string     String to look for substring in.
	 * @param substring  Sub-string to look for.
	 * @return           Count of substrings in string.
	 */
	public static int count(final String string, final String substring)
	{
		int count = 0;
		int idx = 0;

		while ((idx = string.indexOf(substring, idx)) != -1)
		{
			idx++;
			count++;
		}

		return count;
	}

	/**
	 * Escape an html string. Escaping data received from the client helps to
	 * prevent cross-site script vulnerabilities.
	 * 
	 * @param html the html string to escape
	 * @return the escaped string
	 */
	public static String escapeHtml(String html)
	{
		if (html == null)
			return null;

		return html.replaceAll("&", "&amp;").replaceAll("<", "&lt;").replaceAll(">", "&gt;");
	}
    

	/*
	 * Determine if a word is isolated (i.e., not part of another word) within a string
	 */
	public static boolean isIsolatedWord(String word, String str, int wordPos)
	{
		boolean isIsolated = true;
		int wordEnd = wordPos + word.length();
		int strLen = str.length();
		String charBefore = null, charAfter = null;

		// Handle character before word
		if (wordPos > 0)
			charBefore = str.substring(wordPos-1, wordPos);
		
		if (charBefore != null)
		{
			if (!Global.SEPARATOR_CHARS.contains(charBefore))
				isIsolated = false;
		}
		
		// Handle character after word (if character before was OK)
		if (isIsolated == true)
		{
			// "s" in case term is plural
			if (wordEnd < strLen)
			{
				if (str.charAt(wordEnd) == 's')
					wordEnd++;
			}

			// "'" or "'s" in case term is possessive/genitive
			if ((wordEnd+1) < strLen)
			{
				if (str.charAt(wordEnd) == '’' && str.charAt(wordEnd+1) == 's')
					wordEnd += 2;
			}
			if (wordEnd < strLen)
			{
				if (str.charAt(wordEnd) == '’')
					wordEnd++;
			}

			if (wordEnd < strLen)
				charAfter = str.substring(wordEnd, wordEnd+1);
		
			if (charAfter != null)
			{
				if (!Global.SEPARATOR_CHARS.contains(charAfter))
					isIsolated = false;
			}
		}
		
		return isIsolated;
	}
	
	public static String removeDiacriticalMarks(String string)
	{
	    return Normalizer.normalize(string, Form.NFD).replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
	}
	
	/*
	 * Removes all comments (content inside "<!--" and "-->")
	 */
	public static String stripComments(String str)
	{
		String beginStr = "<!--", endStr = "-->";
		String newStr = "";
		int begin = 0, end = 0;
		boolean error = false;

		while ((begin = str.indexOf(beginStr, end)) >= 0)
		{
			newStr += str.substring(end, begin);
			end = str.indexOf(endStr, begin) + endStr.length();

			if (end < begin)	// No matching endStr found
			{
				error = true;
				break;
			}
		}
		if (error)
			System.out.println("ERROR STRIPPING COMMENTS - OPEN-ENDED COMMENT!");
		else
			newStr += str.substring(end);

		return newStr;
	}
}
