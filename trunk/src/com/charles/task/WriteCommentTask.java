/**
 * 
 */

package com.charles.task;

import com.aetrion.flickr.Flickr;
import com.aetrion.flickr.photos.comments.CommentsInterface;
import com.charles.utils.FlickrHelper;

import android.app.DialogFragment;
import android.widget.Toast;

/**
 * @author charles
 */
public class WriteCommentTask extends ProgressDialogAsyncTask<String, Integer, Boolean> {

    private static final String MSG = "Adding comment...";

    private String mToken;
    private DialogFragment mDialog;

    public WriteCommentTask(String token, DialogFragment dialog) {
        super(dialog.getActivity(), MSG);
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
        String msg = "Comment added.";
        if (!result) {
            msg = "Error when adding comment";
        }
        Toast.makeText(mDialog.getActivity(), msg, Toast.LENGTH_SHORT).show();
    }

}
