
package com.charles;

import com.charles.actions.GetActivitiesAction;
import com.charles.services.FlickrViewerService;
import com.charles.ui.ContactsFragment;
import com.charles.ui.HelpFragment;
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

        FlickrViewerApplication app = (FlickrViewerApplication) getApplication();
        String token = app.getFlickrToken();
        if (token != null) {
            startService(new Intent(app, FlickrViewerService.class));
        }
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
        if (Constants.CONTACT_UPLOAD_PHOTO_NOTIF_INTENT_ACTION.equals(intent
                .getAction())) {
            showContactsUploads(intent);
        } else if (Constants.ACT_ON_MY_PHOTO_NOTIF_INTENT_ACTION.equals(intent.getAction())) {
            GetActivitiesAction aaction = new GetActivitiesAction(this);
            aaction.execute();
            NotificationManager notifManager = (NotificationManager) FlickrViewerActivity.this
                    .getSystemService(Context.NOTIFICATION_SERVICE);
            notifManager.cancel(Constants.ACT_ON_MY_PHOTO_NOTIF_ID);
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
        ft.commitAllowingStateLoss();

        NotificationManager notifManager = (NotificationManager) FlickrViewerActivity.this
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
            sb.append(" - ").append(title); //$NON-NLS-1$
        }

        getActionBar().setTitle(sb.toString());
    }

    @Override
    protected void onStart() {
        super.onStart();
        HelpFragment help = new HelpFragment();
        FragmentManager fm = getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.main_area, help);
        ft.commit();
    }
}
