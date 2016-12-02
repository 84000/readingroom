package org.tbrc.readingroom.client;

import java.util.ArrayList;

import org.tbrc.readingroom.shared.Global;
import org.tbrc.readingroom.shared.PartName;
import org.tbrc.readingroom.shared.TextData;
import org.tbrc.readingroom.shared.TextPartData;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DeckPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;

public class Reader extends Composite {

	private static ReaderUiBinder uiBinder = GWT.create(ReaderUiBinder.class);

	interface ReaderUiBinder extends UiBinder<Widget, Reader> {
	}

	public Reader()
	{
		initWidget(uiBinder.createAndBindUi(this));
	}

	@UiField HTMLPanel wrapperPanel;
	@UiField TextHeader header;
	@UiField DeckPanel textDeckPanel;
	
	@UiField Button infoButton;
	@UiField PopupNavPanel navPanel;
	@UiField HTML footerPanel;

	//private String myToken = "";
	
	private TextItem textItem = new TextItem();
	private int currentWidget = 0;
	private Reader me = this;
	public TextData textData = new TextData();

	//public ArrayList<GlossItem> masterGlossItems = new ArrayList<GlossItem>();
	//public ArrayList<NoteItem> masterNoteItems = new ArrayList<NoteItem>();
	
	public void Initialize(String textId, String pageName, BreadCrumb bc)
	{
		// Reject duplicate
		//Window.alert("INITIALIZE: textId = {" + textId + "}, pageName = {" + pageName + "}");
		if (textId.equalsIgnoreCase(textItem.getId()))
		{
			int i = getPageIndex(pageName);
			if (i != currentWidget)
			{
				currentWidget = i;
				textDeckPanel.showWidget(currentWidget);
				History.newItem(textItem.getId() + "/" + pageName);
			}
		}
		else
		{
			// Set breadcrumb (shows while text is loading)
			header.setBreadCrumb(bc);

			// Capture ID
			textItem.initialize();
			textItem.setId(textId);
		
			// Hide pop-up nav panel
			navPanel.setVisible(false);
			
			// Reset headers while query is running
			header.titleEn.setText("Loading...");
		
			// Clear out any previously existing text data
			textDeckPanel.clear();
			textData.glossItems.clear();
			textData.noteItems.clear();
			currentWidget = 0;

			// Initialize for new text
			TextPage titlePage = new TextPage("Loading...", "", "", "Summary");
			titlePage.setReader(me);
			textDeckPanel.add(titlePage);
			textDeckPanel.showWidget(currentWidget);
			History.newItem(textItem.getId() + "/" + pageName);

			// Clear any previously acquired text instance
			/*
			if (myToken.length() > 0)
				Readingroom.rpcService.clearToken(myToken, new ClearTokenCallBack());
			else
			{
				// Issue initial query via RPC service
				Readingroom.rpcService.getToken(new TokenCallBack());
			}
			*/
			Readingroom.rpcService.processText(textItem.getId(), Global.dataFolder, new TextCallBack());
		}
	}
	
	@UiHandler("infoButton")
	void onInfoButtonClick(ClickEvent e)
	{
		navPanel.setVisible(!navPanel.isVisible());
		
		TextPage currentPage = (TextPage)textDeckPanel.getWidget(currentWidget);
		currentPage.accommodateNavPanel(navPanel.isVisible());
	}
	
	public int getPageCount() { return textDeckPanel.getWidgetCount(); }
	
	
	public void prev()
	{
		if (currentWidget > 0)
		{
			currentWidget--;
			checkAndLoadTextParts(currentWidget);
			textDeckPanel.showWidget(currentWidget);
			TextPage textPage = (TextPage)textDeckPanel.getWidget(currentWidget);
			History.newItem(textItem.getId() + "/" + textPage.getTitle().toLowerCase());
			Window.scrollTo (0, 0);	// Force page to scroll to top
			textPage.accommodateNavPanel(navPanel.isVisible());
		}
	}

