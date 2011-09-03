/**
 * 
 */

package com.gmail.charleszq.ui;

import java.net.MalformedURLException;
import java.net.URL;

import android.app.DialogFragment;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.aetrion.flickr.Flickr;
import com.aetrion.flickr.auth.Auth;
import com.aetrion.flickr.auth.AuthInterface;
import com.aetrion.flickr.auth.Permission;
import com.aetrion.flickr.people.User;
import com.gmail.charleszq.FlickrViewerActivity;
import com.gmail.charleszq.FlickrViewerApplication;
import com.gmail.charleszq.R;
import com.gmail.charleszq.actions.IAction;
import com.gmail.charleszq.event.IAuthDoneListener;
import com.gmail.charleszq.task.AuthTask;
import com.gmail.charleszq.utils.FlickrHelper;

/**
 * Represents the auth dialog to grant this application the permission to access
 * user's flickr photos.
 * 
 * @author charles
 */
public class AuthFragmentDialog extends DialogFragment implements
		IAuthDoneListener {

	/**
	 * The oauth interface
	 */
	private AuthInterface mAuthInterface;

	/**
	 * Auth dialog might be brought up in several places if not authed before,
	 * so finish action is that the place where the dialog is brought up, then
	 * after auth, we can continue that action.
	 */
	private IAction mFinishAction;

	private Handler mHandler = new Handler();

	/**
	 * The frob.
	 */
	private String mFrob = null;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		getDialog()
				.setTitle(
						getActivity().getResources().getString(
								R.string.auth_dlg_title));
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
		
		if( result == null ) {
			Toast.makeText(getActivity(), getActivity().getString(R.string.error_no_network), Toast.LENGTH_LONG).show();
			return;
		}
		
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
		} else { // auth done
			Auth auth = (Auth) result;

			FlickrViewerActivity mainActivity = (FlickrViewerActivity) getActivity();
			FlickrViewerApplication app = (FlickrViewerApplication) mainActivity.getApplication();
			
			User user = auth.getUser();
			app.saveFlickrAuthToken(auth.getToken(), user.getId(), user
					.getUsername());

			app.handleContactUploadService();
			app.handlePhotoActivityService();

			// notify main menu panel to update
			MainNavFragment menuFragment = (MainNavFragment) getFragmentManager()
					.findFragmentById(R.id.nav_frg);
			menuFragment.handleUserPanel(menuFragment.getView());
			this.dismiss();

			if (mFinishAction != null) {
				mHandler.post(new Runnable() {
					@Override
					public void run() {
						mFinishAction.execute();
					}
				});
			}

		}
	}

	/**
	 * The button click listener.
	 */
	private OnClickListener mClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			/*Intent intent = new Intent(AuthFragmentDialog.this.getActivity(),
                    OAuthActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra(OAuthActivity.ID_METHOD, OAuthActivity.ID_OAUTH);
            AuthFragmentDialog.this.startActivity(intent);*/
			if (mAuthInterface == null) {
				Flickr f = FlickrHelper.getInstance().getFlickr();
				mAuthInterface = f.getAuthInterface();
			}

			Integer tag = (Integer) v.getTag();
			if (tag == R.id.button_auth) {
				AuthTask task = new AuthTask(AuthTask.TYPE_FROB,
						AuthFragmentDialog.this, mAuthInterface);
				task.execute(""); //$NON-NLS-1$
			} else if (tag == R.id.button_auth_done) {
				if (mFrob == null) {
					Toast.makeText(
							getActivity(),
							getActivity().getResources().getString(
									R.string.toast_click_first_btn),
							Toast.LENGTH_LONG).show();
					return;
				}

				AuthTask authDoneTask = new AuthTask(AuthTask.TYPE_TOKEN,
						AuthFragmentDialog.this, mAuthInterface);
				authDoneTask.execute(mFrob);
			}
		}
	};

	/**
	 * Sets the action after auth.
	 * 
	 * @param action
	 */
	public void setFinishAction(IAction action) {
		this.mFinishAction = action;
	}

}
