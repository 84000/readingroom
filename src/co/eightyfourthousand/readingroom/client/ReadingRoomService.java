package co.eightyfourthousand.readingroom.client;

import java.util.ArrayList;

import co.eightyfourthousand.readingroom.shared.PartName;
import co.eightyfourthousand.readingroom.shared.TextData;
import co.eightyfourthousand.readingroom.shared.TextPartData;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * The client-side stub for the RPC service.
 */
@RemoteServiceRelativePath("rr")
public interface ReadingRoomService extends RemoteService
{
	String doQuery(String queryName, String[] args) throws IllegalArgumentException;
	String processText(String textId, String dataFolder) throws IllegalArgumentException;
	ArrayList<PartName> getTranslationPartNames(String textId) throws IllegalArgumentException;
	TextPartData getTranslationPartByName(String textId, String partName) throws IllegalArgumentException;
	TextData getTextData(String textId) throws IllegalArgumentException;
	String authenticate(String[] creds) throws IllegalArgumentException;
	String generatePdf(String textId) throws IllegalArgumentException;
}
