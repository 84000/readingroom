package co.eightyfourthousand.readingroom.client;

import java.util.ArrayList;

import co.eightyfourthousand.readingroom.shared.Global;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * 
 * This represents the pop-up panel that facilitates navigating in a text,
 * viewing Glossary terms and viewing footnotes ("Notes")
 * 
 * @author curtis
 *
 */
public class PopupNavPanel extends Composite {

	private static TextInfoPanelUiBinder uiBinder = GWT.create(TextInfoPanelUiBinder.class);

	interface TextInfoPanelUiBinder extends UiBinder<Widget, PopupNavPanel>
	{
	}

	public PopupNavPanel()
	{
		initWidget(uiBinder.createAndBindUi(this));
	}

	@UiField VerticalPanel infoPanel;
	@UiField HorizontalPanel tabBar;
	@UiField Button contentsTab;
	@UiField Button glossTab;
	@UiField Button notesTab;
	@UiField ScrollPanel contentsScrollPanel;
	@UiField ScrollPanel glossScrollPanel;
	@UiField ScrollPanel notesScrollPanel;
	@UiField VerticalPanel contentsPanel;
	@UiField VerticalPanel glossPanel;
	@UiField VerticalPanel notesPanel;
	@UiField VerticalPanel notesAbbrPanel;
	@UiField HTMLPanel notesAbbr;

	Reader reader;
	
