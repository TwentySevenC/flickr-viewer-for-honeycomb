/*
 * Created on Jul 25, 2011
 *
 * Copyright (c) Sybase, Inc. 2011   
 * All rights reserved.                                    
 */

package com.gmail.charleszq.task;

import android.app.Activity;
import android.graphics.Bitmap;
import android.util.Log;

import com.aetrion.flickr.photos.GeoData;
import com.aetrion.flickr.photos.Photo;
import com.aetrion.flickr.photos.PhotosInterface;
import com.gmail.charleszq.R;
import com.gmail.charleszq.utils.FlickrHelper;
import com.gmail.charleszq.utils.ImageUtils;

/**
 * @author charles
 */
public class GetPhotoImageTask extends
		ProgressDialogAsyncTask<String, Integer, Bitmap> {

	private static final int MSG_ID = R.string.loading_photo_detail;
	private static final String TAG = GetPhotoImageTask.class.getName();
	private Photo mCurrentPhoto;

	public static enum PhotoType {
		SMALL_SQR_URL, SMALL_URL, MEDIUM_URL, LARGE_URL, ORG_URL
	}

	private PhotoType mPhotoType = PhotoType.MEDIUM_URL;
	private IPhotoFetchedListener mPhotoFetchedListener;

	public GetPhotoImageTask(Activity activity, IPhotoFetchedListener listener) {
		super(activity, MSG_ID);
		this.mPhotoFetchedListener = listener;
	}

	public GetPhotoImageTask(Activity activity, PhotoType photoType,
			IPhotoFetchedListener listener) {
		super(activity, MSG_ID);
		mPhotoType = photoType;
		this.mPhotoFetchedListener = listener;
	}

	public GetPhotoImageTask(Activity act, PhotoType photoType,
			IPhotoFetchedListener listener, String msg) {
		super(act, msg);
		mPhotoType = photoType;
		this.mPhotoFetchedListener = listener;
	}

	@Override
	protected Bitmap doInBackground(String... arg0) {

		if (this.isCancelled()) {
			return null;
		}

		PhotosInterface pi = FlickrHelper.getInstance().getPhotosInterface();
		if (pi == null) {
			return null;
		}

		String photoId = arg0[0];

		try {
			mCurrentPhoto = pi.getPhoto(photoId);
			Log.d(TAG, "Photo description: " + mCurrentPhoto.getDescription()); //$NON-NLS-1$
			GeoData geo = mCurrentPhoto.getGeoData();
			if (geo != null) {
				Log.d(TAG, "geo data: " + geo.getLatitude() + ", " //$NON-NLS-1$//$NON-NLS-2$
						+ geo.getLongitude());
			}
			String url = mCurrentPhoto.getMediumUrl();
			switch (mPhotoType) {
			case LARGE_URL:
				url = mCurrentPhoto.getLargeUrl();
				break;
			case SMALL_URL:
				url = mCurrentPhoto.getSmallUrl();
				break;
			case SMALL_SQR_URL:
				url = mCurrentPhoto.getSmallSquareUrl();
				break;
			case MEDIUM_URL:
				url = mCurrentPhoto.getMediumUrl();
				break;
			case ORG_URL:
				url = mCurrentPhoto.getOriginalUrl();
				break;
			}

			// TODO get cached image from sdcard will make the UI look a little
			// strange, research later.
			// File root = new File(Environment.getExternalStorageDirectory(),
			// Constants.SD_CARD_FOLDER_NAME);
			//            File imageFile = new File(root, photoId + ".jpg"); //$NON-NLS-1$

			Bitmap mDownloadedBitmap = null;
			// if (imageFile.exists()) {
			// mDownloadedBitmap = BitmapFactory.decodeStream(new
			// FileInputStream(imageFile));
			// } else {
			mDownloadedBitmap = ImageUtils.downloadImage(url);
			// }
			return mDownloadedBitmap;
		} catch (Exception e) {
			Log.e(TAG, e.getMessage());
		}
		return null;
	}

	@Override
	protected void onPostExecute(Bitmap result) {
		super.onPostExecute(result);
		if (mPhotoFetchedListener != null) {
			mPhotoFetchedListener.onPhotoFetched(mCurrentPhoto, result);
		}
	}

	public static interface IPhotoFetchedListener {
		void onPhotoFetched(Photo photo, Bitmap bitmap);
	}

}