	public void next()
	{
		if (currentWidget < (textDeckPanel.getWidgetCount() - 1))
		{
			currentWidget++;
			checkAndLoadTextParts(currentWidget);
			textDeckPanel.showWidget(currentWidget);
			TextPage textPage = (TextPage)textDeckPanel.getWidget(currentWidget);
			History.newItem(textItem.getId() + "/" + textPage.getTitle().toLowerCase());
			Window.scrollTo (0, 0);	// Force page to scroll to top
			textPage.accommodateNavPanel(navPanel.isVisible());
		}
	}

	public void onViewportSizeChange(int newWidth)
	{
		wrapperPanel.setWidth("100%");
		
		// Header
		header.onViewportSizeChange(newWidth);
		
		// Nav Panel
		navPanel.setWidth("100%");
		navPanel.onViewportSizeChange(newWidth);
		
		// Main page
		if (textDeckPanel.getWidgetCount() > 0)
		{
			TextPage textPage = (TextPage)textDeckPanel.getWidget(currentWidget);
			if (textPage.isDataSet())
				textPage.onViewportSizeChange(newWidth);
		}
		
		// Footer
		footerPanel.setStyleName("footerReader");
		if (newWidth >= Global.LARGE_VIEWPORT_MIN_WIDTH)
			footerPanel.addStyleName("footerLarge");
		else if (newWidth >= Global.MEDIUM_VIEWPORT_MIN_WIDTH)
			footerPanel.addStyleName("footerMedium");
		else // newWith < med min
			footerPanel.addStyleName("footerSmall");
	}

	/*
	public void onValueChange(ValueChangeEvent<String> event)
	{
		String token = event.getValue();
		Window.alert("TEXT DECK: New value: " + token);
	}
	*/
	
	//
	// Utility functions
	//
	private int getPageIndex(String pageName)
	{
		int index = 0;
		for (int i = 0; i < textDeckPanel.getWidgetCount(); i++)
		{
			TextPage page = (TextPage)textDeckPanel.getWidget(i);
			if (pageName.equalsIgnoreCase(page.getTitle()))
			{
				index = i;
				break;
			}
		}
		
		return index;
	}
	
	private void checkAndLoadTextParts(int pageIndex)
	{
		if (pageIndex >= Global.TRANS_START_PAGE)
		{
			for (int i = pageIndex-2; i < pageIndex + 2; i++)
			{
				if (i < Global.TRANS_START_PAGE)
					continue;
				
				if (i >= textDeckPanel.getWidgetCount())
					break;
				
				TextPage page = (TextPage)textDeckPanel.getWidget(i);
				if (!page.isDataSet())
					Readingroom.rpcService.getTranslationPartByName(textItem.getId(), page.getTitle(), new TransPartCallBack());
			}
		}
	}
	
	private void initializeNavPopup()
	{
		// Initialize popup navigation panel
		navPanel.initialize(this, Global.TRANS_START_PAGE, textData.appendixStart);
	}
	
	/*
	private void refreshNavPopup()
	{
		navPanel.refresh(this);
	}
	*/
	
	public ArrayList<String> getPartNames()
	{
		ArrayList<String> names = new ArrayList<String>();
		
		for (int i = 0; i < textDeckPanel.getWidgetCount(); i++)
		{
			TextPage page = (TextPage)textDeckPanel.getWidget(i);
			String name = page.getTitle();
			if (page.getSubtitle().length() > 0)
				name += " - " + page.getSubtitle();

			names.add(name);
		}
		
		return names;
	}
	
	public void showPage(int pageIndex)
	{
		currentWidget = pageIndex;

		checkAndLoadTextParts(currentWidget);
		textDeckPanel.showWidget(currentWidget);
		TextPage textPage = (TextPage)textDeckPanel.getWidget(currentWidget);
		History.newItem(textItem.getId() + "/" + textPage.getTitle().toLowerCase());
		Window.scrollTo (0, 0);	// Force page to scroll to top
		
		//navPanel.setVisible(false);
		textPage.accommodateNavPanel(navPanel.isVisible());
	}
	
