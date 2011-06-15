package com.charles.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.net.http.AndroidHttpClient;
import android.util.Log;

import com.charles.task.ImageDownloadTask;

public final class ImageUtils {

	private static final String LOG_TAG = "ImageDownloader";

	private static Map<String, SoftReference<Bitmap>> imageCache = new ConcurrentHashMap<String, SoftReference<Bitmap>>(
			20);

	/**
	 * This method must be called in a thread other than UI.
	 * 
	 * @param url
	 * @return
	 */
	public static Bitmap downloadImage(String url) {
		// final int IO_BUFFER_SIZE = 4 * 1024;

		// AndroidHttpClient is not allowed to be used from the main thread
		final HttpClient client = AndroidHttpClient.newInstance("Android");
		final HttpGet getRequest = new HttpGet(url);

		try {
			HttpResponse response = client.execute(getRequest);
			final int statusCode = response.getStatusLine().getStatusCode();
			if (statusCode != HttpStatus.SC_OK) {
				Log.w("ImageDownloader", "Error " + statusCode
						+ " while retrieving bitmap from " + url);
				return null;
			}

			final HttpEntity entity = response.getEntity();
			if (entity != null) {
				InputStream inputStream = null;
				try {
					inputStream = entity.getContent();
					// return BitmapFactory.decodeStream(inputStream);
					// Bug on slow connections, fixed in future release.
					return BitmapFactory.decodeStream(new FlushedInputStream(
							inputStream));
				} finally {
					if (inputStream != null) {
						inputStream.close();
					}
					entity.consumeContent();
				}
			}
		} catch (IOException e) {
			getRequest.abort();
			Log.w(LOG_TAG, "I/O error while retrieving bitmap from " + url, e);
		} catch (IllegalStateException e) {
			getRequest.abort();
			Log.w(LOG_TAG, "Incorrect URL: " + url);
		} catch (Exception e) {
			getRequest.abort();
			Log.w(LOG_TAG, "Error while retrieving bitmap from " + url, e);
		} finally {
			if ((client instanceof AndroidHttpClient)) {
				((AndroidHttpClient) client).close();
			}
		}
		return null;
	}

	public static class DownloadedDrawable extends ColorDrawable {

		private WeakReference<ImageDownloadTask> taskRef;

		public DownloadedDrawable(ImageDownloadTask task) {
			taskRef = new WeakReference<ImageDownloadTask>(task);
		}

		public ImageDownloadTask getBitmapDownloaderTask() {
			if (taskRef != null) {
				return taskRef.get();
			} else {
				return null;
			}
		}
	}

	public static void putToCache(String url, Bitmap bitmap) {
		imageCache.put(url, new SoftReference<Bitmap>(bitmap));
	}

	public static Bitmap getFromCache(String url) {
		if (imageCache.containsKey(url)) {
			return imageCache.get(url).get();
		} else {
			return null;
		}
	}

	/**
	 * Saves the given <code>bitmap</code> into the given <code>destFile</code>
	 * 
	 * @param destFile
	 * @param bitmap
	 * @return <code>true</code> if success, <code>false</code> otherwise.
	 */
	public static boolean saveImageToFile(File destFile, Bitmap bitmap) {
		FileOutputStream fos = null;
		try {
			if (destFile.exists()) {
				destFile.delete();
			}
			fos = new FileOutputStream(destFile);
			bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
		} catch (FileNotFoundException fnfe) {
			return false;
		} finally {
			if (fos != null) {
				try {
					fos.close();
				} catch (IOException ioe) {
				}
			}
		}
		return true;
	}
}
