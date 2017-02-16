package co.eightyfourthousand.readingroom.client;
import java.util.ArrayList;

import co.eightyfourthousand.readingroom.shared.Global;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * 
 * A panel that holds/presents the list (shown in a hierarchy) of texts that have been translated
 * 
 * @author curtis
 *
 */
public class TranslatedTextList extends Composite {

	private static TranslatedTextListUiBinder uiBinder = GWT
			.create(TranslatedTextListUiBinder.class);

	interface TranslatedTextListUiBinder extends
			UiBinder<Widget, TranslatedTextList> {
	}

	@UiField HTMLPanel transListPage;
	@UiField VerticalPanel contentPanel;
	@UiField VerticalPanel noticePanel;

	public TranslatedTextList() {
		initWidget(uiBinder.createAndBindUi(this));
		
		transListPage.setWidth("94%");
		Label noticeLabel = new Label(Global.TRANSLATED_LIST_NOTICE);
		noticeLabel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		noticeLabel.setStyleName("transListNotice");
		noticeLabel.setWidth("70%");
		noticePanel.add(noticeLabel);
	}
		
	public void populate(ArrayList<DataItem> kangyurItems, ArrayList<DataItem> tengyurItems)
	{
		populateMajorSection("THE KANGYUR", kangyurItems);
		populateMajorSection("THE TENGYUR", tengyurItems);
	}

	private void populateMajorSection(String sectionHeading, ArrayList<DataItem> items)
	{
		// Kangyur
		addListing(sectionHeading, 0);

		for (int i = 0; i < items.size(); i++)
		//for (int i = 0; i < 2; i++)
		{
			DataItem item = items.get(i);
			
			if (Integer.parseInt(item.getStatsTrans()) > 0)
			{
				addListing(item, 1);
				
				String[] args = {item.getId()};
				//Readingroom.rpcService.doQuery("section", args, sectionCallback);
				Readingroom.rpcService.doQuery("section", args, new AsyncCallback<String>()
				{
					public void onFailure(Throwable caught)
					{
						// Show the RPC error message to the user
						Window.alert("'section' Remote Procedure Call - Failure");
					}

					public void onSuccess(String result)
					{
						//Window.alert(result);
						ArrayList<DataItem> items = new ArrayList<DataItem>();
						items = Parser.parseSectionQueryResult(result);

						for (DataItem item : items)
						{
							if (Integer.parseInt(item.getStatsTrans()) > 0)
							{
								// If it is a subsection of the parent section, we need to query further
								if (isSubsection(item))
								{
									addListing(item, 2);

									String[] args = {item.getId()};
									Readingroom.rpcService.doQuery("section", args, new AsyncCallback<String>()
									{
										public void onFailure(Throwable caught)
										{
											// Show the RPC error message to the user
											Window.alert("'section' Remote Procedure Call - Failure");
										}

										public void onSuccess(String result)
										{
											ArrayList<DataItem> items = new ArrayList<DataItem>();
											items = Parser.parseSectionQueryResult(result);

											for (DataItem item : items)
											{
												if (Integer.parseInt(item.getStatsTrans()) > 0)
												{
													// If it is a subsection of the parent section, we need to query further
													if (isSubsection(item))
													{
														addListing(item, 3);

														String[] args = {item.getId()};
														Readingroom.rpcService.doQuery("section", args, new AsyncCallback<String>()
														{
															public void onFailure(Throwable caught)
															{
																// Show the RPC error message to the user
																Window.alert("'section' Remote Procedure Call - Failure");
															}

															public void onSuccess(String result)
															{
																ArrayList<DataItem> items = new ArrayList<DataItem>();
																items = Parser.parseSectionQueryResult(result);

																for (DataItem item : items)
																{
																	if (Integer.parseInt(item.getStatsTrans()) > 0)
																	{
																		// If it is a subsection of the parent section, we need to query further
																		if (isSubsection(item))
																		{
																			addListing(item, 4);

																			String[] args = {item.getId()};
																			Readingroom.rpcService.doQuery("section", args, new AsyncCallback<String>()
																			{
																				public void onFailure(Throwable caught)
																				{
																					// Show the RPC error message to the user
																					Window.alert("'section' Remote Procedure Call - Failure");
																				}

																				public void onSuccess(String result)
																				{
																					//Window.alert("SUCCESSful Remote Procedure Call - " + fItem.getNameEn() + ", ID: " + fItem.getId() + ", RID: " + fItem.getRid());
																				}
																			});
																		}
																		else
																		{
																			addListing(item, 4);
																		}
																	}
																}
															}
														});
													}
													else
													{
														addListing(item, 3);
													}
												}
											}
										}
									});
								}
								else
								{
									addListing(item, 2);
								}
							}
						}
					}
				});
			}
		}
	}
	
	
	//
	// Determine if this result (as parsed into items) is a subsection of the parent section
	//
	private boolean isSubsection(DataItem item)
	{
		boolean isSub = false;
		if (item.getType() == "section")
			isSub = true;

		return isSub;
	}
	
	private void addListing(String title, int nestLevel)
	{
		DataItem item = new DataItem();
		
		item.setId("");
		item.setParentId("");
		item.setNameEn(title);
		item.setToh("");
		item.setRid("");

		addListing(item, nestLevel);
	}
	
	private void addListing(DataItem item, int nestLevel)
	{
		ListLabel label = new ListLabel(item.getNameEn());
		label.initialize(item, nestLevel);

		// Add new label to content panel
		boolean added = false;
		for (int i = 0; i < contentPanel.getWidgetCount(); i++)
		{
			if (contentPanel.getWidget(i) instanceof ListLabel)
			{
				ListLabel l = (ListLabel)contentPanel.getWidget(i);
				
				if (l.id.equalsIgnoreCase(label.parentId))
				{
					// We're now in the right section
					for (int j = (i+1); j < contentPanel.getWidgetCount(); j++)
					{
						if (contentPanel.getWidget(j) instanceof ListLabel)
						{
							ListLabel ll = (ListLabel)contentPanel.getWidget(j);

							if (ll.parentId.equalsIgnoreCase(label.parentId))
							{
								continue;
							}
							else
							{
								contentPanel.insert(label, j);
								added = true;
								break;
							}
						}
					}
					
					// If parent was found, we're done looping, no matter if added yet or not
					break;
				}
			}
		}

		// If no slot was found, add at the end
		if (!added)
			contentPanel.add(label);
		
		// Chapuza: Add Tantra warning if we just added the "Tantra" item
		if (nestLevel == 1 && item.getNameEn().equalsIgnoreCase("Tantra"))
		{
			HTML tantraWarning = new HTML();
			tantraWarning.setHTML(Global.TANTRA_WARNING_NOTICE);
			tantraWarning.setStyleName("tantraWarningNotice");
			contentPanel.add(tantraWarning);
		}
	}
}
