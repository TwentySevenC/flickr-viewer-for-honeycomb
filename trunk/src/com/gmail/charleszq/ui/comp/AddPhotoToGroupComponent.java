/*
 * Created on Aug 30, 2011
 *
 * Copyright (c) Sybase, Inc. 2011   
 * All rights reserved.                                    
 */

package com.gmail.charleszq.ui.comp;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ViewAnimator;
import android.widget.ViewSwitcher;

import com.aetrion.flickr.photos.Photo;
import com.aetrion.flickr.photos.PhotoPlace;
import com.gmail.charleszq.R;
import com.gmail.charleszq.model.IListItemAdapter;
import com.gmail.charleszq.task.GetPhotoPoolTask;
import com.gmail.charleszq.task.GetPhotoPoolTask.IPhotoPoolListener;
import com.gmail.charleszq.task.ImageDownloadTask;
import com.gmail.charleszq.task.ImageDownloadTask.ParamType;
import com.gmail.charleszq.task.UserPhotoCollectionTask;
import com.gmail.charleszq.task.UserPhotoCollectionTask.IUserPhotoCollectionFetched;
import com.gmail.charleszq.utils.ImageCache;
import com.gmail.charleszq.utils.ImageUtils.DownloadedDrawable;

/**
 * Represents the UI component to add a photo to a set/group/gallery.
 * 
 * @author charles
 * 
 */
