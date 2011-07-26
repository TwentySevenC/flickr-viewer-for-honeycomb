/*
 * Created on Jul 26, 2011
 *
 * Copyright (c) Sybase, Inc. 2011   
 * All rights reserved.                                    
 */

package com.charles.actions;

import com.aetrion.flickr.Flickr;
import com.aetrion.flickr.favorites.FavoritesInterface;
import com.charles.FlickrViewerApplication;
import com.charles.R;
import com.charles.utils.FlickrHelper;

import android.app.Activity;
import android.os.AsyncTask;
import android.widget.Toast;

/**
 * Represents the action to remove a photo from the favorite list.
 * 
 * @author charles
 */
public class RemoveFavAction extends ActivityAwareAction {

    private String mPhotoId;

    /**
     * @param activity
     */
    public RemoveFavAction(Activity activity, String photoId) {
        super(activity);
        this.mPhotoId = photoId;
    }

    /*
     * (non-Javadoc)
     * @see com.charles.actions.IAction#execute()
     */
    @Override
    public void execute() {
        AsyncTask<String, Integer, Boolean> task = new AsyncTask<String, Integer, Boolean>() {

            @Override
            protected Boolean doInBackground(String... arg0) {
                String photoId = arg0[0];
                FlickrViewerApplication app = (FlickrViewerApplication) mActivity.getApplication();
                String mToken = app.getFlickrToken();
                Flickr f = FlickrHelper.getInstance().getFlickrAuthed(mToken);
                FavoritesInterface fi = f.getFavoritesInterface();
                try {
                    fi.remove(photoId);
                } catch (Exception e) {
                    return false;
                }
                return true;
            }

            @Override
            protected void onPostExecute(Boolean result) {
                super.onPostExecute(result);
                String msg = mActivity.getResources().getString(R.string.remove_fav_done);
                if (!result) {
                    msg = mActivity.getResources().getString(R.string.remove_fav_error);
                }
                Toast.makeText(mActivity, msg, Toast.LENGTH_SHORT).show();
            }
        };

        task.execute(mPhotoId);
    }
}
