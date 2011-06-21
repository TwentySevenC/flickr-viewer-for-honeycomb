/*
 * Created on Jun 20, 2011
 *
 * Copyright (c) Sybase, Inc. 2011   
 * All rights reserved.                                    
 */

package com.charles.ui;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import android.app.Fragment;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView.ScaleType;

import com.aetrion.flickr.contacts.Contact;
import com.charles.R;
import com.charles.actions.ShowMyContactsAction;
import com.charles.actions.ShowPeoplePhotosAction;
import com.charles.event.IContactsFetchedListener;
import com.charles.task.ImageDownloadTask;
import com.charles.utils.ImageCache;
import com.charles.utils.ImageUtils.DownloadedDrawable;

/**
 * @author qiangz
 */
public class ContactsFragment extends Fragment implements
		IContactsFetchedListener, OnItemClickListener {

	private MyAdapter mAdapter;
	private List<Contact> mContacts = null;

	private boolean mCreatedByOS = false;

	/**
	 * Default constructor.
	 */
	public ContactsFragment() {
		mContacts = new ArrayList<Contact>();
		mCreatedByOS = true;
	}

	public ContactsFragment(List<Contact> contacts) {
		mContacts = contacts;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		GridView gv = new GridView(getActivity());
		gv.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,
				LayoutParams.FILL_PARENT));
		gv.setNumColumns(3);
		gv.setHorizontalSpacing(20);
		mAdapter = new MyAdapter(getActivity(), mContacts);
		gv.setAdapter(mAdapter);
		gv.setOnItemClickListener(this);
		return gv;
	}

	@Override
	public void onResume() {
		super.onResume();
		if (mCreatedByOS) {
			ShowMyContactsAction action = new ShowMyContactsAction(
					getActivity(), this);
			action.execute();
			mCreatedByOS = false;
		}
	}

	private static class MyAdapter extends BaseAdapter {

		private List<Contact> mContacts;
		private Context mContext;

		public MyAdapter(Context context, List<Contact> contacts) {
			this.mContacts = contacts;
			this.mContext = context;
		}

		@Override
		public int getCount() {
			return mContacts.size();
		}

		@Override
		public Object getItem(int arg0) {
			return mContacts.get(arg0);
		}

		@Override
		public long getItemId(int arg0) {
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			View view = convertView;
			if (view == null) {
				LayoutInflater li = LayoutInflater.from(mContext);
				view = li.inflate(R.layout.contact_item, null);
			}

			Contact contact = (Contact) getItem(position);

			ImageView photoImage;
			TextView titleView;

			ViewHolder holder = (ViewHolder) view.getTag();
			if (holder == null) {
				photoImage = (ImageView) view.findViewById(R.id.contact_icon);
				titleView = (TextView) view.findViewById(R.id.contact_name);

				holder = new ViewHolder();
				holder.image = photoImage;
				holder.titleView = titleView;
				view.setTag(holder);

			} else {
				photoImage = holder.image;
				titleView = holder.titleView;
			}
			titleView.setText(contact.getUsername());
			photoImage.setScaleType(ScaleType.CENTER_CROP);

			Drawable drawable = photoImage.getDrawable();
			String buddyIconUrl = contact.getBuddyIconUrl();
			if (drawable != null && drawable instanceof DownloadedDrawable) {
				ImageDownloadTask task = ((DownloadedDrawable) drawable)
						.getBitmapDownloaderTask();
				if (!buddyIconUrl.equals(task)) {
					task.cancel(true);
				}
			}

			if (buddyIconUrl == null) {
				photoImage.setImageDrawable(null);
			} else {
				Bitmap cacheBitmap = ImageCache.getFromCache(buddyIconUrl);
				if (cacheBitmap != null) {
					photoImage.setImageBitmap(cacheBitmap);
				} else {
					ImageDownloadTask task = new ImageDownloadTask(photoImage);
					drawable = new DownloadedDrawable(task);
					photoImage.setImageDrawable(drawable);
					task.execute(buddyIconUrl);
				}
			}

			return view;
		}

		private static class ViewHolder {
			ImageView image;
			TextView titleView;
		}

	}

	@Override
	public void onContactsFetched(Collection<Contact> contacts) {
		mContacts.clear();
		mContacts.addAll(contacts);
		mAdapter.notifyDataSetChanged();
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		Contact c = mContacts.get(position);
		String userId = c.getId();
		ShowPeoplePhotosAction action = new ShowPeoplePhotosAction(
				getActivity(), userId);
		action.execute();
	}
}
