/**
 * 
 */
package com.charles.actions;

import java.util.List;

import android.app.Activity;
import android.widget.Toast;

import com.aetrion.flickr.activity.Item;
import com.charles.FlickrViewerApplication;
import com.charles.task.GetActivitiesTask;
import com.charles.task.GetActivitiesTask.IActivityFetchedListener;

/**
 * @author charles
 * 
 */
public class GetActivitiesAction extends ActivityAwareAction {

	private IActivityFetchedListener mTaskDoneListener = new IActivityFetchedListener() {

		@Override
		public void onActivityFetched(List<Item> items) {
			Toast.makeText(mActivity, "Activities fetched: " + items.size(),
					Toast.LENGTH_SHORT).show();
		}

	};

	public GetActivitiesAction(Activity activity) {
		super(activity);
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
		GetActivitiesTask task = new GetActivitiesTask(mActivity, mTaskDoneListener);
		task.execute(token);
	}

}
