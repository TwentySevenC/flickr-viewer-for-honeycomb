/*
 * Created on Aug 30, 2011
 *
 * Copyright (c) Sybase, Inc. 2011   
 * All rights reserved.                                    
 */

package com.gmail.charleszq.ui.comp;

import java.util.List;
import java.util.Map;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;

import com.aetrion.flickr.photos.Photo;
import com.gmail.charleszq.R;
import com.gmail.charleszq.model.IListItemAdapter;
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
		OnClickListener, IUserPhotoCollectionFetched, OnItemClickListener {

	private ListView mListView;
	private Button mOkButton, mCancelButton;
	private SectionAdapter mSectionAdapter;
	private Photo mCurrentPhoto;

	private boolean mIsMyOwnPhoto;

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
		mSectionAdapter = new SimpleSectionAdapter(getContext());
		mListView.setAdapter(mSectionAdapter);

		mIsMyOwnPhoto = authUserId.equals(mCurrentPhoto.getOwner().getId());
		UserPhotoCollectionTask task = new UserPhotoCollectionTask(this);
		task.execute(authUserId, token);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.view.View.OnClickListener#onClick(android.view.View)
	 */
	@Override
	public void onClick(View arg0) {

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
			mSectionAdapter.addSection(getContext().getString(key),
					new PhotoPoolAdapter(getContext(), map.get(key)));
		}
		mListView.setAdapter(mSectionAdapter);
	}

	private class PhotoPoolAdapter extends BaseAdapter {

		private Context mContext;
		private List<IListItemAdapter> mPlaces;

		PhotoPoolAdapter(Context context, List<IListItemAdapter> places) {
			this.mContext = context;
			this.mPlaces = places;
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
		mListView.setItemChecked(position, !mListView.isItemChecked(position));
	}

}
