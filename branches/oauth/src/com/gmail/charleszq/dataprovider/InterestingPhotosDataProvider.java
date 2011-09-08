/**
 * 
 */
package com.gmail.charleszq.dataprovider;

import com.aetrion.flickr.interestingness.InterestingnessInterface;
import com.aetrion.flickr.photos.Extras;
import com.aetrion.flickr.photos.PhotoList;
import com.gmail.charleszq.R;
import com.gmail.charleszq.utils.FlickrHelper;

import android.content.Context;

import java.util.HashSet;
import java.util.Set;

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
	 * @see com.gmail.charleszq.actions.IPhotoListDataProvider#getPhotoList()
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
	public String getDescription(Context context) {
		return context.getResources().getString(R.string.item_interesting_photo);
	}

}
