/**
 * 
 */
package com.charles.task;

import java.util.List;

import android.app.Activity;

import com.aetrion.flickr.activity.Item;
import com.charles.dataprovider.RecentActivitiesDataProvider;

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

	private static final String DLG_MSG = "Get recent activities...";
	
	private IActivityFetchedListener mTaskDoneListener;

	public GetActivitiesTask(Activity activity) {
		super(activity, DLG_MSG);
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
