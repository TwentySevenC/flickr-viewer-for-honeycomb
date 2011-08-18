/**
 * 
 */
package com.gmail.charleszq.ui;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
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

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.user_collection_frag, null);

		UserPhotoCollectionComponent comp = (UserPhotoCollectionComponent) view
				.findViewById(R.id.user_collection_list);
		FlickrViewerApplication app = (FlickrViewerApplication) getActivity()
				.getApplication();
		String userId = app.getUserId();
		String token = app.getFlickrToken();
		comp.initialize(userId, token);
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
		if (item.getItemId() == R.id.menu_item_back_main_menu) {
			FragmentManager fm = getFragmentManager();
			FragmentTransaction ft = fm.beginTransaction();

			MainNavFragment menu = new MainNavFragment();
			ft.replace(R.id.nav_frg, menu);
			ft.addToBackStack(Constants.MAIN_MENU_BACK_STACK);
			ft.commit();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

}
