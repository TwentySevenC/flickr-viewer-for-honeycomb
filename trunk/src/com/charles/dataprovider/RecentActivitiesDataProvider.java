/**
 * 
 */
package com.charles.dataprovider;

import java.util.ArrayList;
import java.util.List;

import com.aetrion.flickr.Flickr;
import com.aetrion.flickr.activity.ActivityInterface;
import com.aetrion.flickr.activity.Item;
import com.aetrion.flickr.activity.ItemList;
import com.charles.utils.FlickrHelper;

/**
 * Represents the data provider for the activities.
 * 
 * @author charles
 */
public class RecentActivitiesDataProvider {
	
	private static final int PER_PAGE = 10;

	private String mToken;

	/**
	 * Constructor.
	 * 
	 * @param token
	 *            the access token
	 */
	public RecentActivitiesDataProvider(String token) {
		this.mToken = token;
	}

	/**
	 * Returns the recent activities, including both the activities of my own
	 * photo, and that on the photos commented by me.
	 * 
	 * @return
	 */
	public List<Item> getRecentActivities() {
		Flickr f = FlickrHelper.getInstance().getFlickrAuthed(mToken);
		ActivityInterface ai = f.getActivityInterface();
		
		List<Item> items = new ArrayList<Item>();
		try {
			ItemList userComments = ai.userComments(PER_PAGE, 1);
			if(userComments!=null) {
				items.addAll(userComments);
			}
			ItemList photoComments = ai.userPhotos(PER_PAGE, 1, "1d");
			if(photoComments!=null) {
				items.addAll(photoComments);
			}
			
		} catch (Exception e) {
		}
		return items;
	}
}