	public void showNotePage(int noteId)
	{
		int noteIndex = noteId - 1;
		if (noteIndex < 0)
			noteIndex = 0;

		int pageNum = textData.noteItems.get(noteIndex).textPartNum;
		showPage(pageNum);
		
		TextPage textPage = (TextPage)textDeckPanel.getWidget(pageNum);
		textPage.scrollToNoteInBody(noteId);
	}

	//
	// Call-back functions from server queries
	//
	/*
	private class ClearTokenCallBack implements AsyncCallback<String>
	{
		public void onFailure(Throwable caught) { Window.alert("'clear-token' RPC failure"); }

		public void onSuccess(String result)
		{
			Readingroom.rpcService.getToken(new TokenCallBack());
		}
	}
	
	private class TokenCallBack implements AsyncCallback<String>
	{
		public void onFailure(Throwable caught) { Window.alert("'token' RPC failure"); }

		public void onSuccess(String result)
		{
			if (result.length() > 0)
			{
				myToken = result;
				Readingroom.rpcService.processText(myToken, Global.dataFolder, textItem.getId(), new TextCallBack());
			}
			else
				Window.alert("RPC Error: Returned bad token");
		}
	}
	*/

	
	private class TextCallBack implements AsyncCallback<String>
	{
		public void onFailure(Throwable caught) { Window.alert("'text' RPC failure"); }

		public void onSuccess(String result)
		{
			// The initial query returns the title info (TEI header), so parse it and get summary
			if (Parser.parseTextQueryHeader(result, textItem))
			{
				header.addBreadCrumb(textItem.mainTitleEn, textItem.getId());
				header.titleEn.setText(textItem.mainTitleEn);
				
				TextPage titlePage = (TextPage)textDeckPanel.getWidget(0);
				titlePage.setDisplayTitles(textItem.mainTitleEn, "");
				titlePage.setTitle("Title");
				titlePage.setReader(me);
				titlePage.setTitleData(textItem);
				
				Readingroom.rpcService.getTranslationPartByName(textItem.getId(), "summary", new SummaryCallBack());
			}
		}
	}

	private class SummaryCallBack implements AsyncCallback<TextPartData>
	{
		public void onFailure(Throwable caught) { Window.alert("'text summary' RPC failure"); }

		public void onSuccess(TextPartData summaryData)
		{
			if (summaryData != null)
			{
				TextPage summaryPage = new TextPage("Summary", "", "Title", "Acknowledgments");
				summaryPage.setReader(me);
				summaryPage.setData(summaryData);
				textDeckPanel.add(summaryPage);
				
				Readingroom.rpcService.getTranslationPartByName(textItem.getId(), "ack", new AckCallBack());
			}
		}
	}

	private class AckCallBack implements AsyncCallback<TextPartData>
	{
		public void onFailure(Throwable caught) { Window.alert("'text acknowledgment' RPC failure"); }

		public void onSuccess(TextPartData ackData)
		{
			if (ackData != null)
			{
				TextPage ackPage = new TextPage("Acknowledgments", "", "Summary", "Introduction");
				ackPage.setReader(me);
				ackPage.setData(ackData);
				textDeckPanel.add(ackPage);
				
				Readingroom.rpcService.getTranslationPartByName(textItem.getId(), "intro", new IntroCallBack());
			}
		}
	}

	private class IntroCallBack implements AsyncCallback<TextPartData>
	{
		public void onFailure(Throwable caught) { Window.alert("'text introduction' RPC failure"); }

		public void onSuccess(TextPartData introData)
		{
			if (introData != null)
			{
				//Window.alert("INTRO CONTENT: " + introData.getContent().substring(0, 500));
				TextPage introPage = new TextPage("Introduction", "", "Acknowledgments", "Chapter Z");
				introPage.setReader(me);
				introPage.setData(introData);
				textDeckPanel.add(introPage);

				Readingroom.rpcService.getTextData(textItem.getId(), new ListsCallBack());
			}
		}
	}
	
