/*
 * Created on Jun 20, 2011
 *
 * Copyright (c) Sybase, Inc. 2011   
 * All rights reserved.                                    
 */

package com.gmail.charleszq.dataprovider;

import com.aetrion.flickr.contacts.Contact;

import java.util.Collection;

/**
 * @author charles
 *
 */
public interface IContactDataProvider {
    
    Collection<Contact> getContacts(String userId);
}
