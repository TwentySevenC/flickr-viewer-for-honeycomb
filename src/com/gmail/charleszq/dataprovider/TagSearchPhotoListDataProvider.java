/**
 * 
 */
package com.gmail.charleszq.dataprovider;

import java.util.HashSet;
import java.util.Set;

import android.content.Context;

import com.aetrion.flickr.photos.Extras;
import com.aetrion.flickr.photos.PhotoList;
import com.aetrion.flickr.photos.PhotosInterface;
import com.aetrion.flickr.photos.SearchParameters;
import com.gmail.charleszq.R;
import com.gmail.charleszq.utils.FlickrHelper;

/**
 * Represents the data provider to search photos according to the tag provided.
 * 
 * @author charles
 */
public class TagSearchPhotoListDataProvider extends
		PaginationPhotoListDataProvider {

	/**
	 * auto gen sid.
	 */
	private static final long serialVersionUID = -3491095189211143857L;

	/**
	 * The tags to be searched.
	 */
	private String mTags;

	/**
	 * Constructor.
	 */
	public TagSearchPhotoListDataProvider(String tags) {
		this.mTags = tags;
		prepareSearchParameter();
	}

	/**
	 * Prepares the search parameter.
	 */
	private SearchParameters prepareSearchParameter() {
		SearchParameters parameter = new SearchParameters();
		String[] tags = mTags.split(" "); //$NON-NLS-1$
		parameter.setTags(tags);

		Set<String> extras = new HashSet<String>();
		extras.add(Extras.OWNER_NAME);
		extras.add(Extras.TAGS);
		extras.add(Extras.GEO);
		parameter.setExtras(extras);

		parameter.setSort(SearchParameters.DATE_POSTED_DESC);
		return parameter;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.gmail.charleszq.dataprovider.IPhotoListDataProvider#getPhotoList()
	 */
	@Override
	public PhotoList getPhotoList() throws Exception {
		PhotosInterface pi = FlickrHelper.getInstance().getPhotosInterface();
		return pi.search(prepareSearchParameter(), mPageSize, mPageNumber);
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
		sb.append(context.getString(R.string.app_title_tags_search_result));
		sb.append(" ").append(mTags); //$NON-NLS-1$
		return sb.toString();
	}

}
