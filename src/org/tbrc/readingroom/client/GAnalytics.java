package org.tbrc.readingroom.client;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.client.Callback;
import com.google.gwt.core.client.ScriptInjector;
import com.google.gwt.core.client.ScriptInjector.FromUrl;

/**
 * GWT - Google Analytics
 * 
 * @author Ayajahmed Shaikh
 * @version 1.0
 */
public class GAnalytics {
	private static final String GOOGLE_ANALYTICS_JAVASCRIPT_URL = "https://www.google-analytics.com/ga.js";

	/**
	 * Constructor is private just to avoid accidental use without
	 * initialization
	 */
	private GAnalytics() {

	}

	/**
	 * This should always be called first to initialize Google Analytics.
	 * 
	 * @param trackingId - Tracking Id of Google Analytics (e.g. UA-XXXXXXXX-X)
	 * @param callback
	 */
	public static void initialize(String trackingId, Callback<Void, Exception> callback) {
		setAnalyticsParams(trackingId);
		Log.info("GAnalytics.initialize calling inject");
		inject(callback);
	}

	/**
	 * Native Method to initialize
	 * @param trackingId
	 * @return
	 */
	private static native String setAnalyticsParams(String trackingId) /*-{
		 $wnd._gaq =  $wnd._gaq || [];
		 $wnd._gaq.push([ '_setAccount', trackingId ]);
		 $wnd._gaq.push([ '_trackPageview' ]);
	}-*/;

	/**
	 * Injects Google Analytics Javascript
	 * @param callback
	 */
	private static void inject(final Callback<Void, Exception> callback) {
		boolean injected = isInjected();
		Log.info("GAnalytics.inject injected = " + injected);
		
		if (! injected) {
			FromUrl fromUrl = ScriptInjector.fromUrl(GOOGLE_ANALYTICS_JAVASCRIPT_URL);
			Log.info("GAnalytics.inject after ScriptInjector.fromUrl");
			
			fromUrl.setWindow(ScriptInjector.TOP_WINDOW);
			Log.info("GAnalytics.inject after fromUrl.setWindow");
			
			fromUrl.setCallback(new Callback<Void, Exception>() {
				public void onFailure(Exception reason) {
					callback.onFailure(reason);
				}

				public void onSuccess(Void result) {
					callback.onSuccess(result);
				}

			});
			Log.info("GAnalytics.inject after fromUrl.setCallback");
			
			fromUrl.inject();
			Log.info("GAnalytics.inject after fromUrl.inject");
		}
	}

	/**
	 * Checks if GA JS is already imported
	 * @return
	 */
	private static native boolean isInjected() /*-{
		return typeof $wnd._gat !== "undefined";
	}-*/;
}
