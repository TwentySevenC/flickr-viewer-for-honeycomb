package com.charles.event;

import java.util.Collection;

import android.graphics.Bitmap;

import com.aetrion.flickr.photos.Exif;

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
	void onExifInfoFetched(Bitmap bitmap, Collection<Exif> exifs);
}
