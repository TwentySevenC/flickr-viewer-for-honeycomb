/**
 * 
 */

package com.gmail.charleszq.task;

import android.app.DialogFragment;
import android.widget.Toast;

import com.gmail.charleszq.R;
import com.gmail.charleszq.utils.FlickrHelper;
import com.gmail.yuyang226.flickr.Flickr;
import com.gmail.yuyang226.flickr.photos.comments.CommentsInterface;

/**
 * @author charles
 */
public class WriteCommentTask extends ProgressDialogAsyncTask<String, Integer, Boolean> {

    private DialogFragment mDialog;

    public WriteCommentTask(DialogFragment dialog) {
        super(dialog.getActivity(), R.string.adding_comments);
        mDialog = dialog;
    }

    @Override
    protected Boolean doInBackground(String... params) {
        String photoId = params[0];
        String comment = params[1];
        String token = params[2];
        String secret = params[3];

        Flickr f = FlickrHelper.getInstance().getFlickrAuthed(token,secret);
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
