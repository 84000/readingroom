package org.tbrc.readingroom.client;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Label;

public class ListLabel extends Label
{
	public String id = "";
	public String parentId = "";
	public String title = "";
	public String toh = "";
	public String url = "";
	public int nestLevel = 0;

	public ListLabel(String title)
	{
		super(title);
	}
	
	public void initialize(DataItem item, int nestLevel)
	{
		this.id = item.getId();
		this.parentId = item.getParentId();
		this.title = item.getNameEn();
		this.toh = item.getToh();
		this.url = item.getRid();
		this.nestLevel = nestLevel;
		
		if (this.parentId == null)
			this.parentId = "";
		if (this.url == null)
			this.url = "";
		if (this.toh == null)
			this.toh = "";

		// Add TOH number, if appropriate
		if (this.toh.length() > 0)
			this.setText(this.getText() + " (TOH: " + this.toh + ")");
		
		switch (nestLevel)
		{
		case 0: this.setStyleName("listH1"); break;
		case 1: this.setStyleName("listH2"); break;
		case 2: this.setStyleName("listH3"); break;
		case 3: this.setStyleName("listH4"); break;
		case 4: this.setStyleName("listH5"); break;
		}
		
		if (this.url.length() > 0)
		{
			this.addStyleName("listLink");
			final String textUrl = this.url;
			this.addClickHandler(new ClickHandler() {
				public void onClick(ClickEvent event)
				{
					Readingroom.main.showReader(textUrl);
				}
			});
		}
	}
}
