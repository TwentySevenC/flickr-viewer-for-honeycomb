
package com.gmail.charleszq.utils;

import android.graphics.Bitmap;
import android.util.Log;

import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;

public final class ImageCache {
	
	private static final String TAG = ImageCache.class.getName();

    private static Map<String, Bitmap> cache = new ConcurrentHashMap<String, Bitmap>();
    private static Queue<String> queue = new LinkedList<String>();

    public static int CACHE_SIZE = Constants.DEF_CACHE_SIZE;

    public static void dispose() {
        cache.clear();
    }

    public static void saveToCache(String url, Bitmap bitmap) {

        if (url == null || bitmap == null) {
            return;
        }

        Log.d(TAG, "Cache size: " + CACHE_SIZE);  //$NON-NLS-1$
        if (cache.size() >= CACHE_SIZE) {
            String firstKey = queue.poll();
            cache.remove(firstKey);
        }

        cache.put(url, bitmap);
        queue.add(url);
    }

    public static Bitmap getFromCache(String url) {
        return cache.get(url);
    }
}
