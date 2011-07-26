/**
 * 
 */

package com.gmail.charleszq;

import com.gmail.charleszq.event.FlickrViewerMessage;
import com.gmail.charleszq.event.IFlickrViewerMessageHandler;
import com.gmail.charleszq.services.FlickrViewerService;
import com.gmail.charleszq.utils.Constants;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Handler;

import java.util.HashSet;
import java.util.Set;

/**
 * Represents the main application.
 * 
 * @author charles
 */
public class FlickrViewerApplication extends Application {

    private Set<IFlickrViewerMessageHandler> mMessageHandlers = new HashSet<IFlickrViewerMessageHandler>();

    @Override
    public void onCreate() {
        super.onCreate();
        String token = getFlickrToken();
        if (token != null) {
            this.startService(new Intent(this, FlickrViewerService.class));
        }
    }

    /**
     * Returns the defiend page size of the grid view.
     * 
     * @return the page size.
     */
    public int getPageSize() {
        String pageSize = getSharedPreferenceValue(Constants.PHOTO_PAGE_SIZE,
                String.valueOf(Constants.DEF_GRID_PAGE_SIZE));
        return Integer.valueOf(pageSize);
    }

    /**
     * Returns the user defined column number of the grid view.
     * 
     * @return
     */
    public int getGridNumColumns() {
        String count = getSharedPreferenceValue(Constants.PHOTO_GRID_COL_COUNT,
                String.valueOf(Constants.DEF_GRID_COL_COUNT));
        return Integer.parseInt(count);
    }

    public String getFlickrToken() {
        String token = getSharedPreferenceValue(Constants.FLICKR_TOKEN, null);
        return token;
    }

    public void saveFlickrAuthToken(String token, String userId, String userName) {
        SharedPreferences sp = getSharedPreferences(Constants.DEF_PREF_NAME,
                Context.MODE_PRIVATE);
        Editor editor = sp.edit();
        editor.putString(Constants.FLICKR_TOKEN, token);
        editor.putString(Constants.FLICKR_USER_ID, userId);
        editor.putString(Constants.FLICKR_USER_NAME, userName);
        editor.commit();
    }

    public String getUserName() {
        return getSharedPreferenceValue(Constants.FLICKR_USER_NAME, null);
    }

    public String getUserId() {
        return getSharedPreferenceValue(Constants.FLICKR_USER_ID, null);
    }

    /**
     * Clear the user token
     */
    public void logout() {
        saveFlickrAuthToken(null, null, null);
    }

    /**
     * Returns the saved value in the shared preferences.
     * 
     * @param key
     * @param defaultValue
     * @return
     */
    private String getSharedPreferenceValue(String key, String defaultValue) {
        SharedPreferences sp = getSharedPreferences(Constants.DEF_PREF_NAME,
                Context.MODE_APPEND);
        String value = sp.getString(key, defaultValue);
        return value;
    }

    @Override
    public void onTerminate() {
        mMessageHandlers = null;
        super.onTerminate();
    }

    public void registerMessageHandler(IFlickrViewerMessageHandler handler) {
        mMessageHandlers.add(handler);
    }

    public void unregisterMessageHandler(IFlickrViewerMessageHandler handler) {
        mMessageHandlers.remove(handler);
    }

    public void handleMessage(final FlickrViewerMessage message) {
        Handler handler = new Handler();
        handler.post(new Runnable() {

            @Override
            public void run() {
                for (IFlickrViewerMessageHandler handler : mMessageHandlers) {
                    handler.handleMessage(message);
                }
            }
        });
    }

}
