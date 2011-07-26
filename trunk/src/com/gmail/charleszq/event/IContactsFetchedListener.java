/*
 * Created on Jun 20, 2011
 *
 * Copyright (c) Sybase, Inc. 2011   
 * All rights reserved.                                    
 */

package com.gmail.charleszq.event;

import com.aetrion.flickr.contacts.Contact;

import java.util.Collection;

/**
 * @author qiangz
 *
 */
public interface IContactsFetchedListener {

    void onContactsFetched(Collection<Contact> contacts);
}
