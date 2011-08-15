package com.gmail.charleszq;

import java.util.HashSet;
import java.util.Set;

import android.app.ActionBar;
import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.SearchView;
import android.widget.SearchView.OnQueryTextListener;

import com.gmail.charleszq.actions.GetActivitiesAction;
import com.gmail.charleszq.actions.TagSearchAction;
import com.gmail.charleszq.ui.ContactsFragment;
import com.gmail.charleszq.ui.HelpFragment;
import com.gmail.charleszq.utils.Constants;
import com.gmail.charleszq.utils.ImageCache;

public class FlickrViewerActivity extends Activity implements
		OnQueryTextListener {

	/**
	 * The search view to search tags.
	 */
	private SearchView mSearchView;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		addTagSearchButton();

		showHelpPage();
		handleIntent();
	}

	/**
	 * Initializes the action bar, adds the tag search view.
	 */
	private void addTagSearchButton() {
		ActionBar actionBar = getActionBar();
		mSearchView = new SearchView(this);
		mSearchView.setIconified(true);
		mSearchView.setSubmitButtonEnabled(true);
		int option = ActionBar.DISPLAY_SHOW_CUSTOM
				| ActionBar.DISPLAY_SHOW_HOME;
		SharedPreferences sp = getSharedPreferences(Constants.DEF_PREF_NAME,
				Context.MODE_APPEND);
		boolean result = sp.getBoolean(Constants.SETTING_SHOW_APP_TITLE, true);
		if (result) {
			option |= ActionBar.DISPLAY_SHOW_TITLE;
		}
		actionBar.setDisplayOptions(option);
		actionBar.setCustomView(mSearchView);

		mSearchView.setOnQueryTextListener(this);
	}

	@Override
	protected void onNewIntent(Intent intent) {
		setIntent(intent);
		handleIntent();
	}

	/**
	 * Checks the intent of this activity.
	 */
	private void handleIntent() {
		Intent intent = getIntent();
		if (Constants.CONTACT_UPLOAD_PHOTO_NOTIF_INTENT_ACTION.equals(intent
				.getAction())) {
			showContactsUploads(intent);
		} else if (Constants.ACT_ON_MY_PHOTO_NOTIF_INTENT_ACTION.equals(intent
				.getAction())) {
			GetActivitiesAction aaction = new GetActivitiesAction(this);
			aaction.execute();
		}
	}

	/**
	 * Shows 'my contacts' page with recent uploads.
	 */
	private void showContactsUploads(Intent intent) {
		final String[] cids = intent
				.getStringArrayExtra(Constants.CONTACT_IDS_WITH_PHOTO_UPLOADED);

		Set<String> cidSet = new HashSet<String>();
		for (String cid : cids) {
			cidSet.add(cid);
		}

		FragmentManager fm = getFragmentManager();
		fm.popBackStack(Constants.CONTACT_BACK_STACK,
				FragmentManager.POP_BACK_STACK_INCLUSIVE);
		FragmentTransaction ft = fm.beginTransaction();
		ContactsFragment fragment = new ContactsFragment(cidSet);
		ft.replace(R.id.main_area, fragment);
		ft.addToBackStack(Constants.CONTACT_BACK_STACK);
		ft.commit();

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		ImageCache.dispose();
	}

	public void changeActionBarTitle(String title) {
		String appName = this.getResources().getString(R.string.app_name);

		StringBuilder sb = new StringBuilder(appName);
		if (title != null) {
			sb.append(" - ").append(title); //$NON-NLS-1$
		}

		getActionBar().setTitle(sb.toString());
	}

	private void showHelpPage() {
		HelpFragment help = new HelpFragment();
		FragmentManager fm = getFragmentManager();
		FragmentTransaction ft = fm.beginTransaction();
		ft.replace(R.id.main_area, help);
		ft.addToBackStack(Constants.HELP_BACK_STACK);
		ft.commit();
	}

	@Override
	public boolean onQueryTextChange(String newText) {
		return false;
	}

	@Override
	public boolean onQueryTextSubmit(String query) {
		TagSearchAction action = new TagSearchAction(this, query);
		action.execute();
		mSearchView.setIconified(true);
		return true;
	}
}
