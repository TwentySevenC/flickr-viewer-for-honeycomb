/*
 * Created on Jun 8, 2011
 *
 * Copyright (c) Sybase, Inc. 2011   
 * All rights reserved.                                    
 */

package com.charles.event;

import com.aetrion.flickr.people.User;

/**
 * @author qiangz
 *
 */
public interface IUserInfoFetchedListener {

    void onUserInfoFetched(User user);
}
