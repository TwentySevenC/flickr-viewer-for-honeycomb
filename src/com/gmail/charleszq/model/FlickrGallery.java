/**
 * 
 */
package com.gmail.charleszq.model;

/**
 * Represents the flickr gallery.
 * 
 * @author charles
 */
public class FlickrGallery {
	private String galleryId;
	private String galleryUrl;
	private String ownerId;
	private String primaryPhotoId;
	private int photoCount;
	private String title;
	private String description;

	public String getGalleryId() {
		return galleryId;
	}

	public void setGalleryId(String galleryId) {
		this.galleryId = galleryId;
	}

	public String getGalleryUrl() {
		return galleryUrl;
	}

	public void setGalleryUrl(String galleryUrl) {
		this.galleryUrl = galleryUrl;
	}

	public String getOwnerId() {
		return ownerId;
	}

	public void setOwnerId(String ownerId) {
		this.ownerId = ownerId;
	}

	public String getPrimaryPhotoId() {
		return primaryPhotoId;
	}

	public void setPrimaryPhotoId(String primaryPhotoId) {
		this.primaryPhotoId = primaryPhotoId;
	}

	public int getPhotoCount() {
		return photoCount;
	}

	public void setPhotoCount(int photoCount) {
		this.photoCount = photoCount;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

}
