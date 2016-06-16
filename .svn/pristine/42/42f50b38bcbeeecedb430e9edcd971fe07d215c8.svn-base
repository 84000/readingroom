package org.tbrc.readingroom.client;

import java.util.ArrayList;

import org.tbrc.readingroom.shared.Global;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.Label;

public class BreadCrumb extends FlowPanel
{
	public ArrayList<Hyperlink> segments = new ArrayList<Hyperlink>();
	private Label separator = new Label("/");
	private Label lastItem = new Label();
	
	public BreadCrumb()
	{
		setWidth("100%");
		setStyleName("flowPanel_inline");
		separator.setStyleName("breadCrumb");
		lastItem.setStyleName("breadCrumb");
	}
	
	public void addCrumb(String text, String url)
	{
		// Fist check for this URL in the list
		int numSegs = segments.size();
		for (int i = 0; i < numSegs; i++)
		{
			// Remove from match to the end of the list
			if (segments.get(i).getTargetHistoryToken() == url)
			{
				for (int ii = i; ii < numSegs; ii++)
					segments.remove(i);
				
				break;
			}
		}

		Hyperlink link = new Hyperlink(Utils.truncateString(text, Global.BREADCRUMB_MAX_LEN), url);

		link.setStyleName("myHyperLink");
		link.addStyleName("flowPanel_inline");
		segments.add(link);
		
		updateDisplay();
	}
	
	public void updateDisplay()
	{
		this.clear();

		int count = segments.size();
		for (int i = 0; i < count; i++)
		{
			if (i == count-1)
			{
				Label lastItem = new Label(segments.get(i).getText());
				lastItem.setStyleName("breadCrumb");
				this.add(lastItem);
			}
			else
			{
				this.add(segments.get(i));
				Label sep = new Label("/");
				sep.setStyleName("breadCrumb");
				this.add(sep);
			}
		}
	}
	
	public void copy(BreadCrumb bc)
	{
		this.segments.clear();
		for (int i = 0; i < bc.segments.size(); i++)
			//this.addCrumb(bc.segments.get(i).getText(), bc.segments.get(i).getTargetHistoryToken());
			this.segments.add(bc.segments.get(i));
	}
}
