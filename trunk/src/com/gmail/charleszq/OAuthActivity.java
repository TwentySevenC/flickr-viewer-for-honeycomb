/**
 * 
 */
package com.gmail.charleszq;

import java.net.URL;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.aetrion.flickr.Flickr;
import com.aetrion.flickr.auth.Permission;
import com.gmail.charleszq.utils.FlickrHelper;
import com.yuyang226.flickr.oauth.OAuthToken;

/**
 * @author Toby Yu(yuyang226@gmail.com)
 * 
 */
public class OAuthActivity extends Activity {
	private static final String TAG = OAuthActivity.class.getName();
	public static final String ID_OAUTH = "OAuth";
	public static final String ID_METHOD = "method";

	private static final String ID_SCHEME = "flickr-viewer-hd-oauth";
	private static final Uri OAUTH_CALLBACK_URI = Uri.parse(ID_SCHEME
			+ "://oauth");

	// twitter variables
	public static final String USER_TOKEN = "user_token";
	public static final String USER_SECRET = "user_secret";
	public static final String CONSUMER_ID = "consumer_id";
	public static final String CONSUMER_SECRET = "consumer_secret";
	public static final String REQUEST_TOKEN = "request_token";
	public static final String REQUEST_SECRET = "request_secret";

	public static final String PREFS = "MyPrefsFile";

	// private OAuthConsumer mConsumer = null;
	// private OAuthProvider mProvider = null;
	SharedPreferences mSettings;

