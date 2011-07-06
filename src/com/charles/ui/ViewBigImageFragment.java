/*
 * Created on Jul 5, 2011
 *
 * Copyright (c) Sybase, Inc. 2011   
 * All rights reserved.                                    
 */

package com.charles.ui;

import com.aetrion.flickr.photos.Photo;
import com.charles.R;
import com.charles.event.IImageDownloadDoneListener;
import com.charles.task.ImageDownloadTask;
import com.charles.task.ImageDownloadTask.ParamType;

import android.app.Fragment;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

/**
 * @author qiangz
 */
public class ViewBigImageFragment extends Fragment implements OnTouchListener,
        IImageDownloadDoneListener {

    private ProgressBar mProgressBar;
    private ImageView mImageView;
    private Photo mPhoto = null;

    public ViewBigImageFragment() {
    }

    public ViewBigImageFragment(Photo photo) {
        this.mPhoto = photo;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.view_big_img, null);
        mProgressBar = (ProgressBar) v.findViewById(R.id.progress);
        mImageView = (ImageView) v.findViewById(R.id.big_image);
        mImageView.setOnTouchListener(this);
        return v;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (mPhoto != null) {
            ImageDownloadTask task = new ImageDownloadTask(mImageView, ParamType.PHOTO_URL, this);
            String url = mPhoto.getLargeUrl();
            task.execute(url);
        } else {
            mProgressBar.setVisibility(View.GONE);
            Toast.makeText(getActivity(), "Unable to get the big image.", Toast.LENGTH_SHORT)
                    .show();
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return false;
    }

    @Override
    public void onImageDownloaded(Bitmap bitmap) {
        if (mProgressBar != null) {
            mProgressBar.setVisibility(View.GONE);
        }
        mImageView.setImageBitmap(bitmap);
    }

}
