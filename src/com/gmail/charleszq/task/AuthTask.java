/**
 * 
 */
package com.gmail.charleszq.task;

import android.os.AsyncTask;

import com.gmail.charleszq.event.IAuthDoneListener;
import com.gmail.yuyang226.flickr.oauth.OAuthInterface;

/**
 * @author charles
 * 
 */
public class AuthTask extends AsyncTask<String, Integer, Object> {

	public static final int TYPE_FROB = 0;
	public static final int TYPE_TOKEN = 1;

	private int mTaskType = TYPE_FROB;
	private IAuthDoneListener mListener;
	private OAuthInterface mAuthInterface;

	public AuthTask(int type, IAuthDoneListener listener,
			OAuthInterface authInterface) {
		this.mTaskType = type;
		this.mListener = listener;
		this.mAuthInterface = authInterface;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.os.AsyncTask#doInBackground(Params[])
	 */
	@Override
	protected Object doInBackground(String... params) {
		//FIXME use new OAuthToken here
		/*if (mTaskType == TYPE_FROB) {
			try {
				return mAuthInterface.getFrob();
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			try {
				return mAuthInterface.getToken(params[0]);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}*/
		return null;
	}

	@Override
	protected void onPostExecute(Object result) {
		mListener.onAuthDone(mTaskType, result);
	}

}
