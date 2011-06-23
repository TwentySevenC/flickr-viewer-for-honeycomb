/**
 * 
 */
package com.charles.dataprovider;

import java.util.HashSet;
import java.util.Set;

import com.aetrion.flickr.interestingness.InterestingnessInterface;
import com.aetrion.flickr.photos.Extras;
import com.aetrion.flickr.photos.PhotoList;
import com.charles.utils.FlickrHelper;

/**
 * Represents the data provider for interesting photos.
 * 
 * @author charles
 */
public class InterestingPhotosDataProvider extends
		PaginationPhotoListDataProvider {

	/**
	 * auto gen sid
	 */
	private static final long serialVersionUID = -8218008529748537946L;

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.charles.actions.IPhotoListDataProvider#getPhotoList()
	 */
	@Override
	public PhotoList getPhotoList() throws Exception {
		InterestingnessInterface iif = FlickrHelper.getInstance()
				.getInterestingInterface();
		Set<String> extras = new HashSet<String>();
		extras.add(Extras.TAGS);
		extras.add(Extras.GEO);
		extras.add(Extras.OWNER_NAME);
		return iif.getList((String) null, extras, mPageSize, mPageNumber);
	}

	@Override
	public String getDescription() {
		return "Interesting photos";
	}

}
