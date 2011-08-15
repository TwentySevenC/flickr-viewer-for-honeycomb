/**
 * 
 */
package com.gmail.charleszq.actions;

import android.app.Activity;

import com.gmail.charleszq.FlickrViewerApplication;
import com.gmail.charleszq.R;
import com.gmail.charleszq.dataprovider.PaginationPhotoListDataProvider;
import com.gmail.charleszq.dataprovider.TagSearchPhotoListDataProvider;
import com.gmail.charleszq.task.AsyncPhotoListTask;

/**
 * @author qiangz
 * 
 */
public class TagSearchAction extends ActivityAwareAction {

	private String mTags;

	/**
	 * @param activity
	 */
	public TagSearchAction(Activity activity, String tags) {
		super(activity);
		this.mTags = tags;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.gmail.charleszq.actions.IAction#execute()
	 */
	@Override
	public void execute() {
		FlickrViewerApplication app = (FlickrViewerApplication) mActivity
				.getApplication();
		final PaginationPhotoListDataProvider photoListDataProvider = new TagSearchPhotoListDataProvider(mTags);
		photoListDataProvider.setPageSize(app.getPageSize());
		final AsyncPhotoListTask task = new AsyncPhotoListTask(mActivity,
				photoListDataProvider, null, mActivity.getResources()
						.getString(R.string.task_searching_tags));
		task.execute();
	}

}
