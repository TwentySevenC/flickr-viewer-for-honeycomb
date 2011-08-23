/*
 * Created on Aug 23, 2011
 *
 * Copyright (c) Sybase, Inc. 2011   
 * All rights reserved.                                    
 */

package com.gmail.charleszq;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Point;
import android.os.Bundle;
import android.util.Log;

import com.gmail.charleszq.utils.ImageUtils;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;

/**
 * Represents the activity to show the map view.
 * 
 * @author charles
 * 
 */
public class PhotoLocationActivity extends MapActivity {

	private static final String TAG = PhotoLocationActivity.class.getName();

	public static final String LAT_VAL = "lat"; //$NON-NLS-1$
	public static final String LONG_VAL = "long"; //$NON-NLS-1$

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.google.android.maps.MapActivity#isRouteDisplayed()
	 */
	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.google.android.maps.MapActivity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		setContentView(R.layout.photo_map);
		Intent intent = getIntent();
		int lat = intent.getExtras().getInt(LAT_VAL);
		int lng = intent.getExtras().getInt(LONG_VAL);

		Log.d(TAG, "location: " + lat + ", " + lng); //$NON-NLS-1$//$NON-NLS-2$
		MapView map = (MapView) findViewById(R.id.mapView);

		MapController mc = map.getController();
		GeoPoint p = new GeoPoint(lat, lng);

		mc.setCenter(p);
		mc.setZoom(15);

		MapOverlay mapOverlay = new MapOverlay(this, p);
		List<Overlay> listOfOverlays = map.getOverlays();
		listOfOverlays.clear();
		listOfOverlays.add(mapOverlay);

		map.invalidate();
	}

	private static class MapOverlay extends com.google.android.maps.Overlay {

		private GeoPoint mPosition;
		private Context mContext;

		MapOverlay(Context context, GeoPoint p) {
			this.mPosition = p;
			this.mContext = context;
		}

		@Override
		public boolean draw(Canvas canvas, MapView mapView, boolean shadow,
				long when) {
			super.draw(canvas, mapView, shadow);

			// ---translate the GeoPoint to screen pixels---
			Point screenPts = new Point();
			mapView.getProjection().toPixels(mPosition, screenPts);

			// ---add the marker---
			Bitmap bmp = BitmapFactory.decodeResource(mContext.getResources(),
					R.drawable.pushpin);
			Bitmap resizedBitmap = ImageUtils.resize(bmp, 0.3f);
			canvas.drawBitmap(resizedBitmap, screenPts.x, screenPts.y - 150,
					null);
			return true;
		}
	}

}
