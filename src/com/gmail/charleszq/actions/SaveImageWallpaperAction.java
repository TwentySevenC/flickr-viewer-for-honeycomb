/**
 * 
 */
package com.gmail.charleszq.actions;

import com.aetrion.flickr.photos.Photo;
import com.gmail.charleszq.R;
import com.gmail.charleszq.event.IImageDownloadDoneListener;
import com.gmail.charleszq.task.ImageDownloadTask;
import com.gmail.charleszq.task.ImageDownloadTask.ParamType;
import com.gmail.charleszq.utils.Constants;
import com.gmail.charleszq.utils.ImageUtils;

import android.app.Activity;
import android.app.WallpaperManager;
import android.graphics.Bitmap;
import android.os.Environment;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * Represents the action to save a photo to sd card, and after that to set it as
 * the wallpaper if <code>mSetAsWallpaper</code> is <code>true</code>.
 * 
 * @author charles
 * 
 */
public class SaveImageWallpaperAction extends ActivityAwareAction implements
		IImageDownloadDoneListener {

	private boolean mSetAsWallpaper = false;
	private Photo mCurrentPhoto;

	/**
	 * @param activity
	 */
	public SaveImageWallpaperAction(Activity activity, Photo photo) {
		super(activity);
		this.mCurrentPhoto = photo;
	}

	public SaveImageWallpaperAction(Activity activity, Photo photo,
			boolean setAsWallpaper) {
		this(activity, photo);
		this.mSetAsWallpaper = setAsWallpaper;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.gmail.charleszq.actions.IAction#execute()
	 */
	@Override
	public void execute() {

		File photoFile = getBitmapFile();
		if (photoFile.exists()) {
			if (mSetAsWallpaper) {
				WallpaperManager wmgr = WallpaperManager.getInstance(mActivity);
				try {
					FileInputStream fis = new FileInputStream(photoFile);
					wmgr.setStream(fis);
				} catch (IOException e) {
				}
			} else {
				Toast.makeText(
						mActivity,
						mActivity.getResources().getString(
								R.string.toast_photo_already_saved),
						Toast.LENGTH_SHORT).show();
			}
		} else {
			ImageDownloadTask task = new ImageDownloadTask(null,
					ParamType.PHOTO_URL, this);
			task.execute(mCurrentPhoto.getLargeUrl());
		}

	}

	private File getBitmapFile() {
		File root = new File(Environment.getExternalStorageDirectory(),
				Constants.SD_CARD_FOLDER_NAME);
		File saveFile = new File(root, mCurrentPhoto.getId() + ".jpg"); //$NON-NLS-1$
		return saveFile;
	}

	@Override
	public void onImageDownloaded(Bitmap bitmap) {
		File photoFile = getBitmapFile();
		boolean saved = ImageUtils.saveImageToFile(photoFile, bitmap);
		if (saved) {
			if (mSetAsWallpaper) {
				WallpaperManager wmgr = WallpaperManager.getInstance(mActivity);
				try {
					wmgr.setBitmap(bitmap);
					Toast.makeText(
							mActivity,
							mActivity.getResources().getString(
									R.string.toast_wallpaper_changed),
							Toast.LENGTH_SHORT).show();
				} catch (IOException e) {
					Toast.makeText(
							mActivity,
							mActivity.getResources().getString(
									R.string.toast_error_set_wallpaper),
							Toast.LENGTH_SHORT).show();
				}
			} else {
				Toast
						.makeText(
								mActivity,
								mActivity.getResources().getString(
										R.string.toast_photo_saved),
								Toast.LENGTH_SHORT).show();
			}
		} else {
			Toast.makeText(
					mActivity,
					mActivity.getResources().getString(
							R.string.toast_error_save_photo),
					Toast.LENGTH_SHORT).show();
		}
	}

}
