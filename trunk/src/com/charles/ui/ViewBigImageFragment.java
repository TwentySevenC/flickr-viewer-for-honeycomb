/*
 * Created on Jul 5, 2011
 *
 * Copyright (c) Sybase, Inc. 2011   
 * All rights reserved.                                    
 */

package com.charles.ui;

import android.app.Fragment;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.os.Bundle;
import android.util.FloatMath;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnTouchListener;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.aetrion.flickr.photos.Photo;
import com.charles.R;
import com.charles.actions.SaveImageWallpaperAction;
import com.charles.event.IImageDownloadDoneListener;
import com.charles.task.ImageDownloadTask;
import com.charles.task.ImageDownloadTask.ParamType;

/**
 * @author qiangz
 */
public class ViewBigImageFragment extends Fragment implements OnTouchListener,
		IImageDownloadDoneListener {

	private static final String TAG = ViewBigImageFragment.class.getName();

	private static final int NONE = 0;
	private static final int DRAG = 1;
	private static final int ZOOM = 2;

	private ProgressBar mProgressBar;
	private ImageView mImageView;
	private Photo mPhoto = null;

	private Matrix matrix = new Matrix();
	private Matrix savedMatrix = new Matrix();

	private int mode = NONE;

	// Remember some things for zooming
	private PointF start = new PointF();
	private PointF mid = new PointF();
	private float oldDist = 1f;

	public ViewBigImageFragment() {
	}

	public ViewBigImageFragment(Photo photo) {
		this.mPhoto = photo;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.menu_view_big_image, menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_item_save:
			SaveImageWallpaperAction action = new SaveImageWallpaperAction(
					getActivity(), mPhoto);
			action.execute();
			return true;
		case R.id.menu_item_wallpaper:
			SaveImageWallpaperAction wallAction = new SaveImageWallpaperAction(
					getActivity(), mPhoto, true);
			wallAction.execute();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
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
			ImageDownloadTask task = new ImageDownloadTask(mImageView,
					ParamType.PHOTO_URL, this);
			String url = mPhoto.getLargeUrl();
			task.execute(url);
		} else {
			mProgressBar.setVisibility(View.GONE);
			Toast.makeText(getActivity(), "Unable to get the big image.",
					Toast.LENGTH_SHORT).show();
		}
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		ImageView view = (ImageView) v;

		// Handle touch events here...
		switch (event.getAction() & MotionEvent.ACTION_MASK) {
		case MotionEvent.ACTION_DOWN:
			savedMatrix.set(matrix);
			start.set(event.getX(), event.getY());
			Log.d(TAG, "mode=DRAG");
			mode = DRAG;
			break;
		case MotionEvent.ACTION_POINTER_DOWN:
			oldDist = spacing(event);
			Log.d(TAG, "oldDist=" + oldDist);
			if (oldDist > 10f) {
				savedMatrix.set(matrix);
				midPoint(mid, event);
				mode = ZOOM;
				Log.d(TAG, "mode=ZOOM");
			}
			break;
		case MotionEvent.ACTION_UP:
		case MotionEvent.ACTION_POINTER_UP:
			mode = NONE;
			Log.d(TAG, "mode=NONE");
			break;
		case MotionEvent.ACTION_MOVE:
			if (mode == DRAG) {
				// ...
				matrix.set(savedMatrix);
				matrix.postTranslate(event.getX() - start.x, event.getY()
						- start.y);
			} else if (mode == ZOOM) {
				float newDist = spacing(event);
				Log.d(TAG, "newDist=" + newDist);
				if (newDist > 10f) {
					matrix.set(savedMatrix);
					float scale = newDist / oldDist;
					matrix.postScale(scale, scale, mid.x, mid.y);
				}
			}
			break;
		}

		view.setImageMatrix(matrix);
		return true; // indicate event was handled
	}

	@Override
	public void onImageDownloaded(Bitmap bitmap) {
		if (mProgressBar != null) {
			mProgressBar.setVisibility(View.GONE);
		}
		mImageView.setImageBitmap(bitmap);
	}

	private float spacing(MotionEvent event) {
		float x = event.getX(0) - event.getX(1);
		float y = event.getY(0) - event.getY(1);
		return FloatMath.sqrt(x * x + y * y);
	}

	private void midPoint(PointF point, MotionEvent event) {
		float x = event.getX(0) + event.getX(1);
		float y = event.getY(0) + event.getY(1);
		point.set(x / 2, y / 2);
	}

}
