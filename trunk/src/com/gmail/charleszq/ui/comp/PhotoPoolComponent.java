/**
 * 
 */
package com.gmail.charleszq.ui.comp;

import java.util.List;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.aetrion.flickr.photos.PhotoPlace;
import com.gmail.charleszq.R;
import com.gmail.charleszq.task.GetPhotoPoolTask;
import com.gmail.charleszq.task.GetPhotoPoolTask.IPhotoPoolListener;

/**
 * Represents the ui component to show the photo pool or set information of a
 * given photo.
 * 
 * @author charles
 * 
 */
public class PhotoPoolComponent extends FrameLayout implements
		IPhotoPoolListener {

	private ListView mPhotoPoolListView;
	private ProgressBar mProgressBar;

	/**
	 * @param context
	 */
	public PhotoPoolComponent(Context context) {
		super(context);
		buildLayout();
	}

	/**
	 * @param context
	 * @param attrs
	 */
	public PhotoPoolComponent(Context context, AttributeSet attrs) {
		super(context, attrs);
		buildLayout();
	}

	/**
	 * @param context
	 * @param attrs
	 * @param defStyle
	 */
	public PhotoPoolComponent(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		buildLayout();
	}

	private void buildLayout() {
		LayoutInflater li = LayoutInflater.from(getContext());
		li.inflate(R.layout.photo_pool_view, this, true);
		mPhotoPoolListView = (ListView) findViewById(R.id.listPools);
		mProgressBar = (ProgressBar) findViewById(R.id.photoPoolProgressBar);
	}

	/**
	 * Sets the photo information.
	 * 
	 * @param photoId
	 */
	public void initialize(String photoId) {
		GetPhotoPoolTask task = new GetPhotoPoolTask(this);
		task.execute(photoId);
	}

	@Override
	public void onPhotoPoolFetched(List<PhotoPlace> photoPlaces) {
		mProgressBar.setVisibility(View.INVISIBLE);
		PhotoPoolAdapter adapter = new PhotoPoolAdapter(this.getContext(),
				photoPlaces);
		mPhotoPoolListView.setAdapter(adapter);
	}

	private class PhotoPoolAdapter extends BaseAdapter {

		private Context mContext;
		private List<PhotoPlace> mPlaces;

		PhotoPoolAdapter(Context context, List<PhotoPlace> places) {
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
			if( view == null ) {
				LayoutInflater li = LayoutInflater.from(mContext);
				view = li.inflate(R.layout.photo_pool_item, null);
			}
			ImageView poolIcon;
			TextView poolTitle;
			ViewHolder holder = (ViewHolder) view.getTag();
			if(holder != null ) {
				poolTitle = holder.title;
				poolIcon = holder.image;
			} else {
				poolIcon = (ImageView) view.findViewById(R.id.photo_pool_icon);
				poolTitle = (TextView) view.findViewById(R.id.photo_pool_title);
				
				holder = new ViewHolder();
				holder.image = poolIcon;
				holder.title = poolTitle;
				view.setTag(holder);
			}
			
			PhotoPlace place = (PhotoPlace) getItem(position);
			poolTitle.setText(place.getTitle());
			
			return view;
		}
		
		class ViewHolder {
			ImageView image;
			TextView title;
		}

	}
}
