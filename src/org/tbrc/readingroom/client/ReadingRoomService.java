package org.tbrc.readingroom.client;

import java.util.ArrayList;

import org.tbrc.readingroom.shared.PartName;
import org.tbrc.readingroom.shared.TextData;
import org.tbrc.readingroom.shared.TextPartData;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * The client-side stub for the RPC service.
 */
@RemoteServiceRelativePath("rr")
public interface ReadingRoomService extends RemoteService
{
	//String greetServer(String name) throws IllegalArgumentException;
	String doQuery(String queryName, String[] args) throws IllegalArgumentException;
	String clearToken(String token) throws IllegalArgumentException;
	String getToken() throws IllegalArgumentException;
	String processText(String token, String dataFolder, String textId) throws IllegalArgumentException;
	ArrayList<PartName> getTranslationPartNames(String token) throws IllegalArgumentException;
	TextPartData getTranslationPartByName(String token, String partName) throws IllegalArgumentException;
	TextData getTextData(String token) throws IllegalArgumentException;
	String authenticate(String[] creds) throws IllegalArgumentException;
}
