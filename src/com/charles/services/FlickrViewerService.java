/**
 * 
 */
package com.charles.services;

import com.charles.FlickrViewerApplication;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import java.util.Calendar;
import java.util.Timer;

/**
 * Represents the service to run in the background, querying the activities of
 * the authed user, his contacts who uploaded photo recently, and then send
 * notifications.
 * 
 * @author charles
 * 
 */
public class FlickrViewerService extends Service {

	private static final String TAG = FlickrViewerService.class.getName();

	@Override
	public void onCreate() {
		super.onCreate();
		Timer timer = new Timer();
		
		String token = null;
		Context context = getApplicationContext();
		if( context instanceof FlickrViewerApplication ) {
			FlickrViewerApplication app = (FlickrViewerApplication) context;
			token = app.getFlickrToken();
		} else {
			Log.e(TAG, "Not the application context provided");
			return;
		}
		ContactUploadTimerTask task = new ContactUploadTimerTask(context,token);
		
		//TODO put the update interval into settings.
		long period = 24L * 60L * 60L * 1000L;
		Calendar cal = Calendar.getInstance();
		timer.scheduleAtFixedRate(task, cal.getTime(), period);
		
		//one hour later, start another task.
		Timer timer2 = new Timer();
		RecentActivityOnMyPhotoTimerTask actTask = new RecentActivityOnMyPhotoTimerTask(context,token);
		cal.set(Calendar.HOUR_OF_DAY, cal.get(Calendar.HOUR_OF_DAY) + 1 );
		timer2.schedule(actTask, cal.getTime(), period);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Service#onBind(android.content.Intent)
	 */
	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		return Service.START_STICKY;
	}
	
}
