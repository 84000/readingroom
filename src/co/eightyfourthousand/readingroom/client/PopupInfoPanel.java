package co.eightyfourthousand.readingroom.client;

import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * 
 * Simple styled popup that is used for things like showing glossary-term definitions
 * 
 * @author curtis
 *
 */
public class PopupInfoPanel extends PopupPanel
{
	HTML html = new HTML();

	PopupInfoPanel()
	{
		this.setAnimationEnabled(true);
		this.setGlassEnabled(true);
		this.setModal(true);
		this.setAutoHideEnabled(true);
		
		this.addStyleName("popupPanel");
		
		DockPanel mainPanel = new DockPanel();
		mainPanel.setSpacing(12);
		VerticalPanel contentPanel = new VerticalPanel();
		
		contentPanel.add(html);
		mainPanel.add(contentPanel, DockPanel.CENTER);
		this.add(mainPanel);
	}
	
	public void setHtml(String html)
	{
		this.html.setHTML(html);
	}

	// This enables pressing "enter" key to submit
	protected void onPreviewNativeEvent(Event.NativePreviewEvent event)
	{
	    if (event.getTypeInt() == Event.ONKEYDOWN)
	    {
	        if (event.getNativeEvent().getKeyCode() == KeyCodes.KEY_ENTER)
	        	hide();
	    }
	    super.onPreviewNativeEvent(event);
	}
}
