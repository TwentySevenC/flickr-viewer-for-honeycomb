/**
 * 
 */

package com.gmail.charleszq.ui;

import com.gmail.charleszq.FlickrViewerActivity;
import com.gmail.charleszq.R;
import com.gmail.charleszq.utils.Constants;
import com.gmail.charleszq.utils.ImageCache;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.util.Log;

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
        }
    }

    @Override
    public void onStop() {
        SharedPreferences sp = this.getPreferenceManager().getSharedPreferences();
        sp.unregisterOnSharedPreferenceChangeListener(this);
        super.onStop();
    }
    
    
}
