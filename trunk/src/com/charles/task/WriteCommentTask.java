/**
 * 
 */
package com.charles.task;

import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.widget.Toast;

import com.aetrion.flickr.Flickr;
import com.aetrion.flickr.photos.comments.CommentsInterface;
import com.charles.utils.FlickrHelper;

/**
 * @author charles
 *
 */
public class WriteCommentTask extends AsyncTask<String, Integer, Boolean> {
	
	private String mToken; 
	private DialogFragment mDialog;
	private ProgressDialog mProgressDialog;
	
	public WriteCommentTask(String token, DialogFragment dialog ) {
		this.mToken = token;
		mDialog = dialog;
	}
	
	

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		mProgressDialog = ProgressDialog.show(mDialog.getActivity(), "", "Adding comment...");
	}



	@Override
	protected Boolean doInBackground(String... params) {
		String photoId = params[0];
		String comment = params[1];
		
		Flickr f = FlickrHelper.getInstance().getFlickrAuthed(mToken);
		CommentsInterface ci = f.getCommentsInterface();
		try {
			ci.addComment(photoId, comment);
		} catch (Exception e) {
			return false;
		}
		return true;
	}

	@Override
	protected void onPostExecute(Boolean result) {
		mDialog.dismiss();
		if( mProgressDialog != null && mProgressDialog.isShowing() ) {
			mProgressDialog.dismiss();
		}
		String msg = "Comment added.";
		if( !result ) {
			msg = "Error when adding comment";
		}
		Toast.makeText(mDialog.getActivity(), msg, Toast.LENGTH_SHORT).show();
	}
	
	

}
