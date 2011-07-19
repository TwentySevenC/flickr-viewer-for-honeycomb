/**
 * 
 */
package com.charles.dataprovider;

import com.charles.utils.Constants;

import android.content.Context;

/**
 * Represents the photo list data provider which has the pagination feature.
 * 
 * @author charles
 * 
 */
public abstract class PaginationPhotoListDataProvider implements
		IPhotoListDataProvider {

	/**
	 * auto generated sid.
	 */
	private static final long serialVersionUID = 4702763028164978288L;
	
	protected int mPageSize = Constants.DEF_GRID_PAGE_SIZE;
	protected int mPageNumber = 1;

	public void setPageSize(int mPageSize) {
		this.mPageSize = mPageSize;
	}

	public void setPageNumber(int mPageNumber) {
		this.mPageNumber = mPageNumber;
	}
	
	/**
	 * @return
	 */
	public abstract String getDescription(Context context);
}
