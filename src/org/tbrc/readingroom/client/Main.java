package org.tbrc.readingroom.client;

import java.util.ArrayList;

import org.tbrc.readingroom.shared.Global;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.client.Callback;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Cursor;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DeckPanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class Main extends Composite implements ValueChangeHandler<String>
{
	private static MainUiBinder uiBinder = GWT.create(MainUiBinder.class);
	
	interface MainUiBinder extends UiBinder<Widget, Main> {
	}

	//@UiField VerticalPanel mainPanel;
	@UiField HTMLPanel mainPanel;
	@UiField Reader reader;
	@UiField Header header;
	@UiField DeckPanel deckPanel;

	private class Page
	{
		public Composite panel;
		public String historyId = "";
		public String style = "";
		public String breadCrumbText = "";
	}
	
	//public BreadCrumb breadCrumb = new BreadCrumb();
	private ArrayList<Page> pages = new ArrayList<Page>();
	private Lobby lobby;
	private Section section;
	
	private DataItem currentDataItem = null;
	private Widget currentInvokingWidget = null;
	
	// Chapuza: gets around query failure when redirect occurs
	private boolean ignoreQueryError = false;

	// TEMPORARY label used for selecting folder to read from
	final Label verLbl = new Label("Build #" + Global.version);
	final Label dataLbl = new Label("PRODUCTION");
	
	public Main()
	{
		initWidget(uiBinder.createAndBindUi(this));
		
		// Initialize Google Analytics
		Log.info("Initializing google analytics for account: " + Global.GA_ACCOUNT);
		GAnalytics.initialize(Global.GA_ACCOUNT, gaInitCallback);

	    // Load initial (lobby) page
		lobby = new Lobby(true);
		deckPanel.add(lobby);
		
		Page p = new Page();
	    p.panel = lobby;
	    p.historyId = "lobby";
	    p.style = "cards";
	    p.breadCrumbText = "Lobby";
	    pages.add(p);
	    
		// Add history listener
	    History.addValueChangeHandler(this);
	    History.fireCurrentHistoryState();
		History.newItem("lobby");
	    
		// TEST TEST
		HorizontalPanel hp = new HorizontalPanel();
		verLbl.setStyleName("testingLabel");
		dataLbl.setStyleName("testingLabel");
		dataLbl.addStyleName("redColor");
		dataLbl.addStyleName("cursorPointer");
		dataLbl.setVisible(false);
		hp.add(verLbl);
		hp.add(dataLbl);
		header.topPanel.add(hp);
		
		// Add a handler to source button -- toggles among available folders
		dataLbl.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event)
			{
				if (dataLbl.getText() == "PRODUCTION")
				{
					dataLbl.setText("DEVELOPMENT");
					Global.dataFolder = "Dev";
				}
				else if (dataLbl.getText() == "DEVELOPMENT")
				{
					dataLbl.setText("STAGING");
					Global.dataFolder = "Staging";
				}
				else if (dataLbl.getText() == "STAGING")
				{
					dataLbl.setText("PRODUCTION");
					Global.dataFolder = "tei";
				}
			}
		});
		
		// Add a handler to version button -- triggers login pop-up
		verLbl.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event)
			{
				
				User.login(loginCallback);
			}
		});
		
		// Initialize and set RPC service for static User class
		User.initialize(Readingroom.rpcService);
		
		// TEST TEST
		
	    // Start on the Lobby page
	    //if (History.getToken().equals(""))
	    //	History.newItem("lobby");
	    // Force return to top-level lobby if page is reloaded
//		History.newItem("lobby");
	    
		// Issue query for lobby content
		Readingroom.rpcService.doQuery("lobby", null, lobbyCallback);
	    