	/**
	 * 
	 */
	public OAuthActivity() {
		super();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		try {
			super.onCreate(savedInstanceState);
			if (getIntent().hasExtra(ID_METHOD)) {
				String methodId = getIntent().getExtras().getString(ID_METHOD);
				if (ID_OAUTH.equals(methodId)) {
					getIntent().removeExtra(ID_METHOD);
					new AuthTwitterOpenIDTask().execute();
				}
			}
		} catch (Exception e) {
			Log.e(TAG, e.toString(), e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onResume()
	 */
	@Override
	protected void onResume() {
		try {
			super.onResume();
			Uri uri = getIntent().getData();

			if (uri != null && ID_SCHEME.equals(uri.getScheme())) {
				String query = uri.getQuery();
				Log.i(TAG, "Returned Query: " + query);
				String[] data = query.split("&");
				if (data != null && data.length == 2) {
					String oauthToken = data[0]
							.substring(data[0].indexOf("=") + 1);
					String oauthVerifier = data[1].substring(data[1]
							.indexOf("=") + 1);
					System.out.println(oauthVerifier);
				}
			}
		} catch (Exception e) {
			Log.e(TAG, e.toString(), e);
		}
	}

	private class AuthTwitterOpenIDTask extends AsyncTask<Void, Void, String> {
		ProgressDialog authDialog;

		@Override
		protected void onPreExecute() {
			authDialog = ProgressDialog.show(OAuthActivity.this,
					getText(R.string.auth_progress_title),
					"Redirecting to twitter for oauth...", true, // indeterminate
																	// duration
					false); // not cancel-able
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see android.os.AsyncTask#doInBackground(Params[])
		 */
		@Override
		protected String doInBackground(Void... params) {
			Intent i = OAuthActivity.this.getIntent();
			if (i.getData() == null) {
				try {
					Flickr f = FlickrHelper.getInstance().getFlickr();
					OAuthToken oauthToken = f.getOAuthInterface()
							.getRequestToken(OAUTH_CALLBACK_URI.toString());
					Log.i(TAG, "OAuthToken: " + oauthToken);
					URL oauthUrl = f.getOAuthInterface()
							.buildAuthenticationUrl(Permission.WRITE,
									oauthToken);
					Log.i(TAG, "OAuth URL: " + oauthUrl);

					mSettings = OAuthActivity.this.getSharedPreferences(PREFS,
							Context.MODE_PRIVATE);
					// This is really important. If you were able to register
					// your real callback Uri with Twitter, and not some fake
					// Uri
					// like I registered when I wrote this example, you need to
					// send null as the callback Uri in this function call. Then
					// Twitter will correctly process your callback redirection
					/*
					 * saveRequestInformation(mSettings,
					 * getIntent().getExtras().getString(KEY_USER_EMAIL),
					 * mConsumer.getToken(), mConsumer.getTokenSecret(),
					 * target.getTargetAppConsumerId(),
					 * target.getTargetAppConsumerSecret());
					 */
					OAuthActivity.this
							.startActivity(new Intent(Intent.ACTION_VIEW, Uri
									.parse(oauthUrl.toString())));
				} catch (Exception e) {
					Log.e(TAG, e.toString(), e);
					return e.toString();
				}
			}
			return null;
		}

		protected void onPostExecute(String result) {
			authDialog.dismiss();
			if (result != null) {
				Toast.makeText(OAuthActivity.this,
						"Twitter OAuth request failed-> " + result,
						Toast.LENGTH_LONG).show();
			}
			finish();
		}

	}

	/*
	 * private class SaveTwitterTokenTask extends
	 * AsyncTask<UserTargetServiceConfigModel, Void, UserModel> { ProgressDialog
	 * twitterAuthDialog;
	 * 
	 * @Override protected void onPreExecute() { twitterAuthDialog =
	 * ProgressDialog.show(OAuthActivity.this, "Saving",
	 * "Saving twitter oauth token to the server...", true, // indeterminate
	 * duration false); // not cancel-able }
	 * 
	 * (non-Javadoc)
	 * 
	 * @see android.os.AsyncTask#doInBackground(Params[])
	 * 
	 * @Override protected UserModel
	 * doInBackground(UserTargetServiceConfigModel... params) { try {
	 * UserTargetServiceConfigModel targetServiceConfig = params[0];
	 * ClientResource cr = new
	 * ClientResource("http://ebaysocialhub.appspot.com/rest/services");
	 * ISociaHubServicesResource resource =
	 * cr.wrap(ISociaHubServicesResource.class);
	 * //resource.addUserTargetServiceConfig(targetServiceConfig);
	 * 
	 * StringBuffer buf = new StringBuffer();
	 * buf.append(targetServiceConfig.getServiceProviderId()); buf.append("/");
	 * buf.append(targetServiceConfig.getUserEmail()); buf.append("/");
	 * buf.append(targetServiceConfig.getServiceAccessToken()); buf.append("/");
	 * buf.append(targetServiceConfig.getServiceTokenSecret()); buf.append("/");
	 * buf.append(targetServiceConfig.getServiceUserId()); buf.append("/");
	 * buf.append(targetServiceConfig.getServiceUserName());
	 * 
	 * resource.addUserServiceConfig(buf.toString());
	 * 
	 * //
	 * resource.addTwitterTargetServiceConfig(targetServiceConfig.getUserEmail
	 * (), // targetServiceConfig.getServiceAccessToken(),
	 * targetServiceConfig.getServiceTokenSecret());
	 * 
	 * cr = new ClientResource(Login.SERVER_LOCATION); ISociaHubResource service
	 * = cr.wrap(ISociaHubResource.class); return
	 * service.retrieve(targetServiceConfig.getUserEmail()); } catch (Exception
	 * e) { Log.e(TAG, e.toString(), e); } return null; }
	 * 
	 * protected void onPostExecute(UserModel user) { if (twitterAuthDialog !=
	 * null) twitterAuthDialog.dismiss(); if (user != null) {
	 * Toast.makeText(OAuthActivity.this,
	 * "Successfully saved twitter oauth token to the server",
	 * Toast.LENGTH_LONG).show(); Intent uIntent = new
	 * Intent(OAuthActivity.this,UserProfileActivity.class);
	 * uIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
	 * uIntent.putExtra(UserProfileActivity.TAG_USER, user);
	 * OAuthActivity.this.startActivity(uIntent); finish(); } else {
	 * Toast.makeText(OAuthActivity.this,
	 * "Failed saving twitter oauth token to the server",
	 * Toast.LENGTH_LONG).show(); } }
	 * 
	 * }
	 */

}
