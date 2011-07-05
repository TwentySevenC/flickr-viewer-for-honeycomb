/*
 * Created on Jul 5, 2011
 *
 * Copyright (c) Sybase, Inc. 2011   
 * All rights reserved.                                    
 */

package com.charles.services;

import com.aetrion.flickr.activity.Item;
import com.charles.R;
import com.charles.dataprovider.RecentActivitiesDataProvider;
import com.charles.utils.Constants;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import java.util.List;
import java.util.TimerTask;

/**
 * @author qiangz
 *
 */
public class RecentActivityOnMyPhotoTimerTask extends TimerTask {
    
    private Context mContext;
    private String mToken;
    
    public RecentActivityOnMyPhotoTimerTask(Context context, String token ) {
        this.mContext = context;
        this.mToken = token;
        
    }

    /* (non-Javadoc)
     * @see java.util.TimerTask#run()
     */
    @Override
    public void run() {
        RecentActivitiesDataProvider dp = new RecentActivitiesDataProvider(mToken,true);
        List<Item> items = dp.getRecentActivities();
        if( !items.isEmpty() ) {
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
