package co.eightyfourthousand.readingroom.client;

import co.eightyfourthousand.readingroom.shared.GlossItem;

import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * 
 * Represents an individual glossary item in the pop-up panel's list
 * 
 * @author curtis
 *
 */
public class GlossTermPanel extends VerticalPanel
{
	HTML titles = new HTML("");
	HTML def = new HTML("");
	
	public void initialize(GlossItem glossItem)
	{
		this.clear();
		titles.setStyleName("glossaryNames");
		def.setStyleName("glossaryDef");
		String styled = "<span style=\"font-family: Georgia,'Times New Roman',serif;\">" +
				" | " + glossItem.nameBo + " | <em>" + glossItem.nameSaLtn + "</em></span>";
		titles.setHTML("<strong>" + glossItem.name + "</strong>" + styled);
		def.setHTML(glossItem.def);
		this.add(titles);
		this.add(def);
	}
}
