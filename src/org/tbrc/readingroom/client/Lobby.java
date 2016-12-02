package org.tbrc.readingroom.client;

import java.util.ArrayList;

import org.tbrc.readingroom.shared.Global;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class Lobby extends Composite {

	private static LobbyUiBinder uiBinder = GWT.create(LobbyUiBinder.class);
	
	interface LobbyUiBinder extends UiBinder<Widget, Lobby> {}

	@UiField HTMLPanel lobbyPage;

	@UiField VerticalPanel titleBlockPanel;
	@UiField Label titleEnLabel;
	@UiField Label titleTiLabel;
	@UiField Label titleWyLabel;
	@UiField Label titleSaLabel;
	@UiField HTML desc;
	@UiField Grid titleTiWyGrid;

	@UiField Grid kangyurGrid;
	@UiField Grid kangyurCatGrid;
	@UiField VerticalPanel kangyurTitleBar;
	@UiField HorizontalPanel kangyurTitleInner;
	@UiField Button kLearnMoreButton;
	@UiField HTML kLearnMoreDisclosure;
	@UiField HTMLPanel kangyurTitlePanel;
	@UiField VerticalPanel kangyurContentPanel;
	@UiField VerticalPanel kangyurLearnMorePanel;
	@UiField Label kangyurTitleEn;
	@UiField Label kangyurTitleTi;
	@UiField HTML kangyurDesc;
	
	@UiField Grid tengyurGrid;
	@UiField Grid tengyurCatGrid;
	@UiField VerticalPanel tengyurTitleBar;
	@UiField HorizontalPanel tengyurTitleInner;
	@UiField Button tLearnMoreButton;
	@UiField HTML tLearnMoreDisclosure;
	@UiField HTMLPanel tengyurTitlePanel;
	@UiField VerticalPanel tengyurContentPanel;
	@UiField VerticalPanel tengyurLearnMorePanel;
	@UiField Label tengyurTitleEn;
	@UiField Label tengyurTitleTi;
	@UiField HTML tengyurDesc;
	
	@UiField FocusPanel kangyurCatPanel;
	@UiField FocusPanel tengyurCatPanel;
	@UiField Label kCatTitleEnLabel;
	@UiField Label kCatTitleWyLabel;
	@UiField Label kCatDescLabel;
	@UiField VerticalPanel kCatBottomPanel;
	@UiField VerticalPanel kCatSidePanel;
	@UiField Label tCatTitleEnLabel;
	@UiField Label tCatTitleWyLabel;
	@UiField Label tCatDescLabel;
	@UiField VerticalPanel tCatBottomPanel;
	@UiField VerticalPanel tCatSidePanel;
	
	private int kGridRows = 0, tGridRows = 0;
	private int gridCols = 0;
	private String colWidth = "25%";
	private int lastPortWidth = 0;
	private boolean isLobbyLevel = true;
	private boolean isKLearnMoreDiscVisible = false;
	private boolean isTLearnMoreDiscVisible = false;

	private ArrayList<DataItem> kangyurItems = new ArrayList<DataItem>();
	private ArrayList<DataItem> tengyurItems = new ArrayList<DataItem>();
	
	public Lobby(boolean isLobbyLevel)
	{
		initWidget(uiBinder.createAndBindUi(this));

		// Establish whether this is our top-level lobby view or not
		this.isLobbyLevel = isLobbyLevel;
		if (isLobbyLevel)
			titleBlockPanel.setVisible(false);
		else
		{
			// Move Tengyur title bar so it is above title block
			tengyurTitlePanel.remove(tengyurTitleBar);
			kangyurTitlePanel.add(tengyurTitleBar);
			
			// Catalogues are not used except for the main lobby view
			kangyurCatGrid.setVisible(false);
			tengyurCatGrid.setVisible(false);
		}
		
		// Establish initial rows & cols based on viewport size
		int width = Window.getClientWidth();
		if (width >= Global.LARGE_VIEWPORT_MIN_WIDTH)
		{
			gridCols = 4;
			colWidth = "25%";
		}
		else if (width >= Global.MEDIUM_VIEWPORT_MIN_WIDTH)
		{
			gridCols = 2;
			colWidth = "50%";
		}
		else // width < med min
		{
			gridCols = 1;
			colWidth = "100%";
		}
	}
	
	@UiHandler("kLearnMoreButton")
	void onMoreKClick(ClickEvent e) {
		//Window.alert("child count: " + kLearnMoreDisclosure.getElement().getChildCount());
		isKLearnMoreDiscVisible = !isKLearnMoreDiscVisible;
		kLearnMoreDisclosure.setVisible(isKLearnMoreDiscVisible);
		if (isKLearnMoreDiscVisible)
			kLearnMoreButton.addStyleName("infoButtonActivated");
		else
		{
			kLearnMoreButton.setStyleName("infoButton");
			kLearnMoreButton.addStyleName("ss-info");
		}
	}
	
//	@UiHandler("kCatTitleEnLabel")
//	void onkCatTitleEnClick(ClickEvent e) {
//		Readingroom.main.newPage(kangyurItems.get(kangyurItems.size()-1), kCatTitleEnLabel);
//	}
	@UiHandler("kangyurCatPanel")
	void onKangyurCatPanelClick(ClickEvent e) {
		Readingroom.main.newPage(kangyurItems.get(kangyurItems.size()-1), kangyurCatPanel);
	}

	@UiHandler("tLearnMoreButton")
	void onMoreTClick(ClickEvent e) {
		isTLearnMoreDiscVisible = !isTLearnMoreDiscVisible;
		tLearnMoreDisclosure.setVisible(isTLearnMoreDiscVisible);
		if (isTLearnMoreDiscVisible)
			tLearnMoreButton.addStyleName("infoButtonActivated");
		else
		{
			tLearnMoreButton.setStyleName("infoButton");
			tLearnMoreButton.addStyleName("ss-info");
		}
	}

//	@UiHandler("tCatTitleEnLabel")
//	void ontCatTitleEnClick(ClickEvent e) {
//		Readingroom.main.newPage(tengyurItems.get(tengyurItems.size()-1), tCatTitleEnLabel);
//	}
	@UiHandler("tengyurCatPanel")
	void onTengyurCatPanelClick(ClickEvent e) {
		Readingroom.main.newPage(tengyurItems.get(tengyurItems.size()-1), tengyurCatPanel);
	}

	public void initialize(DataItem data)
	{
		// NOTE: This is only called for non-top-level instances
		titleEnLabel.setText(data.getNameEn());
		titleTiLabel.setText(data.getNameTi());
		titleWyLabel.setText(Utils.removeTrailingChar(data.getNameWy(), "/"));
		titleSaLabel.setText(data.getNameSa());
		//desc.setText(data.getDesc());
		desc.setHTML(data.getDesc());
		
		//kLearnMoreDisclosure.getElement().setPropertyBoolean("normalize", true);
		kLearnMoreDisclosure.setHTML(data.getLearnMore() + data.getNote());
		if (kLearnMoreDisclosure.getHTML() == "")
			kLearnMoreDisclosure.setHTML("[More information will be made available soon]");
		tLearnMoreDisclosure.setHTML(kLearnMoreDisclosure.getHTML());
		
		// Crazy required chapuza!
		titleTiWyGrid.getColumnFormatter().setWidth(0, "50%");
		titleTiWyGrid.getColumnFormatter().setWidth(1, "50%");
	}

	public void onViewportSizeChange(int newWidth)
	{
		// Only invoke changes if we cross a viewport-change width
		if (Utils.viewPortChanged(newWidth, lastPortWidth))
		{
			if (newWidth >= Global.LARGE_VIEWPORT_MIN_WIDTH)
			{
				gridCols = 4;
				colWidth = "25%";

				titleEnLabel.setStyleName("sectionTitleEn"); titleEnLabel.addStyleName("sectionTitleEnLarge");
				titleTiLabel.setStyleName("sectionTitleTi"); titleTiLabel.addStyleName("sectionTitleLarge");
				titleWyLabel.setStyleName("sectionTitleWy"); titleWyLabel.addStyleName("sectionTitleLarge");
				titleSaLabel.setStyleName("sectionTitleSa"); titleSaLabel.addStyleName("sectionTitleLarge");
				desc.setStyleName("sectionTitleDesc"); desc.addStyleName("sectionTitleDescLarge");
				titleBlockPanel.setWidth("60%");
				kangyurLearnMorePanel.setWidth("60%");
				tengyurLearnMorePanel.setWidth("60%");

				if (isLobbyLevel)
				{
					kangyurTitlePanel.setStyleName("titleLarge");
					kangyurTitleEn.setStyleName("bigTitleFont"); kangyurTitleEn.addStyleName("titleFontLarge");
					kangyurTitleTi.setStyleName("bigTitleTFont"); kangyurTitleTi.addStyleName("titleFontLarge");
					tengyurTitlePanel.setStyleName("titleLarge");
					tengyurTitleEn.setStyleName("bigTitleFont"); tengyurTitleEn.addStyleName("titleFontLarge");
					tengyurTitleTi.setStyleName("bigTitleTFont"); tengyurTitleTi.addStyleName("titleFontLarge");
					kLearnMoreButton.setText("   LEARN MORE ABOUT THE KANGYUR");
					tLearnMoreButton.setText("   LEARN MORE ABOUT THE TENGYUR");
				}
				else
				{
					kangyurTitlePanel.setStyleName("titleSmall");
					kangyurTitleEn.setStyleName("bigTitleFont"); kangyurTitleEn.addStyleName("titleFontSmall");
					kangyurTitleTi.setStyleName("bigTitleTFont"); kangyurTitleTi.addStyleName("titleFontSmall");
					tengyurTitlePanel.setStyleName("titleSmall");
					tengyurTitleEn.setStyleName("bigTitleFont"); tengyurTitleEn.addStyleName("titleFontSmall");
					tengyurTitleTi.setStyleName("bigTitleTFont"); tengyurTitleTi.addStyleName("titleFontSmall");
					kLearnMoreButton.setText("   LEARN MORE ABOUT " + titleEnLabel.getText().toUpperCase());
					tLearnMoreButton.setText("   LEARN MORE ABOUT " + titleEnLabel.getText().toUpperCase());
				}

				kangyurTitleBar.setCellHorizontalAlignment(kangyurTitleInner, HasHorizontalAlignment.ALIGN_LEFT);
				tengyurTitleBar.setCellHorizontalAlignment(tengyurTitleInner, HasHorizontalAlignment.ALIGN_LEFT);
				//tengyurTitleEn.setStyleName("bigTitleFont"); tengyurTitleEn.addStyleName("titleFontLarge");
				//tengyurTitleTi.setStyleName("bigTitleTFont"); tengyurTitleTi.addStyleName("titleFontLarge");

				lobbyPage.setWidth("94%");
				kangyurCatGrid.setWidth("48%");
				tengyurCatGrid.setWidth("48%");
			}
			else if (newWidth >= Global.MEDIUM_VIEWPORT_MIN_WIDTH)
			{
				gridCols = 2;
				colWidth = "50%";

				titleEnLabel.setStyleName("sectionTitleEn"); titleEnLabel.addStyleName("sectionTitleEnMedium");
				titleTiLabel.setStyleName("sectionTitleTi"); titleTiLabel.addStyleName("sectionTitleMedium");
				titleWyLabel.setStyleName("sectionTitleWy"); titleWyLabel.addStyleName("sectionTitleMedium");
				titleSaLabel.setStyleName("sectionTitleSa"); titleSaLabel.addStyleName("sectionTitleMedium");
				desc.setStyleName("sectionTitleDesc"); desc.addStyleName("sectionTitleDescMedium");
				titleBlockPanel.setWidth("60%");
				kangyurLearnMorePanel.setWidth("60%");
				tengyurLearnMorePanel.setWidth("60%");

				kLearnMoreButton.setText("   LEARN MORE");
				tLearnMoreButton.setText("   LEARN MORE");

				if (isLobbyLevel)
				{
					kangyurTitlePanel.setStyleName("titleMedium");
					kangyurTitleEn.setStyleName("bigTitleFont"); kangyurTitleEn.addStyleName("titleFontMedium");
					kangyurTitleTi.setStyleName("bigTitleTFont"); kangyurTitleTi.addStyleName("titleFontMedium");
					tengyurTitlePanel.setStyleName("titleMedium");
					tengyurTitleEn.setStyleName("bigTitleFont"); tengyurTitleEn.addStyleName("titleFontMedium");
					tengyurTitleTi.setStyleName("bigTitleTFont"); tengyurTitleTi.addStyleName("titleFontMedium");
				}
				else
				{
					kangyurTitlePanel.setStyleName("titleSmall");
					kangyurTitleEn.setStyleName("bigTitleFont"); kangyurTitleEn.addStyleName("titleFontSmall");
					kangyurTitleTi.setStyleName("bigTitleTFont"); kangyurTitleTi.addStyleName("titleFontSmall");
					tengyurTitlePanel.setStyleName("titleSmall");
					tengyurTitleEn.setStyleName("bigTitleFont"); tengyurTitleEn.addStyleName("titleFontSmall");
					tengyurTitleTi.setStyleName("bigTitleTFont"); tengyurTitleTi.addStyleName("titleFontSmall");
				}
				kangyurTitleBar.setCellHorizontalAlignment(kangyurTitleInner, HasHorizontalAlignment.ALIGN_CENTER);
				tengyurTitleBar.setCellHorizontalAlignment(tengyurTitleInner, HasHorizontalAlignment.ALIGN_CENTER);
				
				kCatBottomPanel.remove(kCatDescLabel);
				kCatSidePanel.add(kCatDescLabel);
				//kCatBottomPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
			    kangyurCatGrid.getColumnFormatter().setWidth(0, "50%");
			    kangyurCatGrid.getColumnFormatter().setWidth(1, "50%");
				tCatBottomPanel.remove(tCatDescLabel);
				tCatSidePanel.add(tCatDescLabel);
				//tCatBottomPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
			    tengyurCatGrid.getColumnFormatter().setWidth(0, "50%");
			    tengyurCatGrid.getColumnFormatter().setWidth(1, "50%");

				lobbyPage.setWidth("94%");
				kangyurCatGrid.setWidth("94%");
				tengyurCatGrid.setWidth("94%");
			}
			else // newWith < med min
			{
				gridCols = 1;
				colWidth = "100%";

				titleEnLabel.setStyleName("sectionTitleEn"); titleEnLabel.addStyleName("sectionTitleEnSmall");
				titleTiLabel.setStyleName("sectionTitleTi"); titleTiLabel.addStyleName("sectionTitleSmall");
				titleWyLabel.setStyleName("sectionTitleWy"); titleWyLabel.addStyleName("sectionTitleSmall");
				titleSaLabel.setStyleName("sectionTitleSa"); titleSaLabel.addStyleName("sectionTitleSmall");
				desc.setStyleName("sectionTitleDesc"); desc.addStyleName("sectionTitleDescSmall");
				titleBlockPanel.setWidth("90%");
				kangyurLearnMorePanel.setWidth("90%");
				tengyurLearnMorePanel.setWidth("90%");
				
				kLearnMoreButton.setText("   LEARN MORE");
				tLearnMoreButton.setText("   LEARN MORE");

				kangyurTitlePanel.setStyleName("titleSmall");
				kangyurTitleBar.setCellHorizontalAlignment(kangyurTitleInner, HasHorizontalAlignment.ALIGN_CENTER);
				kangyurTitleEn.setStyleName("bigTitleFont"); kangyurTitleEn.addStyleName("titleFontSmall");
				kangyurTitleTi.setStyleName("bigTitleTFont"); kangyurTitleTi.addStyleName("titleFontSmall");

				tengyurTitlePanel.setStyleName("titleSmall");
				tengyurTitleBar.setCellHorizontalAlignment(tengyurTitleInner, HasHorizontalAlignment.ALIGN_CENTER);
				tengyurTitleEn.setStyleName("bigTitleFont"); tengyurTitleEn.addStyleName("titleFontSmall");
				tengyurTitleTi.setStyleName("bigTitleTFont"); tengyurTitleTi.addStyleName("titleFontSmall");

				kCatSidePanel.remove(kCatDescLabel);
				kCatBottomPanel.add(kCatDescLabel);
				//kCatBottomPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
			    kangyurCatGrid.getColumnFormatter().setWidth(0, "100%");
			    kangyurCatGrid.getColumnFormatter().setWidth(1, "0%");
				tCatSidePanel.remove(tCatDescLabel);
				tCatBottomPanel.add(tCatDescLabel);
				//tCatBottomPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
			    tengyurCatGrid.getColumnFormatter().setWidth(0, "100%");
			    tengyurCatGrid.getColumnFormatter().setWidth(1, "0%");
				
				lobbyPage.setWidth("100%");
				kangyurCatGrid.setWidth("92%");
				tengyurCatGrid.setWidth("92%");
			}

			resetGrids();
		}

		lastPortWidth = newWidth;
	}

	public void populatePage(String kDesc, String kLearnMore, ArrayList<DataItem> kItems, 
							 String tDesc, String tLearnMore, ArrayList<DataItem> tItems)
	{
		kangyurItems = kItems;
		tengyurItems = tItems;
		
		if (kangyurItems.size() == 0)
		{
			//kangyurTitleBar.setVisible(false);
			//kLearnMoreButton.setVisible(false);
//			kangyurTitlePanel.removeFromParent();
			kangyurTitleBar.removeFromParent();
			kangyurContentPanel.removeFromParent();
		}

		if (tengyurItems.size() == 0)
		{
			//tengyurTitleBar.setVisible(false);
			//tLearnMoreButton.setVisible(false);
//			tengyurTitlePanel.removeFromParent();
			tengyurTitleBar.removeFromParent();
			tengyurContentPanel.removeFromParent();
		}

		resetGrids();

		// Place top-level description
		//kangyurDesc.setText(kDesc);
		kangyurDesc.setHTML(kDesc);
		if (kLearnMore.length() > 0)
			kLearnMoreDisclosure.setHTML(kLearnMore);

		// Handle last kanjur item ("Kangyur Catalogue")
		if (isLobbyLevel)
		{
			int lastIndex = kangyurItems.size() - 1;
			kCatTitleEnLabel.setText(kangyurItems.get(lastIndex).getNameEn());
			kCatTitleWyLabel.setText(kangyurItems.get(lastIndex).getNameWy());
			kCatDescLabel.setText(kangyurItems.get(lastIndex).getDesc());
		}

		// Place top-level description
		//tengyurDesc.setText(tDesc);
		tengyurDesc.setHTML(tDesc);
		if (tLearnMore.length() > 0)
			tLearnMoreDisclosure.setHTML(tLearnMore);
		
		// Handle last tenjur item ("Tengyur Catalogue")
		if (isLobbyLevel)
		{
			int lastIndex = tengyurItems.size() - 1;
			tCatTitleEnLabel.setText(tengyurItems.get(lastIndex).getNameEn());
			tCatTitleWyLabel.setText(tengyurItems.get(lastIndex).getNameWy());
			tCatDescLabel.setText(tengyurItems.get(lastIndex).getDesc());
		}
	}
	
	private void resetGrids()
	{
		kGridRows = kangyurItems.size() / gridCols;
		int count = kGridRows * gridCols;
		if (isLobbyLevel)
			count++;
		if (count < kangyurItems.size())
			kGridRows++;
		
		count = tGridRows * gridCols;
		if (isLobbyLevel)
			count++;
		tGridRows = tengyurItems.size() / gridCols;
		if (count < tengyurItems.size())
			tGridRows++;

		if (kGridRows > 0)
		{
			kangyurGrid.clear();
			kangyurGrid.resize(kGridRows, gridCols);
			placeKangyurItems();
		}
		
		if (tGridRows > 0)
		{
			tengyurGrid.clear();
			tengyurGrid.resize(tGridRows, gridCols);
			placeTengyurItems();
		}
	}

	private void placeKangyurItems()
	{
		// Make sure rows are top-aligned
		for (int i = 0; i < kGridRows; i++)
			kangyurGrid.getRowFormatter().setVerticalAlign(i, HasVerticalAlignment.ALIGN_TOP);

		// Make sure we have even-width columns
		for (int i = 0; i < gridCols; i++)
		    kangyurGrid.getColumnFormatter().setWidth(i, colWidth);

		// Put lobby sections in appropriate grid cells
		int count = kangyurItems.size();
		if (isLobbyLevel)
			count -= 1;
		for (int i = 0; i < count; i++)
		{
			final LobbySection lobbySection = new LobbySection();
			kangyurGrid.setWidget((i/gridCols), (i%gridCols), lobbySection);
			DataItem item = kangyurItems.get(i);
			item.setCategory("kangyur");
			lobbySection.setData(item);
		}
		// Make sure catalogue has correct category
		kangyurItems.get(kangyurItems.size()-1).setCategory("kangyur");

		// If 2 remain in last row, center them (only for wide viewport)
		if (gridCols == 4)
		{
			if (((kGridRows * gridCols) - count) == 2)
			{
				int row = kGridRows-1;
				LobbySection ls;
				
				kangyurGrid.clearCell(row, 3);
				ls = (LobbySection)kangyurGrid.getWidget(row, 1);
				kangyurGrid.setWidget(row, 2, ls);
				ls = (LobbySection)kangyurGrid.getWidget(row, 0);
				kangyurGrid.setWidget(row, 1, ls);
				kangyurGrid.clearCell(row, 0);
			}
		}
	}
	
	private void placeTengyurItems()
	{
		// Make sure rows are top-aligned
		for (int i = 0; i < tGridRows; i++)
			tengyurGrid.getRowFormatter().setVerticalAlign(i, HasVerticalAlignment.ALIGN_TOP);

		// Make sure we have even-width columns
		for (int i = 0; i < gridCols; i++)
		    tengyurGrid.getColumnFormatter().setWidth(i, colWidth);

		// Put lobby sections in appropriate grid cells
		int count = tengyurItems.size();
		if (isLobbyLevel)
			count -= 1;
		for (int i = 0; i < count; i++)
		{
			LobbySection lobbySection = new LobbySection();
			tengyurGrid.setWidget((i/gridCols), (i%gridCols), lobbySection);
			DataItem item = tengyurItems.get(i);
			item.setCategory("tengyur");
			lobbySection.setData(item);
		}
		// Make sure catalogue has correct category
		tengyurItems.get(tengyurItems.size()-1).setCategory("tengyur");

		// If 2 remain in last row, center them (only for wide viewport)
		if (gridCols == 4)
		{
			if (((tGridRows * gridCols) - count) == 2)
			{
				int row = tGridRows-1;
				LobbySection ls;
				
				tengyurGrid.clearCell(row, 3);
				ls = (LobbySection)tengyurGrid.getWidget(row, 1);
				tengyurGrid.setWidget(row, 2, ls);
				ls = (LobbySection)tengyurGrid.getWidget(row, 0);
				tengyurGrid.setWidget(row, 1, ls);
				tengyurGrid.clearCell(row, 0);
			}
		}
	}
	
	public ArrayList<DataItem> getKangyurItems() { return kangyurItems; }
	public ArrayList<DataItem> getTengyurItems() { return tengyurItems; }
}
