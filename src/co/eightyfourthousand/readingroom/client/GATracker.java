package co.eightyfourthousand.readingroom.client;

/**
 * Tracker - Google Analytics
 * @author jmbataller
 * @version 1.0
 */
public class GATracker 
{
	//********************************************************************************************
	//
	//	BASIC CONFIGURATION FEATURES
	// 
	//	Methods that you use for customizing all aspects of Google Analytics reporting.
	//
	//********************************************************************************************
	
	/**
	 * Returns the Google Analytics ID for this tracker object. 
	 * If you are tracking pages on your website in multiple accounts, 
	 * you can use this method to determine the account that is associated with a particular 
	 * tracker object.
	 * 
	 * @return Account ID this tracker object is instantiated with.
	 */
	public static native String getAccount() /*-{
	  	var pageTracker = $wnd._gat._getTrackerByName(); // Gets the default tracker.
	  	var accountId = pageTracker._getAccount();
		return accountId.toString();
	}-*/;
	
	/**
	 * Returns the name the tracker was given when it was created.
	 * 
	 * @return String The tracker's name.
	 */
	public static native String getName() /*-{
		  var pageTracker = $wnd._gat._getTrackerByName(); // Gets the default tracker.
		  var trackerName = pageTracker._getName();
		  return trackerName.toString();
	}-*/;
	
	/**
	 * Returns the GATC version number.
	 * 
	 * @return String GATC version number.
	 */
	public static native String getVersion() /*-{
		  var pageTracker = $wnd._gat._getTrackerByName(); // Gets the default tracker.
		  var version = pageTracker._getVersion();
		  return version.toString();
	}-*/;	
	
	/**
	 * Sets the web property ID for the tracking object.
	 * @param accountID - The full web property ID (e.g. UA-65432-1) for the tracker object.
	 */
	public static native void setAccount(String accountID) /*-{
		$wnd._gaq.push([ '_setAccount', accountID ]);
	}-*/;
	
	/**
	 * Sets the new sample rate. If your website is particularly large and subject to heavy 
	 * traffic spikes, then setting the sample rate ensures un-interrupted report tracking.
	 * 
	 * @param newRate - New sample rate to set. Provide a numeric string as a whole percentage.
	 */
	public static native void setSampleRate(String newRate) /*-{
		$wnd._gaq.push(['_setSampleRate', newRate]);
	}-*/;	
	
	/**
	 * Defines a new sample set size for Site Speed data collection. By default, a fixed 1% sampling 
	 * of your site visitors make up the data pool from which the Site Speed metrics are derived. 
	 * If you have a relatively small number of daily visitors to your site, such as 100,000 or fewer, 
	 * you might want to adjust the sampling to a larger rate. This will provide increased granularity 
	 * for page load time and other Site Speed metrics.
	 * 
	 * @param sampleRate -	Value between 0 - 100 to define the percentage of visitors to your site that 
	 * 						will be measured for Site Speed purposes. For example, a value of 5 sets the 
	 * 						Site Speed collection sample to 5%.
	 */
	public static native void setSiteSpeedSampleRate(int sampleRate) /*-{
		$wnd._gaq.push(['_setSiteSpeedSampleRate', sampleRate]);
	}-*/;	
	
	/**
	 * Tells Google Analytics to anonymize the information sent by the tracker objects by removing 
	 * the last octet of the IP address prior to its storage. Note that this will slightly reduce 
	 * the accuracy of geographic reporting.
	 */
	public static native void anonymaizeIp() /*-{
		$wnd._gaq.push(['_gat._anonymizeIp']);
	}-*/;	
	
	/**
	 * Main logic for GATC (Google Analytic Tracker Code). 
	 * If linker functionalities are enabled, it attempts to extract cookie values from the URL. 
	 * Otherwise, it tries to extract cookie values from document.cookie. 
	 * It also updates or creates cookies as necessary, then writes them back to the document object. 
	 * Gathers all the appropriate metrics to send to the UCFE (Urchin Collector Front-end).
	 */
	public static native void trackPageview() /*-{
		$wnd._gaq.push([ '_trackPageview' ]);
	}-*/;
	
