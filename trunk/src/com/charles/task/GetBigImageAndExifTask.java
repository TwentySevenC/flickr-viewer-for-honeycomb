
package com.charles.task;

import com.aetrion.flickr.photos.Exif;
import com.aetrion.flickr.photos.Photo;
import com.aetrion.flickr.photos.PhotosInterface;
import com.charles.R;
import com.charles.event.IExifListener;
import com.charles.utils.FlickrHelper;
import com.charles.utils.ImageUtils;

import android.app.Activity;
import android.graphics.Bitmap;
import android.util.Log;

import java.util.Collection;

public class GetBigImageAndExifTask extends
        ProgressDialogAsyncTask<Void, Integer, Collection<Exif>> {

    private String mPhotoId;
    private Photo mPhoto;
    private IExifListener mExifListener;
    private Bitmap mDownloadedBitmap;

    public GetBigImageAndExifTask(Activity activity, String photoId, IExifListener exifListener) {
        super(activity, R.string.loading_photo_detail);
        mPhotoId = photoId;
        this.mExifListener = exifListener;
    }

    @SuppressWarnings("unchecked")
    @Override
    protected Collection<Exif> doInBackground(Void... params) {
        if (this.isCancelled()) {
            return null;
        }

        PhotosInterface pi = FlickrHelper.getInstance().getPhotosInterface();
        if (pi == null) {
            return null;
        }

        Collection<Exif> exifs = null;
        try {
            exifs = pi.getExif(mPhotoId, FlickrHelper.API_SEC);
        } catch (Exception e) {
            Log.e("GetBigImageAndExifTask", "Error to get exif information: "  //$NON-NLS-1$//$NON-NLS-2$
                    + e.getMessage());
        }

        try {
            mPhoto = pi.getPhoto(mPhotoId);
            mDownloadedBitmap = ImageUtils.downloadImage(mPhoto.getMediumUrl());
        } catch (Exception e) {
        }
        return exifs;
    }

    @Override
    protected void onPostExecute(Collection<Exif> result) {
        super.onPostExecute(result);
        mExifListener.onExifInfoFetched(mDownloadedBitmap, mPhoto, result);
    }

}
