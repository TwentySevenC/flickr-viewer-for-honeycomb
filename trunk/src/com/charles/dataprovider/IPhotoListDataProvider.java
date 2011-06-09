package com.charles.dataprovider;

import com.aetrion.flickr.photos.PhotoList;

/**
 * Represents the data provider to provide a list of photos.
 * 
 * @author charles
 */
public interface IPhotoListDataProvider {

	/**
	 * Returns a list of photos.
	 * @return
	 * @throws Exception
	 */
	PhotoList getPhotoList() throws Exception;
}
