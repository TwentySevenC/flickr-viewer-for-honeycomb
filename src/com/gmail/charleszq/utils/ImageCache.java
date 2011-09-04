package com.gmail.charleszq.utils;

import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;

import android.graphics.Bitmap;

public final class ImageCache {

	private static Map<String, Bitmap> cache = new ConcurrentHashMap<String, Bitmap>();
	private static Queue<String> queue = new LinkedList<String>();

	public static int CACHE_SIZE = Constants.DEF_CACHE_SIZE;

	public static void dispose() {
		for (Bitmap bm : cache.values()) {
			if (bm != null) {
				bm.recycle();
			}
		}
		cache.clear();
	}

	public static void saveToCache(String url, Bitmap bitmap) {

		if (url == null || bitmap == null) {
			return;
		}

		if (cache.size() >= CACHE_SIZE) {
			String firstKey = queue.poll();
			Bitmap toBeRemoved = cache.get(firstKey);
			cache.remove(firstKey);
			if (toBeRemoved != null) {
				toBeRemoved.recycle();
				toBeRemoved = null;
			}
		}

		cache.put(url, bitmap);
		queue.add(url);
	}

	public static Bitmap getFromCache(String url) {
		return cache.get(url);
	}
}
