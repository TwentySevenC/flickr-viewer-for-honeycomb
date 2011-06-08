/**
 * 
 */
package com.charles.ui.comp;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.aetrion.flickr.people.User;
import com.charles.R;
import com.charles.event.IUserInfoFetchedListener;
import com.charles.task.GetUserInfoTask;

/**
 * Represents the UI component that provides a list of actions which can be
 * performed on a user, for example, when showing my contact list, for each
 * contact item, we can place this UI component somewhere, with this action bar,
 * user can view this user's detail information, see his/her public photos, etc.
 * 
 * @author charles
 * 
 */
public class UserActionBar extends FrameLayout implements IUserInfoFetchedListener {
	
	private ImageView mBuddyIcon;
	private TextView mUserName;

	public UserActionBar(Context context) {
		super(context);
		buildLayout();
	}

	public UserActionBar(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		buildLayout();
	}

	public UserActionBar(Context context, AttributeSet attrs) {
		super(context, attrs);
		buildLayout();
	}

	/**
	 * Builds the layout.
	 */
	protected void buildLayout() {
		LayoutInflater li = LayoutInflater.from(getContext());
		li.inflate(R.layout.user_action_bar, this, true);
		
		mBuddyIcon = (ImageView) this.findViewById(R.id.user_icon);
		mBuddyIcon.setBackgroundResource(R.drawable.action_bar_item);
		
		ImageView image = (ImageView) this.findViewById(R.id.show_photo);
		image.setBackgroundResource(R.drawable.action_bar_item);
		
		mUserName = (TextView) findViewById(R.id.user_name);
		mUserName.setText("Loading user information...");
	}
	
	/**
	 * @param userId
	 */
	public void setUser(String userId) {
		GetUserInfoTask task = new GetUserInfoTask(mBuddyIcon,this);
		task.execute(userId);
	}

	@Override
	public void onUserInfoFetched(User user) {
		mUserName.setText(user.getUsername());
		ProgressBar pbar = (ProgressBar) findViewById(R.id.progress);
		pbar.setVisibility(INVISIBLE);
	}
	
}
