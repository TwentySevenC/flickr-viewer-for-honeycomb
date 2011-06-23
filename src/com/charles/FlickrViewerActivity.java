package com.charles;

import com.charles.utils.ImageCache;

import android.app.Activity;
import android.os.Bundle;

public class FlickrViewerActivity extends Activity {
	
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
	
	public void changeActionBarTitle(String title) {
		String appName = this.getResources().getString(R.string.app_name);
		
		StringBuilder sb = new StringBuilder(appName);
		if( title != null ) {
			sb.append( " - " ).append(title);
		}
		
		getActionBar().setTitle(sb.toString());
	}

}