	/**
	 * Main logic for GATC (Google Analytic Tracker Code). 
	 * If linker functionalities are enabled, it attempts to extract cookie values from the URL. 
	 * Otherwise, it tries to extract cookie values from document.cookie. 
	 * It also updates or creates cookies as necessary, then writes them back to the document object. 
	 * Gathers all the appropriate metrics to send to the UCFE (Urchin Collector Front-end).
	 * 
	 * @param url - indicate what page URL to track metrics under. 
	 * 				When using this option, use a beginning slash (/) to indicate the page URL.
	 */
	public static native void trackPageview(String url) /*-{
		$wnd._gaq.push([ '_trackPageview', url ]);
	}-*/;
	
	/**
	 * Sets a custom variable with the supplied name, value, and scope for the variable. 
	 * There is a 64-byte character limit for the name and value combined.
	 * 
	 * @param index 	- 	The slot used for the custom variable. Possible values are 1-5, inclusive.
	 * @param name 		- 	The name for the custom variable.
	 * @param value 	-  	The value for the custom variable.
	 * @param opt_scope - 	The scope used for the custom variable. 
	 * 						Possible values are 1 for visitor-level, 2 for session-level, 
	 * 						and 3 for page-level.
	 * @return 
	 */
	public static native void setCustomVar(int index, String name, String value, int opt_scope) /*-{
		$wnd._gaq.push(['_setCustomVar', index, name, value, opt_scope]);
	}-*/;
	
	/**
	 * Returns the visitor level custom variable value assigned for the specified index.
	 * 
	 * @param index	- 	The index of the visitor level custom variable.
	 * @return		-	The value of the visitor level custom variable. 
	 * 					Returns undefined if unable to retrieve the variable for the specified index.
	 */
	public static native String getVisitorCustomVar(int index) /*-{
		  var pageTracker = $wnd._gat._getTrackerByName(); // Gets the default tracker.
		  var visitorCustomVar1Value  = pageTracker._getVisitorCustomVar(index);
		  return visitorCustomVar1Value.toString();
	}-*/;

	/**
	 * Sets the new session cookie timeout in milliseconds. By default, session timeout is set to 30 minutes. 
	 * Session timeout is used to compute visits, since a visit ends after 30 minutes of browser 
	 * inactivity or upon browser exit. If you want to change the definition of a "session" for your particular 
	 * needs, you can pass in the number of milliseconds to define a new value. 
	 * This will impact the Visits reports in every section where the number of visits are calculated, 
	 * and where visits are used in computing other values. For example, the number of visits will increase 
	 * if you shorten the session timeout, and will decrease if you increase the session timeout. 
	 * You can change the expiration timeout to 0 to indicate that this cookie should be deleted 
	 * when the browser is closed.
	 * 
	 * @param cookieTimeoutMillis - New session timeout in milliseconds or 0 to delete the cookie 
	 * 								when the browser is closed.
	 */
	public static native void setSessionCookieTimeout(int cookieTimeoutMillis) /*-{
		$wnd._gaq.push(['_setSessionCookieTimeout', cookieTimeoutMillis]);
	}-*/;	
	
	
	//********************************************************************************************
	//
	// EVENT TRACKING
	// 
	// Event Tracking is a method available in the ga.js tracking code that you can use to record 
	// user interaction with website elements
	//
	//********************************************************************************************
	
	/**
	 * Track event
	 * @param category 	- 	The name you supply for the group of objects you want to track.
	 * @param action 	- 	A string that is uniquely paired with each category, and commonly used 
	 * 				 		to define the type of user interaction for the web object.
	 */
	public static native void trackEvent(String category, String action) /*-{
		$wnd._gaq.push([ '_trackEvent', category, action]);
	}-*/;	
	
