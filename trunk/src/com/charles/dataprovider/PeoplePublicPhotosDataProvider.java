/**
 * 
 */
package com.charles.dataprovider;

import java.util.HashSet;
import java.util.Set;

import com.aetrion.flickr.Flickr;
import com.aetrion.flickr.people.PeopleInterface;
import com.aetrion.flickr.photos.Extras;
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

	/**
	 * auto gen sid
	 */
	private static final long serialVersionUID = -1826894885770697192L;

	/**
	 * the flickr user id, whose photos to be fetched. <code>null</code> means
	 * to fetch my own photos.
	 */
	private String mUserId;
	
	/**
	 * The user name.
	 */
	private String mUserName;

	/**
	 * my own auth token. Some photos needs to know who's viewing the photos.
	 */
	private String mToken;

	/**
	 * Constructor.
	 * @param userId
	 * @param token
	 */
	public PeoplePublicPhotosDataProvider(String userId, String token, String userName ) {
		this.mUserId = userId;
		this.mToken = token;
		this.mUserName = userName;
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
		Set<String> extras = new HashSet<String>();
		extras.add(Extras.TAGS);
		extras.add(Extras.GEO);
		extras.add(Extras.OWNER_NAME);
		return pi.getPublicPhotos(mUserId, extras, mPageSize, mPageNumber);
	}

	@Override
	public String getDescription() {
		StringBuilder builder = new StringBuilder("Photostream of ");
		builder.append( mUserName );
		return builder.toString();
	}

}
