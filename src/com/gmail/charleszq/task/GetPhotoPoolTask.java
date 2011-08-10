/**
 * 
 */
package com.gmail.charleszq.task;

import java.util.ArrayList;
import java.util.List;

import android.os.AsyncTask;

import com.aetrion.flickr.Flickr;
import com.aetrion.flickr.photos.PhotoPlace;
import com.aetrion.flickr.photos.PhotosInterface;
import com.gmail.charleszq.utils.FlickrHelper;

/**
 * @author qiangz
 * 
 */
public class GetPhotoPoolTask extends
		AsyncTask<String, Integer, List<PhotoPlace>> {

	private IPhotoPoolListener mListener;

	public GetPhotoPoolTask(IPhotoPoolListener listener) {
		mListener = listener;
	}

	@SuppressWarnings("unchecked")
	@Override
	protected List<PhotoPlace> doInBackground(String... arg0) {
		String photoId = arg0[0];
		Flickr f = FlickrHelper.getInstance().getFlickr();
		if (f != null) {
			PhotosInterface pi = f.getPhotosInterface();
			try {
				return pi.getAllContexts(photoId);
			} catch (Exception e) {
				return null;
			}
		}
		return null;
	}

	@Override
	protected void onPostExecute(List<PhotoPlace> result) {
		if (mListener != null) {
			mListener
					.onPhotoPoolFetched(result == null ? new ArrayList<PhotoPlace>()
							: result);
		}
	}

	/**
	 * Represents the interface.
	 */
	public interface IPhotoPoolListener {
		void onPhotoPoolFetched(List<PhotoPlace> photoPlaces);
	}

}
