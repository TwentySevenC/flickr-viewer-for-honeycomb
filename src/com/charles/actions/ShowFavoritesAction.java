/**
 * 
 */
package com.charles.actions;

import android.app.Activity;

import com.charles.FlickrViewerApplication;
import com.charles.R;
import com.charles.dataprovider.FavoritePhotosDataProvider;
import com.charles.dataprovider.PaginationPhotoListDataProvider;
import com.charles.task.AsyncPhotoListTask;

/**
 * @author charles
 * 
 */
public class ShowFavoritesAction extends ActivityAwareAction {

	private String mUserId;

	/**
	 * @param activity
	 */
	public ShowFavoritesAction(Activity activity, String userId) {
		super(activity);
		this.mUserId = userId;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.charles.actions.IAction#execute()
	 */
	@Override
	public void execute() {
		FlickrViewerApplication app = (FlickrViewerApplication) mActivity
				.getApplication();
		String token = app.getFlickrToken();
		if (mUserId == null) {
			mUserId = app.getUserId();
		}
		PaginationPhotoListDataProvider dp = new FavoritePhotosDataProvider(
				mUserId, token);
		AsyncPhotoListTask task = new AsyncPhotoListTask(mActivity, dp, null,
				mActivity.getResources().getString(R.string.task_loading_favs));
		task.execute();
	}

}
