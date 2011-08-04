/**
 * 
 */

package com.gmail.charleszq.ui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.util.Log;

import com.gmail.charleszq.FlickrViewerActivity;
import com.gmail.charleszq.FlickrViewerApplication;
import com.gmail.charleszq.R;
import com.gmail.charleszq.services.ContactUploadService;
import com.gmail.charleszq.services.PhotoActivityService;
import com.gmail.charleszq.utils.Constants;
import com.gmail.charleszq.utils.ImageCache;

/**
 * Represents the fragment for the setting page of whole application.
 * 
 * @author charles
 */
public class SettingsFragment extends PreferenceFragment implements
        OnSharedPreferenceChangeListener {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PreferenceManager pm = this.getPreferenceManager();
        pm.setSharedPreferencesName(Constants.DEF_PREF_NAME);
        pm.setSharedPreferencesMode(Context.MODE_PRIVATE);

        this.addPreferencesFromResource(R.xml.preferences);
    }

    @Override
    public void onStart() {
        super.onStart();
        PreferenceManager pm = getPreferenceManager();
        SharedPreferences sp = pm.getSharedPreferences();
        Log.d("SettingsFragment", "Preference name" + pm.getSharedPreferencesName());  //$NON-NLS-1$//$NON-NLS-2$
        sp.registerOnSharedPreferenceChangeListener(this);
        
        FlickrViewerActivity act = (FlickrViewerActivity) getActivity();
		act.changeActionBarTitle(null);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (Constants.PHOTO_LIST_CACHE_SIZE.equals(key)) {
            String size = sharedPreferences
                    .getString(key, String.valueOf(Constants.DEF_CACHE_SIZE));
            ImageCache.CACHE_SIZE = Integer.parseInt(size);
            Log.d("SettingsFragment", "Cache size changed: " + size);  //$NON-NLS-1$//$NON-NLS-2$
            return;
        }
        
        FlickrViewerApplication app = (FlickrViewerApplication) getActivity().getApplication();
        if(Constants.ENABLE_CONTACT_UPLOAD_NOTIF.equals(key)) {
        	boolean contactUpload = sharedPreferences.getBoolean(key, true);
        	Intent contactUploadIntent = new Intent(app,ContactUploadService.class); 
        	if(contactUpload) {
        		app.startService(contactUploadIntent);
        	} else {
        		app.stopService(contactUploadIntent);
        	}
        	return;
        }
        
        if(Constants.ENABLE_PHOTO_ACT_NOTIF.equals(key)) {
        	boolean photoActivity = sharedPreferences.getBoolean(key,true); 
        	Intent photoIntent = new Intent(app,PhotoActivityService.class);
        	if(photoActivity) {
        		app.startService(photoIntent);
        	} else {
        		app.stopService(photoIntent);
        	}
        	return;
        }
        
        if( Constants.NOTIF_CONTACT_UPLOAD_INTERVAL.equals(key)) {
        	Intent contactUploadIntent = new Intent(app,ContactUploadService.class); 
        	app.stopService(contactUploadIntent);
        	app.startService(contactUploadIntent);
        	return;
        }
        
        if( Constants.NOTIF_PHOTO_ACT_INTERVAL.equals(key)) {
        	Intent photoIntent = new Intent(app,PhotoActivityService.class);
        	app.stopService(photoIntent);
        	app.startService(photoIntent);
        	return;
        }
        
        
    }

    @Override
    public void onStop() {
        SharedPreferences sp = this.getPreferenceManager().getSharedPreferences();
        sp.unregisterOnSharedPreferenceChangeListener(this);
        super.onStop();
    }
    
    
}
