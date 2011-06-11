/**
 * 
 */
package com.charles.dataprovider;

import com.aetrion.flickr.Flickr;
import com.aetrion.flickr.people.PeopleInterface;
import com.aetrion.flickr.photos.PhotoList;
import com.charles.utils.FlickrHelper;

/**
 * Represents the data provider to get public photos.
 * 
 * @author charles
 * 
 */
public class PeoplePublicPhotosDataProvider extends
		PaginationPhotoListDataProvider {

	private String mUserId;
	private String mToken;

	public PeoplePublicPhotosDataProvider(String userId, String token) {
		this.mUserId = userId;
		this.mToken = token;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.charles.dataprovider.IPhotoListDataProvider#getPhotoList()
	 */
	@Override
	public PhotoList getPhotoList() throws Exception {
		Flickr f = FlickrHelper.getInstance().getFlickrAuthed(mToken);

		PeopleInterface pi = f.getPeopleInterface();
		return pi.getPublicPhotos(mUserId, mPageSize, mPageNumber);
	}

}
