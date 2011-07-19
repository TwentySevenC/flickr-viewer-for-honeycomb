/*
 * Created on Jun 8, 2011
 *
 * Copyright (c) Sybase, Inc. 2011   
 * All rights reserved.                                    
 */

package com.charles.task;

import java.lang.ref.WeakReference;

import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

import com.aetrion.flickr.Flickr;
import com.aetrion.flickr.people.PeopleInterface;
import com.aetrion.flickr.people.User;
import com.charles.event.IImageDownloadDoneListener;
import com.charles.event.IUserInfoFetchedListener;
import com.charles.task.ImageDownloadTask.ParamType;
import com.charles.utils.FlickrHelper;
import com.charles.utils.ImageUtils.DownloadedDrawable;

/**
 * Represents the task to get user information, and set the buddy icon to a
 * provided <code>ImageView</code>.
 * 
 * @author qiangz
 */
public class GetUserInfoTask extends AsyncTask<String, Integer, User> {

    private static final String TAG = GetUserInfoTask.class.getSimpleName();

    /**
     * The image view to show the buddy icon.
     */
    private WeakReference<ImageView> mImageViewRef;

    /**
     * The task done listener.
     */
    private IUserInfoFetchedListener mListener;

    /**
     * The image downloaded listener.
     */
    private IImageDownloadDoneListener mImageDownloadedListener;

    /**
     * Constructor.
     * 
     * @param userId the flickr user id.
     */
    public GetUserInfoTask(ImageView imageView,
            IUserInfoFetchedListener listener, IImageDownloadDoneListener imageDownloadedListener) {
        this.mImageViewRef = new WeakReference<ImageView>(imageView);
        this.mListener = listener;
        this.mImageDownloadedListener = imageDownloadedListener;
    }

    @Override
    protected User doInBackground(String... params) {
        String userId = params[0];
        Flickr f = FlickrHelper.getInstance().getFlickr();
        PeopleInterface pi = f.getPeopleInterface();
        try {
            User user = pi.getInfo(userId);
            return user;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(User result) {
        if (result == null) {
            Log.d(TAG, "Unable to get user information."); //$NON-NLS-1$
            return;
        }
        if (mListener != null) {
            mListener.onUserInfoFetched(result);
        }

        String buddyIconUrl = result.getBuddyIconUrl();
        ImageView image = mImageViewRef.get();
        if (image != null) {
            ImageDownloadTask task = new ImageDownloadTask(image, ParamType.PHOTO_URL,
                    mImageDownloadedListener);
            Drawable drawable = new DownloadedDrawable(task);
            image.setImageDrawable(drawable);
            task.execute(buddyIconUrl);
        }
    }
}
