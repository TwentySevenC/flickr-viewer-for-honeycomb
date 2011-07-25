/*
 * Created on Jul 5, 2011
 *
 * Copyright (c) Sybase, Inc. 2011   
 * All rights reserved.                                    
 */

package com.charles.actions;

import com.aetrion.flickr.photos.Photo;
import com.charles.R;
import com.charles.task.GetPhotoImageTask;
import com.charles.task.GetPhotoImageTask.IPhotoFetchedListener;
import com.charles.ui.ViewImageDetailFragment;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.graphics.Bitmap;

/**
 * @author qiangz
 */
public class GetPhotoDetailAction extends ActivityAwareAction implements IPhotoFetchedListener {

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
        GetPhotoImageTask task = new GetPhotoImageTask(mActivity, this);
        task.execute(mPhotoId);
    }

    @Override
    public void onPhotoFetched(Photo photo, Bitmap bitmap) {
        ViewImageDetailFragment fragment = new ViewImageDetailFragment(
                photo, bitmap);
        FragmentManager fm = mActivity.getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.main_area, fragment);
        ft.addToBackStack("Detail Image"); //$NON-NLS-1$
        ft.commitAllowingStateLoss();
    }
}
