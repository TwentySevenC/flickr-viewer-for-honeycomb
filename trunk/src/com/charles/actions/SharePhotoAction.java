/**
 * 
 */
package com.charles.actions;

import com.charles.utils.Constants;

import android.app.Activity;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Represents the action to share photos to other applicataions, like twitter,
 * sina weibo, etc.
 * 
 * @author charles
 */
public class SharePhotoAction extends ActivityAwareAction {
	
	private static final String TAG = SharePhotoAction.class.getName();
	private static final String SHARE_PHOTO_FILE_NAME = "share.jpg";

	private Bitmap mPhoto;
	private String mPhotoUrl;

	/**
	 * Constructor.
	 * 
	 * @param photo
	 *            The photo to share
	 * @param url
	 *            the url of this photo.
	 */
	public SharePhotoAction(Activity context,Bitmap photo, String url) {
	    super(context);
		this.mPhoto = photo;
		this.mPhotoUrl = url;
	}

	@Override
	public void execute() {
		File bsRoot = new File(Environment.getExternalStorageDirectory(), Constants.SD_CARD_FOLDER_NAME);
	    if (!bsRoot.exists() && !bsRoot.mkdirs()) {
	      Log.w(TAG, "Couldn't make dir " + bsRoot);
	      return;
	    }

		//save the bitmap to sd card.
		File sharePhotoFile = new File(bsRoot, SHARE_PHOTO_FILE_NAME);
	    sharePhotoFile.delete();
	    FileOutputStream fos = null;
	    try {
	      fos = new FileOutputStream(sharePhotoFile);
	      mPhoto.compress(Bitmap.CompressFormat.JPEG, 100, fos);
	    } catch (FileNotFoundException fnfe) {
	      Log.w(TAG, "Couldn't access file " + sharePhotoFile + " due to " + fnfe);
	      return;
	    } finally {
	      if (fos != null) {
	        try {
	          fos.close();
	        } catch (IOException ioe) {
	        }
	      }
	    }
	    
	    //save the photo url to the clipboard.
	    ClipboardManager cm = (ClipboardManager) mActivity.getSystemService(Context.CLIPBOARD_SERVICE);
	    cm.setText(mPhotoUrl);
	    
	    //send out the intent.
	    Intent intent = new Intent(Intent.ACTION_SEND, Uri.parse("mailto:"));
	    intent.putExtra(Intent.EXTRA_SUBJECT, "Share photo");
	    intent.putExtra(Intent.EXTRA_TEXT, mPhotoUrl);
	    intent.putExtra(Intent.EXTRA_TITLE, mPhotoUrl);
	    intent.putExtra(Intent.EXTRA_STREAM, Uri.parse("file://" + sharePhotoFile.getAbsolutePath()));
	    intent.setType("image/jpeg");
	    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
	    mActivity.startActivity(Intent.createChooser(intent, null));

	}
}
