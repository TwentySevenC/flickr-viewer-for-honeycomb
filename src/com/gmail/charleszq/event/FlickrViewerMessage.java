/*
 * Created on Jul 26, 2011
 *
 * Copyright (c) Sybase, Inc. 2011   
 * All rights reserved.                                    
 */

package com.gmail.charleszq.event;

/**
 * represents the message data structure.
 * 
 * @author charles
 *
 */
public final class FlickrViewerMessage {
    
    public static final String FAV_PHOTO_REMOVED = "fav.photo.removed"; //$NON-NLS-1$
    
    private String mMessageId;
    private Object mMessageData;

    public FlickrViewerMessage(String id, Object data) {
        this.mMessageId = id;
        this.mMessageData = data;
    }

    public String getMessageId() {
        return mMessageId;
    }

    public Object getMessageData() {
        return mMessageData;
    }
    
}