	private class ListsCallBack implements AsyncCallback<TextData>
	{
		public void onFailure(Throwable caught) { Window.alert("'Master item lists' RPC failure"); }

		public void onSuccess(TextData masterData)
		{
			if (masterData != null)
			{
				// Master lists are required for the navigation pop-up
				textData = masterData;

				Readingroom.rpcService.getTranslationPartNames(textItem.getId(), new TransPartNamesCallBack());
			}
		}
	}
	
	private class TransPartNamesCallBack implements AsyncCallback<ArrayList<PartName>>
	{
		public void onFailure(Throwable caught) { Window.alert("'text part names' RPC failure"); }

		public void onSuccess(ArrayList<PartName> partNames)
		{
			String prevPageName, nextPageName;
			
			if (partNames != null)
			{
				// FIRST, CLEAR OUT EXISTING CHAPTERS!!
				if (textDeckPanel.getWidgetCount() > Global.TRANS_START_PAGE)
				{
					int widgetCount = textDeckPanel.getWidgetCount();
					for (int i = Global.TRANS_START_PAGE; i < widgetCount; i++)
						textDeckPanel.remove(textDeckPanel.getWidgetCount() - 1);
				}
				
				for (int i = 0; i < partNames.size(); i++)
				{
					TextPage prevPage = (TextPage)textDeckPanel.getWidget(textDeckPanel.getWidgetCount() - 1);
					prevPage.setNextText(partNames.get(i).title);
					prevPageName = prevPage.getTitle();
					nextPageName = "";
						
					TextPage partPage = new TextPage(partNames.get(i).title, partNames.get(i).subtitle, prevPageName, nextPageName);
					partPage.setReader(me);
					textDeckPanel.add(partPage);
					
					// Set master translation title for first "part"
					if (i == 0)
						partPage.setMasterTransTitles(textData.transTitle, textData.honTitle, textData.mainTitle);
					
					// Pre-load first 3 parts content
					if (i <= 1)
						Readingroom.rpcService.getTranslationPartByName(textItem.getId(), partNames.get(i).title, new TransPartCallBack());
				}

				// Now that we've created all the chapter pages, get the bibliography
				Readingroom.rpcService.getTranslationPartByName(textItem.getId(), "bibl", new BiblCallBack());
			}
			else
				Window.alert("Warning: No translation parts found");
		}
	}

	private class BiblCallBack implements AsyncCallback<TextPartData>
	{
		public void onFailure(Throwable caught) { Window.alert("'bibliography' RPC failure"); }

		public void onSuccess(TextPartData biblData)
		{
			if (biblData != null)
			{
				String prevPageName, nextPageName;
				TextPage prevPage = (TextPage)textDeckPanel.getWidget(textDeckPanel.getWidgetCount() - 1);
				prevPage.setNextText(biblData.getTitle());
				prevPageName = prevPage.getTitle();
				nextPageName = "";

				TextPage biblPage = new TextPage(biblData.getTitle(), "", prevPageName, nextPageName);
				biblPage.setReader(me);
				biblPage.setData(biblData);
				textDeckPanel.add(biblPage);
			}

			// Now that we have all the required data, initialize our navigation pop-up
			initializeNavPopup();
		}
	}
	
	
	private class TransPartCallBack implements AsyncCallback<TextPartData>
	{
		public void onFailure(Throwable caught) { Window.alert("'translation part' RPC failure"); }

		public void onSuccess(TextPartData part)
		{
			if (part != null)
			{
				for (int i = 0; i < textDeckPanel.getWidgetCount(); i++)
				{
					TextPage partPage = (TextPage)textDeckPanel.getWidget(i);
					if (partPage.getTitle().equalsIgnoreCase(part.getTitle()))
					{
						//Window.alert("Setting data for part #" + i + ", Title: " + part.getTitle() + ", Subtitle: " + part.getSubtitle());
						partPage.setReader(me);
						partPage.setDisplayTitles(part.getTitle(), part.getSubtitle());
						partPage.setData(part);
						break;
					}
				}
			}
		}
	}
}
