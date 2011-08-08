package com.gmail.charleszq;

import java.util.HashSet;
import java.util.Set;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.gmail.charleszq.actions.GetActivitiesAction;
import com.gmail.charleszq.services.TimeUpReceiver;
import com.gmail.charleszq.ui.ContactsFragment;
import com.gmail.charleszq.ui.HelpFragment;
import com.gmail.charleszq.utils.Constants;
import com.gmail.charleszq.utils.ImageCache;

public class FlickrViewerActivity extends Activity {

	private static final String TAG = FlickrViewerActivity.class.getName();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		registerTimeCheckReceiver();

		showHelpPage();
		handleIntent();
	}

	private PendingIntent getContactUploadPendingIntent() {
		Intent contactUploadIntent = new Intent(this, TimeUpReceiver.class);
		contactUploadIntent
				.setAction(Constants.INTENT_ACTION_CHECK_CONTACT_UPLOAD_RECEIVER);
		PendingIntent contactUploadPendingIntent = PendingIntent.getBroadcast(
				getApplicationContext(), 0, contactUploadIntent,
				PendingIntent.FLAG_UPDATE_CURRENT);
		return contactUploadPendingIntent;
	}

	private PendingIntent getPhotoCommentPendingIntent() {
		Intent photoIntent = new Intent(this, TimeUpReceiver.class);
		photoIntent
				.setAction(Constants.INTENT_ACTION_CHECK_PHOTO_ACTIVITY_RECEIVER);
		PendingIntent photoPendingIntent = PendingIntent.getBroadcast(
				getApplicationContext(), 0, photoIntent,
				PendingIntent.FLAG_UPDATE_CURRENT);
		return photoPendingIntent;
	}

	/**
	 * Registers a broadcast receiver on the alert manager to check photo
	 * activity or contact upload in the fixed time schedule.
	 */
	private void registerTimeCheckReceiver() {
		FlickrViewerApplication app = (FlickrViewerApplication) getApplication();
		String token = app.getFlickrToken();
		if (token == null) {
			return;
		}

		handleContactUploadService();
		handlePhotoActivityService();
	}

	public void handlePhotoActivityService() {
		FlickrViewerApplication app = (FlickrViewerApplication) getApplication();
		AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
		PendingIntent pendingIntent = getPhotoCommentPendingIntent();
		am.cancel(pendingIntent);
		if (app.isPhotoActivityCheckEnabled()) {

			int pIntervalInHours = app.getPhotoActivityCheckInterval();
			am.setRepeating(AlarmManager.RTC,
					System.currentTimeMillis() + 5 * 60 * 1000L,
					pIntervalInHours * 60 * 60 * 1000L, pendingIntent);
			Log.d(TAG, "Receiver registered to check comments on my photos."); //$NON-NLS-1$
		}
	}

	public void handleContactUploadService() {
		FlickrViewerApplication app = (FlickrViewerApplication) getApplication();
		AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
		PendingIntent pendingIntent = getContactUploadPendingIntent();
		am.cancel(pendingIntent);
		if (app.isContactUploadCheckEnabled()) {

			int cIntervalInHours = app.getContactUploadCheckInterval();
			am.setRepeating(AlarmManager.RTC,
					System.currentTimeMillis() + 2 * 60 * 1000L,
					cIntervalInHours * 60 * 60 * 1000L, pendingIntent);
			Log.d(TAG, "Receiver registered to check contact upload."); //$NON-NLS-1$
		}
	}

	@Override
	protected void onNewIntent(Intent intent) {
		setIntent(intent);
		handleIntent();
	}

	/**
	 * Checks the intent of this activity.
	 */
	private void handleIntent() {
		Intent intent = getIntent();
		if (Constants.CONTACT_UPLOAD_PHOTO_NOTIF_INTENT_ACTION.equals(intent
				.getAction())) {
			showContactsUploads(intent);
		} else if (Constants.ACT_ON_MY_PHOTO_NOTIF_INTENT_ACTION.equals(intent
				.getAction())) {
			GetActivitiesAction aaction = new GetActivitiesAction(this);
			aaction.execute();
		}
	}

	/**
	 * Shows 'my contacts' page with recent uploads.
	 */
	private void showContactsUploads(Intent intent) {
		final String[] cids = intent
				.getStringArrayExtra(Constants.CONTACT_IDS_WITH_PHOTO_UPLOADED);

		Set<String> cidSet = new HashSet<String>();
		for (String cid : cids) {
			cidSet.add(cid);
		}

		FragmentManager fm = getFragmentManager();
		fm.popBackStack(Constants.CONTACT_BACK_STACK,
				FragmentManager.POP_BACK_STACK_INCLUSIVE);
		FragmentTransaction ft = fm.beginTransaction();
		ContactsFragment fragment = new ContactsFragment(cidSet);
		ft.replace(R.id.main_area, fragment);
		ft.addToBackStack(Constants.CONTACT_BACK_STACK);
		ft.commit();

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		ImageCache.dispose();
	}

	public void changeActionBarTitle(String title) {
		String appName = this.getResources().getString(R.string.app_name);

		StringBuilder sb = new StringBuilder(appName);
		if (title != null) {
			sb.append(" - ").append(title); //$NON-NLS-1$
		}

		getActionBar().setTitle(sb.toString());
	}

	private void showHelpPage() {
		HelpFragment help = new HelpFragment();
		FragmentManager fm = getFragmentManager();
		FragmentTransaction ft = fm.beginTransaction();
		ft.replace(R.id.main_area, help);
		ft.addToBackStack(Constants.HELP_BACK_STACK);
		ft.commit();
	}
}
