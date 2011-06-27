
package com.charles;

import com.charles.ui.ContactsFragment;
import com.charles.utils.Constants;
import com.charles.utils.ImageCache;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import java.util.HashSet;
import java.util.Set;

public class FlickrViewerActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        handleIntent();
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
        if (Constants.CONTACT_UPLOAD_PHOTO_NOTIF_INTENT_ACTION.equals(intent.getAction())) {
            showContactsUploads(intent);
        }		
	}

	/**
     * Shows 'my contacts' page with recent uploads.
     */
    private void showContactsUploads(Intent intent) {
        String[] cids = intent.getStringArrayExtra(Constants.CONTACT_IDS_WITH_PHOTO_UPLOADED);
        Set<String> cidSet = new HashSet<String>();
        for (String cid : cids) {
            cidSet.add(cid);
        }

        FragmentManager fm = getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ContactsFragment fragment = new ContactsFragment(cidSet);
        ft.replace(R.id.main_area, fragment);
        ft.addToBackStack(Constants.CONTACT_BACK_STACK);
        ft.commitAllowingStateLoss();

        NotificationManager notifManager = (NotificationManager) this
                .getSystemService(Context.NOTIFICATION_SERVICE);
        notifManager.cancel(Constants.COTACT_UPLOAD_NOTIF_ID);
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
            sb.append(" - ").append(title);
        }

        getActionBar().setTitle(sb.toString());
    }

}
