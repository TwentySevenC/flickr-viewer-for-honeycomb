/**
 * 
 */
package com.charles.task;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.os.AsyncTask;

/**
 * @author charles
 * 
 */
public abstract class ProgressDialogAsyncTask<Params, Progress, Result> extends
		AsyncTask<Params, Progress, Result>{
	
	protected ProgressDialog mDialog;
	protected Activity mActivity;
	protected String mDialogMessage;
	
	public ProgressDialogAsyncTask(Activity activity, String msg) {
		this.mActivity = activity;
		this.mDialogMessage = msg;
	}
	
	public ProgressDialogAsyncTask(Activity activity, int msgResId ) {
	    this.mActivity = activity;
	    this.mDialogMessage = activity.getResources().getString(msgResId);
	}

	@Override
	protected void onPostExecute(Result result) {
		if( mDialog != null && mDialog.isShowing() ) {
			mDialog.dismiss();
		}
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		mDialog = ProgressDialog.show(mActivity, "", mDialogMessage); //$NON-NLS-1$
        mDialog.setCancelable(true);
        mDialog.setCanceledOnTouchOutside(true);
        mDialog.setOnCancelListener(new OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
            	ProgressDialogAsyncTask.this.cancel(true);
            }
        });
	}
}
