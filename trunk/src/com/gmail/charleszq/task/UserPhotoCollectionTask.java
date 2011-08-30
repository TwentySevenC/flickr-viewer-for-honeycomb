/**
 * 
 */
package com.gmail.charleszq.task;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;

import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import com.aetrion.flickr.groups.Group;
import com.aetrion.flickr.groups.pools.PoolsInterface;
import com.aetrion.flickr.photosets.Photoset;
import com.aetrion.flickr.photosets.Photosets;
import com.aetrion.flickr.photosets.PhotosetsInterface;
import com.gmail.charleszq.R;
import com.gmail.charleszq.fapi.GalleryInterface;
import com.gmail.charleszq.model.FlickrGallery;
import com.gmail.charleszq.model.IListItemAdapter;
import com.gmail.charleszq.utils.Constants;
import com.gmail.charleszq.utils.FlickrHelper;
import com.gmail.charleszq.utils.StringUtils;

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

	private static final String TAG = UserPhotoCollectionTask.class.getName();

	private IUserPhotoCollectionFetched mListener;

	/**
	 * The auth token
	 */
	private String mToken;

	private boolean mIsForceFromServer = false;

	/**
	 * Constructor.
	 * 
	 * @param listener
	 */
	public UserPhotoCollectionTask(IUserPhotoCollectionFetched listener) {
		this(listener, false);
	}

	public UserPhotoCollectionTask(IUserPhotoCollectionFetched listener,
			boolean forceFetch) {
		this.mListener = listener;
		this.mIsForceFromServer = forceFetch;
	}

	private Map<Integer, List<IListItemAdapter>> tryGetFromCache()
			throws IOException, JSONException {
		File root = new File(Environment.getExternalStorageDirectory(),
				Constants.SD_CARD_FOLDER_NAME);
		File cacheFile = new File(root, mToken + ".dat"); //$NON-NLS-1$
		if (!cacheFile.exists()) {
			return null;
		}

		List<IListItemAdapter> list = StringUtils.readItemsFromCache(cacheFile);
		Map<Integer, List<IListItemAdapter>> result = new LinkedHashMap<Integer, List<IListItemAdapter>>();

		List<IListItemAdapter> galleries = new ArrayList<IListItemAdapter>();
		List<IListItemAdapter> sets = new ArrayList<IListItemAdapter>();
		List<IListItemAdapter> groups = new ArrayList<IListItemAdapter>();
		for (IListItemAdapter item : list) {
			if (FlickrGallery.class.getName().equals(item.getObjectClassType())) {
				galleries.add(item);
			} else if (Photoset.class.getName().equals(
					item.getObjectClassType())) {
				sets.add(item);
			} else {
				groups.add(item);
			}
		}

		if (!galleries.isEmpty()) {
			result.put(R.string.section_photo_gallery, galleries);
		}
		if (!sets.isEmpty()) {
			result.put(R.string.section_photo_set, sets);
		}
		if (!groups.isEmpty()) {
			result.put(R.string.section_photo_group, groups);
		}
		return result;
	}

	@Override
	protected Map<Integer, List<IListItemAdapter>> doInBackground(
			String... params) {
		String userId = params[0];
		mToken = params[1];

		// the key of this map is the string resource id of gallery, or photo set, or photo group.
		Map<Integer, List<IListItemAdapter>> result = null;
		if (!mIsForceFromServer) {
			try {
				result = tryGetFromCache();
			} catch (Exception e1) {
				Log.d(TAG, "Can not get item list from cache."); //$NON-NLS-1$
			}

			if (result != null) {
				return result;
			}
		}
		result = new LinkedHashMap<Integer, List<IListItemAdapter>>();
		// galleries
		GalleryInterface gi = FlickrHelper.getInstance().getGalleryInterface();
		try {
			List<FlickrGallery> galleries = gi.getGalleries(userId, -1, -1);
			if (!galleries.isEmpty()) {
				List<IListItemAdapter> ga = new ArrayList<IListItemAdapter>();
				for (FlickrGallery gallery : galleries) {
					ga.add(new ListItemAdapter(gallery));
					Log
							.d(
									TAG,
									"Gallery item count: " + gallery.getTotalCount()); //$NON-NLS-1$
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
				.getFlickrAuthed(mToken).getPoolsInterface();
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
		try {
			tryWriteToCache(result);
		} catch (Exception e) {
			Log.w(TAG, "Error to write the cache file."); //$NON-NLS-1$
		}
		if (mListener != null) {
			mListener.onUserPhotoCollectionFetched(result);
		}
	}

	private void tryWriteToCache(Map<Integer, List<IListItemAdapter>> result)
			throws IOException, JSONException {
		List<IListItemAdapter> list = new ArrayList<IListItemAdapter>();
		for (List<IListItemAdapter> items : result.values()) {
			list.addAll(items);
		}

		File root = new File(Environment.getExternalStorageDirectory(),
				Constants.SD_CARD_FOLDER_NAME);
		if (!root.exists()) {
			root.mkdir();
		}
		File cacheFile = new File(root, mToken + ".dat"); //$NON-NLS-1$
		StringUtils.writeItemsToFile(list, cacheFile);
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

	/**
	 * Represents the model for photo gallery, set and groups.
	 */
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
		public String getBuddyIconPhotoIdentifier() {
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
		public String getObjectClassType() {
			return mObject.getClass().getName();
		}

		@Override
		public String getId() {
			if (mObject instanceof FlickrGallery) {
				return ((FlickrGallery) mObject).getGalleryId();
			} else if (mObject instanceof Photoset) {
				return ((Photoset) mObject).getId();
			} else if (mObject instanceof Group) {
				return ((Group) mObject).getId();
			} else {
				throw new IllegalArgumentException("Object type not supported."); //$NON-NLS-1$
			}
		}

		@Override
		public int getItemCount() {
			if (mObject instanceof FlickrGallery) {
				return ((FlickrGallery) mObject).getTotalCount();
			} else {
				return 0;
			}
		}

	}

}
