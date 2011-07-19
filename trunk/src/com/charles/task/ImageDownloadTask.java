/**
 * 
 */

package com.charles.task;

import com.aetrion.flickr.Flickr;
import com.aetrion.flickr.photos.Photo;
import com.aetrion.flickr.photos.PhotosInterface;
import com.charles.event.IImageDownloadDoneListener;
import com.charles.utils.FlickrHelper;
import com.charles.utils.ImageCache;
import com.charles.utils.ImageUtils;
import com.charles.utils.ImageUtils.DownloadedDrawable;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

import java.lang.ref.WeakReference;

/**
 * Represents the image download task which takes an image url as the parameter,
 * after the download, set the bitmap to an associated <code>ImageView</code>.
 * 
 * @author charles
 */
public class ImageDownloadTask extends AsyncTask<String, Integer, Bitmap> {

    private static final String TAG = ImageDownloadTask.class.getName();
    private WeakReference<ImageView> imgRef = null;
    private String mUrl;

    public static enum ParamType {
        PHOTO_URL, PHOTO_ID_SMALL, PHOTO_ID_SMALL_SQUARE, PHOTO_ID_MEDIUM, PHOTO_ID_LARGE
    };

    /**
     * The parameter type to say whether the passed in parameter is the url, or
     * just photo id.
     */
    private ParamType mParamType = ParamType.PHOTO_URL;

    /**
     * The image downloaded listener.
     */
    private IImageDownloadDoneListener mImageDownloadedListener;

    /**
     * Constructor.
     * 
     * @param imageView
     */
    public ImageDownloadTask(ImageView imageView) {
        this(imageView, ParamType.PHOTO_URL, null);
    }

    /**
     * Constructor.
     * 
     * @param imageView
     */
    public ImageDownloadTask(ImageView imageView, ParamType paramType) {
        this(imageView, paramType, null);
    }

    public ImageDownloadTask(ImageView imageView, ParamType paramType,
            IImageDownloadDoneListener listener) {
        this.imgRef = new WeakReference<ImageView>(imageView);
        this.mParamType = paramType;
        this.mImageDownloadedListener = listener;
    }

    @Override
    protected Bitmap doInBackground(String... params) {
        mUrl = params[0];
        String url = mUrl;
        if (!mParamType.equals(ParamType.PHOTO_URL)) {
            Flickr f = FlickrHelper.getInstance().getFlickr();
            PhotosInterface pi = f.getPhotosInterface();
            try {
                Photo photo = pi.getPhoto(mUrl);
                if (mParamType.equals(ParamType.PHOTO_ID_SMALL_SQUARE)) {
                    url = photo.getSmallSquareUrl();
                } else {
                	// TODO other url types.
                	url = null;
                }
            } catch (Exception e) {
                Log.e(TAG, "Unable to get the photo detail information: " + e.getMessage()); //$NON-NLS-1$
                return null;
            }
        }
        if( url == null ) {
        	return null;
        }
        return ImageUtils.downloadImage(url);
    }

    @Override
    protected void onPostExecute(Bitmap result) {
        if (this.isCancelled()) {
            result = null;
            return;
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

        if (mImageDownloadedListener != null) {
            mImageDownloadedListener.onImageDownloaded(result);
        }
    }

    /**
     * This method name should be changed later, for sometimes, it will return photo id.
     * @return
     */
    public String getUrl() {
        return mUrl;
    }

    /**
     * @param imageView Any imageView
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