	/**
	 * Track event
	 * @param category 	- 	The name you supply for the group of objects you want to track.
	 * @param action 	- 	A string that is uniquely paired with each category, and commonly used to 
	 * 						define the type of user interaction for the web object.
	 * @param label 	- 	An optional string to provide additional dimensions to the event data.
	 */
	public static native void trackEvent(String category, String action, String label) /*-{
		$wnd._gaq.push([ '_trackEvent', category, action, label]);
	}-*/;		

	/**
	 * Track event
	 * @param category 	- 	The name you supply for the group of objects you want to track.
	 * @param action 	- 	A string that is uniquely paired with each category, and commonly used to 
	 * 						define the type of user interaction for the web object.
	 * @param label 	- 	An optional string to provide additional dimensions to the event data.
	 * @param intArg 	- 	An integer that you can use to provide numerical data about the user event.
	 */
	public static native void trackEvent(String category, String action, String label, int intArg) /*-{
		$wnd._gaq.push([ '_trackEvent', category, action, label, intArg ]);
	}-*/;	
	
	/**
	 * Track Event
	 * @param category 			- 	The name you supply for the group of objects you want to track.
	 * @param action 			- 	A string that is uniquely paired with each category, and commonly used to 
	 * 								define the type of user interaction for the web object.
	 * @param label 			- 	An optional string to provide additional dimensions to the event data.
	 * @param intArg 			- 	An integer that you can use to provide numerical data about the user event.
	 * @param nonInteraction 	- 	A boolean that when set to true, indicates that the event hit 
	 * 								will not be used in bounce-rate calculation.
	 */
	public static native void trackEvent(String category, String action, String label, int intArg, boolean nonInteraction) /*-{
		$wnd._gaq.push([ '_trackEvent', category, action, label, intArg, nonInteraction ]);
	}-*/;	
		
	
	//********************************************************************************************
	//
	// CAMPAIGN TRACKING
	// 
	// methods that you use for setting up and customizing campaign tracking in Google Analytics 
	// reporting.
	//
	//********************************************************************************************
	
	/**
	 * This method sets the # sign as the query string delimiter in campaign tracking. 
	 * This option is set to false by default.
	 * 
	 * @param anchor -	true or false  If this parameter is set to true, then campaign uses anchors. 
	 * 					Otherwise, the campaign uses search strings.
	 */
	public static native void setAllowAnchor(boolean anchor) /*-{
		$wnd._gaq.push([ '_setAllowAnchor', anchor ]);
	}-*/;	
	
	/**
	 * Sets the campaign ad content key. The campaign content key is used to retrieve the ad content 
	 * (description) of your advertising campaign from your campaign URLs. Use this function on the 
	 * landing page defined in your campaign.
	 * 
	 * @param newCampContentKey -	New campaign content key to set.
	 */
	public static native void setCampContentKey(String newCampContentKey) /*-{
		$wnd._gaq.push([ '_setCampContentKey', newCampContentKey ]);
	}-*/;	
	
	/**
	 * Sets the campaign medium key, which is used to retrieve the medium from your campaign URLs. 
	 * The medium appears as a segment option in the Campaigns report. 
	 * 
	 * @param newCampMedKey -	Campaign medium key to set.
	 */
	public static native void setCampMediumKey(String newCampMedKey) /*-{
		$wnd._gaq.push([ '_setCampMediumKey', newCampMedKey ]);
	}-*/;
	
	/**
	 * Sets the campaign name key. The campaign name key is used to retrieve the name of your advertising 
	 * campaign from your campaign URLs. You would use this function on any page that you want to track 
	 * click-campaigns on.
	 * 
	 * @param newCampNameKey -	Campaign name key.
	 */
	public static native void setCampNameKey(String newCampNameKey) /*-{
		$wnd._gaq.push([ '_setCampNameKey', newCampNameKey ]);
	}-*/;
	
