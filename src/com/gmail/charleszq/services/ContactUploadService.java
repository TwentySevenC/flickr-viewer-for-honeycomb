/**
 * 
 */
package com.gmail.charleszq.services;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.aetrion.flickr.Flickr;
import com.aetrion.flickr.contacts.Contact;
import com.aetrion.flickr.contacts.ContactsInterface;
import com.gmail.charleszq.FlickrViewerApplication;
import com.gmail.charleszq.R;
import com.gmail.charleszq.utils.Constants;
import com.gmail.charleszq.utils.FlickrHelper;

/**
 * Represents the service to check whether there are new photos uploaded by my
 * contacts.
 * 
 * @author qiangz
 * 
 */
public class ContactUploadService extends IntentService {

	private static final String TAG = ContactUploadService.class.getName();

	/**
	 * Constructor.
	 */
	public ContactUploadService() {
		super(Constants.ENABLE_CONTACT_UPLOAD_NOTIF);
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		Log.d( TAG, "Contact upload service, in onHandleIntent.");  //$NON-NLS-1$
		Context context = getApplicationContext();
		
		String token = null;
		int intervalInHours = Constants.SERVICE_CHECK_INTERVAL;
		if( context instanceof FlickrViewerApplication) {
			FlickrViewerApplication app = (FlickrViewerApplication) context;
			token = app.getFlickrToken();
			intervalInHours = app.getContactUploadCheckInterval();
			
			if( token == null || !app.isContactUploadCheckEnabled() ) {
				Log.d(TAG, "Not auth or notification is disabled."); //$NON-NLS-1$
				return;
			}
			
			
		} else {
			Log.w(TAG, "Error to get application context."); //$NON-NLS-1$
			return;
		}
		
		checkContactUpload(token, intervalInHours);
	}
	
	private void checkContactUpload(String token, int intervalInHours ) {
		Flickr f = FlickrHelper.getInstance().getFlickrAuthed(token);
		ContactsInterface ci = f.getContactsInterface();
		Date sinceDate = new Date();
		Long time = sinceDate.getTime() - intervalInHours * 60 * 60 * 1000;
		sinceDate = new Date(time);
		try {
		    Date now = new Date();
		    SimpleDateFormat formater = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss"); //$NON-NLS-1$
		    Log.d(TAG,"Task runs at " + formater.format(now)); //$NON-NLS-1$
            Collection<?> col = ci.getListRecentlyUploaded(sinceDate, "all"); //$NON-NLS-1$
			if (col.size() > 0) {
				Log.d(TAG, col.size() + " contacts have new photos uploaded."); //$NON-NLS-1$
				sendNotifications(col);
			} else {
				Log.i(TAG,"No recent upload."); //$NON-NLS-1$
			}
		} catch (Exception e) {
		    Log.w(TAG, "unable to get recent upload: " + e.getMessage()); //$NON-NLS-1$
		} 
	}
	
    private void sendNotifications(Collection<?> col) {
    	
    	Context mContext = getApplicationContext();
    	
		// notification manager.
		NotificationManager notifManager = (NotificationManager)mContext
				.getSystemService(Context.NOTIFICATION_SERVICE);

		// notification itself.
		Notification notif = new Notification(R.drawable.icon,
				mContext.getResources().getString(
						R.string.notif_message_recent_upload), System
						.currentTimeMillis());
		notif.defaults = Notification.DEFAULT_LIGHTS | Notification.DEFAULT_SOUND;
		notif.flags =  Notification.FLAG_AUTO_CANCEL;
		// init the contact id string array
		List<String> cIds = new ArrayList<String>();
		Iterator<?> it = col.iterator();
		while (it.hasNext()) {
			Contact c = (Contact) it.next();
			cIds.add(c.getId());
		}

		// notification intent.
		CharSequence contentTitle = mContext.getResources().getString(
				R.string.app_name);
		CharSequence contentText = mContext.getResources().getString(
				R.string.notif_message_recent_upload);
		Intent notificationIntent = new Intent(
				Constants.CONTACT_UPLOAD_PHOTO_NOTIF_INTENT_ACTION);
		notificationIntent.putExtra(Constants.CONTACT_IDS_WITH_PHOTO_UPLOADED,
				cIds.toArray(new String[0]));
		PendingIntent contentIntent = PendingIntent.getActivity(mContext, 0,
				notificationIntent, PendingIntent.FLAG_ONE_SHOT);

		notif.setLatestEventInfo(mContext, contentTitle, contentText,
				contentIntent);

		// send out the notif
		notifManager.notify(Constants.COTACT_UPLOAD_NOTIF_ID, notif);
	}
}
