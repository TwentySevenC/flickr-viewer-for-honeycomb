/**
 * 
 */
package com.charles.task;

import com.aetrion.flickr.activity.Item;
import com.charles.R;
import com.charles.dataprovider.RecentActivitiesDataProvider;

import android.app.Activity;

import java.util.List;

/**
 * Represents the task to get the rencent activites.
 * <p>
 * Parameter is the token.
 * 
 * @author charles
 * 
 */
public class GetActivitiesTask extends
		ProgressDialogAsyncTask<String, Integer, List<Item>> {

	private IActivityFetchedListener mTaskDoneListener;

	public GetActivitiesTask(Activity activity) {
		super(activity, R.string.loading_recent_act);
	}
	
	public GetActivitiesTask(Activity activity, IActivityFetchedListener listener) {
		this(activity);
		this.mTaskDoneListener = listener;
	}

	@Override
	protected List<Item> doInBackground(String... arg0) {
		String token = arg0[0];
		RecentActivitiesDataProvider dp = new RecentActivitiesDataProvider(token);
		return dp.getRecentActivities();
	}
	
	@Override
	protected void onPostExecute(List<Item> result) {
		super.onPostExecute(result);
		if( mTaskDoneListener != null ) {
			mTaskDoneListener.onActivityFetched(result);
		}
	}

	public static interface IActivityFetchedListener {
		void onActivityFetched(List<Item> items);
	}

}
