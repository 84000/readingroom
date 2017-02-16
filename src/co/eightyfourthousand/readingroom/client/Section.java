package co.eightyfourthousand.readingroom.client;

import java.util.ArrayList;

import co.eightyfourthousand.readingroom.shared.Global;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * 
 * Top level "section" view - showing a list of texts within a given section
 * 
 * @author curtis
 *
 */
public class Section extends Composite {

	private static SectionUiBinder uiBinder = GWT.create(SectionUiBinder.class);

	interface SectionUiBinder extends UiBinder<Widget, Section> {
	}

	@UiField VerticalPanel titleBlockPanel;
	
	@UiField HTMLPanel sectionPage;
	@UiField HTMLPanel titlePanel;
	@UiField Label titleEnLabel;
	@UiField Label titleTiLabel;
	@UiField Label titleWyLabel;
	@UiField Label titleSaLabel;
	@UiField HTML desc;
	@UiField Grid titleTiWyGrid;
	
	@UiField VerticalPanel titleBar;
	@UiField HorizontalPanel titleInner;
	@UiField Label titleEn;
	@UiField Label titleTi;
	@UiField VerticalPanel sectionListPanel;
	@UiField Label transTitleLabel;
	@UiField VerticalPanel listingPanel;
	
	@UiField Button learnMoreButton;
	@UiField HTML learnMoreDisclosure;
	
	@UiField Label expandAllLabel;
	@UiField HorizontalPanel filterPanel;
	@UiField HorizontalPanel filterButtonPanel;
	@UiField Label filterTextLabel;
	@UiField Button filterAllButton;
	@UiField Button filterTransButton;
	
	@UiField VerticalPanel expandPanel;
	@UiField VerticalPanel widePanel;
	@UiField VerticalPanel narrowPanel;

	private ArrayList<DataItem> sectionItems = new ArrayList<DataItem>();
	DataItem itemData = null;
	private int lastPortWidth = 0;
	private Boolean allExpanded = false;
	private Boolean filterAll = true;
	private boolean isLearnMoreDiscVisible = false;

	public Section()
	{
		initWidget(uiBinder.createAndBindUi(this));
		
//		SectionListing listing = new SectionListing(3);
//		sectionListPanel.add(listing);

		onViewportSizeChange(Window.getClientWidth());
	}

	@UiHandler("learnMoreButton")
	void onMoreKClick(ClickEvent e)
	{
		isLearnMoreDiscVisible = !isLearnMoreDiscVisible;
		learnMoreDisclosure.setVisible(isLearnMoreDiscVisible);
		if (isLearnMoreDiscVisible)
			learnMoreButton.addStyleName("infoButtonActivated");
		else
		{
			learnMoreButton.setStyleName("infoButton");
			learnMoreButton.addStyleName("ss-info");
		}
	}
	
	@UiHandler("expandAllLabel")
	void onExpandAllClick(ClickEvent e)
	{
		int sectionCount = sectionListPanel.getWidgetCount();

		if (allExpanded)
		{
			expandAllLabel.removeStyleName("ss-navigatedown");
			expandAllLabel.addStyleName("ss-navigateright");
			for (int i = 0; i < sectionCount; i++)
			{
				SectionListing listing = (SectionListing)sectionListPanel.getWidget(i);
				listing.hideSummaryText();
			}
		}
		else
		{
			expandAllLabel.removeStyleName("ss-navigateright");
			expandAllLabel.addStyleName("ss-navigatedown");
			for (int i = 0; i < sectionCount; i++)
			{
				SectionListing listing = (SectionListing)sectionListPanel.getWidget(i);
				listing.showSummaryText();
			}
		}
		allExpanded = !allExpanded;
	}

	@UiHandler("filterAllButton")
	void onFilterAllClick(ClickEvent e)
	{
		if (!filterAll)
		{
			filterAllButton.addStyleName("filterButtonSelected");
			filterTransButton.removeStyleName("filterButtonSelected");
			
			sectionListPanel.clear();
			for (int i = 0; i < sectionItems.size(); i++)
			{
				SectionListing listing = new SectionListing(i, sectionItems.get(i));
				sectionListPanel.add(listing);
				listing.onViewportSizeChange(Window.getClientWidth());
			}
			
			filterAll = true;
		}
	}

