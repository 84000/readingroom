package org.tbrc.readingroom.client;

import java.util.ArrayList;

public class TextItem
{
	private String id			= null;

	public String getId() {return id;}					public void setId(String id) {this.id = id;}

	///////////////////////////////////////////////////////////////////////////////
	// TEI Header
	///////////////////////////////////////////////////////////////////////////////
	public String mainTitleEn = "";
	public String mainTitleTi = "";
	public String mainTitleWy = "";
	public String mainTitleSa = "";
	public String longTitleEn = "";
	public String longTitleTi = "";
	public String longTitleWy = "";
	public String longTitleSa = "";
	public String publisher = "";
	public String publisherImageUrl = "";
	public String publisherDesc = "";
	public String edition = "";
	public String publishDate = "";
	public String licenseText = "";
	public String licenseImageUrl = "";
	
	public String toh = "";
	public String biblTitle = "";
	public String biblScope = "";
	public String translator = "";
	
	///////////////////////////////////////////////////////////////////////////////
	// Text body
	///////////////////////////////////////////////////////////////////////////////
	//-------------------------------------
	// "Front" elements
	//-------------------------------------
	// Summary
	public String summaryHeading = "";
	public String summaryText = "";
	public ArrayList<DataItem> sectionItems = new ArrayList<DataItem>();	
	
	// Acknowledgment
	public String ackHeading = "";
	public String ackText = "";

	// Introduction
	public String introHeading = "";
	public String introText = "";
	
	//-------------------------------------
	// "Body" elements
	//-------------------------------------
	
	//-------------------------------------
	// "Back" elements
	//-------------------------------------

	public void initialize()
	{
		mainTitleEn = "";
		mainTitleTi = "";
		mainTitleWy = "";
		mainTitleSa = "";
		longTitleEn = "";
		longTitleTi = "";
		longTitleWy = "";
		longTitleSa = "";
		publisher = "";
		publisherImageUrl = "";
		publisherDesc = "";
		edition = "";
		publishDate = "";
		licenseText = "";
		licenseImageUrl = "";
		
		toh = "";
		biblTitle = "";
		biblScope = "";
		translator = "";
		
		summaryHeading = "";
		summaryText = "";
		sectionItems.clear();
		
		ackHeading = "";
		ackText = "";

		introHeading = "";
		introText = "";
	}
}
