/*
 * Created on Aug 31, 2011
 *
 * Copyright (c) Sybase, Inc. 2011   
 * All rights reserved.                                    
 */

package com.gmail.charleszq.task;

import java.io.IOException;

import org.xml.sax.SAXException;

import android.os.AsyncTask;

import com.aetrion.flickr.FlickrException;
import com.gmail.charleszq.fapi.GalleryInterface;
import com.gmail.charleszq.utils.FlickrHelper;

/**
 * @author charles
 * 
 */
public class CreateGalleryTask extends AsyncTask<String, Integer, String> {

	private ICreateGalleryListener mListener;

	public CreateGalleryTask(ICreateGalleryListener listener) {
		this.mListener = listener;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.os.AsyncTask#doInBackground(Params[])
	 */
	@Override
	protected String doInBackground(String... params) {

		if (params.length != 3)
			throw new IllegalArgumentException(
					"Arguments should be title, description and photo id"); //$NON-NLS-1$
		String title = params[0];
		String description = params[1];
		String primaryPhotoId = params[2];

		GalleryInterface gi = FlickrHelper.getInstance().getGalleryInterface();
		try {
			String galleryId = gi.createGallery(title, description,
					primaryPhotoId);
			return galleryId;
		} catch (Exception e) {
			return "error: " + e.getMessage(); //$NON-NLS-1$
		}
	}

	@Override
	protected void onPostExecute(String result) {
		// if the task succeed, return the gallery id, otherwise the result will
		// be started with "error: ", then error message is appended after.
		if (mListener != null) {
			mListener.onGalleryCreated(!result.startsWith("error:"), result); //$NON-NLS-1$
		}
	}

	public interface ICreateGalleryListener {
		void onGalleryCreated(boolean ok, String result);
	}

}
