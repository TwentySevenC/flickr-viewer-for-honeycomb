/**
 * 
 */

package com.gmail.charleszq.dataprovider;

import java.util.ArrayList;
import java.util.List;

import android.util.Log;

import com.aetrion.flickr.Flickr;
import com.aetrion.flickr.activity.ActivityInterface;
import com.aetrion.flickr.activity.Item;
import com.aetrion.flickr.activity.ItemList;
import com.gmail.charleszq.utils.Constants;
import com.gmail.charleszq.utils.FlickrHelper;

/**
 * Represents the data provider for the activities.
 * 
 * @author charles
 */
public class RecentActivitiesDataProvider {

	private static final int PER_PAGE = 20;
	private static final String TAG = RecentActivitiesDataProvider.class
			.getName();

	private String mToken;
	private boolean mOnlyMyPhoto = false;

	/**
	 * The check interval of activities on my photos.
	 */
	private int mMyPhotoInterval = Constants.SERVICE_CHECK_INTERVAL;
	
	/**
	 * The page size.
	 */
	private int mPageSize = -1;

	/**
	 * Constructor.
	 * 
	 * @param token
	 *            the access token
	 */
	public RecentActivitiesDataProvider(String token) {
		this.mToken = token;
	}

	public RecentActivitiesDataProvider(String token, boolean onlyMyPhoto) {
		this(token);
		this.mOnlyMyPhoto = onlyMyPhoto;
	}

	/**
	 * Returns the recent activities, including both the activities of my own
	 * photo, and that on the photos commented by me.
	 * <p>
	 * TODO only support photo item right now, for 'photoset', support later.
	 * 
	 * @return
	 */
	public List<Item> getRecentActivities() {
		Flickr f = FlickrHelper.getInstance().getFlickrAuthed(mToken);
		ActivityInterface ai = f.getActivityInterface();

		List<Item> items = new ArrayList<Item>();
		try {
			if (!mOnlyMyPhoto) {
				ItemList userComments = ai.userComments(getPageSize(), 1);
				if (userComments != null) {
					for (int i = 0; i < userComments.size(); i++) {
						Item item = (Item) userComments.get(i);
						Log.d(TAG, "Activity item type : " + item.getType()); //$NON-NLS-1$
						if ("photo".equals(item.getType())) { //$NON-NLS-1$
							items.add(item);
						}
					}
				}
			}

			String sInterval = String.valueOf(mMyPhotoInterval) + "h"; //$NON-NLS-1$
			if (mMyPhotoInterval == 24) {
				sInterval = "1d"; //$NON-NLS-1$
			}

			ItemList photoComments = ai.userPhotos(getPageSize(), 1, sInterval);
			if (photoComments != null) {
				for (int j = 0; j < photoComments.size(); j++) {
					Item item = (Item) photoComments.get(j);
					Log.d(TAG, "Activity item type : " + item.getType()); //$NON-NLS-1$
					if ("photo".equals(item.getType())) { //$NON-NLS-1$
						items.add(item);
					}
				}
			}

		} catch (Exception e) {
		}
		return items;
	}

	/**
	 * Sets the check interval which indicates that since when we're going to
	 * check activities on my photos.
	 * 
	 * @param interval
	 */
	public void setCheckInterval(int interval) {
		this.mMyPhotoInterval = interval;
	}

	private int getPageSize() {
		return mPageSize == -1 ? PER_PAGE : mPageSize;
	}

	public void setPageSize(int mPageSize) {
		this.mPageSize = mPageSize;
	}
	
	
	
}
