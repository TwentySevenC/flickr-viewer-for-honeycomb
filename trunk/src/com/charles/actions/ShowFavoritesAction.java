/**
 * 
 */

package com.charles.actions;

import com.aetrion.flickr.photos.Photo;
import com.charles.FlickrViewerApplication;
import com.charles.R;
import com.charles.dataprovider.FavoritePhotosDataProvider;
import com.charles.dataprovider.IPhotoListDataProvider;
import com.charles.dataprovider.PaginationPhotoListDataProvider;
import com.charles.event.DefaultPhotoListReadyListener;
import com.charles.event.IPhotoListReadyListener;
import com.charles.task.AsyncPhotoListTask;
import com.charles.ui.comp.IContextMenuHandler;

import android.app.Activity;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView.AdapterContextMenuInfo;

/**
 * @author charles
 */
public class ShowFavoritesAction extends ActivityAwareAction {

    private String mUserId;

    /**
     * @param activity
     */
    public ShowFavoritesAction(Activity activity, String userId) {
        super(activity);
        this.mUserId = userId;
    }

    /*
     * (non-Javadoc)
     * @see com.charles.actions.IAction#execute()
     */
    @Override
    public void execute() {
        FlickrViewerApplication app = (FlickrViewerApplication) mActivity
                .getApplication();
        String token = app.getFlickrToken();
        if (mUserId == null) {
            mUserId = app.getUserId();
        }
        PaginationPhotoListDataProvider dp = new FavoritePhotosDataProvider(
                mUserId, token);
        IContextMenuHandler menuHandler = new FavContextMenuHandler(mActivity, dp);
        IPhotoListReadyListener photoReadyListener = new DefaultPhotoListReadyListener(mActivity,
                dp, menuHandler);
        AsyncPhotoListTask task = new AsyncPhotoListTask(mActivity, dp, photoReadyListener,
                mActivity.getResources().getString(R.string.task_loading_favs));
        task.execute();
    }

    private class FavContextMenuHandler implements IContextMenuHandler {

        private Activity mActivity;
        private IPhotoListDataProvider mDataProvider;

        /**
         * Constructor.
         * 
         * @param activity
         */
        FavContextMenuHandler(Activity activity, IPhotoListDataProvider dataProvider) {
            this.mActivity = activity;
            this.mDataProvider = dataProvider;
        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View view, ContextMenuInfo info) {
            MenuInflater mi = mActivity.getMenuInflater();
            int start = menu.size();
            mi.inflate(R.menu.menu_remove_fav, menu);
            for (int i = start; i < menu.size(); i++) {
                menu.getItem(i).setOnMenuItemClickListener(this);
            }
        }

        @Override
        public boolean onMenuItemClick(MenuItem item) {
            ContextMenuInfo info = item.getMenuInfo();
            int pos = ((AdapterContextMenuInfo) info).position;
            try {
                Photo photo = (Photo) mDataProvider.getPhotoList().get(pos);
                RemoveFavAction action = new RemoveFavAction(mActivity, photo.getId());
                action.execute();
                return true;
            } catch (Exception e) {
                return false;
            }
        }

    }

}
