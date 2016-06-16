package org.tbrc.readingroom.shared;

import com.google.gwt.user.client.rpc.IsSerializable;

public class GlossItem implements IsSerializable
{
	public String id = "";
	public String name = "";
	public String nameBoLtn = "";
	public String nameBo = "";
	public String nameSaLtn = "";
	public String def = "";
	
	public GlossItem createDuplicate()
	{
		GlossItem dup = new GlossItem();
		
		dup.id = id;
		dup.name = name;
		dup.nameBoLtn = nameBoLtn;
		dup.nameBo = nameBo;
		dup.nameSaLtn = nameSaLtn;
		dup.def = def;
		
		return dup;
	}
}
