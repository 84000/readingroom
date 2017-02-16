package co.eightyfourthousand.readingroom.client;

import co.eightyfourthousand.readingroom.shared.NoteItem;

import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * 
 * This represents a single note (footnote) on the notes tab of the nav pop-up
 * 
 * @author curtis
 *
 */
public class NotePanel extends VerticalPanel
{
	HorizontalPanel subPanel = new HorizontalPanel();
	public Label idLabel = new Label();
	public HTML noteText = new HTML("");
	
	public void initialize(NoteItem noteItem)
	{
		this.clear();
		idLabel.setStyleName("glossaryNames");
		idLabel.setText(noteItem.id + ".");
		noteText.setStyleName("glossaryNames");
		noteText.addStyleName("addActivePointer");
		noteText.setHTML(noteItem.text);
		subPanel.setSpacing(7);
		subPanel.add(idLabel);
		subPanel.add(noteText);

		this.add(subPanel);
	}
}
