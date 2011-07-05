package com.charles.event;

import com.aetrion.flickr.photos.Exif;
import com.aetrion.flickr.photos.Photo;

import android.graphics.Bitmap;

import java.util.Collection;

/**
 * Represents the exif listener, it will be used in ViewImageDetailFragment.
 * When got the exif information, the photo itself, that is, the
 * <code>bitmap</code> should also be returned to be shown in an
 * <code>ImageView</code>.
 * 
 * @author charles
 * 
 */
public interface IExifListener {

	/**
	 * After exif inforamtion got
	 * 
	 * @param bitmap
	 *            the photo image bitmap
	 * @param exifs
	 *            the exif information.
	 */
	void onExifInfoFetched(Bitmap bitmap, Photo photo, Collection<Exif> exifs);
}