//	    History.fireCurrentHistoryState();
		onViewportSizeChange();

		// Check for IE
		String agentInfo = Window.Navigator.getUserAgent();
		//Window.alert(agentInfo);
		if (agentInfo.contains(".NET"))
		{
			Window.alert("It appears that you are using Internet Explorer. Please be aware that there could be display " + 
						 "issues when using IE. This site is best viewed with Chrome, Firefox, or Safari.");
		}
		else if (agentInfo.contains("Android"))
		{
			Global.usingAndroid = true;
		}
		
		// Add window resize handler (responsive design)
		Window.addResizeHandler(new ResizeHandler() {
			public void onResize(ResizeEvent event)
			{
				onViewportSizeChange();
			}
		});
	}

	// Google Analytics callback
	Callback<Void, Exception> gaInitCallback = new Callback<Void, Exception>()
	{
		@Override
		public void onSuccess(Void arg0) {
			Log.info("gaInitCallback Success!");
//			GATracker.trackEvent(GWT.getModuleName(), "Initializing");
		}

		@Override
		public void onFailure(Exception ex) {
			Log.warn("gaInitCallback Failure!", ex);

		}
	};
	
	// Login callback
	private Callback<String, String> loginCallback = new Callback<String, String>()
	{
		public void onSuccess(String result)
		{
			// If a dev has logged in, turn on the folder toggle
			if (result.equals("dev"))
			{
				verLbl.setText("Build #" + Global.version);
				dataLbl.setVisible(true);
			}
			else if (result.equals("admin"))
			{
				verLbl.setText("Build #" + Global.version + " (ADMIN)");
				dataLbl.setText("STAGING");
				Global.dataFolder = "Staging";
				dataLbl.setVisible(false);
			}
			else // result = "none"
			{
				verLbl.setText("Build #" + Global.version);
				dataLbl.setText("PRODUCTION");
				Global.dataFolder = "tei";
				dataLbl.setVisible(false);
			}
		}

		public void onFailure(String result)
		{
		}
	};
	
	private void onViewportSizeChange()
	{
		int newWidth = Window.getClientWidth();

		if (reader.isVisible())
			reader.onViewportSizeChange(newWidth);
		else
		{
			header.onViewportSizeChange(newWidth);
			if (pages.get(deckPanel.getVisibleWidget()).style == "cards")
			{
				Lobby l = (Lobby)pages.get(deckPanel.getVisibleWidget()).panel;
				l.onViewportSizeChange(newWidth);
			}
			else
			{
				Section s = (Section)pages.get(deckPanel.getVisibleWidget()).panel;
				s.onViewportSizeChange(newWidth);
			}
		}
	}
	
	public void showPage(int pageIndex)
	{
		deckPanel.showWidget(pageIndex);
		onViewportSizeChange();
		Window.scrollTo (0, 0);	// Force page to scroll to top
	}

	public void onValueChange(ValueChangeEvent<String> event)
	{
		String token = event.getValue();
		//Window.alert("onValueChange: New value: " + token);
		Log.info("BrowserHistoryChangeHandler ValueChangeEvent token: " + token);
		GATracker.trackPageview("/" + token);
		
		/* THIS WAS REQUIRED FOR OLD READER
		if (event.getValue().contains("!ReadingRoom"))
		{
			//String url = GWT.getHostPageBaseURL() + "old-app/#" + token;
			String url = "old-app/#" + token;	// FOR TESTING
			//Window.alert("Got a route-worthy URL: " + url);
			Window.open(url, "_self", "");
			ignoreQueryError = true;
			return;
		}
		*/
	
		// Check for match in lobby/section pages - if no match, it must be a reader page
		boolean readerPage = true;
		for (int i = 0; i < pages.size(); i++)
		{
			if (token.equals(pages.get(i).historyId))
			{
				hideReader();
				readerPage = false;
				
				deckPanel.showWidget(i);
				header.addBreadCrumb(pages.get(i).breadCrumbText, pages.get(i).historyId);
				onViewportSizeChange();
				break;
			}
		}
		
		// For reader pages, the text ID is the token value
		if (readerPage)
		{
			// Ignore any left-over history items
			if (token.substring(0, 2) == "UT")
			{
				// Extract RID portion (trim off page indicator)
				int endOfRid = token.indexOf("/");
				String rid, page;
				if (endOfRid > 0)
				{
					rid = token.substring(0, endOfRid);
					page = token.substring(endOfRid+1);
				}
				else
				{
					rid = token;
					page = "title";
				}
				showReader(rid, page);
			}
		}
	}
	
	public void newPage(DataItem item, Widget invokingWidget)
	{
		//Queries.sectionQuery(item.getId(), sectionCallback);
		// Issue query via RPC service
		String[] args = {item.getId()};
		Readingroom.rpcService.doQuery("section", args, sectionCallback);
		currentDataItem = item;
		currentInvokingWidget = invokingWidget;
		
		// Show some activity during query (change cursor/mouse-pointer)
		currentInvokingWidget.getElement().getStyle().setCursor(Cursor.WAIT); 
	}
	
	public void showReader(String rid)
	{
		showReader(rid, "title");
	}
	
	public void showReader(String rid, String page)
	{
		// Turn off lobby
		header.setVisible(false);
		deckPanel.setVisible(false);
		
		// Turn on reader
		reader.setVisible(true);
		reader.onViewportSizeChange(Window.getClientWidth());
		reader.Initialize(rid, page, header.getBreadCrumb());
	}
	
	public void hideReader()
	{
		// Turn off reader
		reader.setVisible(false);

		// Turn off lobby
		header.setVisible(true);
		deckPanel.setVisible(true);
	}
	
	private void addNewPage(String style)
	{
		Page page = new Page();
		String id = "section-" + currentDataItem.getId();
		page.historyId = id;
		page.breadCrumbText = currentDataItem.getNameEn();

		if (style == "cards")
		{
			// Create & initialize new panel
			//lobby = null;
			lobby = new Lobby(false);
			lobby.initialize(currentDataItem);
			deckPanel.add(lobby);
			
			// Add corresponding "page" data
			page.panel = lobby;
			page.style = "cards";
		}
		else // if (style == "list")
		{
			// Create & initialize new panel
			//section = null;
			section = new Section();
			section.initialize(currentDataItem);
			deckPanel.add(section);
			
			// Add corresponding "page" data
			page.panel = section;
			page.style = "list";
		}

		// Return cursor (mouse pointer) to normal now that query is done
		currentInvokingWidget.getElement().getStyle().setCursor(Cursor.POINTER);
		
		pages.add(page);
		showPage(pages.size()-1);
		//Window.alert("addNewPage: id = {" + id + "}");
		History.newItem(id);
		//header.addBreadCrumb(currentDataItem.getNameEn(), id);
	}
	
	//
	// Call-back function from server query
	//
	protected AsyncCallback<String> lobbyCallback = new AsyncCallback<String>()
	{
		public void onFailure(Throwable caught)
		{
			// Show the RPC error message to the user
			// Chapuza: We ignore this if we have redirected the URL
			if (!ignoreQueryError)
				Window.alert("'lobby' Remote Procedure Call - Failure");
			ignoreQueryError = false;
		}

		public void onSuccess(String result)
		{
			ArrayList<DataItem> kItems = new ArrayList<DataItem>();
			ArrayList<DataItem> tItems = new ArrayList<DataItem>();
			kItems = Parser.parseLobbyQueryResult(result, "kanjur");
			tItems = Parser.parseLobbyQueryResult(result, "tenjur");
			String kDesc = Parser.getKangyurDesc();
			String tDesc = Parser.getTengyurDesc();
			String kLearnMore = Parser.getKangyurLearnMore();
			String tLearnMore = Parser.getTengyurLearnMore();
			
			lobby.populatePage(kDesc, kLearnMore, kItems, tDesc, tLearnMore, tItems);
			onViewportSizeChange();
		}
	};

	//
	// Call-back function from server query
	//
	protected AsyncCallback<String> sectionCallback = new AsyncCallback<String>()
	{
		public void onFailure(Throwable caught)
		{
			// Show the RPC error message to the user
			Window.alert("'section' Remote Procedure Call - Failure");
		}

		public void onSuccess(String result)
		{
			ArrayList<DataItem> items = new ArrayList<DataItem>();
			ArrayList<DataItem> noItems = new ArrayList<DataItem>();
			items = Parser.parseSectionQueryResult(result);
			currentDataItem.setLearnMore(Parser.getSectionLearnMore());
			currentDataItem.setNote(Parser.getSectionNote());
			
			String style = "list";
			if (items.size() > 0)
			{
				if (items.get(0).getType() == "section")
					style = "cards";
			}
			
			if (style == "list")
			{
				addNewPage(style);
				section.populatePage(items, currentDataItem.getCategory());
			}
			else // if (style == "cards")
			{
				addNewPage(style);
				if (currentDataItem.getCategory() == "kangyur")
					lobby.populatePage("", "", items, "", "", noItems);
				else
					lobby.populatePage("", "", noItems, "", "", items);
			}
			
			onViewportSizeChange();
		}
	};
}