	/**
	 * Sets the campaign no-override key variable, which is used to retrieve the campaign no-override 
	 * value from the URL. By default, this variable and its value are not set. For campaign tracking 
	 * and conversion measurement, this means that, by default, the most recent impression is the campaign 
	 * that is credited in your conversion tracking. If you prefer to associate the first-most impressions 
	 * to a conversion, you would set this method to a specific key, and in the situation where you use 
	 * custom campaign variables, you would use this method to set the variable name for campaign overrides. 
	 * The no-override value prevents the campaign data from being over-written by similarly-defined 
	 * campaign URLs that the visitor might also click on. 
	 * 
	 * @param newCampNOKey -	Campaign no-override key to set.
	 */
	public static native void setCampNOKey(String newCampNOKey) /*-{
		$wnd._gaq.push([ '_setCampNOKey', newCampNOKey ]);
	}-*/;
	
	/**
	 * Sets the campaign source key, which is used to retrieve the campaign source from the URL. 
	 * "Source" appears as a segment option in the Campaigns report. 
	 * 
	 * @param newCampSrcKey -	Campaign source key to set.
	 */
	public static native void setCampSourceKey(String newCampSrcKey) /*-{
		$wnd._gaq.push([ '_setCampSourceKey', newCampSrcKey ]);
	}-*/;
	
	/**
	 * Sets the campaign term key, which is used to retrieve the campaign keywords from the URL. 
	 * 
	 * @param newCampTermKey -	Term key to set.
	 */
	public static native void setCampTermKey(String newCampTermKey) /*-{
		$wnd._gaq.push([ '_setCampTermKey', newCampTermKey ]);
	}-*/;	
	
	/**
	 * Sets the campaign tracking flag. By default, campaign tracking is set to true for standard 
	 * Google Analytics set up. If you wish to disable campaign tracking and the associated cookies 
	 * that are set for campaign tracking, you can use this method.
	 * 
	 * @param track - 	true or false True by default, which enables campaign tracking. If set to false, 
	 * 					campaign tracking is disabled.
	 */
	public static native void setCampaignTrack(boolean track) /*-{
		$wnd._gaq.push([ '_setCampaignTrack', track ]);
	}-*/;		
	
	/**
	 * Sets the campaign tracking cookie expiration time in milliseconds. By default, campaign tracking 
	 * is set for 6 months. In this way, you can determine over a 6-month period whether visitors to your 
	 * site convert based on a specific campaign. However, your business might have a longer or shorter 
	 * campaign time-frame, so you can use this method to adjust the campaign tracking for that purpose.
	 * 
	 * @param cookieTimeoutMillis -	New cookie expiration time in milliseconds or 0 to delete the cookie 
	 * 								when the browser is closed.
	 */
	public static native void setCampaignCookieTimeout( int cookieTimeoutMillis ) /*-{
		$wnd._gaq.push([ '_setCampaignCookieTimeout', cookieTimeoutMillis ]);
	}-*/;	
	
	/**
	 * Sets the referrer URL used to determine campaign tracking values. Use this method to allow gadgets 
	 * within an iFrame to track referrals correctly. By default, campaign tracking uses the 
	 * document.referrer property to determine the referrer URL, which is passed in the utmr parameter 
	 * of the GIF request. However, you can over-ride this parameter with your own value.
	 * 
	 * @param newReferrerUrl -	The new url for the document referrer.
	 */
	public static native void setReferrerOverride( String newReferrerUrl ) /*-{
		$wnd._gaq.push([ '_setReferrerOverride', newReferrerUrl ]);
	}-*/;		
	
	//********************************************************************************************
	//
	// DOMAINS AND DIRECTORIES
	// 
	// methods that you use for customizing how Google Analytics reporting works across domains, 
	// across different hosts, or within sub-directories of a website.
	//
	//********************************************************************************************	
	
	/**
	 * Changes the paths of all GATC cookies to the newly-specified path. Use this feature to track 
	 * user behavior from one directory structure to another on the same domain.
	 * 
	 * @param newPath -	New path to store GATC cookies under.
	 */
	public static native void cookiePathCopy( String newPath ) /*-{
		$wnd._gaq.push([ '_cookiePathCopy', newPath ]);
	}-*/;	
	
