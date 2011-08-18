/**
 * 
 */
package com.gmail.charleszq.dataprovider;

import java.util.HashSet;
import java.util.Set;

import android.content.Context;

import com.aetrion.flickr.Flickr;
import com.aetrion.flickr.groups.pools.PoolsInterface;
import com.aetrion.flickr.photos.Extras;
import com.aetrion.flickr.photos.PhotoList;
import com.aetrion.flickr.photos.PhotoPlace;
import com.aetrion.flickr.photosets.PhotosetsInterface;
import com.gmail.charleszq.R;
import com.gmail.charleszq.ui.comp.UserPhotoCollectionComponent.ListItemAdapterPhotoPlace;
import com.gmail.charleszq.utils.FlickrHelper;

/**
 * Represents the data provider to get the photos from a given photo set or
 * photo group.
 * 
 * @author charles
 * 
 */
public class PhotoPoolDataProvider extends PaginationPhotoListDataProvider {

	/**
	 * auto gened sid.
	 */
	private static final long serialVersionUID = 7813993447701103209L;

	private String mPhotoPlaceId;
	private int mPhotoPlaceKind;
	private String mPhotoPlaceTitle;

	public PhotoPoolDataProvider(PhotoPlace photoPlace) {
		this.mPhotoPlaceId = photoPlace.getId();
		this.mPhotoPlaceKind = photoPlace.getKind();
		this.mPhotoPlaceTitle = photoPlace.getTitle();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.gmail.charleszq.dataprovider.IPhotoListDataProvider#getPhotoList()
	 */
	@Override
	public PhotoList getPhotoList() throws Exception {

		Set<String> extras = new HashSet<String>();
		extras.add(Extras.TAGS);
		extras.add(Extras.GEO);
		extras.add(Extras.OWNER_NAME);

		Flickr f = FlickrHelper.getInstance().getFlickr();
		switch (mPhotoPlaceKind) {
		case PhotoPlace.SET:
			PhotosetsInterface psi = f.getPhotosetsInterface();
			return psi.getPhotos(mPhotoPlaceId, extras,
					Flickr.PRIVACY_LEVEL_NO_FILTER, mPageSize, mPageNumber);
		case PhotoPlace.POOL:
			PoolsInterface gi = f.getPoolsInterface();
			return gi.getPhotos(mPhotoPlaceId, null, extras, mPageSize,
					mPageNumber);
		case ListItemAdapterPhotoPlace.PHOTO_GALLERY:
			return new PhotoList();

		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.gmail.charleszq.dataprovider.PaginationPhotoListDataProvider#
	 * getDescription(android.content.Context)
	 */
	@Override
	public String getDescription(Context context) {
		StringBuilder sb = new StringBuilder();
		if (mPhotoPlaceKind == PhotoPlace.SET) {
			sb.append(context.getString(R.string.dp_photo_pool_desc_set));
		} else {
			sb.append(context.getString(R.string.dp_photo_pool_desc_pool));
		}
		sb.append(" \"").append(mPhotoPlaceTitle); //$NON-NLS-1$
		sb.append("\""); //$NON-NLS-1$
		return sb.toString();
	}

}
