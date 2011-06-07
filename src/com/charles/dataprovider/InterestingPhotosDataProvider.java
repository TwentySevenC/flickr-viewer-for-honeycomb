/**
 * 
 */
package com.charles.dataprovider;

import com.aetrion.flickr.interestingness.InterestingnessInterface;
import com.aetrion.flickr.photos.PhotoList;
import com.charles.utils.FlickrHelper;

/**
 * Represents the data provider for interesting photos.
 * 
 * @author charles
 */
public class InterestingPhotosDataProvider extends
		PaginationPhotoListDataProvider {

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.charles.actions.IPhotoListDataProvider#getPhotoList()
	 */
	@Override
	public PhotoList getPhotoList() throws Exception {
		InterestingnessInterface iif = FlickrHelper.getInstance()
				.getInterestingInterface();
		return iif.getList((String) null, null, mPageSize, mPageNumber);
	}

}
