/**
 * 
 */
package com.gmail.charleszq.ui.comp;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.gmail.charleszq.R;

/**
 * Represents the UI component to show the photo collection information for a
 * given user. It will include galleries, photo sets and photo groups.
 * 
 * @author charles
 * 
 */
public class UserPhotoCollectionComponent extends FrameLayout {

	/**
	 * The list view
	 */
	private ListView mListView;

	/**
	 * the progress bar.
	 */
	private ProgressBar mProgressBar;

	/**
	 * @param context
	 */
	public UserPhotoCollectionComponent(Context context) {
		super(context);
		buildLayout();
	}

	/**
	 * @param context
	 * @param attrs
	 */
	public UserPhotoCollectionComponent(Context context, AttributeSet attrs) {
		super(context, attrs);
		buildLayout();
	}

	/**
	 * @param context
	 * @param attrs
	 * @param defStyle
	 */
	public UserPhotoCollectionComponent(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
		buildLayout();
	}

	/**
	 * Builds the ui.
	 */
	private void buildLayout() {
		LayoutInflater li = LayoutInflater.from(getContext());
		li.inflate(R.layout.user_photo_col_view, this, true);
		mListView = (ListView) findViewById(R.id.list);
		mProgressBar = (ProgressBar) findViewById(R.id.progress);
	}
	
	public void initialize(String userId) {
		
	}

}
