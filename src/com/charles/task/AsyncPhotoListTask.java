/**
 * 
 */
package com.charles.task;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.os.AsyncTask;
import android.util.Log;

import com.aetrion.flickr.photos.PhotoList;
import com.charles.dataprovider.IPhotoListDataProvider;
import com.charles.event.IPhotoListReadyListener;

/**
 * Represents the task to fetch the photo list of a user.
 * 
 * @author charles
 * 
 */
public class AsyncPhotoListTask extends AsyncTask<Void, Integer, PhotoList> {

	private static final String DEF_MSG = "Loading photos ...";

	private Context mContext;
	private IPhotoListDataProvider mPhotoListProvider;
	private IPhotoListReadyListener mPhotoListReadyListener;
	private String mDialogMessage;

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
		this.mPhotoListReadyListener = listener;
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
