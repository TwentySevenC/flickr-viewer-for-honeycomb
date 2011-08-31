/**
 * 
 */
package com.gmail.charleszq.fapi;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.aetrion.flickr.FlickrException;
import com.aetrion.flickr.Parameter;
import com.aetrion.flickr.Response;
import com.aetrion.flickr.Transport;
import com.aetrion.flickr.auth.AuthUtilities;
import com.aetrion.flickr.photos.Extras;
import com.aetrion.flickr.photos.PhotoList;
import com.aetrion.flickr.photos.PhotoUtils;
import com.aetrion.flickr.util.StringUtilities;
import com.aetrion.flickr.util.XMLUtilities;
import com.gmail.charleszq.model.FlickrGallery;

/**
 * Represents the interface to get the gallery information from flickr. To get
 * an instance of this class, use the following way: <br/>
 * GalleryInterface gi = FlickrHelper.getInstance().getGalleryInterface();
 * 
 * @author charles
 * 
 */
public class GalleryInterface {

	private static final String KEY_METHOD = "method"; //$NON-NLS-1$
	private static final String KEY_API_KEY = "api_key"; //$NON-NLS-1$
	private static final String KEY_PER_PAGE = "per_page"; //$NON-NLS-1$
	private static final String KEY_PAGE = "page"; //$NON-NLS-1$
	private static final String KEY_USER_ID = "user_id"; //$NON-NLS-1$
	private static final String KEY_GALLERY_ID = "gallery_id"; //$NON-NLS-1$

	private static final String METHOD_GET_LIST = "flickr.galleries.getList"; //$NON-NLS-1$
	private static final String METHOD_GET_PHOTOS = "flickr.galleries.getPhotos"; //$NON-NLS-1$
	private static final Object METHOD_CREATE = "flickr.galleries.create"; //$NON-NLS-1$

	/**
	 * The api key.
	 */
	private String mApiKey;

	/**
	 * Only support REST for now.
	 */
	private Transport mTransport;

	private String mSharedSecret;

	/**
	 * Constructor.
	 * 
	 * @param apiKey
	 * @param transport
	 */
	public GalleryInterface(String apiKey, String sharedSecret,
			Transport transport) {
		this.mApiKey = apiKey;
		this.mTransport = transport;
		this.mSharedSecret = sharedSecret;
	}

	/**
	 * Returns the gallery list of a given user.
	 * 
	 * @param userId
	 * @param pageSize
	 * @param pageNo
	 * @return
	 * @throws SAXException
	 * @throws IOException
	 * @throws FlickrException
	 */
	public List<FlickrGallery> getGalleries(String userId, int perPage, int page)
			throws IOException, SAXException, FlickrException {
		List<Parameter> parameters = new ArrayList<Parameter>();
		parameters.add(new Parameter(KEY_METHOD, METHOD_GET_LIST));
		parameters.add(new Parameter(KEY_API_KEY, mApiKey));
		parameters.add(new Parameter(KEY_USER_ID, userId));
		if (perPage > 0) {
			parameters
					.add(new Parameter(KEY_PER_PAGE, String.valueOf(perPage)));
		}
		if (page > 0) {
			parameters.add(new Parameter(KEY_PAGE, String.valueOf(page)));
		}

		List<FlickrGallery> galleries = new ArrayList<FlickrGallery>();
		Response response = mTransport.get(mTransport.getPath(), parameters);
		if (response.isError()) {
			throw new FlickrException(response.getErrorCode(),
					response.getErrorMessage());
		}
		Element galleriesElement = response.getPayload();
		NodeList galleryNodes = galleriesElement
				.getElementsByTagName("gallery"); //$NON-NLS-1$
		for (int i = 0; i < galleryNodes.getLength(); i++) {
			Element galleryElement = (Element) galleryNodes.item(i);
			FlickrGallery gallery = new FlickrGallery();
			gallery.setGalleryId(galleryElement.getAttribute("id")); //$NON-NLS-1$
			gallery.setGalleryUrl(galleryElement.getAttribute("url")); //$NON-NLS-1$
			gallery.setOwnerId(galleryElement.getAttribute("owner")); //$NON-NLS-1$
			gallery.setPrimaryPhotoId(galleryElement
					.getAttribute("primary_photo_id")); //$NON-NLS-1$
			gallery.setPhotoCount(Integer.parseInt(galleryElement
					.getAttribute("count_photos"))); //$NON-NLS-1$
			gallery.setVideoCount(Integer.parseInt(galleryElement
					.getAttribute("count_videos"))); //$NON-NLS-1$
			String title = XMLUtilities.getChildValue(galleryElement, "title"); //$NON-NLS-1$
			gallery.setTitle(title == null ? "" : title); //$NON-NLS-1$

			String desc = XMLUtilities.getChildValue(galleryElement,
					"description"); //$NON-NLS-1$
			gallery.setDescription(desc == null ? "" : desc); //$NON-NLS-1$
			galleries.add(gallery);
		}
		return galleries;
	}

