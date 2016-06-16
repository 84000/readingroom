package org.tbrc.readingroom.shared;

import java.util.ArrayList;

import com.google.gwt.user.client.rpc.IsSerializable;

/*
 * Data returned from server to the client
 */
public class TextPartData implements IsSerializable
{
	private String title = "";
	private String subtitle = "";
	private String content = "";
	public String partType = "";
	public ArrayList<GlossItem> glossItems = new ArrayList<GlossItem>();
	public ArrayList<MilestoneItem> milestoneItems = new ArrayList<MilestoneItem>();
	public ArrayList<NoteItem> noteItems = new ArrayList<NoteItem>();

	public String getTitle() { return title; }
	public void setTitle(String title) { this.title = title; }
	public String getSubtitle() { return subtitle; }
	public void setSubtitle(String subtitle) { this.subtitle = subtitle; }
	public String getContent() { return content; }
	public void setContent(String content) { this.content = content; }
		
	public TextPartData()
	{
	}
}
