/**
 * 
 */
package com.gmail.charleszq.ui;

import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ImageView;

import com.gmail.charleszq.FlickrViewerApplication;
import com.gmail.charleszq.R;
import com.gmail.charleszq.ui.comp.UserPhotoCollectionComponent;
import com.gmail.charleszq.utils.Constants;

/**
 * Represents the fragment to show user's galleries, photo sets and photo
 * groups.
 * 
 * @author charles
 */
public class PhotoCollectionFragment extends Fragment implements
		OnClickListener {

	private UserPhotoCollectionComponent mCollectionComponent;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.user_collection_frag, null);

		mCollectionComponent = (UserPhotoCollectionComponent) view
				.findViewById(R.id.user_collection_list);
		FlickrViewerApplication app = (FlickrViewerApplication) getActivity()
				.getApplication();
		String userId = app.getUserId();
		String token = app.getFlickrToken();
		mCollectionComponent.initialize(userId, token, app
				.getFlickrTokenSecrent());

		ImageView btnBack = (ImageView) view.findViewById(R.id.btn_back);
		btnBack.setTag(R.id.btn_back);
		btnBack.setOnClickListener(this);

		ImageView btnRefresh = (ImageView) view.findViewById(R.id.btn_refresh);
		btnRefresh.setTag(R.id.btn_refresh);
		btnRefresh.setOnClickListener(this);

		return view;
	}

	@Override
	public void onClick(View v) {
		Object tag = v.getTag();
		if (tag == null) {
			return;
		}

		Integer nTag = (Integer) tag;
		switch (nTag) {
		case R.id.btn_back:
			FragmentManager fm = getFragmentManager();
			fm.popBackStack(Constants.USER_COLL_BACK_STACK,
					FragmentManager.POP_BACK_STACK_INCLUSIVE);
			break;
		case R.id.btn_refresh:
			if (mCollectionComponent != null) {
				mCollectionComponent.refreshList();
			}
			break;
		}
	}

}
