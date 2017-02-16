package co.eightyfourthousand.readingroom.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

/**
 * 
 * Represents an individual "card" or "tomb-stone" in the main "lobby" view
 * 
 * @author curtis
 *
 */
public class LobbySection extends Composite {

	private static LobbySectionUiBinder uiBinder = GWT
			.create(LobbySectionUiBinder.class);

	interface LobbySectionUiBinder extends UiBinder<Widget, LobbySection> {
	}

	@UiField FocusPanel mainPanel;
	@UiField HorizontalPanel sectionTable;
	
	@UiField Label titleEnLabel;
	@UiField Label titleTiLabel;
	@UiField Label titleWyLabel;
	@UiField Label titleSaLabel;
	@UiField Grid titleTiWyGrid;
	//@UiField Label descLabel;
	@UiField HTML descLabel;
	@UiField Label textsLabel;
	@UiField Label translatedLabel;
	@UiField Label inProgressLabel;
	@UiField Label notBegunLabel;

	private DataItem data = null;
	
	public LobbySection()
	{
		initWidget(uiBinder.createAndBindUi(this));
	}

	@UiHandler("mainPanel")
	void onMainPanelClick(ClickEvent e) { Readingroom.main.newPage(data, mainPanel); }

	//@UiHandler("titleEnLabel")
	//void onTitleEnClick(ClickEvent e) {	Readingroom.main.newPage(data, titleEnLabel); }

	//@UiHandler("descLabel")
	//void onDescClick(ClickEvent e)	  {	Readingroom.main.newPage(data, descLabel); }

	public void setData(DataItem item)
	{
		data = item;
		
		titleEnLabel.setText(item.getNameEn());
		titleTiLabel.setText(item.getNameTi());
		titleWyLabel.setText(Utils.removeTrailingChar(item.getNameWy(), "/"));
		titleSaLabel.setText(item.getNameSa());

		// Crazy required chapuza!
		titleTiWyGrid.getColumnFormatter().setWidth(0, "50%");
		titleTiWyGrid.getColumnFormatter().setWidth(2, "50%");
		
		//descLabel.setText(item.getDesc());
		descLabel.setHTML(item.getDesc());
		textsLabel.setText(item.getStatsTotal());
		translatedLabel.setText(item.getStatsTrans());
		inProgressLabel.setText(item.getStatsInProg());
		notBegunLabel.setText(item.getStatsNotBeg());
	}
}
