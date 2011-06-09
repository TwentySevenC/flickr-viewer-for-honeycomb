package com.charles.event;

import com.aetrion.flickr.photos.PhotoList;

/**
 * @author charles
 *
 */
public interface IPhotoListReadyListener {

	void onPhotoListReady(PhotoList list);
}
