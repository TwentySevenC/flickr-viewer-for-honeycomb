
package com.gmail.charleszq.task;

import com.aetrion.flickr.photos.Exif;
import com.aetrion.flickr.photos.PhotosInterface;
import com.gmail.charleszq.event.IExifListener;
import com.gmail.charleszq.utils.FlickrHelper;

import android.os.AsyncTask;
import android.util.Log;

import java.util.Collection;

public class GetPhotoExifTask extends
        AsyncTask<String, Integer, Collection<Exif>> {

    private IExifListener mExifListener;

    public GetPhotoExifTask(IExifListener exifListener) {
        this.mExifListener = exifListener;
    }

    @SuppressWarnings("unchecked")
    @Override
    protected Collection<Exif> doInBackground(String... params) {
        if (this.isCancelled()) {
            return null;
        }

        PhotosInterface pi = FlickrHelper.getInstance().getPhotosInterface();
        if (pi == null) {
            return null;
        }

        String photoId = params[0];
        Collection<Exif> exifs = null;
        try {
            exifs = pi.getExif(photoId, FlickrHelper.API_SEC);
        } catch (Exception e) {
            Log.e("GetBigImageAndExifTask", "Error to get exif information: "  //$NON-NLS-1$//$NON-NLS-2$
                    + e.getMessage());
        }

        return exifs;
    }

    @Override
    protected void onPostExecute(Collection<Exif> result) {
        super.onPostExecute(result);
        mExifListener.onExifInfoFetched(result);
    }

}
