/**
 * 
 */
package com.gmail.charleszq.services;

import java.util.Calendar;
import java.util.Timer;

import com.gmail.charleszq.FlickrViewerApplication;

import android.content.Context;
import android.util.Log;

/**
 * Represents the service to check whether my photos got comments or not.s
 * 
 * @author qiangz
 * 
 */
public class PhotoActivityService extends FlickrViewerService {

	private static final String TAG = PhotoActivityService.class.getName();

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
		
		// one hour later, start another task.
		Timer timer2 = new Timer();
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.MINUTE, cal.get(Calendar.MINUTE) + 10);
		
		long period = interval * 60L * 60L * 1000L;
		RecentActivityOnMyPhotoTimerTask actTask = new RecentActivityOnMyPhotoTimerTask(
				context, token, interval);
		timer2.schedule(actTask, cal.getTime(), period);

	}

	
}
