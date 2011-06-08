/**
 * 
 */
package com.charles.ui;

import java.net.MalformedURLException;
import java.net.URL;

import android.app.DialogFragment;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import com.aetrion.flickr.Flickr;
import com.aetrion.flickr.auth.Auth;
import com.aetrion.flickr.auth.AuthInterface;
import com.aetrion.flickr.auth.Permission;
import com.aetrion.flickr.people.User;
import com.charles.FlickrViewerApplication;
import com.charles.R;
import com.charles.event.IAuthDoneListener;
import com.charles.task.AuthTask;
import com.charles.utils.FlickrHelper;

/**
 * @author charles
 * 
 */
public class AuthFragmentDialog extends DialogFragment implements
		IAuthDoneListener {

	/**
	 * The auth interface
	 */
	private AuthInterface mAuthInterface;

	/**
	 * The frob.
	 */
	private String mFrob = null;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		getDialog().setTitle("Flickr Authorization");
		View view = inflater.inflate(R.layout.auth_dlg, null);

		Button authButton = (Button) view.findViewById(R.id.button_auth);
		authButton.setTag(R.id.button_auth);
		authButton.setOnClickListener(mClickListener);

		Button authDoneButton = (Button) view
				.findViewById(R.id.button_auth_done);
		authDoneButton.setTag(R.id.button_auth_done);
		authDoneButton.setOnClickListener(mClickListener);

		return view;
	}

	@Override
	public void onAuthDone(int type, Object result) {
		if (type == AuthTask.TYPE_FROB) {
			mFrob = result.toString();
			try {
				URL url = mAuthInterface.buildAuthenticationUrl(
						Permission.WRITE, mFrob);
				Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url
						.toExternalForm()));
				getActivity().startActivity(intent);
				return;
			} catch (MalformedURLException e) {

			}
		} else {
			Auth auth = (Auth) result;
			FlickrViewerApplication app = (FlickrViewerApplication) getActivity()
					.getApplication();
			User user = auth.getUser();
			app.saveFlickrAuthToken(auth.getToken(), user.getId(), user
					.getUsername());
			
			//notify main menu panel to update
			//TODO refactor later.
			MainNavFragment menuFragment = (MainNavFragment) getFragmentManager().findFragmentById(R.id.nav_frg);
			menuFragment.handleUserPanel(menuFragment.getView());
			
			this.dismiss();
		}
	}

	/**
	 * The button click listener.
	 */
	private OnClickListener mClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {

			if (mAuthInterface == null) {
				Flickr f = FlickrHelper.getInstance().getFlickr();
				mAuthInterface = f.getAuthInterface();
			}

			Integer tag = (Integer) v.getTag();
			if (tag == R.id.button_auth) {
				AuthTask task = new AuthTask(AuthTask.TYPE_FROB,
						AuthFragmentDialog.this, mAuthInterface);
				task.execute("");
			} else if (tag == R.id.button_auth_done) {
				if (mFrob == null) {
					Toast.makeText(getActivity(),
							"Click the 1st button first please.",
							Toast.LENGTH_LONG).show();
					return;
				}

				AuthTask authDoneTask = new AuthTask(AuthTask.TYPE_TOKEN,
						AuthFragmentDialog.this, mAuthInterface);
				authDoneTask.execute(mFrob);
			}
		}
	};

}