	@UiHandler("filterTransButton")
	void onFilterTransClick(ClickEvent e)
	{
		if (filterAll)
		{
			filterAllButton.removeStyleName("filterButtonSelected");
			filterTransButton.addStyleName("filterButtonSelected");

			sectionListPanel.clear();
			int count = 0;
			for (int i = 0; i < sectionItems.size(); i++)
			{
				if (Integer.parseInt(sectionItems.get(i).getStatsTrans()) > 0)
				{
					SectionListing listing = new SectionListing(count++, sectionItems.get(i));
					sectionListPanel.add(listing);
					listing.onViewportSizeChange(Window.getClientWidth());
				}
			}
			
			filterAll = false;
		}
	}

	public void initialize(DataItem data)
	{
		itemData = data;
		
		titleEnLabel.setText(data.getNameEn());
		titleTiLabel.setText(data.getNameTi());
		titleWyLabel.setText(Utils.removeTrailingChar(data.getNameWy(), "/"));
		titleSaLabel.setText(data.getNameSa());
		//desc.setText(data.getDesc());
		desc.setHTML(data.getDesc());

		learnMoreDisclosure.setHTML(data.getLearnMore() + data.getNote());
		if (learnMoreDisclosure.getHTML() == "")
			learnMoreDisclosure.setHTML("[More information will be made available soon]");
		
		// Crazy required chapuza!
		titleTiWyGrid.getColumnFormatter().setWidth(0, "50%");
		titleTiWyGrid.getColumnFormatter().setWidth(1, "50%");

		// Clear out previous listings and issue query for data
		sectionListPanel.clear();
		sectionItems.clear();
	}

