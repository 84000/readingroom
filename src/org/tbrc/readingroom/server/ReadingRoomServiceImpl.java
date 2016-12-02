package org.tbrc.readingroom.server;

import java.util.ArrayList;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpSession;

import org.eightyfourthousand.pdf.tei2pdf;

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

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

/**
 * The server-side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class ReadingRoomServiceImpl extends RemoteServiceServlet implements ReadingRoomService
{
	String appUrl = "";
	String dbUser = "";
	String dbPswd = "";
	String devPswd = "";
	String adminPswd = "";
	String pdfPath = "";

	// Override init function to access web.xml values
	public void init(ServletConfig config) throws ServletException
	{
	    super.init(config);

	    // Get values from web.xml
    	dbUser = config.getServletContext().getInitParameter("dbUser");
    	dbPswd = config.getServletContext().getInitParameter("dbPswd");
    	devPswd = config.getServletContext().getInitParameter("devPswd");
    	adminPswd = config.getServletContext().getInitParameter("adminPswd");
    	Global.donateUrl = config.getServletContext().getInitParameter("donateUrl");

    	if (Global.isProduction)
	    {
	    	appUrl = config.getServletContext().getInitParameter("appUrl");
	    	Global.dbUrl = config.getServletContext().getInitParameter("dbUrl");
	    	pdfPath = config.getServletContext().getInitParameter("pdfPath");
	    }
	    else
	    {
	    	appUrl = config.getServletContext().getInitParameter("appUrlDev");
	    	Global.dbUrl = config.getServletContext().getInitParameter("dbUrlDev");
	    	pdfPath = config.getServletContext().getInitParameter("pdfPathDev");
	    }

	    System.out.println("devPswd: " + devPswd);
	    System.out.println("adminPswd: " + adminPswd);
	    System.out.println("pdfPath: " + pdfPath);
	}
	
	// Class globals
	// teiMap is a key/value map of tei content by client/user
	// Using this map, we can deliver portions of the parsed text on subsequent RPC calls
    //private Tei tei = new Tei();
    //private Map<String, Tei> teiMap = new HashMap<String, Tei>();

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
	
	/*
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
    */
    
	public String processText(String textId, String dataFolder)
	{
       	System.out.println("PROCESSING TEXT ID: " + textId);
		Tei tei = new Tei();
		//teiMap.put(token, tei);

		String[] args = {dataFolder, textId};
		String returnStr = doQuery("tei", args);
		tei.parse(returnStr);
		
		// TEST TEST TEST
		// If not already there, store TEI object in session
		HttpSession session = getSession();
		if (session.getAttribute(textId) == null)
			session.setAttribute(textId, tei);
		// TEST TEST TEST
	
//		// TEST TEST TEST
//		// STEP 1: Write TEI to text file
//		String path = "/home/ec2-user/browser/tomcat/webapps/pdf/";
//		try
//		{
//			BufferedWriter writer = new BufferedWriter ( new FileWriter(path + textId + ".tei") );
//			writer.write(returnStr);
//			writer.close();
//
//			// STEP 2: Convert TEI to PDF
//	       	System.out.println("ABOUT TO CONVERT TEI TO PDF...");
//			new tei2pdf(path+textId+".tei", path+textId+".pdf");
//	       	System.out.println("CONVERTED TEI TO PDF.");
//		}
//		catch (IOException e)
//		{
//			System.out.println("ERROR WRITING TO PDF FILE: [" + e.toString() + "]");
//			e.printStackTrace();
//		}
//		// TEST TEST TEST
		
		return tei.header;
	}
	
	public ArrayList<PartName> getTranslationPartNames(String textId)
	{
		//return teiMap.get(token).getTranslationPartNames();
		HttpSession session = getSession();
		Tei tei = (Tei)session.getAttribute(textId);
		return tei.getTranslationPartNames();
	}
	
	public TextPartData getTranslationPartByName(String textId, String partName)
	{
		/*
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
		*/
		HttpSession session = getSession();
		Tei tei = (Tei)session.getAttribute(textId);
		if (partName.equalsIgnoreCase("summary"))
			return tei.getTextSummaryData();
		else if (partName.equalsIgnoreCase("ack"))
			return tei.getTextAckData();
		else if (partName.equalsIgnoreCase("intro"))
			return tei.getTextIntroData();
		else if (partName.equalsIgnoreCase("bibl"))
			return tei.getTextBiblData();
		else
			return tei.getTransPartByName(partName);
	}
	
	public TextData getTextData(String textId)
	{
		//return teiMap.get(token).getTextData();
		HttpSession session = getSession();
		Tei tei = (Tei)session.getAttribute(textId);
		return tei.getTextData();
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
	
	//
	// Converts given text to a PDF and returns URL to resulting PDF
	//
	public String generatePdf(String textId)
	{
		String url = null;
		
		// STEP 1: Write TEI to text file
		try
		{
			BufferedWriter writer = new BufferedWriter ( new FileWriter(pdfPath + textId + ".tei") );
			Tei tei = (Tei)getSession().getAttribute(textId);
			writer.write(tei.teiStr);
			writer.close();

			// STEP 2: Convert TEI to PDF
	       	System.out.println("ABOUT TO CONVERT TEI TO PDF...");
			new tei2pdf(pdfPath+textId+".tei", pdfPath+textId+".pdf");
	       	System.out.println("CONVERTED TEI TO PDF.");
	       	
	       	// Construct URL to resulting PDF file
	       	url = appUrl + "pdf/" + textId + ".pdf";
		}
		catch (IOException e)
		{
			System.out.println("ERROR WRITING TO PDF FILE: [" + e.toString() + "]");
			e.printStackTrace();
		}
		
		return url;
	}
	
	/*
	 * Returns the current session
	 *
	 * @return  The current Session
	 */
	private HttpSession getSession()
	{
		// Get the current request and then return its session
		HttpSession session = this.getThreadLocalRequest().getSession();
		//System.out.println("*** SESSION ID: " + session.getId());
		return session;
	}
}
