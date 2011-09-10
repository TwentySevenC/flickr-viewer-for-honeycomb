/**
 * 
 */
package com.gmail.charleszq.task;

import java.net.URL;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.gmail.charleszq.FlickrViewerApplication;
import com.gmail.charleszq.utils.Constants;
import com.gmail.charleszq.utils.FlickrHelper;
import com.gmail.yuyang226.flickr.Flickr;
import com.gmail.yuyang226.flickr.auth.Permission;
import com.gmail.yuyang226.flickr.oauth.OAuthToken;

/**
 * Represents the task to start the oauth process.
 * 
 * @author charles
 * 
 */
public class OAuthTask extends AsyncTask<Void, Integer, String> {

	private static final String TAG = OAuthTask.class.getName();

	private static final Uri OAUTH_CALLBACK_URI = Uri.parse(Constants.ID_SCHEME
			+ "://oauth"); //$NON-NLS-1$

	/**
	 * The context.
	 */
	private Context mContext;

	/**
	 * Constructor.
	 * 
	 * @param context
	 */
	public OAuthTask(Context context) {
		super();
		this.mContext = context;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.os.AsyncTask#doInBackground(Params[])
	 */
	@Override
	protected String doInBackground(Void... params) {
		try {
			Flickr f = FlickrHelper.getInstance().getFlickr();
			OAuthToken oauthToken = f.getOAuthInterface().getRequestToken(
					OAUTH_CALLBACK_URI.toString());
			saveTokenSecrent(oauthToken.getOauthTokenSecret());

			Log.d(TAG, "OAuthToken: " + oauthToken); //$NON-NLS-1$
			URL oauthUrl = f.getOAuthInterface().buildAuthenticationUrl(
					Permission.WRITE, oauthToken);
			Log.d(TAG, "OAuth URL: " + oauthUrl); //$NON-NLS-1$
			return oauthUrl.toString();
		} catch (Exception ex) {
			Log.e(TAG, "Error to oauth: " + ex.getMessage()); //$NON-NLS-1$
			return null;
		}
	}

	/**
	 * Saves the oauth token secrent.
	 * 
	 * @param tokenSecret
	 */
	private void saveTokenSecrent(String tokenSecret) {
		Activity act = (Activity) mContext;
		FlickrViewerApplication app = (FlickrViewerApplication) act
				.getApplication();
		app.saveFlickrTokenSecret(tokenSecret);
		Log.d(TAG, "oauth token secrent saved: " + tokenSecret); //$NON-NLS-1$
	}

	@Override
	protected void onPostExecute(String result) {
		if (result != null) {
			mContext.startActivity(new Intent(Intent.ACTION_VIEW, Uri
					.parse(result)));
		}
	}

}
