package co.eightyfourthousand.readingroom.client;

import co.eightyfourthousand.readingroom.shared.Global;

/**
 * 
 * A collection of generally useful utility functions
 * 
 * @author curtis
 *
 */
public class Utils {
	public static Boolean viewPortChanged(int newWidth, int oldWidth)
	{
		Boolean portChanged = false;
		
		if (oldWidth == 0)
			portChanged = true;
		else if (newWidth >= Global.LARGE_VIEWPORT_MIN_WIDTH)
		{
			if (oldWidth < Global.LARGE_VIEWPORT_MIN_WIDTH)
				portChanged = true;
		}
		else if (newWidth >= Global.MEDIUM_VIEWPORT_MIN_WIDTH && newWidth < Global.LARGE_VIEWPORT_MIN_WIDTH)
		{
			if (oldWidth < Global.MEDIUM_VIEWPORT_MIN_WIDTH || oldWidth >= Global.LARGE_VIEWPORT_MIN_WIDTH)
				portChanged = true;
		}
		else // newWidth < med min
		{
			if (oldWidth >= Global.MEDIUM_VIEWPORT_MIN_WIDTH)
				portChanged = true;
		}
		
		return portChanged;
	}
	
	public static String truncateString(String fullString, int len)
	{
		String truncatedString = "";

		if (fullString.length() > len)
			truncatedString = fullString.substring(0, len) + "...";
		else
			truncatedString = fullString;
		
		return truncatedString;
	}
	
	public static String removeTrailingChar(String str, String ch)
	{
		String finalStr = "";
		
		if (str.endsWith(ch))
			finalStr = str.substring(0, (str.length() - 1));
		else
			finalStr = str;

		return finalStr;
	}
}

