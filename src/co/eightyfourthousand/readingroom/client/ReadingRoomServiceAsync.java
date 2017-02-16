package co.eightyfourthousand.readingroom.client;

import java.util.ArrayList;

import co.eightyfourthousand.readingroom.shared.PartName;
import co.eightyfourthousand.readingroom.shared.TextData;
import co.eightyfourthousand.readingroom.shared.TextPartData;

import com.google.gwt.user.client.rpc.*;

/**
 * The async counterpart of <code>GreetingService</code>.
 */
public interface ReadingRoomServiceAsync
{
	void doQuery(String queryName, String[] args, AsyncCallback<String> callback) throws IllegalArgumentException;
	void processText(String textId, String dataFolder, AsyncCallback<String> callback) throws IllegalArgumentException;
	void getTranslationPartNames(String textId, AsyncCallback<ArrayList<PartName>> callback) throws IllegalArgumentException;
	void getTranslationPartByName(String textId, String partName, AsyncCallback<TextPartData> callback) throws IllegalArgumentException;
	void getTextData(String textId, AsyncCallback<TextData> callback) throws IllegalArgumentException;
	void authenticate(String[] creds, AsyncCallback<String> callback) throws IllegalArgumentException;
	void generatePdf(String textId, AsyncCallback<String> callback) throws IllegalArgumentException;
}
