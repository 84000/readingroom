package co.eightyfourthousand.readingroom.client;

import co.eightyfourthousand.readingroom.shared.Global;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * 
 * The menu associated with the "hamburger" menu button for narrow viewports
 * 
 * @author curtis
 *
 */
public class PopupMenu extends PopupPanel
{
	public PopupMenu()
	{
		super(true);

		VerticalPanel panel = new VerticalPanel();
		panel.setStyleName("catalog");
		panel.setSpacing(10);
		
		Button homeButton = new Button("Home");	homeButton.setStyleName("infoButton"); homeButton.setWidth("140px");
		Button newsButton = new Button("News");	newsButton.setStyleName("infoButton"); newsButton.setWidth("140px");
		Button rrButton = new Button("Reading Room"); rrButton.setStyleName("infoButton"); rrButton.setWidth("140px");
		Button resourcesButton = new Button("News"); resourcesButton.setStyleName("infoButton"); resourcesButton.setWidth("140px");
		Button aboutButton = new Button("About"); aboutButton.setStyleName("infoButton"); aboutButton.setWidth("140px");
		Button helpButton = new Button("How You Can Help");	helpButton.setStyleName("infoButton"); helpButton.setWidth("140px");
		panel.add(homeButton);
		panel.add(newsButton);
		panel.add(rrButton);
		panel.add(resourcesButton);
		panel.add(aboutButton);
		panel.add(helpButton);
		
		homeButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				Window.open(Global.HOME_URL, "_self", "");
			}
		});

		newsButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				Window.open(Global.NEWS_URL, "_self", "");
			}
		});
		
		rrButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				Window.open(Global.RR_URL, "_self", "");
			}
		});

		rrButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				Window.open(Global.RR_URL, "_self", "");
			}
		});
		
		resourcesButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				Window.open(Global.RESOURCES_URL, "_self", "");
			}
		});
		
		aboutButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				Window.open(Global.ABOUT_URL, "_self", "");
			}
		});
		
		helpButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				Window.open(Global.HELP_URL, "_self", "");
			}
		});
		
		setWidget(panel);
	}
	
	protected void onUnload()
	{
		//Window.alert("closing!");
	}
}
