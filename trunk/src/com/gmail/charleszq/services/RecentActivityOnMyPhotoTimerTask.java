/*
 * Created on Jul 5, 2011
 *
 * Copyright (c) Sybase, Inc. 2011   
 * All rights reserved.                                    
 */

package com.gmail.charleszq.services;

import com.aetrion.flickr.activity.Item;
import com.gmail.charleszq.R;
import com.gmail.charleszq.dataprovider.RecentActivitiesDataProvider;
import com.gmail.charleszq.utils.Constants;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.List;
import java.util.TimerTask;

/**
 * @author qiangz
 */
public class RecentActivityOnMyPhotoTimerTask extends TimerTask {
    private static final String TAG = RecentActivityOnMyPhotoTimerTask.class.getName();
    private Context mContext;
    private String mToken;
    
    /**
     * The check interval of activities on my photos, in 'hour's.
     */
    private int mInterval = 24;

    public RecentActivityOnMyPhotoTimerTask(Context context, String token, int interval ) {
        this.mContext = context;
        this.mToken = token;
        this.mInterval = interval;
    }

    /*
     * (non-Javadoc)
     * @see java.util.TimerTask#run()
     */
    @Override
    public void run() {
        RecentActivitiesDataProvider dp = new RecentActivitiesDataProvider(mToken, true);
        dp.setCheckInterval(mInterval);
        List<Item> items = dp.getRecentActivities();
        Log.d(TAG, "Recent activity task executed, item size: " + items.size()); //$NON-NLS-1$
        if (!items.isEmpty()) {
            sendNotification();
        }
    }

    private void sendNotification() {
        // notification manager.
        NotificationManager notifManager = (NotificationManager) mContext
                .getSystemService(Context.NOTIFICATION_SERVICE);

        // notification itself.
        Notification notif = new Notification(R.drawable.icon,
                mContext.getResources().getString(
                        R.string.notif_message_act_on_my_photo), System
                        .currentTimeMillis());
        notif.defaults = Notification.DEFAULT_SOUND;
        notif.flags = Notification.FLAG_AUTO_CANCEL;

        // notification intent.
        CharSequence contentTitle = mContext.getResources().getString(
                R.string.app_name);
        CharSequence contentText = mContext.getResources().getString(
                R.string.notif_message_act_on_my_photo);
        Intent notificationIntent = new Intent(
                Constants.ACT_ON_MY_PHOTO_NOTIF_INTENT_ACTION);
        PendingIntent contentIntent = PendingIntent.getActivity(mContext, 0,
                notificationIntent, 0);

        notif.setLatestEventInfo(mContext, contentTitle, contentText,
                contentIntent);

        // send out the notif
        notifManager.notify(Constants.ACT_ON_MY_PHOTO_NOTIF_ID, notif);
    }

}
