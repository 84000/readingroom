package org.tbrc.readingroom.client;

import java.util.ArrayList;

import org.tbrc.readingroom.shared.Global;
import org.tbrc.readingroom.shared.TextPartData;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HasAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.InlineHTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class TextPage extends Composite
{
	Reader reader = null;
	private TextPartData textPartData = new TextPartData();
	private ArrayList<InlineHTML> glossTerms = new ArrayList<InlineHTML>();
	private ArrayList<InlineHTML> milestones = new ArrayList<InlineHTML>();
	private ArrayList<InlineHTML> notes = new ArrayList<InlineHTML>();
	private PopupInfoPanel infoPopup = new PopupInfoPanel();
	private String title = "", subtitle = "";
	private int postLoadScrollToNote = 0;
	private int lastPortWidth = 0;
	
	private static TextPageUiBinder uiBinder = GWT
			.create(TextPageUiBinder.class);

	interface TextPageUiBinder extends UiBinder<Widget, TextPage> {
	}

	public TextPage() {
		initWidget(uiBinder.createAndBindUi(this));
	}
	
	public TextPage(String title, String subtitle, String prevLabel, String nextLabel) {
		initWidget(uiBinder.createAndBindUi(this));

		// Set up page-navigation buttons
		if (prevLabel == "")
			prevButton.setText("");
		else
			prevButton.setText("Prev: " + prevLabel);
			
		if (nextLabel == "")
			nextButton.setText("");
		else
			nextButton.setText("Next: " + nextLabel);

		prevButton.setVisible(false);
		nextButton.setVisible(false);

		// Populate titles
		transTitlePanel.setVisible(false);
		setDisplayTitles(title, subtitle);
		setTitle(title);
		setSubtitle(subtitle);
	}

	@UiField HTMLPanel htmlPanel;
	@UiField HorizontalPanel mainPanel;
	@UiField VerticalPanel pagePanel;
	@UiField VerticalPanel transTitlePanel;
	@UiField Label transTitle1Label;
	@UiField Label transTitle2Label;
	@UiField Label transTitle3Label;
	@UiField VerticalPanel spacerPanel;
	@UiField HTMLPanel titlePanel;
	@UiField Label subtitleLabel;
	@UiField Label titleLabel;
	@UiField HTMLPanel contentPanel;
	@UiField HorizontalPanel buttonPanel;
	@UiField Button prevButton;
	@UiField Button nextButton;
	
	public void setMasterTransTitles(String title1, String title2, String title3)
	{
		// Translation description (e.g., "The Translation")
		transTitle1Label.setText(title1);
		
		// Honorific title (sometimes this is not present)
		if (title2.length() > 0)
		{
			transTitle2Label.setText(title2);
			transTitle2Label.setVisible(true);
		}
		else
			transTitle2Label.setVisible(false);

		// Main title
		transTitle3Label.setText(title3);
		transTitlePanel.setVisible(true);
	}
	
	public void setDisplayTitles(String title, String subtitle)
	{
		if ((title == null || title == "") && (subtitle == null || subtitle == ""))
		{
			titlePanel.setVisible(false);
		}
		if (title.equalsIgnoreCase("translation") && (subtitle == null || subtitle == ""))
		{
			//titlePanel.setVisible(false);
			titleLabel.setText("");
			subtitleLabel.setText("");
			titlePanel.addStyleName("blankSectionTitle");
		}
		else if (subtitle == null || subtitle == "")
		{
			titleLabel.setText(title);
			//titlePanel.remove(subtitleLabel);
			subtitleLabel.setVisible(false);
		}
		else
		{
			// Note that title/subtitle are reversed when both are present (per design)
			titleLabel.setText(subtitle);
			subtitleLabel.setVisible(true);
			subtitleLabel.setText(title);		
		}
	}

	public void setTitle(String title) { this.title = title; }
	public String getTitle()	{ return title; }
	public void setSubtitle(String subtitle) { this.subtitle = subtitle; }
	public String getSubtitle()	{ return subtitle; }
	
	public void setPrevText(String text)
	{
		if (text == "")
			prevButton.setVisible(false);
		else
		{
			prevButton.setVisible(true);
			prevButton.setText("Prev: " + text);
		}
	}

	public void setNextText(String text)
	{
		if (text == "")
			nextButton.setVisible(false);
		else
		{
			nextButton.setVisible(true);
			nextButton.setText("Next: " + text);
		}
	}

	public void setReader(Reader r)
	{
		reader = r;
	}

	public void setData(TextPartData tData)
	{
		textPartData = tData;

		// Add HTML content to panel
		contentPanel.clear();
		HTML contentHtml = new HTML(textPartData.getContent());
		contentHtml.setStyleName("contentText");
		contentPanel.add(contentHtml);
		
		// Turn on appropriate nav buttons
		activateNavButtons();

		// Add title to panel
		// NOT NECESSARY, now just use "<cite>" for titles
		//		InlineHTML titleHtml = new InlineHTML(data.getTitle());
		//		titleHtml.setStyleName("itemTitleFont");
		//		contentPanel.add(titleHtml, "title");

		// Create and add glossary term widgets to panel
		glossTerms.clear();
		for (int i = 0; i < textPartData.glossItems.size(); i++)
		{
			InlineHTML glossTerm = new InlineHTML(textPartData.glossItems.get(i).name);
			glossTerm.setStyleName("glossLink");
			contentPanel.add(glossTerm, textPartData.glossItems.get(i).id);
			glossTerms.add(glossTerm);

			glossTerm.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					int j;
					for (j = 0; j < textPartData.glossItems.size(); j++)
					{
						if (event.getSource() == glossTerms.get(j))
							break;
					}
					//Window.alert(textPartData.glossItems.get(j).def);
					String html = "<div class='englishName'>" + textPartData.glossItems.get(j).name + "</div>";
//					if (textPartData.glossItems.get(j).nameBoLtn.length() > 0)
//					{
//						html += "<hr style='border: 0; border-bottom: 1px solid #e2af85; background: #e2af85;'>";
//						html += textPartData.glossItems.get(j).nameBoLtn;
//					}
					if (textPartData.glossItems.get(j).nameBo.length() > 0)
					{
						html += "<hr style='border: 0; border-bottom: 1px solid #e2af85; background: #e2af85;'>";
						html += textPartData.glossItems.get(j).nameBo;
					}
					if (textPartData.glossItems.get(j).nameSaLtn.length() > 0)
					{
						html += "<hr style='border: 0; border-bottom: 1px solid #e2af85; background: #e2af85;'>";
						html += "<em>" + textPartData.glossItems.get(j).nameSaLtn + "</em>";
					}
					if (textPartData.glossItems.get(j).def.length() > 0)
					{
						html += "<hr style='border: 0; border-bottom: 1px solid #e2af85; background: #e2af85;'>";
						html += textPartData.glossItems.get(j).def;
					}
					infoPopup.setHtml(html);
					//infoPopup.center();
					InlineHTML obj = (InlineHTML)event.getSource();
					infoPopup.showRelativeTo(obj);
					
					// If nav panel is present, scroll it to proper glossary term
					scrollGlossPanelTo(textPartData.glossItems.get(j).name);
				}
			});
		}

		// Create and add milestone widgets to panel
		milestones.clear();
		for (int i = 0; i < textPartData.milestoneItems.size(); i++)
		{
			InlineHTML milestone = new InlineHTML(textPartData.milestoneItems.get(i).id);
			//Anchor milestone = new Anchor(data.milestoneItems.get(i).id);
			milestone.setStyleName("milestone");
			contentPanel.add(milestone, textPartData.milestoneItems.get(i).id);
			milestones.add(milestone);
		}

		// Create and add notes widgets to panel
		notes.clear();
		for (int i = 0; i < textPartData.noteItems.size(); i++)
		{
			final InlineHTML note = new InlineHTML(textPartData.noteItems.get(i).id);
			note.setStyleName("glossLink");
			note.addStyleName("addSuper");
			contentPanel.add(note, textPartData.noteItems.get(i).id);
			notes.add(note);

			note.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					int j;
					for (j = 0; j < textPartData.noteItems.size(); j++)
					{
						if (event.getSource() == notes.get(j))
							break;
					}
					String html = textPartData.noteItems.get(j).text;
					infoPopup.setHtml(html);
					//infoPopup.center();
					InlineHTML obj = (InlineHTML)event.getSource();
					infoPopup.showRelativeTo(obj);
					
					// If nav panel is present, scroll it to proper note
					scrollNotesPanelTo(textPartData.noteItems.get(j).id);
				}
			});
		}

		// If we've designated a scroll-to position for this page, go there now that it's loaded
		if (postLoadScrollToNote > 0)
		{
			scrollToNoteInBody(postLoadScrollToNote);
			postLoadScrollToNote = 0;
		}
	}
	
	private void scrollGlossPanelTo(String term)
	{
		if (reader != null)
		{
			if (reader.navPanel.isVisible())
			{
				int glossIndex;
				for (glossIndex = 0; glossIndex < reader.textData.glossItems.size(); glossIndex++)
				{
					if (term.equals(reader.textData.glossItems.get(glossIndex).name))
						break;
				}
				
				int pos = Global.GLOSS_LIST_TOP_MARGIN;	// Accommodate margin at top of list
				int widgetIndex = glossIndex * 2;		// For each term, there are 2 widgets: a line & a panel
				for (int i = 0; i < widgetIndex; i++)
				{
					Widget w = reader.navPanel.glossPanel.getWidget(i);
					if (w instanceof GlossTermPanel)
					{
						GlossTermPanel p = (GlossTermPanel)w;
						pos += p.getOffsetHeight() + Global.GLOSS_LIST_ITEM_MARGIN;
					}
					else
						pos += Global.GLOSS_LIST_SEPARATOR_HEIGHT;
				}
				
				reader.navPanel.glossScrollPanel.setVerticalScrollPosition(pos);
			}
		}
	}
	
	private void scrollNotesPanelTo(String noteId)
	{
		if (reader != null)
		{
			if (reader.navPanel.isVisible())
			{
				int noteIndex;
				for (noteIndex = 0; noteIndex < reader.textData.noteItems.size(); noteIndex++)
				{
					if (noteId.equals(reader.textData.noteItems.get(noteIndex).id))
						break;
				}
				
				int pos = Global.NOTE_LIST_TOP_MARGIN;	// Accommodate margin at top of list
				int widgetIndex = noteIndex * 2;		// For each term, there are 2 widgets: a line & a panel
				for (int i = 0; i < widgetIndex; i++)
				{
					Widget w = reader.navPanel.notesPanel.getWidget(i);
					if (w instanceof NotePanel)
					{
						NotePanel n = (NotePanel)w;
						pos += n.getOffsetHeight() + Global.NOTE_LIST_ITEM_MARGIN;
					}
					else
						pos += Global.NOTE_LIST_SEPARATOR_HEIGHT;
				}
				
				reader.navPanel.notesScrollPanel.setVerticalScrollPosition(pos);
			}
		}
	}
	
	public void setTitleData(TextItem tItem)
	{
		activateNavButtons();
		
		titlePanel.setVisible(false);
		
		contentPanel.setStyleName("textTitlePanel");
		Label mainTiLabel = new Label(tItem.mainTitleTi);
		mainTiLabel.setStyleName("textTitleAll");
		mainTiLabel.addStyleName("textTitleH1-ti");
		Label mainEnLabel = new Label(tItem.mainTitleEn);
		mainEnLabel.setStyleName("textTitleAll");
		mainEnLabel.addStyleName("textTitleH1-en");
		Label mainSaLabel = new Label(tItem.mainTitleSa);
		mainSaLabel.setStyleName("textTitleAll");
		mainSaLabel.addStyleName("textTitleH1-sa");

		Label longTiLabel = new Label(tItem.longTitleTi);
		longTiLabel.setStyleName("textTitleAll");
		longTiLabel.addStyleName("textTitleH2-ti");
		Label longWyLabel = new Label(tItem.longTitleWy);
		longWyLabel.setStyleName("textTitleAll");
		longWyLabel.addStyleName("textTitleH2-sa");
		Label longEnLabel = new Label(tItem.longTitleEn);
		longEnLabel.setStyleName("textTitleAll");
		longEnLabel.addStyleName("textTitleH2-en");
		Label longSaLabel = new Label(tItem.longTitleSa);
		longSaLabel.setStyleName("textTitleAll");
		longSaLabel.addStyleName("textTitleH2-sa");
		
		Label tohLabel = new Label(tItem.toh);
		tohLabel.setStyleName("textTitleNotice");
		tohLabel.addStyleName("addBold");
		Label scopeLabel = new Label(tItem.biblScope);
		scopeLabel.setStyleName("textTitleNotice");
		//Label noticeLabel = new Label("Translated by the Dharmachakra Translation Committee under the patronage and supervision of 84000: Translating the Word of the Buddha");
		HTML noticeLabel = new HTML(tItem.translator);
		noticeLabel.setStyleName("textTitleNotice");
		noticeLabel.addStyleName("textTitleNotice-bg");
		
		Label editionLabel = new Label(tItem.edition + ", " + tItem.publishDate);
		editionLabel.setStyleName("pubNotice");
		
		HTMLPanel pubNoticeHtml = new HTMLPanel(tItem.publisher);
		pubNoticeHtml.setStyleName("pubNotice");
		
		// TEST TEST TEST
		Label downloadLabel = new Label("Download this text");
		//downloadLabel.setStyleName("textTitleNotice");
		//downloadLabel.setHeight("2em");
		Button downloadButton = new Button("");
		downloadButton.setStyleName("download");
		HorizontalPanel downloadPanel = new HorizontalPanel();
		downloadPanel.setVerticalAlignment(HasAlignment.ALIGN_MIDDLE);
		downloadPanel.setSpacing(10);
		downloadPanel.add(downloadLabel);
		downloadPanel.add(downloadButton);
		
		final String tId = tItem.getId();
		
		downloadButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				// TEMPORARILY DISABLE PDF-CONVERSION FOR NORMAL USERS
				if (User.isAdmin() || User.isDev() || tId.equals("UT22084-044-005") || tId.equals("UT22084-044-006") || tId.equals("UT22084-055-004"))
					Readingroom.rpcService.generatePdf(tId, new pdfCallBack());
				else
				{
					AlertPanel alert = new AlertPanel("Conversion to, and downloading of, a PDF version of this translation will be available soon.");
					alert.center();
				}
				//Readingroom.rpcService.generatePdf(tId, new pdfCallBack());
			}
		});
		// TEST TEST TEST
		
		//String html = "<div>" + tItem.licenseText + "</div>";
		HTMLPanel licNoticeHtml = new HTMLPanel(tItem.licenseText);
		licNoticeHtml.setStyleName("licNotice");
		
		VerticalPanel vPanel = new VerticalPanel();
		vPanel.setHorizontalAlignment(HasAlignment.ALIGN_CENTER);
		VerticalPanel innerPanel = new VerticalPanel();
		innerPanel.setHorizontalAlignment(HasAlignment.ALIGN_CENTER);
		
		innerPanel.add(mainTiLabel);
		innerPanel.add(mainEnLabel);
		innerPanel.add(mainSaLabel);
		
		innerPanel.add(longTiLabel);
		innerPanel.add(longWyLabel);
		innerPanel.add(longEnLabel);
		innerPanel.add(longSaLabel);
		
		innerPanel.add(tohLabel);
		innerPanel.add(scopeLabel);
		innerPanel.add(noticeLabel);

		innerPanel.add(editionLabel);
		innerPanel.add(pubNoticeHtml);
		// TEST TEST TEST
		innerPanel.add(downloadPanel);
		// TEST TEST TEST
		innerPanel.add(licNoticeHtml);
		
		innerPanel.add(nextButton);
		vPanel.add(innerPanel);
		innerPanel.setWidth("90%");
		contentPanel.add(vPanel);
		vPanel.setWidth("100%");
		
		// Set some text-part data, so we can know this has been loaded
		textPartData.setContent("*");
	}
	
	private class pdfCallBack implements AsyncCallback<String>
	{
		public void onFailure(Throwable caught) { Window.alert("'PDF generator' RPC failure"); }

		public void onSuccess(String url)
		{
			// The initial query returns the title info (TEI header), so parse it and get summary
			if (url == null)
				Window.alert("failed to write PDF file!");
			else
			{
				// Presenting custom alert allows us to trigger opening the PDF from a button event,
				// This, in turn, prevents triggering browser's pop-up blocker
				final AlertPanel alert = new AlertPanel("PDF will be opened in new browser tab");
				final String pdfUrl = url;
				alert.addCloseHandler(new CloseHandler<PopupPanel>() {
					@Override
					public void onClose(CloseEvent<PopupPanel> event)
					{
					    // If detail button was clicked, show correct detail panel
						if (!alert.hasCancelled())
						{
							//Window.open(thisItem.getUrl(), "_self", "");			// Opens in same browser window
							//Window.open(thisItem.getUrl(), "_blank", "");			// Opens in new tab (Chrome, Safari, Firefox)
							//Window.open(thisItem.getUrl(), "_blank", "enabled");	// Opens in new window (Chrome, Safari, Firefox)
							Window.open(pdfUrl, "_blank", "");
						}
					}
				});

				alert.center();
			}
		}
	}
	
	private void activateNavButtons()
	{
		if (prevButton.getText() != "")
			prevButton.setVisible(true);
		if (nextButton.getText() != "")
			nextButton.setVisible(true);
	}
	
	public void showData()
	{
		//Window.alert("Data: {" + textPartData.getContent() + "}");
		Log.info("Data: {" + textPartData.getContent() + "}"); 		
	}
	
	public boolean isDataSet()
	{
		if (textPartData.getContent() == "")
			return false;
		else
			return true;
	}
	
	public void accommodateNavPanel(boolean navPresent)
	{
		if (navPresent)
		{
			int width = Window.getClientWidth();
			if (width >= Global.LARGE_VIEWPORT_MIN_WIDTH)
			{
				//int panelWidth = (int)(width * 0.2);
				//spacerPanel.setWidth("" + panelWidth + "px");
				int extraSpace = (width - Global.READER_PANEL_MAX_WIDTH) / 2;
				if (extraSpace < 0)
					extraSpace = 0;
				spacerPanel.setWidth("" + (Global.NAV_PANEL_WIDTH_LARGE_PORTAL - extraSpace - 60) + "px");
				spacerPanel.setVisible(true);
			}
			else if (width >= Global.MEDIUM_VIEWPORT_MIN_WIDTH)
			{
				//int panelWidth = 0;
				//spacerPanel.setWidth("" + panelWidth + "px");
				spacerPanel.setVisible(false);
			}
			else // newWith < med min
			{
				//int panelWidth = 0;
				//spacerPanel.setWidth("" + panelWidth + "px");
				spacerPanel.setVisible(false);
			}			
		}
		else
			spacerPanel.setVisible(false);
	}
	
	public void scrollToNoteInBody(int noteId)
	{
		if (isDataSet())
		{
			for (int i = 0; i < notes.size(); i++)
			{
				String s = "" + noteId;
				if (notes.get(i).getText().equals(s))
				{
					Window.scrollTo(0, notes.get(i).getAbsoluteTop());
					break;
				}
			}
		}
		else
			postLoadScrollToNote = noteId;
	}
	
	public void onViewportSizeChange(int newWidth)
	{
		// Only invoke changes if we cross a viewport-change width
		if (Utils.viewPortChanged(newWidth, lastPortWidth))
		{
			//mainPanel.setStyleName("mainPage");
			if (newWidth >= Global.LARGE_VIEWPORT_MIN_WIDTH)
			{
				//mainPanel.addStyleName("limitWidth");
				//mainPanel.addStyleName("textHeaderLarge");
				mainPanel.setWidth("94%");
				pagePanel.setWidth("60%");
				for (int i = 0; i < milestones.size(); i++)
					milestones.get(i).setStyleName("milestone");
			}
			else if (newWidth >= Global.MEDIUM_VIEWPORT_MIN_WIDTH)
			{
				//mainPanel.addStyleName("textHeaderMedium");
				mainPanel.setWidth("94%");
				pagePanel.setWidth("80%");
				for (int i = 0; i < milestones.size(); i++)
					milestones.get(i).setStyleName("milestone");
			}
			else // newWith < med min
			{
				//mainPanel.addStyleName("textHeaderSmall");
				mainPanel.setWidth("100%");
				pagePanel.setWidth("80%");
				for (int i = 0; i < milestones.size(); i++)
				{
					milestones.get(i).setStyleName("milestone");
					milestones.get(i).addStyleName("milestone-small");
				}
			}
		}

		accommodateNavPanel(reader.navPanel.isVisible());

		lastPortWidth = newWidth;
	}
	
	@UiHandler("prevButton")
	void onPrevClick(ClickEvent e)
	{
		reader.prev();
	}

	@UiHandler("nextButton")
	void onNextClick(ClickEvent e)
	{
		reader.next();
	}
}
