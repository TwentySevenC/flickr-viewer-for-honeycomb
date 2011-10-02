/**
 * 
 */
package com.gmail.charleszq.ui;

import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.gmail.charleszq.R;

/**
 * @author charles
 * 
 */
public class AddToPoolDialog extends DialogFragment implements OnClickListener {

	private Button mBeginButton, mCancelButton;

	/**
	 * 
	 */
	public AddToPoolDialog() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		getDialog().setTitle(
				getActivity().getString(R.string.add_photo_to_pool_dlg_title));

		View view = inflater.inflate(R.layout.add_photo_to_pool_dlg, null);
		mBeginButton = (Button) view.findViewById(R.id.begin_btn);
		mCancelButton = (Button) view.findViewById(R.id.cancel_btn);

		mBeginButton.setOnClickListener(this);
		mCancelButton.setOnClickListener(this);

		return view;
	}

	@Override
	public void onClick(View v) {
		if (v == mBeginButton) {

		} else if (v == mCancelButton) {
			this.dismiss();
		}
	}

}
