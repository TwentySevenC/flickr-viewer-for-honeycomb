/**
 * 
 */
package com.charles.task;

import android.os.AsyncTask;
import android.util.Log;

import com.aetrion.flickr.photos.PhotoList;
import com.charles.dataprovider.IPhotoListDataProvider;
import com.charles.event.IPhotoListReadyListener;

/**
 * @author charles
 * 
 */
public class AsyncPhotoListTask extends AsyncTask<Void, Integer, PhotoList> {

	private IPhotoListDataProvider mPhotoListProvider;
	private IPhotoListReadyListener mPhotoListReadyListener;

	public AsyncPhotoListTask(IPhotoListDataProvider photoListProvider) {
		this.mPhotoListProvider = photoListProvider;
	}

	public void setPhotoListReadyListener(IPhotoListReadyListener listener) {
		this.mPhotoListReadyListener = listener;
	}

	@Override
	protected PhotoList doInBackground(Void... params) {
		try {
			return mPhotoListProvider.getPhotoList();
		} catch (Exception e) {
			Log.e("AsyncPhotoListTask", "error to get photo list: "
					+ e.getMessage());
			return null;
		}
	}

	@Override
	protected void onPostExecute(PhotoList result) {
		if (mPhotoListReadyListener != null) {
			mPhotoListReadyListener.onPhotoListReady(result);
		}
	}

}
