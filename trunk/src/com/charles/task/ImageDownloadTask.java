/**
 * 
 */
package com.charles.task;

import java.lang.ref.WeakReference;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.widget.ImageView;

import com.charles.event.IImageDownloadDoneListener;
import com.charles.utils.ImageCache;
import com.charles.utils.ImageUtils;
import com.charles.utils.ImageUtils.DownloadedDrawable;

/**
 * Represents the image download task which takes an image url as the parameter, after the download,
 * set the bitmap to an associated <code>ImageView</code>.
 * 
 * @author charles
 * 
 */
public class ImageDownloadTask extends AsyncTask<String, Integer, Bitmap> {

	private WeakReference<ImageView> imgRef = null;
	private String mUrl;
	
	/**
	 * The image downloaded listener.
	 */
	private IImageDownloadDoneListener mImageDownloadedListener;

	/**
	 * Constructor.
	 * @param imageView
	 */
	public ImageDownloadTask(ImageView imageView) {
		this(imageView,null);
	}
	
	public ImageDownloadTask(ImageView imageView, IImageDownloadDoneListener listener) {
		this.imgRef = new WeakReference<ImageView>(imageView);
		this.mImageDownloadedListener = listener;
	}
	
	@Override
	protected Bitmap doInBackground(String... params) {
		mUrl = params[0];
		return ImageUtils.downloadImage(mUrl);
	}

	@Override
	protected void onPostExecute(Bitmap result) {
		if (this.isCancelled()) {
			result = null;
		}

		ImageCache.saveToCache(mUrl, result);
		if (imgRef != null) {
			ImageView imageView = imgRef.get();
			ImageDownloadTask bitmapDownloaderTask = getBitmapDownloaderTask(imageView);
			// Change bitmap only if this process is still associated with it
			// Or if we don't use any bitmap to task association
			// (NO_DOWNLOADED_DRAWABLE mode)
			if (this == bitmapDownloaderTask) {
				imageView.setImageBitmap(result);
			}
		}
		
		if(mImageDownloadedListener != null ) {
			mImageDownloadedListener.onImageDownloaded(result);
		}
	}

	public String getUrl() {
		return mUrl;
	}

	/**
	 * @param imageView
	 *            Any imageView
	 * @return Retrieve the currently active download task (if any) associated
	 *         with this imageView. null if there is no such task.
	 */
	private ImageDownloadTask getBitmapDownloaderTask(ImageView imageView) {
		if (imageView != null) {
			Drawable drawable = imageView.getDrawable();
			if (drawable instanceof DownloadedDrawable) {
				DownloadedDrawable downloadedDrawable = (DownloadedDrawable) drawable;
				return downloadedDrawable.getBitmapDownloaderTask();
			}
		}
		return null;
	}
}
