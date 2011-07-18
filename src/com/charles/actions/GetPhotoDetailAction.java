/*
 * Created on Jul 5, 2011
 *
 * Copyright (c) Sybase, Inc. 2011   
 * All rights reserved.                                    
 */

package com.charles.actions;

import com.aetrion.flickr.photos.Exif;
import com.aetrion.flickr.photos.Photo;
import com.charles.R;
import com.charles.event.IExifListener;
import com.charles.task.GetBigImageAndExifTask;
import com.charles.ui.ViewImageDetailFragment;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.graphics.Bitmap;

import java.util.Collection;

/**
 * @author qiangz
 */
public class GetPhotoDetailAction extends ActivityAwareAction implements IExifListener {

    private String mPhotoId;

    public GetPhotoDetailAction(Activity activity, String photoId) {
        super(activity);
        this.mPhotoId = photoId;
    }

    /*
     * (non-Javadoc)
     * @see com.charles.actions.IAction#execute()
     */
    @Override
    public void execute() {
        GetBigImageAndExifTask task = new GetBigImageAndExifTask(mActivity, mPhotoId, this);
        task.execute();
    }

    @Override
    public void onExifInfoFetched(Bitmap bitmap, Photo photo, Collection<Exif> exifs) {
        ViewImageDetailFragment fragment = new ViewImageDetailFragment(
                photo, bitmap, exifs);
        FragmentManager fm = mActivity.getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.main_area, fragment);
        ft.addToBackStack("Detail Image"); //$NON-NLS-1$
        ft.commitAllowingStateLoss();
    }

}
