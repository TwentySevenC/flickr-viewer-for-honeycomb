/**
 * 
 */
package com.gmail.charleszq.task;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.os.AsyncTask;

import com.aetrion.flickr.groups.Group;
import com.aetrion.flickr.groups.pools.PoolsInterface;
import com.aetrion.flickr.photosets.Photoset;
import com.aetrion.flickr.photosets.Photosets;
import com.aetrion.flickr.photosets.PhotosetsInterface;
import com.gmail.charleszq.R;
import com.gmail.charleszq.fapi.GalleryInterface;
import com.gmail.charleszq.model.FlickrGallery;
import com.gmail.charleszq.task.UserPhotoCollectionTask.IListItemAdapter;
import com.gmail.charleszq.utils.FlickrHelper;

/**
 * Represents the task to fetch the collection of a user, his gallery list,
 * photo set list, and photo group list.
 * <p>
 * To run this task, the parameters must be [user_id, token]
 * 
 * @author charles
 * 
 */
public class UserPhotoCollectionTask extends
		AsyncTask<String, Integer, Map<Integer, List<IListItemAdapter>>> {

	private IUserPhotoCollectionFetched mListener;

	/**
	 * Constructor.
	 * 
	 * @param listener
	 */
	public UserPhotoCollectionTask(IUserPhotoCollectionFetched listener) {
		this.mListener = listener;
	}

	@Override
	protected Map<Integer, List<IListItemAdapter>> doInBackground(
			String... params) {
		String userId = params[0];
		String token = params[1];

		Map<Integer, List<IListItemAdapter>> result = new HashMap<Integer, List<IListItemAdapter>>();

		// galleries
		GalleryInterface gi = FlickrHelper.getInstance().getGalleryInterface();
		try {
			List<FlickrGallery> galleries = gi.getGalleries(userId, -1, -1);
			if (!galleries.isEmpty()) {
				List<IListItemAdapter> ga = new ArrayList<IListItemAdapter>();
				for (FlickrGallery gallery : galleries) {
					ga.add(new ListItemAdapter(gallery));
				}
				result.put(R.string.section_photo_gallery, ga);
			}
		} catch (Exception e) {
			// just ignore it.
		}

		// photo sets
		PhotosetsInterface psi = FlickrHelper.getInstance().getFlickr()
				.getPhotosetsInterface();
		try {
			Photosets photosets = psi.getList(userId);
			Collection<?> photosetList = photosets.getPhotosets();
			if (!photosetList.isEmpty()) {
				List<IListItemAdapter> psa = new ArrayList<IListItemAdapter>();
				for (Object photoset : photosetList) {
					psa.add(new ListItemAdapter(photoset));
				}
				result.put(R.string.section_photo_set, psa);
			}
		} catch (Exception e) {
			// just ignore
		}

		// photo groups
		PoolsInterface poolInterface = FlickrHelper.getInstance()
				.getFlickrAuthed(token).getPoolsInterface();
		try {
			Collection<?> groups = poolInterface.getGroups();
			if (!groups.isEmpty()) {
				List<IListItemAdapter> groupAdapters = new ArrayList<IListItemAdapter>();
				for (Object group : groups) {
					groupAdapters.add(new ListItemAdapter(group));
				}
				result.put(R.string.section_photo_group, groupAdapters);
			}
		} catch (Exception e) {
			// Ignore.
		}

		return result;
	}

	@Override
	protected void onPostExecute(Map<Integer, List<IListItemAdapter>> result) {
		if (mListener != null) {
			mListener.onUserPhotoCollectionFetched(result);
		}
	}

	public interface IUserPhotoCollectionFetched {

		/**
		 * Notifies the listener when collection information is fetched.
		 * 
		 * @param map
		 *            the key will be R.string.xxx, which will identify the
		 *            section name.
		 */
		void onUserPhotoCollectionFetched(
				Map<Integer, List<IListItemAdapter>> map);
	}

	public interface IListItemAdapter {
		public static final int PHOTO_GROUP_ID = 0;
		public static final int PHOTO_ID = 1;

		/**
		 * Returns the collection title, that is, the gallery title, photo set
		 * title or the group name.
		 * 
		 * @return
		 */
		String getTitle();

		/**
		 * Returns the photo url or the photo id.
		 * 
		 * @return
		 */
		String getPhotoIdentifier();

		/**
		 * Returns the photo url type, either 0 or 1, that is, the url or the
		 * photo id.
		 * 
		 * @return
		 */
		int getType();

		/**
		 * Returns the underlying object of this adapter.
		 * 
		 * @return
		 */
		Object getObject();
	}

	private static class ListItemAdapter implements IListItemAdapter {

		private Object mObject;

		ListItemAdapter(Object object) {
			this.mObject = object;
		}

		@Override
		public String getTitle() {
			if (mObject instanceof FlickrGallery) {
				return ((FlickrGallery) mObject).getTitle();
			} else if (mObject instanceof Photoset) {
				return ((Photoset) mObject).getTitle();
			} else if (mObject instanceof Group) {
				return ((Group) mObject).getName();
			} else {
				throw new IllegalArgumentException("Object type not supported."); //$NON-NLS-1$
			}
		}

		@Override
		public String getPhotoIdentifier() {
			if (mObject instanceof FlickrGallery) {
				return ((FlickrGallery) mObject).getPrimaryPhotoId();
			} else if (mObject instanceof Photoset) {
				return ((Photoset) mObject).getPrimaryPhoto().getId();
			} else if (mObject instanceof Group) {
				return ((Group) mObject).getId();
			} else {
				throw new IllegalArgumentException("Object type not supported."); //$NON-NLS-1$
			}
		}

		@Override
		public int getType() {
			if (mObject instanceof FlickrGallery || mObject instanceof Photoset) {
				return PHOTO_ID;
			} else if (mObject instanceof Group) {
				return PHOTO_GROUP_ID;
			} else {
				throw new IllegalArgumentException("Object type not supported."); //$NON-NLS-1$
			}
		}

		@Override
		public Object getObject() {
			return mObject;
		}

	}

}
