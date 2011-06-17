/**
 * 
 */

package com.charles.ui;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import android.app.Fragment;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import com.aetrion.flickr.people.User;
import com.aetrion.flickr.photos.Exif;
import com.aetrion.flickr.photos.Photo;
import com.charles.FlickrViewerApplication;
import com.charles.R;
import com.charles.actions.IAction;
import com.charles.actions.SharePhotoAction;
import com.charles.actions.ShowAuthDialogAction;
import com.charles.actions.ShowWriteCommentAction;
import com.charles.event.IUserCommentsFetchedListener;
import com.charles.model.UserComment;
import com.charles.task.GetPhotoCommentsTask;
import com.charles.task.ImageDownloadTask;
import com.charles.ui.comp.PhotoDetailActionBar;
import com.charles.utils.ImageCache;
import com.charles.utils.ImageUtils.DownloadedDrawable;

/**
 * The fragment to view the detail information of a picture, including exif,
 * author, title and comments.
 * 
 * @author charles
 */
public class ViewImageDetailFragment extends Fragment implements
		IUserCommentsFetchedListener {

	private static final String TAG = ViewImageDetailFragment.class
			.getSimpleName();
	   
    private static final String PHOTO_ID_ATTR = "photo.id";
    private static final String PHOTO_TITLE_ATTR = "photo.title";
    private static final String PHOTO_OWNER_ID = "photo.owner.id";

	private WeakReference<Bitmap> mBitmapRef;
	private Photo mCurrentPhoto;
	private Exif[] mExifs;
	private UserCommentAdapter mCommentAdapter;

	/**
	 * The user comments of this photo.
	 */
	private List<UserComment> mComments = new ArrayList<UserComment>();

	private boolean mShowingExif = true;
	private ViewSwitcher mViewSwitcher;
	private View mCommentProgressBar;

	/**
	 * Default constructor for the framework.
	 */
	public ViewImageDetailFragment() {
	    mCurrentPhoto = new Photo();
	}

	/**
	 * Constructor.
	 * 
	 * @param photo
	 * @param bitmap
	 * @param exifs
	 */
	public ViewImageDetailFragment(Photo photo, Bitmap bitmap,
			Collection<Exif> exifs) {
		this.mCurrentPhoto = photo;
		mBitmapRef = new WeakReference<Bitmap>(bitmap);
		if (exifs == null) {
			this.mExifs = new Exif[0];
		} else {
			this.mExifs = exifs.toArray(new Exif[0]);
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setHasOptionsMenu(true);
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.menu_view_image, menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_item_share:
			IAction action = new SharePhotoAction(getActivity(), mBitmapRef
					.get(), this.mCurrentPhoto.getUrl());
			action.execute();
			return true;
		case R.id.menu_item_write_comment:
			FlickrViewerApplication app = (FlickrViewerApplication) getActivity()
					.getApplication();
			String token = app.getFlickrToken();
			ShowWriteCommentAction commentAction = new ShowWriteCommentAction(
					getActivity(), mCurrentPhoto.getId());
			if (token == null) {
				ShowAuthDialogAction act = new ShowAuthDialogAction(
						getActivity(), commentAction);
				act.execute();
			} else {
				commentAction.execute();
			}
			return true;
		case R.id.menu_item_switch:
			if (mShowingExif) {
				item.setTitle(R.string.menu_show_exif);
				mViewSwitcher.showNext();
				mViewSwitcher.animate().setDuration(2000).rotationY(360f);
			} else {
				item.setTitle(R.string.menu_show_comment);
				mViewSwitcher.showPrevious();
				mViewSwitcher.animate().setDuration(2000).rotationY(0f);
			}
			mShowingExif = !mShowingExif;
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.view_image_detail, null);
		ImageView image = (ImageView) view.findViewById(R.id.image);
		if (mBitmapRef != null && mBitmapRef.get() != null) {
			image.setImageBitmap(mBitmapRef.get());
		}
		image.setFocusable(true);
		image.setClickable(true);
		image.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				getActivity().onBackPressed();
			}
		});
		
		if( savedInstanceState != null ) {
		    String photoId = savedInstanceState.getString(PHOTO_ID_ATTR);
		    String photoTitle = savedInstanceState.getString(PHOTO_TITLE_ATTR);
		    String ownerId = savedInstanceState.getString(PHOTO_OWNER_ID);
		    mCurrentPhoto.setId(photoId);
		    mCurrentPhoto.setTitle(photoTitle);
		    User user = new User();
		    user.setId(ownerId);
		    mCurrentPhoto.setOwner(user);
		}

		// photo title.
		TextView photoTitle = (TextView) view.findViewById(R.id.titlebyauthor);
		photoTitle.setText(mCurrentPhoto.getTitle());

		// exif list.
		ListView list = (ListView) view.findViewById(R.id.exifList);
		TextView empty = (TextView) view.findViewById(R.id.empty);
		list.setEmptyView(empty);
		list.setAdapter(new ExifAdapter(getActivity(), mExifs));

		// comment list.
		ListView commentListView = (ListView) view
				.findViewById(R.id.listComments);
		mCommentAdapter = new UserCommentAdapter(getActivity(), this.mComments);
		commentListView.setAdapter(mCommentAdapter);

		// view swithcer
		mViewSwitcher = (ViewSwitcher) view.findViewById(R.id.switcher);

		// comment progress bar
		mCommentProgressBar = view.findViewById(R.id.commentProgressBar);

		// get user information.
		PhotoDetailActionBar pBar = (PhotoDetailActionBar) view
				.findViewById(R.id.user_action_bar);
		pBar.setUser(mCurrentPhoto.getOwner().getId());
		pBar.setPhotoId(mCurrentPhoto.getId());

		return view;
	}

	private GetPhotoCommentsTask mPhotoCommentTask;

	@Override
	public void onResume() {
		super.onResume();
		String photoId = mCurrentPhoto.getId();
		Log.d(TAG, "Current photo id: " + photoId);
		mPhotoCommentTask = new GetPhotoCommentsTask(this);
		mPhotoCommentTask.execute(photoId);
	}

	@Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(PHOTO_ID_ATTR, mCurrentPhoto.getId());
        outState.putString(PHOTO_TITLE_ATTR, mCurrentPhoto.getTitle());
        outState.putString(PHOTO_OWNER_ID, mCurrentPhoto.getOwner().getId());
    }

    @Override
	public void onPause() {
		if (mPhotoCommentTask != null) {
			mPhotoCommentTask.cancel(true);
		}
		super.onPause();
	}

	/**
	 * Represents the adapter for the user comment list.
	 */
	private static class UserCommentAdapter extends BaseAdapter {

		private List<UserComment> mComments;
		private Context mContext;

		UserCommentAdapter(Context context, List<UserComment> comments) {
			this.mComments = comments;
			this.mContext = context;
		}

		@Override
		public int getCount() {
			return mComments.size();
		}

		@Override
		public Object getItem(int arg0) {
			return mComments.get(arg0);
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
				view = li.inflate(R.layout.user_comment_item, null);
			}

			ImageView buddyIcon;
			TextView author, commentDate, comment;
			ViewHolder holder = (ViewHolder) view.getTag();
			if (holder == null) {
				buddyIcon = (ImageView) view.findViewById(R.id.buddy_icon);
				author = (TextView) view.findViewById(R.id.author);
				comment = (TextView) view.findViewById(R.id.comment);
				commentDate = (TextView) view.findViewById(R.id.commentDate);

				holder = new ViewHolder();
				holder.image = buddyIcon;
				holder.author = author;
				holder.comment = comment;
				holder.commentDate = commentDate;
				view.setTag(holder);
			} else {
				buddyIcon = holder.image;
				author = holder.author;
				commentDate = holder.commentDate;
				comment = holder.comment;
			}

			UserComment userComment = (UserComment) getItem(position);
			author.setText(userComment.getUserName());
			comment.setText(userComment.getCommentText());
			commentDate.setText(userComment.getCommentDateString());

			Drawable drawable = buddyIcon.getDrawable();
			String smallUrl = userComment.getBuddyIconUrl();
			if (drawable != null && drawable instanceof DownloadedDrawable) {
				ImageDownloadTask task = ((DownloadedDrawable) drawable)
						.getBitmapDownloaderTask();
				if (!smallUrl.equals(task)) {
					task.cancel(true);
				}
			}

			if (smallUrl == null) {
				buddyIcon.setImageDrawable(null);
			} else {
				Bitmap cacheBitmap = ImageCache.getFromCache(smallUrl);
				if (cacheBitmap != null) {
					buddyIcon.setImageBitmap(cacheBitmap);
				} else {
					ImageDownloadTask task = new ImageDownloadTask(buddyIcon);
					drawable = new DownloadedDrawable(task);
					buddyIcon.setImageDrawable(drawable);
					task.execute(smallUrl);
				}
			}

			return view;
		}

	}

	private static class ViewHolder {
		ImageView image;
		TextView author;
		TextView commentDate;
		TextView comment;
	}

	/**
	 * The adapter for exif list.
	 */
	private static class ExifAdapter extends BaseAdapter {

		private Exif[] mExifs;
		private Context mContext;

		ExifAdapter(Context context, Exif[] exifs) {
			this.mExifs = exifs;
			this.mContext = context;
		}

		@Override
		public int getCount() {
			return mExifs.length;
		}

		@Override
		public Object getItem(int position) {
			return mExifs[position];
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view = convertView;
			if (view == null) {
				view = new TextView(mContext);
			}
			Exif exif = (Exif) getItem(position);
			if (exif != null) {
				((TextView) view).setText(exif.getLabel() + " : "
						+ exif.getRaw());
			}
			return view;
		}

	}

	@Override
	public void onCommentFetched(List<UserComment> comments) {
		Log.d(TAG, "comments fetched, comment size: " + comments.size());
		this.mComments.clear();
		for (UserComment comment : comments) {
			mComments.add(comment);
		}
		mCommentAdapter.notifyDataSetChanged();
		this.mCommentProgressBar.setVisibility(View.GONE);
	}

}
