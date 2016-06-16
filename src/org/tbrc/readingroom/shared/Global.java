package org.tbrc.readingroom.shared;

public class Global
{
	// Mutable globals
    public static String dataFolder = "tei";
	// USE THIS ADDRESS FOR DEVELOPMENT ENVIRONMENT!
	//public static String CAD_DOMAIN		= "23.23.5.28";		// AWS SERVER
	public static String dbUrl			= "23.23.5.28";		// AWS SERVER
	// USE THIS ADDRESS FOR PRODUCTION!
	//public static String CAD_DOMAIN		= "localhost";
	public static Boolean usingAndroid = false;

	// Immutable globals
	public static final String version = "0.030";
	public static final String CAD_SERVER_PORT			= "51173";
	
	public static final int LARGE_VIEWPORT_MIN_WIDTH = 1100;
	public static final int MEDIUM_VIEWPORT_MIN_WIDTH = 600;
	
	public static final String KANGYUR_TI	= "བཀའ་འགྱུར།";
	public static final String KANGYUR_WY	= "bka' 'gyur";
	public static final String TENGYUR_TI	= "བསྟན་འགྱུར།";
	public static final String TENGYUR_WY	= "bstan 'gyur";
	
	public static final int LOBBY_INDEX = 0;
	public static final int SECTION_INDEX = 1;
	public static final int READER_INDEX = 2;
	
	public static final int BREADCRUMB_MAX_LEN = 15;
	public static final int MAX_SANSKRIT_NAME_LEN = 30;
	
	public static final String HOME_URL = "http://84000.co";
	public static final String NEWS_URL = "http://84000.co/news";
	public static final String RR_URL = "http://84000.co/reading-room";
	public static final String RESOURCES_URL = "http://84000.co/translator-training";
	public static final String ABOUT_URL = "http://84000.co/about/vision";
	public static final String HELP_URL = "http://84000.co/how-you-can-help/sponsor-a-page";
	
    public static final String MODULE_NAME = "84000";
    public static final String MOD_FOLDER = "/db/84000/modules";
    public static final String NAMESPACE	= "ww";
    public static final int TIMEOUT_SECS	= 15;
	
	//public static final String GA_ACCOUNT = "UA-22773108-1";	// 84000 ID obtained by Seng Kiat Koh
	public static final String GA_ACCOUNT = "UA-64229329-1";	// Corrected "read.84000.co" ID

	// Reader related
	public static final int HIGHLIGHT_SPACING_CHARS = 2000;
	public static final String SEPARATOR_CHARS = " ,.;:()\n";
	public static final int GLOSS_LIST_TOP_MARGIN = 24;
	public static final int GLOSS_LIST_ITEM_MARGIN = 16;
	public static final int GLOSS_LIST_SEPARATOR_HEIGHT = 1;
	public static final int NOTE_LIST_TOP_MARGIN = 24;
	public static final int NOTE_LIST_ITEM_MARGIN = 16;
	public static final int NOTE_LIST_SEPARATOR_HEIGHT = 1;
	
	public static final int TEXT_TITLE_PAGE = 0;
	public static final int TEXT_SUMMARY_PAGE = 1;
	public static final int TEXT_ACK_PAGE = 2;
	public static final int TEXT_INTRO_PAGE = 3;
	public static final int TRANS_START_PAGE = 4;	// Title=0, Summary=1, Ack=2, Intro=3, Trans Part 1=4...
	public static final int NAV_PANEL_WIDTH_LARGE_PORTAL = 330;
	public static final int READER_PANEL_MAX_WIDTH = 1128;
}
