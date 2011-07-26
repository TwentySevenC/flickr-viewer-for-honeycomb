/**
 * 
 */
package com.gmail.charleszq.task;

import com.aetrion.flickr.Flickr;
import com.aetrion.flickr.favorites.FavoritesInterface;
import com.gmail.charleszq.FlickrViewerApplication;
import com.gmail.charleszq.R;
import com.gmail.charleszq.utils.FlickrHelper;

import android.app.Activity;
import android.widget.Toast;

/**
 * Represents the task to add a photo to my favorite photo list.
 * 
 * @author charles
 * 
 */
public class AddPhotoAsFavoriteTask extends
		ProgressDialogAsyncTask<String, Integer, Boolean> {

	/**
	 * Constructor.
	 */
	public AddPhotoAsFavoriteTask(Activity activity) {
		super(activity, R.string.adding_fav);
	}

	@Override
	protected void onPostExecute(Boolean result) {
		super.onPostExecute(result);
		String msg = mActivity.getResources().getString(R.string.fav_added);
		if (!result) {
			msg = mActivity.getResources().getString(R.string.error_add_fav);
		}
		Toast.makeText(mActivity, msg, Toast.LENGTH_SHORT).show();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.os.AsyncTask#doInBackground(Params[])
	 */
	@Override
	protected Boolean doInBackground(String... params) {
		String photoId = params[0];

		FlickrViewerApplication app = (FlickrViewerApplication) mActivity
				.getApplication();
		String token = app.getFlickrToken();

		Flickr f = FlickrHelper.getInstance().getFlickrAuthed(token);
		FavoritesInterface fi = f.getFavoritesInterface();
		try {
			fi.add(photoId);
		} catch (Exception e) {
			return false;
		}
		return true;
	}

}
