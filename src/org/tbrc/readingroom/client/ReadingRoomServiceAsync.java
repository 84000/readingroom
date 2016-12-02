package org.tbrc.readingroom.client;

import java.util.ArrayList;

import org.tbrc.readingroom.shared.PartName;
import org.tbrc.readingroom.shared.TextData;
import org.tbrc.readingroom.shared.TextPartData;

import com.google.gwt.user.client.rpc.*;

/**
 * The async counterpart of <code>GreetingService</code>.
 */
public interface ReadingRoomServiceAsync
{
	void doQuery(String queryName, String[] args, AsyncCallback<String> callback) throws IllegalArgumentException;
	//void clearToken(String token, AsyncCallback<String> callback) throws IllegalArgumentException;
	//void getToken(AsyncCallback<String> callback) throws IllegalArgumentException;
	void processText(String textId, String dataFolder, AsyncCallback<String> callback) throws IllegalArgumentException;
	void getTranslationPartNames(String textId, AsyncCallback<ArrayList<PartName>> callback) throws IllegalArgumentException;
	void getTranslationPartByName(String textId, String partName, AsyncCallback<TextPartData> callback) throws IllegalArgumentException;
	void getTextData(String textId, AsyncCallback<TextData> callback) throws IllegalArgumentException;
	void authenticate(String[] creds, AsyncCallback<String> callback) throws IllegalArgumentException;
	void generatePdf(String textId, AsyncCallback<String> callback) throws IllegalArgumentException;
}
