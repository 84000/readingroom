package org.tbrc.readingroom.client;

import org.tbrc.readingroom.shared.Global;

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
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class SectionListing extends Composite {

	private static SectionListingUiBinder uiBinder = GWT
			.create(SectionListingUiBinder.class);

	interface SectionListingUiBinder extends UiBinder<Widget, SectionListing> {
	}

	@UiField Label tohLabel;
	@UiField Label titleEnLabel;
	@UiField Label titleTiLabel;
	@UiField Label titleWyLabel;
	@UiField Label titleSaLabel;
	@UiField Grid listingGrid;
	@UiField Button actionButton;
	@UiField Button origButton;
	@UiField HorizontalPanel mainBottomHorizPanel;
	@UiField VerticalPanel mainBottomVertPanel;
	@UiField VerticalPanel rightSidePanel;
	@UiField Label summaryLabel;
	@UiField HTML summaryText;
	
	private DataItem thisItem = new DataItem();
	private Boolean summaryShowing = false;
	private String actionButtonMessage = "";
	
	// If "subsections" button is pressed, create nested Section
	@UiHandler("actionButton")
	void onClick(ClickEvent e)
	{
		if (Integer.parseInt(thisItem.getNumChapters()) > 0)
		{
			Readingroom.main.newPage(thisItem, actionButton);
		}
		else
		{
			if (Integer.parseInt(thisItem.getStatsTrans()) > 0)
			{
				//Window.open(thisItem.getUrl(), "_self", "");			// Opens in same browser window
				//Window.open(thisItem.getUrl(), "_blank", "");			// Opens in new tab (Chrome, Safari, Firefox)
				//Window.open(thisItem.getUrl(), "_blank", "enabled");	// Opens in new window (Chrome, Safari, Firefox)
				
				// TEMPORARY UNTIL RELEASE!
				if (Readingroom.main.dataLbl.getText() == "DEVELOPMENT")
					Global.dataFolder = "Dev";
				else if (Readingroom.main.dataLbl.getText() == "STAGING")
					Global.dataFolder = "Staging";
				else if (Readingroom.main.dataLbl.getText() == "PRODUCTION")
					Global.dataFolder = "tei";
				// REPLACE THE ABOVE WITH THIS WHEN IN PRODUCTION!
				// Global.dataFolder = "tei";
				Readingroom.main.showReader(thisItem.getRid());
			}
			else if (Integer.parseInt(thisItem.getStatsInProg()) > 0)
			{
				if (User.isAdmin())
				{
					Window.alert("Warning: You are reviewing a text transation that is not finalized");
					Global.dataFolder = "Staging";
					Readingroom.main.showReader(thisItem.getRid());
				}
				else
					Window.alert(actionButtonMessage);
			}
			else
				Window.alert(actionButtonMessage);
		}
	}

	// Original Text viewing not yet available
	@UiHandler("origButton")
	void onOriginalTextClick(ClickEvent e)
	{
		Window.alert("This function is not yet available");
	}	
	
	public SectionListing()
	{
		initWidget(uiBinder.createAndBindUi(this));

		// Make sure row is top-aligned
		listingGrid.getRowFormatter().setVerticalAlign(0, HasVerticalAlignment.ALIGN_TOP);
	}
	
	public SectionListing(int index, DataItem item)
	{
		initWidget(uiBinder.createAndBindUi(this));
		
		// Make sure row is top-aligned and has correct background color
		listingGrid.getRowFormatter().setVerticalAlign(0, HasVerticalAlignment.ALIGN_TOP);
		if ((index % 2) == 1)
			listingGrid.addStyleName("listing_odd");
		
		// Populate listing with item data
		titleEnLabel.setText(item.getNameEn());
		titleTiLabel.setText(item.getNameTi());
		titleWyLabel.setText(Utils.removeTrailingChar(item.getNameWy(), "/"));

		//if (item.getNameSa().length() > Constants.MAX_SANSKRIT_NAME_LEN)
		//	titleSaLabel.setText(item.getNameSa().substring(0, Constants.MAX_SANSKRIT_NAME_LEN) + "...");
		//else
		//	titleSaLabel.setText(item.getNameSa());

		//summaryText.setText(item.getDesc());
		summaryText.setHTML("SUMMARY<br/>" + item.getSummary() + "<br/>TITLE VARIANTS<br/>" + item.getTitles());
		summaryText.setVisible(false);
		tohLabel.setText(item.getToh());
		
		// Capture our item
		thisItem = item;
		
		if (Integer.parseInt(thisItem.getNumChapters()) > 0)
		{
			actionButton.removeStyleName("ss-view");
			actionButton.addStyleName("ss-rows");
			actionButton.setHTML("&nbsp;&nbsp;" + thisItem.getNumChapters() + " Chapters");
			actionButton.addStyleName("alignCenter");
		}
		else if (thisItem.getStatsTrans() == "0" && thisItem.getStatsInProg() == "0")
		{
			actionButton.setStyleName("transNotBegunButton");
			actionButton.addStyleName("ss-caution");
			actionButton.setHTML("&nbsp;&nbsp;" + "Translation Not Begun");
			actionButtonMessage = "Translation of this text has not yet begun.";
		}
		else if (Integer.parseInt(thisItem.getStatsInProg()) > 0)
		{
			actionButton.setStyleName("transNotBegunButton");
			actionButton.addStyleName("ss-clock");
			actionButton.setHTML("&nbsp;&nbsp;" + "Translation In Progress");
			actionButtonMessage = "Translation of this text is in progress.";
		}

	}
	
	public void onViewportSizeChange(int newWidth)
	{
		// Dynamically truncate any super-long, unbroken Sanskrit titles
		int maxSanskritNameLen = (int)((newWidth * .6) / 10);
		if (newWidth < Global.MEDIUM_VIEWPORT_MIN_WIDTH)
			maxSanskritNameLen = (int)((newWidth * .7) / 10);
		
		if (thisItem.getNameSa().length() > maxSanskritNameLen)
			titleSaLabel.setText(thisItem.getNameSa().substring(0, maxSanskritNameLen) + "...");
		else
			titleSaLabel.setText(thisItem.getNameSa());
		
		if (newWidth >= Global.LARGE_VIEWPORT_MIN_WIDTH)
		{
			// Resize grid elements
			listingGrid.resizeColumns(3);
			listingGrid.setWidget(0, 2, rightSidePanel);
			listingGrid.getColumnFormatter().setStyleName(1, "title");
			listingGrid.getColumnFormatter().setStyleName(2, "translation");
			
			// Reposition translation buttons
			if (mainBottomHorizPanel.remove(actionButton))
				rightSidePanel.add(actionButton);
			if (mainBottomHorizPanel.remove(origButton))
				rightSidePanel.add(origButton);
			//rightSidePanel.setHeight("60px");

			tohLabel.setStyleName("sectionListingTitleEn"); tohLabel.addStyleName("sectionListingTitleEnLarge");
			titleEnLabel.setStyleName("sectionListingTitleEn"); titleEnLabel.addStyleName("sectionListingTitleEnLarge");
			titleTiLabel.setStyleName("sectionListingTitleTi"); titleEnLabel.addStyleName("sectionListingTitleLarge");
			titleWyLabel.setStyleName("sectionListingTitleWy"); titleWyLabel.addStyleName("sectionListingTitleLarge");
			titleSaLabel.setStyleName("sectionListingTitleSa"); titleSaLabel.addStyleName("sectionListingTitleLarge");
		}
		else if (newWidth >= Global.MEDIUM_VIEWPORT_MIN_WIDTH)
		{
			// Resize grid elements
			listingGrid.resizeColumns(2);

			// Reposition translation buttons - either from the wide view...
			if (rightSidePanel.remove(actionButton))
				mainBottomHorizPanel.add(actionButton);
			if (rightSidePanel.remove(origButton))
				mainBottomHorizPanel.add(origButton);
			// ...or from the narrow view
			if (mainBottomVertPanel.remove(actionButton))
				mainBottomHorizPanel.add(actionButton);
			if (mainBottomVertPanel.remove(origButton))
				mainBottomHorizPanel.add(origButton);

			tohLabel.setStyleName("sectionListingTitleEn"); tohLabel.addStyleName("sectionListingTitleEnMedium");
			titleEnLabel.setStyleName("sectionListingTitleEn"); titleEnLabel.addStyleName("sectionListingTitleEnMedium");
			titleTiLabel.setStyleName("sectionListingTitleTi"); titleEnLabel.addStyleName("sectionListingTitleMedium");
			titleWyLabel.setStyleName("sectionListingTitleWy"); titleWyLabel.addStyleName("sectionListingTitleMedium");
			titleSaLabel.setStyleName("sectionListingTitleSa"); titleSaLabel.addStyleName("sectionListingTitleMedium");
		}
		else // newWidth < med min
		{
			// Resize grid elements
			listingGrid.resizeColumns(2);

			// Reposition translation buttons - either from the wide view...
			if (rightSidePanel.remove(actionButton))
				mainBottomVertPanel.add(actionButton);
			if (rightSidePanel.remove(origButton))
				mainBottomVertPanel.add(origButton);
			// ...or from the medium view
			if (mainBottomHorizPanel.remove(actionButton))
				mainBottomVertPanel.add(actionButton);
			if (mainBottomHorizPanel.remove(origButton))
				mainBottomVertPanel.add(origButton);

			tohLabel.setStyleName("sectionListingTitleEn"); tohLabel.addStyleName("sectionListingTitleEnSmall");
			titleEnLabel.setStyleName("sectionListingTitleEn"); titleEnLabel.addStyleName("sectionListingTitleEnSmall");
			titleTiLabel.setStyleName("sectionListingTitleTi"); titleEnLabel.addStyleName("sectionListingTitleSmall");
			titleWyLabel.setStyleName("sectionListingTitleWy"); titleWyLabel.addStyleName("sectionListingTitleSmall");
			titleSaLabel.setStyleName("sectionListingTitleSa"); titleSaLabel.addStyleName("sectionListingTitleSmall");
		}
	}
	
	@UiHandler("summaryLabel")
	void onSummaryClick(ClickEvent e) {
		if (summaryShowing)
			hideSummaryText();
		else
			showSummaryText();
	}
	
	public void showSummaryText()
	{
		if (!summaryShowing)
		{
			summaryLabel.removeStyleName("ss-navigateright");
			summaryLabel.addStyleName("ss-navigatedown");
			summaryText.setVisible(true);

			summaryShowing = true;
		}
	}

	public void hideSummaryText()
	{
		if (summaryShowing)
		{
			summaryLabel.removeStyleName("ss-navigatedown");
			summaryLabel.addStyleName("ss-navigateright");
			summaryText.setVisible(false);

			summaryShowing = false;
		}
	}
}
