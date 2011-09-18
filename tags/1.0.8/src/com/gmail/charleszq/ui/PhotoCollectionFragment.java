/**
 * 
 */
package com.gmail.charleszq.ui;

import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.gmail.charleszq.FlickrViewerApplication;
import com.gmail.charleszq.R;
import com.gmail.charleszq.ui.comp.UserPhotoCollectionComponent;
import com.gmail.charleszq.utils.Constants;

/**
 * @author charles
 * 
 */
public class PhotoCollectionFragment extends Fragment {

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
		mCollectionComponent.initialize(userId, token);
		return view;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setHasOptionsMenu(true);
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.menu_user_collection, menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_item_back_main_menu:
			FragmentManager fm = getFragmentManager();
			fm.popBackStack(Constants.USER_COLL_BACK_STACK,
					FragmentManager.POP_BACK_STACK_INCLUSIVE);
			return true;
		case R.id.menu_item_refresh_user_col:
			if (mCollectionComponent != null) {
				mCollectionComponent.refreshList();
			}
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

}
