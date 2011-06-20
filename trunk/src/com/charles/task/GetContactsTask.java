/*
 * Created on Jun 20, 2011
 *
 * Copyright (c) Sybase, Inc. 2011   
 * All rights reserved.                                    
 */

package com.charles.task;

import com.aetrion.flickr.contacts.Contact;
import com.charles.FlickrViewerApplication;
import com.charles.dataprovider.DefaultContactDataProvider;
import com.charles.event.IContactsFetchedListener;

import android.app.Activity;

import java.util.Collection;

/**
 * @author qiangz
 */
public class GetContactsTask extends ProgressDialogAsyncTask<String, Integer, Collection<Contact>> {

    private static final String DEF_MSG = "Fetching contacts ...";
    private IContactsFetchedListener mListener;

    public GetContactsTask(Activity activity, IContactsFetchedListener listener) {
        super(activity, DEF_MSG);
        this.mListener = listener;
    }

    @Override
    protected Collection<Contact> doInBackground(String... params) {
        FlickrViewerApplication app = (FlickrViewerApplication) mActivity.getApplication();
        String token = app.getFlickrToken();
        DefaultContactDataProvider dp = new DefaultContactDataProvider(token);
        return dp.getContacts(params[0]);
    }

    @Override
    protected void onPostExecute(Collection<Contact> result) {
        super.onPostExecute(result);
        if (mListener != null) {
            mListener.onContactsFetched(result);
        }
    }

}
