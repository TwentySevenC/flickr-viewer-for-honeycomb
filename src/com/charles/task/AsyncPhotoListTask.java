/**
 * 
 */

package com.charles.task;

import com.aetrion.flickr.photos.PhotoList;
import com.charles.R;
import com.charles.dataprovider.IPhotoListDataProvider;
import com.charles.dataprovider.PaginationPhotoListDataProvider;
import com.charles.event.IPhotoListReadyListener;
import com.charles.ui.PhotoListFragment;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

/**
 * Represents the task to fetch the photo list of a user.
 * <p>
 * By default, if no photo list ready listener is specified, we're going to show
 * the photo list into the photo list fragment.
 * 
 * @author charles
 */
public class AsyncPhotoListTask extends AsyncTask<Void, Integer, PhotoList> {

    private static final String DEF_MSG = "Loading photos ...";

    private Context mContext;
    private IPhotoListDataProvider mPhotoListProvider;
    private IPhotoListReadyListener mPhotoListReadyListener;
    private String mDialogMessage;

    private IPhotoListReadyListener mDefaultPhotoReadyListener = new IPhotoListReadyListener() {
        @Override
        public void onPhotoListReady(PhotoList list) {
            if (list == null) {
                Toast.makeText(mContext, "Unable to get photo list",
                        Toast.LENGTH_SHORT).show();
                return;
            }

            PhotoListFragment fragment = new PhotoListFragment(list,
                    (PaginationPhotoListDataProvider) mPhotoListProvider);
            FragmentManager fm = ((Activity) mContext).getFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);

            int stackCount = fm.getBackStackEntryCount();
            for (int i = 0; i < stackCount; i++) {
                fm.popBackStack();
            }
            ft.replace(R.id.main_area, fragment);
            ft.commitAllowingStateLoss();
        }
    };

    private ProgressDialog mDialog;

    public AsyncPhotoListTask(Context context,
            IPhotoListDataProvider photoListProvider,
            IPhotoListReadyListener listener) {
        this(context, photoListProvider, listener, DEF_MSG);
    }

    public AsyncPhotoListTask(Context context,
            IPhotoListDataProvider photoListProvider,
            IPhotoListReadyListener listener, String prompt) {
        this.mContext = context;
        this.mPhotoListProvider = photoListProvider;
        this.mPhotoListReadyListener = listener == null ? mDefaultPhotoReadyListener : listener;
        this.mDialogMessage = prompt == null ? DEF_MSG : prompt;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        mDialog = ProgressDialog.show(mContext, "", mDialogMessage);
        mDialog.setCancelable(true);
        mDialog.setCanceledOnTouchOutside(true);
        mDialog.setOnCancelListener(new OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                AsyncPhotoListTask.this.cancel(true);
            }
        });
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
        if (mDialog != null && mDialog.isShowing()) {
            mDialog.dismiss();
        }
        if (mPhotoListReadyListener != null) {
            mPhotoListReadyListener.onPhotoListReady(result);
        }
    }

}
