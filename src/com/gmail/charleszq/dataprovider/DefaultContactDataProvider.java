/*
 * Created on Jun 20, 2011
 *
 * Copyright (c) Sybase, Inc. 2011   
 * All rights reserved.                                    
 */

package com.gmail.charleszq.dataprovider;

import com.aetrion.flickr.Flickr;
import com.aetrion.flickr.contacts.Contact;
import com.aetrion.flickr.contacts.ContactsInterface;
import com.gmail.charleszq.utils.FlickrHelper;

import java.util.ArrayList;
import java.util.Collection;

/**
 * TODO refactor.
 * @author qiangz
 */
public class DefaultContactDataProvider implements IContactDataProvider {

    private String mToken;

    public DefaultContactDataProvider(String token) {
        this.mToken = token;
    }

    /*
     * (non-Javadoc)
     * @see
     * com.gmail.charleszq.dataprovider.IContactDataProvider#getContacts(java.lang.String
     * )
     */
    @SuppressWarnings("unchecked")
    @Override
    public Collection<Contact> getContacts(String userId) {
        Flickr f = FlickrHelper.getInstance().getFlickrAuthed(mToken);
        ContactsInterface ci = f.getContactsInterface();
        try {
            return ci.getList();
        } catch (Exception e) {
            return new ArrayList<Contact>();
        }
    }

}
