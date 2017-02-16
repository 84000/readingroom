package co.eightyfourthousand.readingroom.client;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * 
 * A general-purpose appropriately styled notification panel
 * 
 * @author curtis
 *
 */
public class AlertPanel extends PopupPanel
{
	private final int PANEL_WIDTH = 300;
	private final int PANEL_HEIGHT = 170;
	
	private boolean cancelled = false;

	private Button submitButton = new Button("OK");

	AlertPanel(String msg)
	{
		this.setAnimationEnabled(true);
		this.setGlassEnabled(true);
		this.setModal(true);
		this.addStyleName("popupPanel");
		 
		VerticalPanel mainPanel = new VerticalPanel();
		AbsolutePanel authPanel = new AbsolutePanel();
		authPanel.setPixelSize(PANEL_WIDTH, PANEL_HEIGHT);
		
		Label msgLabel = new Label(msg);
		msgLabel.setWidth("" + PANEL_WIDTH + "px");
		msgLabel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		
		authPanel.add(msgLabel, 0, 35);
		submitButton.setSize("50px", "25px");
		authPanel.add(submitButton, 225, 130);
		
		mainPanel.add(authPanel);
		this.add(mainPanel);
		
		// Add a handler to submit button
		submitButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event)
			{
				cancelled = false;
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

	public boolean hasCancelled() { return cancelled; }
}
