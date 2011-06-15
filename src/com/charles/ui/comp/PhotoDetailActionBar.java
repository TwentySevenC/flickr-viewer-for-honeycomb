/**
 * 
 */
package com.charles.ui.comp;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import com.aetrion.flickr.people.User;
import com.charles.R;
import com.charles.actions.ShowPeoplePhotosAction;
import com.charles.event.IUserInfoFetchedListener;
import com.charles.task.AddPhotoAsFavoriteTask;
import com.charles.task.GetUserInfoTask;

/**
 * Represents the UI component that provides a list of actions which can be
 * performed on a photo, for example, when showing my contact list, for each
 * contact item, we can place this UI component somewhere, with this action bar,
 * user can view this user's detail information, see his/her public photos, etc.
 * 
 * @author charles
 * 
 */
public class PhotoDetailActionBar extends FrameLayout implements
		IUserInfoFetchedListener {

	private ImageView mBuddyIcon;
	private TextView mUserName;
	private ViewSwitcher mViewSwitcher;

	private String mPhotoId;
	private String mUserId;

	public PhotoDetailActionBar(Context context) {
		super(context);
		buildLayout();
	}

	public PhotoDetailActionBar(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
		buildLayout();
	}

	public PhotoDetailActionBar(Context context, AttributeSet attrs) {
		super(context, attrs);
		buildLayout();
	}

	/**
	 * Builds the layout.
	 */
	protected void buildLayout() {
		LayoutInflater li = LayoutInflater.from(getContext());
		li.inflate(R.layout.photo_detail_action_bar, this, true);

		mBuddyIcon = (ImageView) this.findViewById(R.id.user_icon);

		mUserName = (TextView) findViewById(R.id.user_name);
		mUserName.setText("Loading user information...");

		mViewSwitcher = (ViewSwitcher) findViewById(R.id.vswitcher);

		ImageView commandButton = (ImageView) findViewById(R.id.commands);
		commandButton.setTag(R.id.commands);
		commandButton.setOnClickListener(mOnClickListener);

		ImageView backButton = (ImageView) findViewById(R.id.back);
		backButton.setTag(R.id.back);
		backButton.setOnClickListener(mOnClickListener);

		ImageView galleryButton = (ImageView) findViewById(R.id.gallery);
		galleryButton.setTag(R.id.gallery);
		galleryButton.setOnClickListener(mOnClickListener);

		ImageView addFavoriteButton = (ImageView) findViewById(R.id.add_favourite);
		addFavoriteButton.setTag(R.id.add_favourite);
		addFavoriteButton.setOnClickListener(mOnClickListener);

	}

	private View.OnClickListener mOnClickListener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			int id = (Integer) v.getTag();
			switch (id) {
			case R.id.commands:
				mViewSwitcher.showNext();
				break;
			case R.id.back:
				mViewSwitcher.showPrevious();
				break;
			case R.id.gallery:
				Context context = getContext();
				ShowPeoplePhotosAction action = new ShowPeoplePhotosAction(
						(Activity) context, mUserId);
				action.execute();
				break;
			case R.id.add_favourite:
				AddPhotoAsFavoriteTask task = new AddPhotoAsFavoriteTask(
						(Activity) getContext());
				task.execute(mPhotoId);
				break;
			}
		}
	};

	/**
	 * @param userId
	 */
	public void setUser(String userId) {
		this.mUserId = userId;
		GetUserInfoTask task = new GetUserInfoTask(mBuddyIcon, this, null);
		task.execute(userId);
	}

	public void setPhotoId(String photoId) {
		this.mPhotoId = photoId;
	}

	@Override
	public void onUserInfoFetched(User user) {
		mUserName.setText(user.getUsername());
		ProgressBar pbar = (ProgressBar) findViewById(R.id.progress);
		pbar.setVisibility(INVISIBLE);
	}

}
