package co.eightyfourthousand.readingroom.shared;

/**
 * <p>
 * QueryVerifier validates that the name of the given query is valid.
 * </p>
 */
public class QueryVerifier
{
	/**
	 * Verifies that the specified query name is valid for our service.
	 * 
	 * @param name the query to validate
	 * @return true if valid, false if invalid
	 */
	public static boolean isValidName(String queryName)
	{
		boolean queryOK = false;
		
		if (queryName.equals("lobby") ||
			queryName.equals("section") ||
			queryName.equals("get-config") ||
			queryName.equals("tei"))
		{
			queryOK = true;
		}
		
		return queryOK;
	}
}
