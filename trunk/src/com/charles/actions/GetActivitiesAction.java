/**
 * 
 */

package com.charles.actions;

import com.aetrion.flickr.activity.Item;
import com.charles.FlickrViewerApplication;
import com.charles.R;
import com.charles.task.GetActivitiesTask;
import com.charles.task.GetActivitiesTask.IActivityFetchedListener;
import com.charles.ui.RecentActivityFragment;
import com.charles.utils.Constants;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.widget.Toast;

import java.util.List;

/**
 * @author charles
 */
public class GetActivitiesAction extends ActivityAwareAction {

    private IActivityFetchedListener mTaskDoneListener = new IActivityFetchedListener() {

        @Override
        public void onActivityFetched(List<Item> items) {
            if (items.isEmpty()) {
                Toast.makeText(mActivity, "No recent activites.",
                        Toast.LENGTH_SHORT).show();
            } else {
                FragmentManager fm = mActivity.getFragmentManager();
                fm.popBackStack(Constants.ACTIVITY_BACK_STACK,
                        FragmentManager.POP_BACK_STACK_INCLUSIVE);
                FragmentTransaction ft = fm.beginTransaction();

                RecentActivityFragment frag = new RecentActivityFragment(items);
                ft.replace(R.id.main_area, frag);
                ft.addToBackStack(Constants.ACTIVITY_BACK_STACK);
                ft.commitAllowingStateLoss();
            }
        }

    };

    public GetActivitiesAction(Activity activity) {
        super(activity);
    }

    /*
     * (non-Javadoc)
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
