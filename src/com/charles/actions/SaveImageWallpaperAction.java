/**
 * 
 */
package com.charles.actions;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import android.app.Activity;
import android.app.WallpaperManager;
import android.graphics.Bitmap;
import android.os.Environment;
import android.widget.Toast;

import com.aetrion.flickr.photos.Photo;
import com.charles.event.IImageDownloadDoneListener;
import com.charles.task.ImageDownloadTask;
import com.charles.task.ImageDownloadTask.ParamType;
import com.charles.utils.Constants;
import com.charles.utils.ImageUtils;

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
	 * @see com.charles.actions.IAction#execute()
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
				Toast.makeText(mActivity, "Photo already saved.",
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
		File saveFile = new File(root, mCurrentPhoto.getId() + ".jpg");
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
					Toast.makeText(mActivity, "Wallpaper changed.",
							Toast.LENGTH_SHORT).show();
				} catch (IOException e) {
					Toast.makeText(mActivity,
							"Error to set the photo as wallpaper",
							Toast.LENGTH_SHORT).show();
				}
			} else {
				Toast.makeText(mActivity, "Photo saved successfully.",
						Toast.LENGTH_SHORT).show();
			}
		} else {
			Toast.makeText(mActivity, "Error to save the photo",
					Toast.LENGTH_SHORT).show();
		}
	}

}
