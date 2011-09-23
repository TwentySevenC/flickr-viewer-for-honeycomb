/**
 * 
 */
package com.gmail.charleszq.ui;

import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.gmail.charleszq.R;
import com.gmail.charleszq.ui.comp.CreateGalleryComponent;

/**
 * Represents the dialog to create photo gallery or phot set.
 * 
 * @author charles
 * 
 */
public class CreateGalleryDialog extends DialogFragment {

	/**
	 * The enum type to represent what to be created, photo set, or gallery.
	 */
	public enum CollectionCreationType {
		GALLERY, PHOTO_SET;
	};

	/**
	 * The creation type.
	 */
	private CollectionCreationType mCreationType;

	/**
	 * The photo id, which could be <code>null</code> for <code>GALLERY</code>.
	 */
	private String mPrimaryPhotoId;

	/**
	 * The ui component to create gallery.
	 */
	private CreateGalleryComponent mCreateGalleryComponent;

	/**
	 * Constructor.
	 * 
	 */
	public CreateGalleryDialog(CollectionCreationType createType, String photoId) {
		this.mCreationType = createType;
		this.mPrimaryPhotoId = photoId;
		if (mCreationType == CollectionCreationType.PHOTO_SET
				&& mPrimaryPhotoId == null) {
			throw new IllegalArgumentException(
					"To create photo set, the primary photo id must be provided."); //$NON-NLS-1$
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		getDialog()
				.setTitle(
						getActivity()
								.getString(
										mCreationType == CollectionCreationType.GALLERY ? R.string.dlg_title_crt_gallery
												: R.string.dlg_title_crt_photo_set));
		View view = inflater.inflate(R.layout.create_gallery_dlg, null);
		mCreateGalleryComponent = (CreateGalleryComponent) view
				.findViewById(R.id.crt_gallery);
		mCreateGalleryComponent.init(); // TODO
		return view;
	}

}
