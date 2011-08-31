/*
 * Created on Aug 31, 2011
 *
 * Copyright (c) Sybase, Inc. 2011   
 * All rights reserved.                                    
 */

package com.gmail.charleszq.task;

import android.os.AsyncTask;

/**
 * @author qiangz
 * 
 */
public class CreateGalleryTask extends AsyncTask<String, Integer, String> {

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.os.AsyncTask#doInBackground(Params[])
	 */
	@Override
	protected String doInBackground(String... arg0) {
		// TBD Auto-generated method stub
		return null;
	}

	public interface ICreateGalleryListener {
		void onGalleryCreated(String result);
	}

}
