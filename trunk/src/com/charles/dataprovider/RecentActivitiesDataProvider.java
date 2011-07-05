/**
 * 
 */

package com.charles.dataprovider;

import java.util.ArrayList;
import java.util.List;

import android.util.Log;

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
    private static final String TAG = RecentActivitiesDataProvider.class
            .getName();

    private String mToken;
    private boolean mOnlyMyPhoto = false;

    /**
     * Constructor.
     * 
     * @param token the access token
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
     * photo, and that on the photos commented by me. TODO only support photo
     * item right now, for 'photoset', support later.
     * 
     * @return
     */
    public List<Item> getRecentActivities() {
        Flickr f = FlickrHelper.getInstance().getFlickrAuthed(mToken);
        ActivityInterface ai = f.getActivityInterface();

        List<Item> items = new ArrayList<Item>();
        try {
            if (!mOnlyMyPhoto) {
                ItemList userComments = ai.userComments(PER_PAGE, 1);
                if (userComments != null) {
                    for (int i = 0; i < userComments.size(); i++) {
                        Item item = (Item) userComments.get(i);
                        Log.d(TAG, "Activity item type : " + item.getType());
                        if ("photo".equals(item.getType())) {
                            items.add(item);
                        }
                    }
                }
            }
            ItemList photoComments = ai.userPhotos(PER_PAGE, 1, "1d");
            if (photoComments != null) {
                for (int j = 0; j < photoComments.size(); j++) {
                    Item item = (Item) photoComments.get(j);
                    Log.d(TAG, "Activity item type : " + item.getType());
                    if ("photo".equals(item.getType())) {
                        items.add(item);
                    }
                }
            }

        } catch (Exception e) {
        }
        return items;
    }
}
