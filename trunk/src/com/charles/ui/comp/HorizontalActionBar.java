/**
 * 
 */
package com.charles.ui.comp;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.charles.R;
import com.charles.actions.AbstractImageAction;

/**
 * Represents the ui component of horizontal action bar.
 * 
 * @author charles
 * 
 */
public class HorizontalActionBar extends FrameLayout {

	/**
	 * The list of quick actions.
	 */
	private List<AbstractImageAction> mQuickActions;

	/**
	 * The container of the quick action items.
	 */
	private LinearLayout mQuickActionContainer;

	public HorizontalActionBar(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		buildLayout();
	}

	public HorizontalActionBar(Context context, AttributeSet attrs) {
		super(context, attrs);
		buildLayout();
	}

	public HorizontalActionBar(Context context) {
		super(context);
		buildLayout();
	}

	/**
	 * Builds the layout.
	 */
	private void buildLayout() {
		LayoutInflater li = LayoutInflater.from(getContext());
		li.inflate(R.layout.quick_action_bar, this, true);
	}

	/**
	 * Adds quick action item.
	 * 
	 * @param action
	 */
	public void addActionItem(final AbstractImageAction action) {
		if (mQuickActions == null) {
			mQuickActions = new ArrayList<AbstractImageAction>();
		}

		mQuickActions.add(action);

		if (mQuickActionContainer == null) {
			mQuickActionContainer = (LinearLayout) findViewById(R.id.quick_action_items);
		}

		ImageView imageView = new ImageView(getContext());
		imageView.setImageResource(action.getImageResourceId());
		imageView.setBackgroundResource(R.drawable.action_bar_item);
		LayoutParams params = new LayoutParams(48, LayoutParams.FILL_PARENT);
		params.gravity = Gravity.CENTER_VERTICAL;
		imageView.setClickable(true);
		mQuickActionContainer.addView(imageView, params);

		imageView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				action.execute();
			}
		});
	}

}
