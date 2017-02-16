package co.eightyfourthousand.readingroom.shared;

import java.util.ArrayList;

import com.google.gwt.user.client.rpc.IsSerializable;

/*
 * Data returned from server to the client
 */
public class TextData implements IsSerializable
{
	// Titles used at beginning of text translation (body)
	public String transTitle = "";
	public String honTitle = "";
	public String mainTitle = "";

	// List of note abbreviations
	public String notesAbbr = "";

	// Number of text "parts" in the appendix (if any)
	public int appendixStart = 0;
	
	// Master lists of items for entire text
	public ArrayList<GlossItem> glossItems = new ArrayList<GlossItem>();
	public ArrayList<NoteItem> noteItems = new ArrayList<NoteItem>();
	
	public TextData()
	{
	}
}
