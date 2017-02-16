package co.eightyfourthousand.readingroom.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * 
 * Entry-point for app, specifically onModuleLoad()
 * 
 * @author curtis
 *
 */
public class Readingroom implements EntryPoint
{
	// RPC service proxy
	public static ReadingRoomServiceAsync rpcService;
	
	// Main reading-room page
	public static Main main;

	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad()
	{
		// Initialize RPC
		rpcService = GWT.create(ReadingRoomService.class);
		
		// Build out main page
		main = new Main();
		RootPanel.get("mainPanel").add(main);
		RootPanel.get().setStyleName("background");
	}
}