	/**
	 * This method works in conjunction with the _setDomainName() and _setAllowLinker() methods to 
	 * enable cross-domain user tracking specifically for iFrames and links that open in a new window. 
	 * This method returns a string of all the GATC cookie data from the initiating link by appending 
	 * it to the URL parameter. This can then be passed on to a another site or iFrame.
	 * 
	 * @param targetUrl -	URL of target site to send cookie values to.
	 * @param useHash -		Set to true for passing tracking code variables by using the # anchortag 
	 * 						separator rather than the default ? query string separator.
	 * @return				The linker URL.
	 */
	public static native String getLinkerUrl( String targetUrl, String useHash ) /*-{
		return $wnd._gaq.push([ '_getLinkerUrl', targetUrl, useHash ]).toString();
	}-*/;	
	
	
	/**
	 * This method works in conjunction with the _setDomainName() and _setAllowLinker() methods to enable 
	 * cross-domain user tracking. The _link() method passes the GATC cookies from this site to another via 
	 * URL parameters (HTTP GET). It also changes the document.location and redirects the user to the new URL.
	 * 
	 * @param targetUrl	-	URL of target site to send cookie values to.
	 * @param useHash	-	Set to true for passing tracking code variables by using the # anchortag 
	 * 						separator rather than the default ? query string separator.
	 */
	public static native void link( String targetUrl, String useHash ) /*-{
		$wnd._gaq.push([ '_link', targetUrl, useHash ]);
	}-*/;
	
	
	/**
	 * This method works in conjunction with the _setDomainName() and _setAllowLinker() methods to enable 
	 * cross-domain user tracking. The _linkByPost() method passes the GATC cookies from the referring form 
	 * to another site in a string appended to the action value of the form (HTTP POST). This method is 
	 * typically used when tracking user behavior from one site to a 3rd-party shopping cart site, but can 
	 * also be used to send cookie data to other domains in pop-ups or in iFrames.
	 * 
	 * @param formObject	-	Form object encapsulating the POST request.
	 * @param useHash		-	Set to true for passing tracking code variables by using the # anchortag 
	 * 							separator rather than the default ? query string separator.
	 */
	public static native void linkByPost( String formObject, String useHash ) /*-{
		$wnd._gaq.push([ '_linkByPost', formObject, useHash ]);
	}-*/;	
	
	
	/**
	 * Sets the linker functionality flag as part of enabling cross-domain user tracking. By default, 
	 * this method is set to false and linking is disabled. See also _link(), _linkByPost(), and 
	 * _setDomainName() methods to enable cross-domain tracking. Use the _setAllowLinker method on the 
	 * target site, so that the target site uses the cookie data in the URL parameter, instead of the 
	 * standard session logic.
	 * 
	 * @param allow	-	true or false Default value is false. If this parameter is set to true, 
	 * 					then linker is enabled. Otherwise, domain linking is disabled.
	 */
	public static native void setAllowLinker( boolean allow ) /*-{
		$wnd._gaq.push([ '_setAllowLinker', allow ]);
	}-*/;		
	
	
	/**
	 * Sets the new cookie path for your site. By default, Google Analytics sets the cookie path to the 
	 * root level (/). In most situations, this is the appropriate option and works correctly with the 
	 * tracking code you install on your website, blog, or corporate web directory. However, in a few 
	 * cases where user access is restricted to only a sub-directory of a domain, this method can resolve 
	 * tracking issues by setting a sub-directory as the default path for all tracking.
	 * 
	 * @param newCookiePath	-	New cookie path to set.
	 */
	public static native void setCookiePath( String newCookiePath ) /*-{
		$wnd._gaq.push([ '_setCookiePath', newCookiePath ]);
	}-*/;		
	

