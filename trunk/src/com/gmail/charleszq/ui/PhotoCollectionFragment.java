/**
 * 
 */
package com.gmail.charleszq.ui;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;

import com.gmail.charleszq.ui.comp.UserPhotoCollectionComponent;

/**
 * @author charles
 * 
 */
public class PhotoCollectionFragment extends Fragment {

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		UserPhotoCollectionComponent comp = new UserPhotoCollectionComponent(
				getActivity());
		comp.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,
				LayoutParams.FILL_PARENT));

		return comp;
	}

}
