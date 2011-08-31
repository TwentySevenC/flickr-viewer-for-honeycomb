package com.gmail.charleszq.utils;

import javax.xml.parsers.ParserConfigurationException;

import com.aetrion.flickr.Flickr;
import com.aetrion.flickr.REST;
import com.aetrion.flickr.RequestContext;
import com.aetrion.flickr.auth.Auth;
import com.aetrion.flickr.auth.Permission;
import com.aetrion.flickr.interestingness.InterestingnessInterface;
import com.aetrion.flickr.photos.PhotosInterface;
import com.gmail.charleszq.fapi.GalleryInterface;

public final class FlickrHelper {

	private static FlickrHelper instance = null;
	private static final String API_KEY = "56893c4690000edac61e265c4d1bbf0f"; //$NON-NLS-1$
	public static final String API_SEC = "30156baf9e81fcdc"; //$NON-NLS-1$

	private FlickrHelper() {

	}

	public static FlickrHelper getInstance() {
		if (instance == null) {
			instance = new FlickrHelper();
		}

		return instance;
	}

	public Flickr getFlickr() {
		try {
			Flickr f = new Flickr(API_KEY, API_SEC, new REST());
			return f;
		} catch (ParserConfigurationException e) {
			return null;
		}
	}

	public Flickr getFlickrAuthed(String token) {
		Flickr f = getFlickr();
		RequestContext requestContext = RequestContext.getRequestContext();
		Auth auth = new Auth();
		auth.setPermission(Permission.WRITE);
		auth.setToken(token);
		requestContext.setAuth(auth);

		return f;
	}

	public InterestingnessInterface getInterestingInterface() {
		Flickr f = getFlickr();
		if (f != null) {
			return f.getInterestingnessInterface();
		} else {
			return null;
		}
	}

	public PhotosInterface getPhotosInterface() {
		Flickr f = getFlickr();
		if (f != null) {
			return f.getPhotosInterface();
		} else {
			return null;
		}
	}

	public GalleryInterface getGalleryInterface() {
		try {
			return new GalleryInterface(API_KEY, API_SEC, new REST());
		} catch (ParserConfigurationException e) {
			return null;
		}
	}

}