public class AddPhotoToGroupComponent extends FrameLayout implements
		OnClickListener, IUserPhotoCollectionFetched, OnItemClickListener,
		IPhotoPoolListener {

	private static final String TAG = AddPhotoToGroupComponent.class.getName();
	private static final int IDX_PROGRESS = 0;
	private static final int IDX_LIST = 1;
	private static final int IDX_CRT_GALLERY = 2;
	private static final int IDX_CRT_SET = 3;

	private ViewAnimator mViewContainer;
	private ListView mListView;
	private Button mOkButton, mCancelButton;
	private SectionAdapter mSectionAdapter;
	private CreateGalleryComponent mCreateGalleryComponent;

	private Photo mCurrentPhoto;
	private String mUserId;
	private String mToken;

	private boolean mIsMyOwnPhoto;

	/**
	 * The set to store the checked item id of gallery/set/group.
	 */
	private Set<String> mCheckedItems = new HashSet<String>();

	/**
	 * The set to store the group/set ids which the given photo is already in.
	 */
	private Set<String> mPhotoGroupIds = new HashSet<String>();

	/**
	 * @param context
	 */
	public AddPhotoToGroupComponent(Context context) {
		super(context);
		buildLayout();
	}

	/**
	 * @param context
	 * @param attrs
	 * @param defStyle
	 */
	public AddPhotoToGroupComponent(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
		buildLayout();
	}

	/**
	 * @param context
	 * @param attrs
	 */
	public AddPhotoToGroupComponent(Context context, AttributeSet attrs) {
		super(context, attrs);
		buildLayout();
	}

	private void buildLayout() {
		LayoutInflater li = LayoutInflater.from(getContext());
		li.inflate(R.layout.add_photo_to_group, this, true);

		mViewContainer = (ViewAnimator) findViewById(R.id.views);
		mViewContainer.setInAnimation(AnimationUtils.loadAnimation(
				getContext(), R.anim.push_right_in));
		mViewContainer.setOutAnimation(AnimationUtils.loadAnimation(
				getContext(), R.anim.push_left_out));

		mCreateGalleryComponent = (CreateGalleryComponent) findViewById(R.id.crt_gallery);

		mListView = (ListView) findViewById(R.id.group_check_list);
		mOkButton = (Button) findViewById(R.id.ok_btn);
		mCancelButton = (Button) findViewById(R.id.cancel_btn);

		mOkButton.setOnClickListener(this);
		mCancelButton.setOnClickListener(this);
		mListView.setOnItemClickListener(this);
		mListView.setItemsCanFocus(false);
	}

	/**
	 * Initializes this component
	 * 
	 * @param photo
	 *            the photo be added to group/set/gallery
	 * @param authUserId
	 *            the authed user id, if user is not authed, this UI should not
	 *            be shown.
	 */
	public void init(Photo photo, String authUserId, String token) {
		this.mCurrentPhoto = photo;
		this.mUserId = authUserId;
		this.mToken = token;

		mCheckedItems.clear();
		mPhotoGroupIds.clear();
		mViewContainer.setDisplayedChild(IDX_PROGRESS);

		mSectionAdapter = new SimpleSectionAdapter(getContext());
		mListView.setAdapter(mSectionAdapter);

		mIsMyOwnPhoto = authUserId.equals(mCurrentPhoto.getOwner().getId());
		if (mIsMyOwnPhoto) {
			GetPhotoPoolTask getPhotoPoolTask = new GetPhotoPoolTask(this);
			getPhotoPoolTask.execute(photo.getId(), token);
		} else {
			UserPhotoCollectionTask task = new UserPhotoCollectionTask(this);
			task.execute(authUserId, token);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.view.View.OnClickListener#onClick(android.view.View)
	 */
	@Override
	public void onClick(View view) {
		if (view == mCancelButton) {
			ViewSwitcher vs = (ViewSwitcher) getParent();
			vs.setInAnimation(AnimationUtils.loadAnimation(getContext(),
					R.anim.push_left_in));
			vs.setOutAnimation(AnimationUtils.loadAnimation(getContext(),
					R.anim.push_right_out));
			vs.showPrevious();
		} else if( view == mOkButton ) {
			int index = mViewContainer.getDisplayedChild();
			if( index == IDX_CRT_GALLERY ) {
				boolean val = mCreateGalleryComponent.validate();
				if( val ) {
					//call method to create gallery.
				}
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.gmail.charleszq.task.UserPhotoCollectionTask.IUserPhotoCollectionFetched
	 * #onUserPhotoCollectionFetched(java.util.Map)
	 */
	@Override
	public void onUserPhotoCollectionFetched(
			Map<Integer, List<IListItemAdapter>> map) {
		mSectionAdapter.clearSections();

		int count = 0; // whether we finally got at least one item in the list.
		for (Integer key : map.keySet()) {
			if (mIsMyOwnPhoto) {
				if (key == R.string.section_photo_gallery) {
					continue;
				}
			} else {
				if (key != R.string.section_photo_gallery) {
					continue;
				}
			}
			List<IListItemAdapter> items = new ArrayList<IListItemAdapter>();
			List<IListItemAdapter> values = map.get(key);
			for (IListItemAdapter item : values) {
				if (!mPhotoGroupIds.contains(item.getId())) {
					items.add(item);
					count++;
				}
			}
			mSectionAdapter.addSection(getContext().getString(key),
					new PhotoPoolAdapter(getContext(), items, mCheckedItems));
		}
		mListView.setAdapter(mSectionAdapter);
		mViewContainer.setDisplayedChild(IDX_LIST);

		if (count == 0) {
			Log.d(TAG,
					"No photo sets/gallery/group availabe to add this photo, need to create one."); //$NON-NLS-1$
			if (mIsMyOwnPhoto) {
				// no photo set/group avaliable to add this photo, prompt user
				// to create a new photo set.
			} else {
				// no gallery to store this photo, prompt user to create a new
				// gallery.
				mViewContainer.setDisplayedChild(IDX_CRT_GALLERY);
			}
		}
	}

	private class PhotoPoolAdapter extends BaseAdapter {

		private Context mContext;
		private List<IListItemAdapter> mPlaces;
		private Set<String> mCheckedItems;

		PhotoPoolAdapter(Context context, List<IListItemAdapter> places,
				Set<String> checkedItems) {
			this.mContext = context;
			this.mPlaces = places;
			this.mCheckedItems = checkedItems;
		}

		@Override
		public int getCount() {
			return mPlaces.size();
		}

		@Override
		public Object getItem(int position) {
			return mPlaces.get(position);
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view = convertView;
			if (view == null) {
				LayoutInflater li = LayoutInflater.from(mContext);
				view = li.inflate(R.layout.add_photo_to_group_list_item, null);
			}
			ImageView poolIcon;
			CheckedTextView poolTitle;
			ViewHolder holder = (ViewHolder) view.getTag();
			if (holder != null) {
				poolTitle = holder.title;
				poolIcon = holder.image;
			} else {
				poolIcon = (ImageView) view.findViewById(R.id.photo_pool_icon);
				poolTitle = (CheckedTextView) view
						.findViewById(android.R.id.text1);

				holder = new ViewHolder();
				holder.image = poolIcon;
				holder.title = poolTitle;
				view.setTag(holder);
			}

			IListItemAdapter place = (IListItemAdapter) getItem(position);
			poolTitle.setText(place.getTitle());
			if (mCheckedItems.contains(place.getId())) {
				poolTitle.setChecked(true);
			} else {
				poolTitle.setChecked(false);
			}

			Drawable drawable = poolIcon.getDrawable();
			String photoPoolId = place.getBuddyIconPhotoIdentifier();
			if (drawable != null && drawable instanceof DownloadedDrawable) {
				ImageDownloadTask task = ((DownloadedDrawable) drawable)
						.getBitmapDownloaderTask();
				if (!photoPoolId.equals(task.getUrl())) {
					task.cancel(true);
				}
			}

			if (photoPoolId != null) {
				Bitmap cacheBitmap = ImageCache.getFromCache(photoPoolId);
				if (cacheBitmap != null) {
					poolIcon.setImageBitmap(cacheBitmap);
				} else {
					ImageDownloadTask task = new ImageDownloadTask(
							poolIcon,
							place.getType() == IListItemAdapter.PHOTO_ID ? ParamType.PHOTO_ID_SMALL_SQUARE
									: ParamType.PHOTO_POOL_ID);
					drawable = new DownloadedDrawable(task);
					poolIcon.setImageDrawable(drawable);
					task.execute(photoPoolId);
				}
			}

			return view;
		}

		class ViewHolder {
			ImageView image;
			CheckedTextView title;
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * android.widget.AdapterView.OnItemClickListener#onItemClick(android.widget
	 * .AdapterView, android.view.View, int, long)
	 */
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		IListItemAdapter item = (IListItemAdapter) mSectionAdapter
				.getItem(position);
		if (mCheckedItems.contains(item.getId())) {
			mCheckedItems.remove(item.getId());
		} else {
			mCheckedItems.add(item.getId());
		}
		mSectionAdapter.notifyDataSetChanged();
	}

	@Override
	public void onPhotoPoolFetched(List<PhotoPlace> photoPlaces) {
		for (PhotoPlace place : photoPlaces) {
			mPhotoGroupIds.add(place.getId());
		}

		// get user's set/group list
		UserPhotoCollectionTask task = new UserPhotoCollectionTask(this);
		task.execute(mUserId, mToken);
	}

}
