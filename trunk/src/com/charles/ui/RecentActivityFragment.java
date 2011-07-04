/*
 * Created on Jul 4, 2011
 *
 * Copyright (c) Sybase, Inc. 2011   
 * All rights reserved.                                    
 */

package com.charles.ui;

import com.aetrion.flickr.activity.Item;
import com.charles.R;
import com.charles.task.ImageDownloadTask;
import com.charles.task.ImageDownloadTask.ParamType;
import com.charles.utils.ImageCache;
import com.charles.utils.ImageUtils.DownloadedDrawable;

import android.app.Fragment;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * @author qiangz
 */
public class RecentActivityFragment extends Fragment {

    private List<Item> mActivities;

    public RecentActivityFragment() {
        super();
        mActivities = new ArrayList<Item>();
    }

    public RecentActivityFragment(List<Item> activites) {
        super();
        this.mActivities = activites;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.recent_act, null);
        ListView actList = (ListView) v.findViewById(R.id.act_list);
        ActivityAdapter adapter = new ActivityAdapter(this.getActivity(),mActivities);
        actList.setAdapter(adapter);
        return v;
    }
    
    /**
     * The adapter for the recent activity list.
     */
    private static class ActivityAdapter extends BaseAdapter {
        
        private List<Item> mActivities;
        private Context mContext;
        
        ActivityAdapter(Context context, List<Item> items ) {
            this.mContext = context;
            this.mActivities = items;
        }

        @Override
        public int getCount() {
            return mActivities.size();
        }

        @Override
        public Object getItem(int position) {
            return mActivities.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View v = convertView;
            if( v == null ) {
                LayoutInflater li = LayoutInflater.from(mContext);
                v = li.inflate(R.layout.act_item, null);
            }
            
            Item item = (Item) getItem(position);
            String title = item.getTitle();
            String author = item.getOwner();
            
            TextView titleView = (TextView) v.findViewById(R.id.act_photo_title);
            TextView authorView = (TextView) v.findViewById(R.id.act_photo_author);
            
            titleView.setText(title);
            authorView.setText(author);
            
            ImageView image = (ImageView) v.findViewById(R.id.act_photo_image);
            Drawable drawable = image.getDrawable();
            String photoId = item.getId();
            if (drawable != null && drawable instanceof DownloadedDrawable) {
                ImageDownloadTask task = ((DownloadedDrawable) drawable)
                        .getBitmapDownloaderTask();
                if (!photoId.equals(task.getUrl())) {
                    task.cancel(true);
                }
            }

            if (photoId == null) {
                image.setImageDrawable(null);
            } else {
                Bitmap cacheBitmap = ImageCache.getFromCache(photoId);
                if (cacheBitmap != null) {
                    image.setImageBitmap(cacheBitmap);
                } else {
                    ImageDownloadTask task = new ImageDownloadTask(image,ParamType.PHOTO_ID_SMALL_SQUARE);
                    drawable = new DownloadedDrawable(task);
                    image.setImageDrawable(drawable);
                    task.execute(photoId);
                }
            }
            
            return v;
        }
        
    }
    
    

}
