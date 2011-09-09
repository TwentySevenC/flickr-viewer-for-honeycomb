/*
 * Created on Jun 20, 2011
 *
 * Copyright (c) Sybase, Inc. 2011   
 * All rights reserved.                                    
 */

package com.gmail.charleszq.dataprovider;

import java.util.ArrayList;
import java.util.Collection;

import com.gmail.charleszq.utils.FlickrHelper;
import com.gmail.yuyang226.flickr.Flickr;
import com.gmail.yuyang226.flickr.contacts.Contact;
import com.gmail.yuyang226.flickr.contacts.ContactsInterface;

/**
 * @author charles
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