	public void initialize(Reader r, int transStartPage, int appendixStartPage)
	{
		reader = r;
		final int startPage = transStartPage;
		final int appendixPage = appendixStartPage;
		ArrayList<String> names = reader.getPartNames();

		// Contents
		contentsPanel.clear();
		for (int i = 0; i < names.size(); i++)
		{
			Label partLabel = new Label(names.get(i));

			// Handle "The Translation" heading
			if (i == transStartPage)
			{
				Label transLabel = new Label("The Translation");
				transLabel.setStyleName("navPanelTitle");
				contentsPanel.add(transLabel);
				
				// If "The Translation" is clicked, go to first chapter/part
				transLabel.addClickHandler(new ClickHandler() {
					@Override
					public void onClick(ClickEvent event)
					{
						reader.showPage(startPage);
					}
				});
			}
			
			// Handle "Appendix" heading
			if (appendixStartPage > 0 && i == appendixStartPage)
			{
				Label appendixLabel = new Label("Appendix");
				appendixLabel.setStyleName("navPanelTitle");
				contentsPanel.add(appendixLabel);
				
				// If "The Translation" is clicked, go to first chapter/part
				appendixLabel.addClickHandler(new ClickHandler() {
					@Override
					public void onClick(ClickEvent event)
					{
						reader.showPage(appendixPage);
					}
				});
			}
			
			// Front matter and last page (bibliography) are not styled as "chapters"
			if (i < transStartPage || i == (names.size()-1))
			{
				partLabel.setStyleName("frontmatter");
				if (i == (names.size()-1))
					partLabel.addStyleName("contentsBibl");
			}
			else
			{
				// Handle single-section text with no section title
				//if (partLabel.getText().length() == 0)
				if (partLabel.getText().equalsIgnoreCase("translation"))
					partLabel.setText(r.textData.mainTitle);
				partLabel.setStyleName("chapters");
			}
			contentsPanel.add(partLabel);
			
			partLabel.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event)
				{
					int j;
					for (j = 0; j < contentsPanel.getWidgetCount(); j++)
					{
						if (event.getSource() == contentsPanel.getWidget(j))
							break;
					}
					
					// Chapuza!!
					// Adjust index to accommodate translation and appendix headings
					if (j > startPage)
					{
						if (appendixPage > 0)
						{
							if (j > appendixPage)
								j -= 2;
							else
								j--;
						}
						else
							j--;
					}
					reader.showPage(j);
				}
			});
		}
		
		// Glossary
		glossPanel.clear();
		for (int i = 0; i < r.textData.glossItems.size(); i++)
		{
			GlossTermPanel glossTerm = new GlossTermPanel();
			HTMLPanel horizLine = new HTMLPanel("<hr size=1 width=100% color=#e2af85>");
			glossPanel.add(horizLine);
			glossTerm.initialize(r.textData.glossItems.get(i));
			glossPanel.add(glossTerm);
		}
		
		// Notes
		notesAbbr.clear();
		notesAbbr.add(new HTML(r.textData.notesAbbr));
		notesPanel.clear();
		for (int i = 0; i < r.textData.noteItems.size(); i++)
		{
			NotePanel note = new NotePanel();
			HTMLPanel horizLine = new HTMLPanel("<hr size=1 width=100% color=#e2af85>");
			notesPanel.add(horizLine);
			note.initialize(r.textData.noteItems.get(i));
			notesPanel.add(note);

			note.noteText.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					for (int j = 0; j < notesPanel.getWidgetCount(); j++)
					{
						Widget w = notesPanel.getWidget(j);
						if (w instanceof NotePanel)
						{
							NotePanel n = (NotePanel)w;
							if (event.getSource() == n.noteText)
							{
								String s = n.idLabel.getText();
								s = s.substring(0, s.length()-1);
								int noteId = Integer.parseInt(s);
								//Window.alert("Clicked note #" + noteId);
								reader.showNotePage(noteId);
								break;
							}
						}
					}
				}
			});
		}
	}
	
	public void onViewportSizeChange(int newWidth)
	{
		int panelWidth = (int)(newWidth * 0.30);

		// Only invoke changes if we cross a viewport-change width
		if (newWidth >= Global.LARGE_VIEWPORT_MIN_WIDTH)
		{
			//panelWidth = (int)(newWidth * 0.30);
			infoPanel.setStyleName("textInfoWrapper");
			panelWidth = 330;
			infoPanel.setWidth("" + panelWidth + "px");
		}
		else if (newWidth >= Global.MEDIUM_VIEWPORT_MIN_WIDTH)
		{
			infoPanel.setStyleName("textInfoWrapper");
			panelWidth = (int)(newWidth * 0.40);
			infoPanel.setWidth("" + panelWidth + "px");
		}
		else // newWith < med min
		{
			panelWidth = newWidth;
			infoPanel.addStyleName("noMargin");
			infoPanel.setWidth("100%");
		}

		int tabWidth = panelWidth / 3;
		contentsTab.setWidth("" + tabWidth + "px");
		contentsScrollPanel.setHeight("" + (Window.getClientHeight() * .6) + "px");
		glossTab.setWidth("" + tabWidth + "px");
		glossScrollPanel.setHeight("" + (Window.getClientHeight() * .6) + "px");
		notesTab.setWidth("" + tabWidth + "px");
		notesScrollPanel.setHeight("" + ((Window.getClientHeight() * .6) - notesAbbr.getOffsetHeight()) + "px");
	}

	@UiHandler("contentsTab")
	void onContentsTabClick(ClickEvent e)
	{
		contentsScrollPanel.setVisible(true);	contentsTab.addStyleName("infoPanelTabSelected");
		glossScrollPanel.setVisible(false);		glossTab.removeStyleName("infoPanelTabSelected");
		notesScrollPanel.setVisible(false);		notesTab.removeStyleName("infoPanelTabSelected");
		notesAbbrPanel.setVisible(false);
	}

	@UiHandler("glossTab")
	void onGlossTabClick(ClickEvent e)
	{
		contentsScrollPanel.setVisible(false);	contentsTab.removeStyleName("infoPanelTabSelected");
		glossScrollPanel.setVisible(true);		glossTab.addStyleName("infoPanelTabSelected");
		notesScrollPanel.setVisible(false);		notesTab.removeStyleName("infoPanelTabSelected");
		notesAbbrPanel.setVisible(false);
	}

	@UiHandler("notesTab")
	void onNotesTabClick(ClickEvent e)
	{
		contentsScrollPanel.setVisible(false);	contentsTab.removeStyleName("infoPanelTabSelected");
		glossScrollPanel.setVisible(false);		glossTab.removeStyleName("infoPanelTabSelected");
		notesScrollPanel.setVisible(true);		notesTab.addStyleName("infoPanelTabSelected");
		if (reader.textData.notesAbbr.length() > 0)
			notesAbbrPanel.setVisible(true);
		// Force a resize of notes panel after adding Abbr section
		notesScrollPanel.setHeight("" + ((Window.getClientHeight() * .6) - notesAbbr.getOffsetHeight()) + "px");
	}
}