	/**
	 * Sets the domain name for the GATC cookies. There are three modes to this method: 
	 * ("auto" | "none" | [domain]). By default, the method is set to auto, which attempts 
	 * to resolve the domain name based on the document.domain property in the DOM.
	 * 
	 * @param newDomainName	-	New default domain name to set.
	 */
	public static native void setDomainName( String newDomainName ) /*-{
		$wnd._gaq.push([ '_setDomainName', newDomainName ]);
	}-*/;
	
	
	//********************************************************************************************
	//
	// ECOMMERCE
	// 
	// methods that you use for ecommerce in Google Analytics reporting. 
	//
	//********************************************************************************************	
	
	/**
	 * Use this method to track items purchased by visitors to your ecommerce site. This method tracks 
	 * individual items by their SKU. This means that the sku parameter is required. 
	 * This method then associates the item to the parent transaction object via the orderId argument.
	 * 
	 * @param orderId	-	Optional Order ID of the transaction to associate with item.
	 * @param sku		-	Required. Item's SKU code.
	 * @param name		-	Required. Product name. Required to see data in the product detail report.
	 * @param category	-	Optional. Product category.
	 * @param price		-	Required. Product price.
	 * @param quantity	-	Required. Purchase quantity.
	 */
	public static native void addItem( String orderId, String sku, String name, String category, String price, String quantity ) /*-{
		$wnd._gaq.push([ '_addItem', orderId, sku, name, category, price, quantity ]);
	}-*/;	
	
	
	/**
	 * Creates a transaction object with the given values. As with _addItem(), this method handles only
	 * transaction tracking and provides no additional ecommerce functionality. Therefore, if the transaction 
	 * is a duplicate of an existing transaction for that session, the old transaction values are over-written 
	 * with the new transaction values. Arguments for this method are matched by position, so be sure to 
	 * supply all parameters, even if some of them have an empty value.
	 * 
	 * @param orderId		-	Required. Internal unique order id number for this transaction.
	 * @param affiliation	-	Optional. Partner or store affiliation (undefined if absent).
	 * @param total			-	Required. Total dollar amount of the transaction.
	 * @param tax			-	Optional. Tax amount of the transaction.
	 * @param shipping		-	Optional. Shipping charge for the transaction.
	 * @param city			-	Optional. City to associate with transaction.
	 * @param state			-	Optional. State to associate with transaction.
	 * @param country		-	Optional. Country to associate with transaction.
	 */
	public static native void addTrans( String orderId, String affiliation, String total, String tax, String shipping, String city, String state, String country ) /*-{
		$wnd._gaq.push([ '_addTrans', orderId, affiliation, total, tax, shipping, city, state, country ]);
	}-*/;	
	
	
	/**
	 * Sends both the transaction and item data to the Google Analytics server. 
	 * This method should be called after _trackPageview(), and used in conjunction with the _addItem() 
	 * and addTrans() methods. It should be called after items and transaction elements have been set up.
	 */
	public static native void trackTrans() /*-{
		$wnd._gaq.push([ '_trackTrans' ]);
	}-*/;		
	
	
	//********************************************************************************************
	//
	// SEARCH ENGINES AND REFERRERS
	// 
	// methods that you use for customizing search engines and referral traffic in 
	// Google Analytics reporting. 
	//
	//********************************************************************************************	
	
