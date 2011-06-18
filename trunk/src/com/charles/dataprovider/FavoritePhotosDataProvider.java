/**
 * 
 */
package com.charles.dataprovider;

import java.util.HashSet;
import java.util.Set;

import com.aetrion.flickr.Flickr;
import com.aetrion.flickr.favorites.FavoritesInterface;
import com.aetrion.flickr.photos.Extras;
import com.aetrion.flickr.photos.PhotoList;
import com.charles.utils.FlickrHelper;

/**
 * Represents the data provider to get all the favorite photos of a given user.
 * 
 * @author charles
 * 
 */
public class FavoritePhotosDataProvider extends
		PaginationPhotoListDataProvider {

	/**
	 * auto gen sid.
	 */
	private static final long serialVersionUID = -3266731748865760819L;
	
	private String mUserId;
	private String mToken;
	/**
	 * Constructor. 
	 */
	public FavoritePhotosDataProvider(String userId, String token) {
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
		FavoritesInterface fi = f.getFavoritesInterface();
		Set<String> extras = new HashSet<String>();
		extras.add(Extras.TAGS);
		extras.add(Extras.GEO);
		extras.add(Extras.OWNER_NAME);
		return fi.getList(mUserId, this.mPageSize, this.mPageNumber, extras);
	}

}
