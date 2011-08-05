/**
 * 
 */
package com.gmail.charleszq.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

/**
 * Represents the service to run in the background, querying the activities of
 * the authed user, his contacts who uploaded photo recently, and then send
 * notifications.
 * 
 * @author charles
 * 
 */
public abstract class  FlickrViewerService extends Service {
	
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

	@Override
	public void onDestroy() {
		super.onDestroy();
		Log.d(this.getClass().getName(), "Service destroyed."); //$NON-NLS-1$
	}
	
	
	
}
