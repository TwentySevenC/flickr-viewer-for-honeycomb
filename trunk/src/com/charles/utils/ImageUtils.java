package com.charles.utils;

import com.charles.task.ImageDownloadTask;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.net.http.AndroidHttpClient;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

public final class ImageUtils {

	private static final String LOG_TAG = "ImageDownloader";

	private static Map<String, SoftReference<Bitmap>> imageCache = new ConcurrentHashMap<String, SoftReference<Bitmap>>(
			20);
	
	private static final float ROTATION_ANGLE_MIN = 2.5f;
    private static final float ROTATION_ANGLE_EXTRA = 5.5f;
    private static Random mRandom = new Random();
    private static final float PHOTO_BORDER_WIDTH = 3.0f;
    private static final int PHOTO_BORDER_COLOR = 0x0;
    private static final Paint sPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);
    private static final Paint sStrokePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    
    static {
        sStrokePaint.setStrokeWidth(PHOTO_BORDER_WIDTH);
        sStrokePaint.setStyle(Paint.Style.STROKE);
        sStrokePaint.setColor(PHOTO_BORDER_COLOR);
    }

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
	
	public static Bitmap rotateAndFrame(Bitmap bitmap) {
        final boolean positive = mRandom.nextFloat() >= 0.5f;
        final float angle = (ROTATION_ANGLE_MIN + mRandom.nextFloat() * ROTATION_ANGLE_EXTRA) *
                (positive ? 1.0f : -1.0f);
        final double radAngle = Math.toRadians(angle);

        final int bitmapWidth = bitmap.getWidth();
        final int bitmapHeight = bitmap.getHeight();

        final double cosAngle = Math.abs(Math.cos(radAngle));
        final double sinAngle = Math.abs(Math.sin(radAngle));

        final int strokedWidth = (int) (bitmapWidth + 2 * PHOTO_BORDER_WIDTH);
        final int strokedHeight = (int) (bitmapHeight + 2 * PHOTO_BORDER_WIDTH);

        final int width = (int) (strokedHeight * sinAngle + strokedWidth * cosAngle);
        final int height = (int) (strokedWidth * sinAngle + strokedHeight * cosAngle);

        final float x = (width - bitmapWidth) / 2.0f;
        final float y = (height - bitmapHeight) / 2.0f;

        final Bitmap decored = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        final Canvas canvas = new Canvas(decored);

        canvas.rotate(angle, width / 2.0f, height / 2.0f);
        canvas.drawBitmap(bitmap, x, y, sPaint);
        canvas.drawRect(x, y, x + bitmapWidth, y + bitmapHeight, sStrokePaint);

        return decored;
    }
}
