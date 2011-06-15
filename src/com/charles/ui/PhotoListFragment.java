/**
 * 
 */

package com.charles.ui;

import com.aetrion.flickr.photos.Exif;
import com.aetrion.flickr.photos.Photo;
import com.aetrion.flickr.photos.PhotoList;
import com.charles.FlickrViewerApplication;
import com.charles.R;
import com.charles.dataprovider.PaginationPhotoListDataProvider;
import com.charles.event.IExifListener;
import com.charles.event.IPhotoListReadyListener;
import com.charles.task.AsyncPhotoListTask;
import com.charles.task.GetBigImageAndExifTask;
import com.charles.task.ImageDownloadTask;
import com.charles.utils.Constants;
import com.charles.utils.ImageCache;
import com.charles.utils.ImageUtils.DownloadedDrawable;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Collection;

/**
 * @author charles
 */
public class PhotoListFragment extends Fragment implements
        AdapterView.OnItemClickListener, IExifListener, IPhotoListReadyListener {

    private static final String BUNDLE_ATTR_DATA_PROVIDER = "data.provider";
    private static final String TAG = PhotoListFragment.class.getSimpleName();

    private PhotoList mPhotoList;
    private MyAdapter mGridAdapter;
    private GridView mGridView;

    private int mCurrentGridColumnCount = Constants.DEF_GRID_COL_COUNT;

    /**
     * The current page number.
     */
    private int mCurrentPageNumber = 1;

    /**
     * The task to to fetch exif information and the big image.
     */
    private GetBigImageAndExifTask mExifTask;

    /**
     * The photo list data provider.
     */
    private PaginationPhotoListDataProvider mPhotoListDataProvider;

    /**
     * The async task to fetch photo list.
     */
    private AsyncPhotoListTask mPhotoListTask = null;

    /**
     * Default constructor.
     */
    public PhotoListFragment() {
        mPhotoList = new PhotoList();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setHasOptionsMenu(true);
    }

    /**
     * Constructor with a list of photos.
     * 
     * @param photoList
     */
    public PhotoListFragment(PhotoList photoList,
            PaginationPhotoListDataProvider photoListDataProvider) {
        this.mPhotoList = photoList;
        this.mPhotoListDataProvider = photoListDataProvider;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        
        //handle the case that this fragment is created by the os.
        if (savedInstanceState != null) {
            PaginationPhotoListDataProvider savedDataProvider = (PaginationPhotoListDataProvider) savedInstanceState
                    .getSerializable(BUNDLE_ATTR_DATA_PROVIDER);
            if (mPhotoListDataProvider == null) {
                mPhotoListDataProvider = savedDataProvider;
                this.runPhotoListTask();
            }
        }
        View mRootContainer = (View) inflater.inflate(R.layout.photo_grid_view,
                null);

        // grid view.
        mGridView = (GridView) mRootContainer.findViewById(R.id.grid);
        FlickrViewerApplication app = (FlickrViewerApplication) getActivity()
                .getApplication();
        mCurrentGridColumnCount = app.getGridNumColumns();
        mGridView.setNumColumns(mCurrentGridColumnCount);
        mGridAdapter = new MyAdapter(getActivity(), mPhotoList);
        mGridView.setAdapter(mGridAdapter);
        mGridView.setOnItemClickListener(this);
        return mRootContainer;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_photo_list, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        FlickrViewerApplication app = (FlickrViewerApplication) getActivity().getApplication();
        int pageSize = app.getPageSize();

        switch (item.getItemId()) {
            case R.id.menu_item_previous_page:
                if (mCurrentPageNumber <= 1) {
                    Toast.makeText(getActivity(), "First page right now",
                            Toast.LENGTH_SHORT).show();
                } else {
                    mPhotoListDataProvider.setPageNumber(--mCurrentPageNumber);
                    mPhotoListDataProvider.setPageSize(pageSize);
                    runPhotoListTask();
                }
                return true;
            case R.id.menu_item_next_page:
                mPhotoListDataProvider.setPageNumber(++mCurrentPageNumber);
                mPhotoListDataProvider.setPageSize(pageSize);
                runPhotoListTask();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Fetches the photo list in another thread.
     */
    private void runPhotoListTask() {
        if (mPhotoListTask != null) {
            mPhotoListTask.cancel(true);
        }
        mPhotoListTask = new AsyncPhotoListTask(getActivity(), mPhotoListDataProvider, this);
        mPhotoListTask.execute();
    }

    private static class MyAdapter extends BaseAdapter {

        private PhotoList mPhotoList;
        private Context mContext;

        public MyAdapter(Context context, PhotoList mPhotoList) {
            this.mPhotoList = mPhotoList;
            this.mContext = context;
        }

        @Override
        public int getCount() {
            return mPhotoList.size();
        }

        @Override
        public Object getItem(int arg0) {
            return mPhotoList.get(arg0);
        }

        @Override
        public long getItemId(int arg0) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            View view = convertView;
            if (view == null) {
                LayoutInflater li = LayoutInflater.from(mContext);
                view = li.inflate(R.layout.interesting_list_item, null);
            }

            Photo photo = (Photo) getItem(position);

            ImageView photoImage;
            TextView titleView;

            ViewHolder holder = (ViewHolder) view.getTag();
            if (holder == null) {
                photoImage = (ImageView) view.findViewById(R.id.small_img);
                titleView = (TextView) view.findViewById(R.id.title);

                holder = new ViewHolder();
                holder.image = photoImage;
                holder.titleView = titleView;
                view.setTag(holder);

            } else {
                photoImage = holder.image;
                titleView = holder.titleView;
            }
            titleView.setText(photo.getTitle());
            photoImage.setScaleType(ScaleType.CENTER_CROP);

            Drawable drawable = photoImage.getDrawable();
            String smallUrl = photo.getSmallUrl();
            if (drawable != null && drawable instanceof DownloadedDrawable) {
                ImageDownloadTask task = ((DownloadedDrawable) drawable)
                        .getBitmapDownloaderTask();
                if (!smallUrl.equals(task)) {
                    task.cancel(true);
                }
            }

            if (smallUrl == null) {
                photoImage.setImageDrawable(null);
            } else {
                Bitmap cacheBitmap = ImageCache.getFromCache(smallUrl);
                if (cacheBitmap != null) {
                    photoImage.setImageBitmap(cacheBitmap);
                } else {
                    ImageDownloadTask task = new ImageDownloadTask(photoImage);
                    drawable = new DownloadedDrawable(task);
                    photoImage.setImageDrawable(drawable);
                    task.execute(smallUrl);
                }
            }

            return view;
        }

        private static class ViewHolder {
            ImageView image;
            TextView titleView;
        }

    }

    private ProgressDialog mProgresDialog;
    private Photo mSelectedPhoto;

    @Override
    public void onItemClick(AdapterView<?> parentView, View view, int position,
            long id) {
        mSelectedPhoto = (Photo) mGridAdapter.getItem(position);
        if (mExifTask != null) {
            mExifTask.cancel(true);
        }

        mExifTask = new GetBigImageAndExifTask(mSelectedPhoto, this);
        mProgresDialog = ProgressDialog.show(getActivity(), "",
                "Loading photo details ...");
        mProgresDialog.setCancelable(true);
        mProgresDialog.setCanceledOnTouchOutside(true);
        mProgresDialog.setOnCancelListener(new OnCancelListener() {

            @Override
            public void onCancel(DialogInterface dialog) {
                if (mExifTask != null && !mExifTask.isCancelled()) {
                    mExifTask.cancel(true);
                }
            }
        });
        mExifTask.execute();

    }

    @Override
    public void onExifInfoFetched(Bitmap bitmap, Collection<Exif> exifs) {
        if (mProgresDialog != null && mProgresDialog.isShowing()) {
            mProgresDialog.dismiss();
        }

        ViewImageDetailFragment fragment = new ViewImageDetailFragment(
                mSelectedPhoto, bitmap, exifs);
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.main_area, fragment);
        ft.addToBackStack("Detail Image");
        ft.commitAllowingStateLoss();
    }

    @SuppressWarnings("unchecked")
    @Override
    public void onPhotoListReady(PhotoList list) {
        if (list == null || list.isEmpty()) {
            return;
        }

        mPhotoList.clear();
        for (int i = 0; i < list.size(); i++) {
            mPhotoList.add(list.get(i));
        }
        mGridAdapter.notifyDataSetChanged();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(BUNDLE_ATTR_DATA_PROVIDER, mPhotoListDataProvider);
        Log.d(TAG, "data provider is saved.");
    }

}
