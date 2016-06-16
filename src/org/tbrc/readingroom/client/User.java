package org.tbrc.readingroom.client;

import com.google.gwt.core.client.Callback;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.PopupPanel;

public class User
{
	private static String id = "";
	private static String password = "";
	//private static boolean admin = false;
	private static String userType = "none";
	private static boolean userChanged = false;
	private static LoginPanel login = new LoginPanel();
	
	private static ReadingRoomServiceAsync rpcService;
	private static Callback<String, String> loginCallback;
	
	public static void initialize(ReadingRoomServiceAsync service)
	{
		rpcService = service;

		login.addCloseHandler(new CloseHandler<PopupPanel>() {
			@Override
			public void onClose(CloseEvent<PopupPanel> event)
			{
			    // If detail button was clicked, show correct detail panel
				if (!login.hasCancelled())
				{
					// Check credentials
					String[] creds = login.getCreds();
					rpcService.authenticate(creds, userValidationCallback);
				}
			}
		});
	}
	
	public static void login(Callback<String, String> callback)
	{
		// Reset
		id = "";
		password = "";
		userType = "none";
		login.clearFields();
		
		// Capture callback
		loginCallback = callback;
		
		// Present login dialog
		login.center();
	}
	
	public static String getId() { return id; }
	public static String getPassword() { return password; }
	public static boolean isAdmin()
	{
		if (userType.equals("admin"))
			return true;
		else
			return false;
	}
	public static boolean isDev()
	{
		if (userType.equals("dev"))
			return true;
		else
			return false;
	}
	public static boolean hasUserChanged() { return userChanged; }
	public static void resetUser() { userChanged = false; }

	protected static AsyncCallback<String> userValidationCallback = new AsyncCallback<String>()
	{
		public void onFailure(Throwable caught)
		{
			// Show the RPC error message to the user
			Window.alert("'user-validation' Remote Procedure Call - Failure");
		}

		public void onSuccess(String result)
		{
			if (result == null)
				Window.alert("NULL RESULT!");
			else
			{
				if (result.equals("none"))
				{
					userType = "none";
					Window.alert("Invalid login");
				}
				else
				{
					id = login.getUser();
					password = login.getPswd();
					userChanged = true;

					if (result.equals("admin"))
						userType = "admin";
					else if (result.equals("dev"))
						userType = "dev";
					
					loginCallback.onSuccess(userType);
					//Window.alert("Authenticated as '" + userType + "' user");
				}
			}
		}
	};
}
