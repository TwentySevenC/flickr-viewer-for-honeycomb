/**
 * 
 */
package com.charles.services;

import java.util.Date;
import java.util.Timer;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

import com.charles.FlickrViewerApplication;

/**
 * Represents the service to run in the background, querying the activities of
 * the authed user, his contacts who uploaded photo recently, and then send
 * notifications.
 * 
 * @author charles
 * 
 */
public class FlickrViewerService extends Service {

	@Override
	public void onCreate() {
		super.onCreate();
		Timer timer = new Timer();
		Context context = getApplicationContext();
		String token = ((FlickrViewerApplication)context).getFlickrToken();
		ContactUploadTimerTask task = new ContactUploadTimerTask(context,token);
		
		//TODO put the update interval into settings.
		timer.scheduleAtFixedRate(task, new Date(), 60 * 60 * 1000L );
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
