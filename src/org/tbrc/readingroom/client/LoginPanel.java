package org.tbrc.readingroom.client;

import org.tbrc.readingroom.shared.Global;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PasswordTextBox;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

public class LoginPanel extends PopupPanel
{
	private final int PANEL_WIDTH = 300;
	private final int PANEL_HEIGHT = 170;
	
	private boolean cancelled = false;

	private Button submitButton = new Button("Submit");
	private Button cancelButton = new Button("Cancel");

	private String user = "";
	private String pswd = "";

	private final TextBox nameInput = new TextBox();
	private final PasswordTextBox pswdInput = new PasswordTextBox();

	LoginPanel()
	{
		this.setAnimationEnabled(true);
		this.setGlassEnabled(true);
		this.setModal(true);
		this.addStyleName("popupPanel");
		 
		VerticalPanel mainPanel = new VerticalPanel();
		AbsolutePanel authPanel = new AbsolutePanel();
		authPanel.setPixelSize(PANEL_WIDTH, PANEL_HEIGHT);
		
		Label versionLabel = new Label("84000 Login - Build #" + Global.version);
		versionLabel.setStyleName("version");
		
		Label nameLabel = new Label("User Name");
		Label pswdLabel = new Label("Password");
		
		nameInput.setPixelSize(160, 20);
		pswdInput.setPixelSize(160, 20);

		authPanel.add(nameLabel, 25, 35);
		authPanel.add(pswdLabel, 25, 75);
		authPanel.add(nameInput, 105, 30);
		authPanel.add(pswdInput, 105, 70);
		authPanel.add(submitButton, 225, 130);
		authPanel.add(cancelButton, 20, 130);
		
		mainPanel.add(versionLabel);
		mainPanel.add(authPanel);
		this.add(mainPanel);
		
		// Add a handler to submit button
		submitButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event)
			{
				user = nameInput.getText();
				pswd = pswdInput.getText();
				
				cancelled = false;
				hide();
			}
		});

		// Add a handler to cancel button
		cancelButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event)
			{
				cancelled = true;
				hide();
			}
		});
	}

	// This enables pressing "enter" key to submit
	protected void onPreviewNativeEvent(Event.NativePreviewEvent event)
	{
	    if (event.getTypeInt() == Event.ONKEYDOWN)
	    {
	        if (event.getNativeEvent().getKeyCode() == KeyCodes.KEY_ENTER)
	            submitButton.click();
	    }
	    super.onPreviewNativeEvent(event);
	}

	public String getUser() { return user; }
	public String getPswd() { return pswd; }
	public void clearFields()
	{
		nameInput.setText("");
		pswdInput.setText("");
		
		Scheduler.get().scheduleDeferred(new ScheduledCommand() {
		    @Override
		    public void execute() {
		        nameInput.setFocus(true);
		    }
		});
	}
	public boolean hasCancelled() { return cancelled; }
	
	public String[] getCreds()
	{
		String[] creds = {user, pswd};
		return creds;
	}
}
