package com.charles.task;

import java.util.Collection;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;

import com.aetrion.flickr.photos.Exif;
import com.aetrion.flickr.photos.Photo;
import com.aetrion.flickr.photos.PhotosInterface;
import com.charles.event.IExifListener;
import com.charles.utils.FlickrHelper;
import com.charles.utils.ImageUtils;

public class GetBigImageAndExifTask extends
		AsyncTask<Void, Integer, Collection<Exif>> {

	private Photo mPhoto;
	private IExifListener mExifListener;
	private Bitmap mDownloadedBitmap;

	public GetBigImageAndExifTask(Photo photo, IExifListener exifListener) {
		mPhoto = photo;
		this.mExifListener = exifListener;
	}

	@SuppressWarnings("unchecked")
	@Override
	protected Collection<Exif> doInBackground(Void... params) {
		if( this.isCancelled() ) {
			return null;
		}
		
		String photoId = mPhoto.getId();
		PhotosInterface pi = FlickrHelper.getInstance().getPhotosInterface();
		if (pi == null) {
			return null;
		}

		Collection<Exif> exifs = null;
		try {
			exifs = pi.getExif(photoId, FlickrHelper.API_SEC);
		} catch (Exception e) {
			Log.e("GetBigImageAndExifTask", "Error to get exif information: "
					+ e.getMessage());
		}
		
		mDownloadedBitmap = ImageUtils.downloadImage(mPhoto.getMediumUrl());
		return exifs;
	}

	@Override
	protected void onPostExecute(Collection<Exif> result) {
		mExifListener.onExifInfoFetched(mDownloadedBitmap,result);
	}
	
	

}
