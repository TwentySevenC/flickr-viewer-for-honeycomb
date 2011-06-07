package com.charles;

import com.charles.utils.ImageCache;

import android.app.Activity;
import android.os.Bundle;

public class FlickrViewerActivity extends Activity {
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		ImageCache.dispose();
	}

}