	/**
	 * Sets the string as ignored term(s) for Keywords reports. Use this to configure Google Analytics 
	 * to treat certain search terms as direct traffic, such as when users enter your domain name as a 
	 * search term. When you set keywords using this method, the search terms are still included in your 
	 * overall page view counts, but not included as elements in the Keywords reports.
	 * 
	 * @param newIgnoredOrganicKeyword	-	Keyword search terms to treat as direct traffic.
	 */
	public static native void addIgnoredOrganic(String newIgnoredOrganicKeyword) /*-{
		$wnd._gaq.push([ '_addIgnoredOrganic', newIgnoredOrganicKeyword ]);
	}-*/;		
	
	
	/**
	 * Excludes a source as a referring site. Use this option when you want to set certain referring 
	 * links as direct traffic, rather than as referring sites. For example, your company might own 
	 * another domain that you want to track as direct traffic so that it does not show up on the 
	 * "Referring Sites" reports. Requests from excluded referrals are still counted in your overall 
	 * page view count.
	 * 
	 * @param newIgnoredReferrer	-	Referring site to exclude.
	 */
	public static native void addIgnoredRef(String newIgnoredReferrer) /*-{
		$wnd._gaq.push([ '_addIgnoredRef', newIgnoredReferrer ]);
	}-*/;		
	
	
	/**
	 * Adds a search engine to be included as a potential search engine traffic source. 
	 * By default, Google Analytics recognizes a number of common search engines, 
	 * but you can add additional search engine sources to the list.
	 * 
	 * @param newOrganicEngine	-	Engine for new organic source.
	 * @param newOrganicKeyword	-	Keyword name for new organic source.
	 * @param opt_prepend		-	If true prepends the new engine to the beginning of the
	 * 								organic source list. If false adds the new engine to the 
	 * 								end of the list. This parameter's default value is set to false.
	 */
	public static native void addOrganic(String newOrganicEngine, String newOrganicKeyword, boolean opt_prepend) /*-{
		$wnd._gaq.push([ '_addOrganic', newOrganicEngine, newOrganicKeyword, opt_prepend ]);
	}-*/;
	
	
	/**
	 * Clears all strings previously set for exclusion from the Keyword reports.
	 */
	public static native void clearIgnoredOrganic() /*-{
		$wnd._gaq.push([ '_clearIgnoredOrganic' ]);
	}-*/;	
	
	
	/**
	 * Clears all items previously set for exclusion from the Referring Sites report.
	 */
	public static native void clearIgnoredRef() /*-{
		$wnd._gaq.push([ '_clearIgnoredRef' ]);
	}-*/;	
	
	
	/**
	 * Clears all search engines as organic sources. Use this method when you want to define 
	 * a customized search engine ordering precedence.
	 */
	public static native void clearOrganic() /*-{
		$wnd._gaq.push([ '_clearOrganic' ]);
	}-*/;	
	
	
	//********************************************************************************************
	//
	// SOCIAL PLUG-IN ANALYTICS
	// 
	// method that you use for using Analytics on social sharing buttons located on your website pages, 
	// such as Facebook "Like" buttons and Del.icio.us bookmarks. You do not need to use this method 
	// for the Google +1 button, which automatically sends interaction data to your Analytics account.
	//
	//********************************************************************************************	
	
	/**
	 * Constructs and sends the social tracking call to the Google Analytics Tracking Code. 
	 * Use this to record clicks on social sharing buttons on your website other than the Google +1 button 
	 * (for which reporting is pre-configured).
	 * 
	 * @param network		-	The network on which the action occurs (e.g. Facebook, Twitter)
	 * @param socialAction	-	The type of action that happens (e.g. Like, Send, Tweet).
	 * @param opt_target	-	Optional. The text value that indicates the subject of the action; 
	 * 							most typically this is a URL target. If undefined, defaults to document.location.href. 
	 * 							For example, if a user clicks the Facebook Like button for a news article, 
	 * 							the URL for that news article could be sent as the string. 
	 * 							You could also supply any other ID identifying the target, such as an ID from 
	 * 							your content management system. As another example, a user could click the Like 
	 * 							button next to a blog post, in which case no value need be sent for this 
	 * 							parameter because the current document location URL is sent by default.
	 * @param opt_pagePath	-	Optional. The page (by path, not full URL) from which the action occurred. 
	 * 							If undefined, defaults to document.location.pathname plus document.location.search. 
	 * 							You will only need to supply a value for this string if you use virtual 
	 * 							(or custom) page paths for Analytic reporting purposes. When using this 
	 * 							option, use a beginning slash (/) to indicate the page URL.
	 */
	public static native void trackSocial( String network, String socialAction, String opt_target, String opt_pagePath ) /*-{
		$wnd._gaq.push([ '_trackSocial', network, socialAction, opt_target, opt_pagePath ]);
	}-*/;	
	
}
