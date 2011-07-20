/**
 * 
 */

package com.charles.task;

import com.aetrion.flickr.Flickr;
import com.aetrion.flickr.photos.comments.CommentsInterface;
import com.charles.R;
import com.charles.utils.FlickrHelper;

import android.app.DialogFragment;
import android.widget.Toast;

/**
 * @author charles
 */
public class WriteCommentTask extends ProgressDialogAsyncTask<String, Integer, Boolean> {

    private String mToken;
    private DialogFragment mDialog;

    public WriteCommentTask(String token, DialogFragment dialog) {
        super(dialog.getActivity(), R.string.adding_comments);
        this.mToken = token;
        mDialog = dialog;
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
        super.onPostExecute(result);
        String msg = mActivity.getResources().getString(R.string.comment_added);
        if (!result) {
            msg = mActivity.getResources().getString(R.string.error_add_comment);
        }
        Toast.makeText(mDialog.getActivity(), msg, Toast.LENGTH_SHORT).show();
        if( result ) {
            mDialog.dismiss();
        }
    }

}
