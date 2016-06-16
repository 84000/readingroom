package org.tbrc.readingroom.server;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;

import org.tbrc.readingroom.client.ReadingRoomService;
import org.tbrc.readingroom.shared.Global;
import org.tbrc.readingroom.shared.PartName;
import org.tbrc.readingroom.shared.TextData;
import org.tbrc.readingroom.shared.QueryVerifier;
import org.tbrc.readingroom.shared.TextPartData;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xmldb.api.base.Collection;
import org.xmldb.api.base.ResourceSet;
import org.xmldb.api.modules.XMLResource;
import org.xmldb.api.modules.XPathQueryService;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * The server-side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class ReadingRoomServiceImpl extends RemoteServiceServlet implements ReadingRoomService
{
	String dbUser = "";
	String dbPswd = "";
	String devPswd = "";
	String adminPswd = "";

	// Override init function to access web.xml values
	public void init(ServletConfig config) throws ServletException
	{
	    super.init(config);

	    Global.dbUrl = config.getServletContext().getInitParameter("dbUrl");
	    dbUser = config.getServletContext().getInitParameter("dbUser");
	    dbPswd = config.getServletContext().getInitParameter("dbPswd");
	    devPswd = config.getServletContext().getInitParameter("devPswd");
	    adminPswd = config.getServletContext().getInitParameter("adminPswd");

	    System.out.println("devPswd: " + devPswd);
	    System.out.println("adminPswd: " + adminPswd);
	}
	
	// Class globals
	// teiMap is a key/value map of tei content by client/user
	// Using this map, we can deliver portions of the parsed text on subsequent RPC calls
    //private Tei tei = new Tei();
    private Map<String, Tei> teiMap = new HashMap<String, Tei>();

	// Main query method for accessing eXist database
	public String doQuery(String queryName, String[] args)
	{
		String returnStr = null;
		
		// Verify that the query is valid. 
		if (!QueryVerifier.isValidName(queryName))
		{
			// If the query name is not valid, throw an IllegalArgumentException back to the client.
			throw new IllegalArgumentException("Invalid query type received");
		}

		// Query eXist database
		System.out.println("Querying eXist database...");
        
		//boolean validResults = false;
        try
        {
        	//Collection col = Data.getCollection(MOD_FOLDER, null, null);
        	Collection col = Data.getCollection(Global.MOD_FOLDER, dbUser, dbPswd);
            
            XPathQueryService service = (XPathQueryService)col.getService("XPathQueryService", "1.0");  
            service.setProperty("indent", "yes");

            // generate query
    		//String xQuery = Data.generateQuery("queries", "allArtObjects", null, "q", 10);
    		// moduleName = "84000", queryName = "lobby", args = null
    		String xQuery = Data.generateQuery(Global.MODULE_NAME, queryName, args, Global.NAMESPACE, Global.TIMEOUT_SECS);

    		// execute query
            ResourceSet resSet = service.query(xQuery);
            if (resSet == null)
            	System.out.println("Query returned no results");
            else
            {
            	System.out.println("Query returned results");
				if (resSet.getSize() > 0)
				{
					//validResults = true;
					XMLResource res = (XMLResource) resSet.getResource(0l);
					Object cont = res.getContent();
					returnStr = cont.toString();

					// 
					Document doc = (Document) res.getContentAsDOM();
					Element parent = (Element) doc.getFirstChild();
					NodeList nodes = parent.getChildNodes();

					// count the number of result Element nodes
					int elemNodes = nodes.getLength();
					for (int i = 0; i < nodes.getLength(); i++)
					{
						if (nodes.item(i).getNodeType() != Node.ELEMENT_NODE)
							elemNodes--;
						else
						{
							//String content = nodes.item(i).getTextContent();
							//System.out.println("Node items[" + i + "] " + nodes.item(i).getNodeName() + " content: " + content);
						}
					}

					System.out.println("Query found " + elemNodes + " elements in results");
            	}
            }
		}
        catch (Exception e)
        {
			System.out.println("Exception in query: " + e.getMessage());
        }

       	System.out.println("RETURNED STRING: [" + returnStr.substring(0, 100) + "...]");
        
        return returnStr;
	}
	
	// Clear a previous token from the same client
	public String clearToken(String token)
	{
		teiMap.remove(token);
		return "ok";
	}
	
    // Retrieves a token that identifies the client for subsequent actions on same text
    public String getToken()
    {
    	UUID token = UUID.randomUUID();
    	
    	return token.toString();
    }
    
	public String processText(String token, String dataFolder, String textId)
	{		
		Tei tei = new Tei();
		teiMap.put(token, tei);

		String[] args = {dataFolder, textId};
		String returnStr = doQuery("tei", args);
		tei.parse(returnStr);
		
		return tei.header;
	}
	
	public ArrayList<PartName> getTranslationPartNames(String token)
	{
		return teiMap.get(token).getTranslationPartNames();
	}
	
	public TextPartData getTranslationPartByName(String token, String partName)
	{
		if (partName.equalsIgnoreCase("summary"))
			return teiMap.get(token).getTextSummaryData();
		else if (partName.equalsIgnoreCase("ack"))
			return teiMap.get(token).getTextAckData();
		else if (partName.equalsIgnoreCase("intro"))
			return teiMap.get(token).getTextIntroData();
		else if (partName.equalsIgnoreCase("bibl"))
			return teiMap.get(token).getTextBiblData();
		else
			return teiMap.get(token).getTransPartByName(partName);
	}
	
	public TextData getTextData(String token)
	{
		return teiMap.get(token).getTextData();
	}
	
	public String authenticate(String[] creds)
	{
		String userType = "none";
		
		// Short-term chapuza!!
		/*
		if (creds[0].equals("admin") && creds[1].equals("translation"))
			userType = "admin";
		else if (creds[0].equals("dev") && creds[1].equals("tei"))
			userType = "dev";
		*/
		if (creds[0].equals("admin") && creds[1].equals(adminPswd))
			userType = "admin";
		else if (creds[0].equals("dev") && creds[1].equals(devPswd))
			userType = "dev";
		
		return userType;
	}
}
