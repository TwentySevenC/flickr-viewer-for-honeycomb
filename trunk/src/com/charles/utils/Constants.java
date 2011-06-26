/**
 * 
 */
package com.charles.utils;

/**
 * Represents the class to store constants
 * 
 * @author charles
 */
public final class Constants {
	
	/**
	 * The preference name.
	 */
	public static final String DEF_PREF_NAME = "flickr_viewer";
	
	/**
	 * The folder name stored in the sd card to save temp files of this application.
	 */
	public static final String SD_CARD_FOLDER_NAME = "flickrviewer";
	
	/**
	 * The preference setting keys.
	 */
	public static final String PHOTO_LIST_CACHE_SIZE = "photo.list.cache.size";
	public static final String PHOTO_GRID_COL_COUNT = "photo.grid.col.count";
	public static final String PHOTO_PAGE_SIZE = "photo.grid.page.size";
	
	public static final String FLICKR_TOKEN = "flickr.token";
	public static final String FLICKR_USER_ID = "flickr.user.id";
	public static final String FLICKR_USER_NAME = "flickr.user.name";
	public static final String FLICKR_BUDDY_IMAGE_FILE_NAME = "mybuddyicon.jpg";
	
	/**
	 * The default setting values.
	 */
	public static final int DEF_CACHE_SIZE = 20;
	public static final int DEF_GRID_COL_COUNT = 3;
	public static final int DEF_GRID_PAGE_SIZE = 18;

	
	public static final String PHOTO_LIST_BACK_STACK = "photo.list";
	public static final String SETTING_BACK_STACK = "settings";
	public static final String CONTACT_BACK_STACK = "contacts";

	//notifications
	public static final String CONTACT_UPLOAD_PHOTO_NOTIF_INTENT_ACTION = "INTENT_ACTION_CONTACT_PHOTO_UPLOADED";
	public static final String CONTACT_IDS_WITH_PHOTO_UPLOADED = "BUNDLE_KEY_CONTACTS_WITH_PHOTO_UPLOADED";
	public static final int COTACT_UPLOAD_NOTIF_ID = 1;
	
	/**
	 * Private constructor to prevent this class to be instanced.
	 */
	private Constants() {
	}
}