	public void onViewportSizeChange(int newWidth)
	{
		// Only invoke changes if we cross a viewport-change width
		if (Utils.viewPortChanged(newWidth, lastPortWidth))
		{
			if (newWidth >= Global.LARGE_VIEWPORT_MIN_WIDTH)
			{
				titlePanel.setStyleName("titleSmall");
				titleEn.setStyleName("bigTitleFont"); titleEn.addStyleName("titleFontSmall");
				titleTi.setStyleName("bigTitleTFont"); titleTi.addStyleName("titleFontSmall");
				titleEnLabel.setStyleName("sectionTitleEn"); titleEnLabel.addStyleName("sectionTitleEnLarge");
				titleTiLabel.setStyleName("sectionTitleTi"); titleTiLabel.addStyleName("sectionTitleLarge");
				titleWyLabel.setStyleName("sectionTitleWy"); titleWyLabel.addStyleName("sectionTitleLarge");
				titleSaLabel.setStyleName("sectionTitleSa"); titleSaLabel.addStyleName("sectionTitleLarge");
				desc.setStyleName("sectionTitleDesc"); desc.addStyleName("sectionTitleDescLarge");

				learnMoreButton.setText("   LEARN MORE ABOUT " + titleEnLabel.getText().toUpperCase());
				learnMoreButton.setText("   LEARN MORE ABOUT " + titleEnLabel.getText().toUpperCase());

				sectionPage.setWidth("94%");
				listingPanel.setWidth("94%");
				transTitleLabel.setVisible(true);

				if (narrowPanel.remove(filterTextLabel))
					filterPanel.add(filterTextLabel);
				if (narrowPanel.remove(filterButtonPanel))
					filterPanel.add(filterButtonPanel);
				if (narrowPanel.remove(expandAllLabel))
					expandPanel.add(expandAllLabel);
				
				titleBlockPanel.setWidth("60%");
			}
			else if (newWidth >= Global.MEDIUM_VIEWPORT_MIN_WIDTH)
			{
				titlePanel.setStyleName("titleSmall");
				titleEn.setStyleName("bigTitleFont"); titleEn.addStyleName("titleFontSmall");
				titleTi.setStyleName("bigTitleTFont"); titleTi.addStyleName("titleFontSmall");
				titleEnLabel.setStyleName("sectionTitleEn"); titleEnLabel.addStyleName("sectionTitleEnMedium");
				titleTiLabel.setStyleName("sectionTitleTi"); titleTiLabel.addStyleName("sectionTitleMedium");
				titleWyLabel.setStyleName("sectionTitleWy"); titleWyLabel.addStyleName("sectionTitleMedium");
				titleSaLabel.setStyleName("sectionTitleSa"); titleSaLabel.addStyleName("sectionTitleMedium");
				desc.setStyleName("sectionTitleDesc"); desc.addStyleName("sectionTitleDescMedium");

				learnMoreButton.setText("   LEARN MORE");
				learnMoreButton.setText("   LEARN MORE");

				sectionPage.setWidth("94%");
				listingPanel.setWidth("94%");
				transTitleLabel.setVisible(false);
				
				if (filterPanel.remove(filterTextLabel))
					narrowPanel.add(filterTextLabel);
				if (filterPanel.remove(filterButtonPanel))
					narrowPanel.add(filterButtonPanel);
				if (expandPanel.remove(expandAllLabel))
					narrowPanel.add(expandAllLabel);

				titleBlockPanel.setWidth("60%");
			}
			else // newWith < med min
			{
				titlePanel.setStyleName("titleSmall");
				titleEn.setStyleName("bigTitleFont"); titleEn.addStyleName("titleFontSmall");
				titleTi.setStyleName("bigTitleTFont"); titleTi.addStyleName("titleFontSmall");
				titleEnLabel.setStyleName("sectionTitleEn"); titleEnLabel.addStyleName("sectionTitleEnSmall");
				titleTiLabel.setStyleName("sectionTitleTi"); titleTiLabel.addStyleName("sectionTitleSmall");
				titleWyLabel.setStyleName("sectionTitleWy"); titleWyLabel.addStyleName("sectionTitleSmall");
				titleSaLabel.setStyleName("sectionTitleSa"); titleSaLabel.addStyleName("sectionTitleSmall");
				desc.setStyleName("sectionTitleDesc"); desc.addStyleName("sectionTitleDescSmall");

				learnMoreButton.setText("   LEARN MORE");
				learnMoreButton.setText("   LEARN MORE");

				sectionPage.setWidth("100%");
				listingPanel.setWidth("100%");
				transTitleLabel.setVisible(false);

				if (filterPanel.remove(filterTextLabel))
					narrowPanel.add(filterTextLabel);
				if (filterPanel.remove(filterButtonPanel))
					narrowPanel.add(filterButtonPanel);
				if (expandPanel.remove(expandAllLabel))
					narrowPanel.add(expandAllLabel);

				titleBlockPanel.setWidth("90%");
			}

			//for (int i = 0; i < sectionListPanel.getWidgetCount(); i++)
			//{
			//	SectionListing sl = (SectionListing)sectionListPanel.getWidget(i);
			//	sl.onViewportSizeChange(newWidth);
			//}
		}

		for (int i = 0; i < sectionListPanel.getWidgetCount(); i++)
		{
			SectionListing sl = (SectionListing)sectionListPanel.getWidget(i);
			sl.onViewportSizeChange(newWidth);
		}
		
		lastPortWidth = newWidth;
	}
	
	public void populatePage(ArrayList<DataItem> sItems, String category)
	{
		sectionItems = sItems;
		
		if (category == "kangyur")
		{
			titleEn.setText("The Kangyur");
			titleTi.setText(Global.KANGYUR_TI);
		}
		else // if (category == "tengyur")
		{
			titleEn.setText("The Tengyur");
			titleTi.setText(Global.TENGYUR_TI);
		}
		
		for (int i = 0; i < sectionItems.size(); i++)
		{
			sectionItems.get(i).setCategory(itemData.getCategory());
			SectionListing listing = new SectionListing(i, sectionItems.get(i));
			sectionListPanel.add(listing);
			//listing.onViewportSizeChange(Window.getClientWidth());
		}

		// Force a redraw
		lastPortWidth = 0;
		onViewportSizeChange(Window.getClientWidth());
	}

	/*
	//
	// Call-back function from server query
	//
	protected AsyncCallback<String> lobbyCallback = new AsyncCallback<String>()
	{
		public void onFailure(Throwable caught)
		{
			// Show the RPC error message to the user
			Window.alert("'section' Remote Procedure Call - Failure");
		}

		public void onSuccess(String result)
		{
			// Parse out items from result
			sectionItems = Queries.parseSectionQueryResult(result);
			
			for (int i = 0; i < sectionItems.size(); i++)
			{
				SectionListing listing = new SectionListing(i, sectionItems.get(i));
				sectionListPanel.add(listing);
				listing.onViewportSizeChange(Window.getClientWidth());
			}
		}
	};
	*/
}
