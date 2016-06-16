package org.tbrc.readingroom.client;

import org.tbrc.readingroom.shared.GlossItem;

import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

public class GlossTermPanel extends VerticalPanel
{
	HTML titles = new HTML("");
	HTML def = new HTML("");
	
	public void initialize(GlossItem glossItem)
	{
		this.clear();
		titles.setStyleName("glossaryNames");
		def.setStyleName("glossaryDef");
//		String styled = "<span style=\"font-family: Georgia,'Times New Roman',serif;\">" +
//						" | <em>" + glossItem.nameBoLtn + "</em> | <em>" + glossItem.nameSaLtn + "</em></span>";
		String styled = "<span style=\"font-family: Georgia,'Times New Roman',serif;\">" +
				" | " + glossItem.nameBo + " | <em>" + glossItem.nameSaLtn + "</em></span>";
		//		" | <em>" + glossItem.nameBo + "</em> | <em>" + glossItem.nameSaLtn + "</em></span>";
		titles.setHTML("<strong>" + glossItem.name + "</strong>" + styled);
		def.setHTML(glossItem.def);
		this.add(titles);
		this.add(def);
	}
}
