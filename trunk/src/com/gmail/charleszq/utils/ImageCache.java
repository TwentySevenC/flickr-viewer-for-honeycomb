package com.gmail.charleszq.utils;

import java.lang.ref.SoftReference;
import java.util.LinkedHashMap;
import java.util.Map;

import android.graphics.Bitmap;

public final class ImageCache {
	public static int CACHE_SIZE = Constants.DEF_CACHE_SIZE;

	private static final Map<String, SoftReference<Bitmap>> cache = new LinkedHashMap<String, SoftReference<Bitmap>>() {
		private static final long serialVersionUID = 1L;

		/* (non-Javadoc)
		 * @see java.util.LinkedHashMap#removeEldestEntry(java.util.Map.Entry)
		 */
		@Override
		protected boolean removeEldestEntry(
				Entry<String, SoftReference<Bitmap>> eldest) {
			return size() > CACHE_SIZE;
		}
		
	};

	public static void dispose() {
		for (SoftReference<Bitmap> bm : cache.values()) {
			if (bm != null && bm.get() != null) {
				bm.get().recycle();
			}
		}
		cache.clear();
	}

	public static void saveToCache(String url, Bitmap bitmap) {
		cache.put(url, new SoftReference<Bitmap>(bitmap));
	}

	public static Bitmap getFromCache(String url) {
		if(!cache.containsKey(url))
            return null;
		Bitmap bitmap = cache.get(url).get();
		if (bitmap == null || bitmap.isRecycled()) {
			cache.remove(url);
			bitmap = null;
		}

		return bitmap;
	}
}
