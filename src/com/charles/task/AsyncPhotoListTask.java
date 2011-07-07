/**
 * 
 */

package com.charles.task;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.util.Log;
import android.widget.Toast;

import com.aetrion.flickr.photos.PhotoList;
import com.charles.R;
import com.charles.dataprovider.IPhotoListDataProvider;
import com.charles.dataprovider.PaginationPhotoListDataProvider;
import com.charles.event.IPhotoListReadyListener;
import com.charles.ui.PhotoListFragment;
import com.charles.utils.Constants;

/**
 * Represents the task to fetch the photo list of a user.
 * <p>
 * By default, if no photo list ready listener is specified, we're going to show
 * the photo list into the photo list fragment.
 * 
 * @author charles
 */
public class AsyncPhotoListTask extends ProgressDialogAsyncTask<Void, Integer, PhotoList> {

    private static final String DEF_MSG = "Loading photos ...";

    private IPhotoListDataProvider mPhotoListProvider;
    private IPhotoListReadyListener mPhotoListReadyListener;

    private IPhotoListReadyListener mDefaultPhotoReadyListener = new IPhotoListReadyListener() {
        @Override
        public void onPhotoListReady(PhotoList list, boolean cancelled ) {
        	if( cancelled ) {
        		return;
        	}
            if (list == null) {
                Toast.makeText(mActivity, "Unable to get photo list",
                        Toast.LENGTH_SHORT).show();
                return;
            }

            PhotoListFragment fragment = new PhotoListFragment(list,
                    (PaginationPhotoListDataProvider) mPhotoListProvider);
            FragmentManager fm = mActivity.getFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);

            int stackCount = fm.getBackStackEntryCount();
            for (int i = 0; i < stackCount; i++) {
                fm.popBackStack();
            }
            ft.replace(R.id.main_area, fragment);
            ft.addToBackStack(Constants.PHOTO_LIST_BACK_STACK);
            ft.commitAllowingStateLoss();
        }
    };

    public AsyncPhotoListTask(Activity context,
            IPhotoListDataProvider photoListProvider,
            IPhotoListReadyListener listener) {
        this(context, photoListProvider, listener, DEF_MSG);
    }

    public AsyncPhotoListTask(Activity context,
            IPhotoListDataProvider photoListProvider,
            IPhotoListReadyListener listener, String prompt) {
    	super(context,prompt);
        this.mPhotoListProvider = photoListProvider;
        this.mPhotoListReadyListener = listener == null ? mDefaultPhotoReadyListener : listener;
        this.mDialogMessage = prompt == null ? DEF_MSG : prompt;
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
        super.onPostExecute(result);
        if (mPhotoListReadyListener != null) {
            mPhotoListReadyListener.onPhotoListReady(result, false);
        }
    }

	@Override
	protected void onCancelled() {
		super.onCancelled();
		if (mPhotoListReadyListener != null) {
            mPhotoListReadyListener.onPhotoListReady(null, true);
        }
	}
    
    

}
