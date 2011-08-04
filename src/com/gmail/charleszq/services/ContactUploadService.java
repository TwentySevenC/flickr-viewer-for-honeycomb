/**
 * 
 */
package com.gmail.charleszq.services;

import java.util.Calendar;
import java.util.Timer;

import android.content.Context;
import android.util.Log;

import com.gmail.charleszq.FlickrViewerApplication;

/**
 * Represents the service to check whether there are new photos uploaded by my
 * contacts.
 * 
 * @author qiangz
 * 
 */
public class ContactUploadService extends FlickrViewerService {

	private static final String TAG = ContactUploadService.class.getName();

	@Override
	public void onCreate() {
		super.onCreate();

		String token = null;
		Context context = getApplicationContext();

		int interval = 24;
		if (context instanceof FlickrViewerApplication) {
			FlickrViewerApplication app = (FlickrViewerApplication) context;
			token = app.getFlickrToken();
			interval = app.getContactUploadCheckInterval();
		} else {
			Log.e(TAG, "Not the application context provided"); //$NON-NLS-1$
			return;
		}

		if (token == null) {
			Log.d(TAG, "User not authorizes the flickr access yet."); //$NON-NLS-1$
			return;
		}
		ContactUploadTimerTask task = new ContactUploadTimerTask(context, token);

		long period = interval * 60L * 60L * 1000L;
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.MINUTE, cal.get(Calendar.MINUTE) + 5);
		Timer timer = new Timer();
		timer.schedule(task, cal.getTime(), period);
	}
}
