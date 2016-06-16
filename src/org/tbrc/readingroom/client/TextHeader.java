package org.tbrc.readingroom.client;

import org.tbrc.readingroom.shared.Global;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class TextHeader extends Composite
{
	private static TextHeaderUiBinder uiBinder = GWT.create(TextHeaderUiBinder.class);

	interface TextHeaderUiBinder extends UiBinder<Widget, TextHeader> {
	}

	@UiField VerticalPanel mainHeaderPanel;
	@UiField Label titleEn;
	@UiField VerticalPanel breadCrumbPanel;
	//@UiField BreadCrumb breadCrumb;
	private BreadCrumb breadCrumb = new BreadCrumb();
	
	public TextHeader()
	{
		initWidget(uiBinder.createAndBindUi(this));
	}

	public TextHeader(String title)
	{
		initWidget(uiBinder.createAndBindUi(this));
		setTitle(title);
	}
	
	public void setTitle(String title)
	{
		titleEn.setText(title);
	}

	public void setBreadCrumb(BreadCrumb bc)
	{
		breadCrumb.copy(bc);
		breadCrumbPanel.add(breadCrumb);
	}

	public void addBreadCrumb(String text, String url)
	{
		breadCrumb.addCrumb(text, url);
	}

	public void onViewportSizeChange(int newWidth)
	{
		mainHeaderPanel.setStyleName("textHeader");
		if (newWidth >= Global.LARGE_VIEWPORT_MIN_WIDTH)
		{
			mainHeaderPanel.addStyleName("limitWidth");
			mainHeaderPanel.addStyleName("textHeaderLarge");
		}
		else if (newWidth >= Global.MEDIUM_VIEWPORT_MIN_WIDTH)
			mainHeaderPanel.addStyleName("textHeaderMedium");
		else // newWith < med min
			mainHeaderPanel.addStyleName("textHeaderSmall");
	}
}