	public PhotoList getPhotos(String galleryId, Set<String> extras,
			int perPage, int page) throws IOException, SAXException,
			FlickrException {
		List<Parameter> parameters = new ArrayList<Parameter>();
		parameters.add(new Parameter(KEY_METHOD, METHOD_GET_PHOTOS));
		parameters.add(new Parameter(KEY_API_KEY, mApiKey));
		parameters.add(new Parameter(KEY_GALLERY_ID, galleryId));
		if (perPage > 0) {
			parameters
					.add(new Parameter(KEY_PER_PAGE, String.valueOf(perPage)));
		}
		if (page > 0) {
			parameters.add(new Parameter(KEY_PAGE, String.valueOf(page)));
		}

		if (extras != null && !extras.isEmpty()) {
			parameters.add(new Parameter(Extras.KEY_EXTRAS, StringUtilities
					.join(extras, ","))); //$NON-NLS-1$
		}

		Response response = mTransport.get(mTransport.getPath(), parameters);
		if (response.isError()) {
			throw new FlickrException(response.getErrorCode(),
					response.getErrorMessage());
		}
		PhotoList photos = new PhotoList();
		Element photoset = response.getPayload();
		NodeList photoElements = photoset.getElementsByTagName("photo"); //$NON-NLS-1$
		photos.setPage(photoset.getAttribute("page")); //$NON-NLS-1$
		photos.setPages(photoset.getAttribute("pages")); //$NON-NLS-1$
		photos.setPerPage(photoset.getAttribute("per_page")); //$NON-NLS-1$
		photos.setTotal(photoset.getAttribute("total")); //$NON-NLS-1$

		for (int i = 0; i < photoElements.getLength(); i++) {
			Element photoElement = (Element) photoElements.item(i);
			photos.add(PhotoUtils.createPhoto(photoElement, photoset));
		}

		return photos;

	}

	/**
	 * Creates a gallery, return -1 says there is error, returns the gallery id
	 * if success.
	 * 
	 * @param title
	 * @param description
	 * @param primaryPhotoId
	 * @return
	 * @throws SAXException
	 * @throws IOException
	 * @throws FlickrException
	 */
	public String createGallery(String title, String description,
			String primaryPhotoId) throws IOException, SAXException,
			FlickrException {
		List<Parameter> parameters = new ArrayList<Parameter>();
		parameters.add(new Parameter(KEY_METHOD, METHOD_CREATE));
		parameters.add(new Parameter(KEY_API_KEY, mApiKey));

		parameters.add(new Parameter("title", title)); //$NON-NLS-1$
		parameters.add(new Parameter(
				"description", description == null ? title : description)); //$NON-NLS-1$
		parameters.add(new Parameter("primary_photo_id", primaryPhotoId)); //$NON-NLS-1$
		parameters.add(new Parameter("api_sig", //$NON-NLS-1$
				AuthUtilities.getSignature(mSharedSecret, parameters)));

		Response response = mTransport.post(mTransport.getPath(), parameters);
		if (response.isError()) {
			throw new FlickrException(response.getErrorCode(),
					response.getErrorMessage());
		}
		Element gallery = response.getPayload();
		return gallery.getAttribute("id"); //$NON-NLS-1$
	}
}
