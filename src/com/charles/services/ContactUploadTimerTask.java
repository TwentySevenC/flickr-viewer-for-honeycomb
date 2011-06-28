/**
 * 
 */
package com.charles.services;

import com.aetrion.flickr.Flickr;
import com.aetrion.flickr.contacts.Contact;
import com.aetrion.flickr.contacts.ContactsInterface;
import com.charles.R;
import com.charles.utils.Constants;
import com.charles.utils.FlickrHelper;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.TimerTask;

/**
 * @author charles
 * 
 */
public class ContactUploadTimerTask extends TimerTask {

	private static final String TAG = ContactUploadTimerTask.class.getName();
    private String mToken;
	private Context mContext;

	/**
	 * Constructor.
	 */
	public ContactUploadTimerTask(Context context, String token) {
		this.mToken = token;
		this.mContext = context;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.TimerTask#run()
	 */
	@Override
	@SuppressWarnings("rawtypes")
	public void run() {
		Flickr f = FlickrHelper.getInstance().getFlickrAuthed(mToken);
		ContactsInterface ci = f.getContactsInterface();
		Date sinceDate = new Date();
		Long time = sinceDate.getTime() - 24 * 60 * 60 * 1000;
		sinceDate = new Date(time);
		try {
		    Date now = new Date();
		    SimpleDateFormat formater = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		    Log.d(TAG,"Task runs at " + formater.format(now));
            Collection col = ci.getListRecentlyUploaded(sinceDate, "all");
			if (col.size() > 0) {
				sendNotifications(col);
			}
		} catch (Exception e) {
		    Log.w(TAG, "unable to get recent upload: " + e.getMessage());
		}
	}

	@SuppressWarnings("rawtypes")
    private void sendNotifications(Collection col) {
		// notification manager.
		NotificationManager notifManager = (NotificationManager) mContext
				.getSystemService(Context.NOTIFICATION_SERVICE);

		// notification itself.
		Notification notif = new Notification(R.drawable.icon,
				mContext.getResources().getString(
						R.string.notif_message_recent_upload), System
						.currentTimeMillis());
		// init the contact id string array
		List<String> cIds = new ArrayList<String>();
		Iterator it = col.iterator();
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
				notificationIntent, 0);

		notif.setLatestEventInfo(mContext, contentTitle, contentText,
				contentIntent);

		// send out the notif
		notifManager.notify(Constants.COTACT_UPLOAD_NOTIF_ID, notif);
	}

}
