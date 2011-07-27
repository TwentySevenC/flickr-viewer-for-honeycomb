/**
 * 
 */

package com.gmail.charleszq.ui;

import com.aetrion.flickr.people.User;
import com.aetrion.flickr.photos.Exif;
import com.aetrion.flickr.photos.Photo;
import com.aetrion.flickr.tags.Tag;
import com.gmail.charleszq.FlickrViewerApplication;
import com.gmail.charleszq.R;
import com.gmail.charleszq.actions.IAction;
import com.gmail.charleszq.actions.SaveImageWallpaperAction;
import com.gmail.charleszq.actions.SharePhotoAction;
import com.gmail.charleszq.actions.ShowAuthDialogAction;
import com.gmail.charleszq.actions.ShowPeoplePhotosAction;
import com.gmail.charleszq.actions.ShowWriteCommentAction;
import com.gmail.charleszq.event.IExifListener;
import com.gmail.charleszq.event.IUserCommentsFetchedListener;
import com.gmail.charleszq.model.UserComment;
import com.gmail.charleszq.task.AddPhotoAsFavoriteTask;
import com.gmail.charleszq.task.GetPhotoCommentsTask;
import com.gmail.charleszq.task.GetPhotoExifTask;
import com.gmail.charleszq.task.ImageDownloadTask;
import com.gmail.charleszq.ui.comp.PhotoDetailActionBar;
import com.gmail.charleszq.utils.ImageCache;
import com.gmail.charleszq.utils.ImageUtils.DownloadedDrawable;
import com.gmail.charleszq.utils.StringUtils;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * The fragment to view the detail information of a picture, including exif,
 * author, title and comments.
 * 
 * @author charles
 */
public class ViewImageDetailFragment extends Fragment implements
        IUserCommentsFetchedListener, IExifListener {

    private static final String TAG = ViewImageDetailFragment.class
            .getSimpleName();

    private static final String PHOTO_ID_ATTR = "photo.id"; //$NON-NLS-1$
    private static final String PHOTO_TITLE_ATTR = "photo.title"; //$NON-NLS-1$
    private static final String PHOTO_OWNER_ID = "photo.owner.id"; //$NON-NLS-1$
    private static final String PHOTO_DESC_ATTR = "photo.desc"; //$NON-NLS-1$

    private WeakReference<Bitmap> mBitmapRef;
    private Photo mCurrentPhoto;
    private UserCommentAdapter mCommentAdapter;
    private ExifAdapter mExifAdapter;

    /**
     * The user comments of this photo.
     */
    private List<UserComment> mComments = new ArrayList<UserComment>();
    private List<Exif> mExifs = new ArrayList<Exif>();

    private boolean mShowingExif = true;
    private ViewSwitcher mViewSwitcher;
    private View mCommentProgressBar;
    private View mExifProgressBar;

    /**
     * Default constructor for the framework.
     */
    public ViewImageDetailFragment() {
        mCurrentPhoto = new Photo();
    }

    /**
     * Constructor.
     * 
     * @param photo
     * @param bitmap
     * @param exifs
     */
    public ViewImageDetailFragment(Photo photo, Bitmap bitmap) {
        this.mCurrentPhoto = photo;
        mBitmapRef = new WeakReference<Bitmap>(bitmap);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_view_image, menu);
        inflater.inflate(R.menu.menu_view_big_image, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_share:
                IAction action = new SharePhotoAction(getActivity(), mBitmapRef
                        .get(), this.mCurrentPhoto.getUrl());
                action.execute();
                return true;
            case R.id.menu_item_write_comment:
                FlickrViewerApplication app = (FlickrViewerApplication) getActivity()
                        .getApplication();
                String token = app.getFlickrToken();
                ShowWriteCommentAction commentAction = new ShowWriteCommentAction(
                        getActivity(), mCurrentPhoto.getId());
                if (token == null) {
                    ShowAuthDialogAction act = new ShowAuthDialogAction(
                            getActivity(), commentAction);
                    act.execute();
                } else {
                    commentAction.execute();
                }
                return true;
            case R.id.menu_item_switch:
                if (mShowingExif) {
                    mViewSwitcher.setInAnimation(AnimationUtils.loadAnimation(getActivity(),
                            R.anim.push_right_in));
                    mViewSwitcher.setOutAnimation(AnimationUtils.loadAnimation(getActivity(),
                            R.anim.push_left_out));
                    item.setTitle(R.string.menu_show_exif);
                    mViewSwitcher.showNext();
                    // mViewSwitcher.animate().setDuration(2000).rotationY(360f);
                } else {
                    mViewSwitcher.setInAnimation(AnimationUtils.loadAnimation(getActivity(),
                            R.anim.push_left_in));
                    mViewSwitcher.setOutAnimation(AnimationUtils.loadAnimation(getActivity(),
                            R.anim.push_right_out));
                    item.setTitle(R.string.menu_show_comment);
                    mViewSwitcher.showPrevious();
                    // mViewSwitcher.animate().setDuration(2000).rotationY(0f);
                }
                mShowingExif = !mShowingExif;
                return true;
            case R.id.menu_item_add_as_fav:
                AddPhotoAsFavoriteTask task = new AddPhotoAsFavoriteTask(
                        getActivity());
                task.execute(mCurrentPhoto.getId());
                return true;
            case R.id.menu_item_show_owner_photos:
                ShowPeoplePhotosAction showOwnerPhotosAction = new ShowPeoplePhotosAction(
                        getActivity(), mCurrentPhoto.getOwner().getId(),
                        mCurrentPhoto.getOwner().getUsername());
                showOwnerPhotosAction.execute();
                return true;
            case R.id.menu_item_view_big_photo:
                FragmentManager fm = getActivity().getFragmentManager();
                ViewBigImageFragment fragment = new ViewBigImageFragment(
                        mCurrentPhoto);
                FragmentTransaction ft = fm.beginTransaction();
                ft.replace(R.id.main_area, fragment);
                ft.addToBackStack("BigImage"); //$NON-NLS-1$
                ft.commitAllowingStateLoss();
                return true;
            case R.id.menu_item_save:
                SaveImageWallpaperAction sa = new SaveImageWallpaperAction(
                        getActivity(), mCurrentPhoto);
                sa.execute();
                return true;
            case R.id.menu_item_wallpaper:
                SaveImageWallpaperAction wallAction = new SaveImageWallpaperAction(
                        getActivity(), mCurrentPhoto, true);
                wallAction.execute();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.view_image_detail, null);
        ImageView image = (ImageView) view.findViewById(R.id.image);
        if (mBitmapRef != null && mBitmapRef.get() != null) {
            image.setImageBitmap(mBitmapRef.get());
        }
        image.setFocusable(true);
        image.setClickable(true);

        if (savedInstanceState != null) {
            String photoId = savedInstanceState.getString(PHOTO_ID_ATTR);
            String photoTitle = savedInstanceState.getString(PHOTO_TITLE_ATTR);
            String ownerId = savedInstanceState.getString(PHOTO_OWNER_ID);
            String desc = savedInstanceState.getString(PHOTO_DESC_ATTR);
            mCurrentPhoto.setId(photoId);
            mCurrentPhoto.setTitle(photoTitle);
            mCurrentPhoto.setDescription(desc);
            User user = new User();
            user.setId(ownerId);
            mCurrentPhoto.setOwner(user);
        }

        // photo title.
        TextView photoTitle = (TextView) view.findViewById(R.id.titlebyauthor);
        photoTitle.setText(mCurrentPhoto.getTitle());

        // photo description
        TextView photoDesc = (TextView) view.findViewById(R.id.photo_desc);
        if (mCurrentPhoto.getDescription() == null) {
            mCurrentPhoto.setDescription(getActivity().getResources().getString(
                    R.string.no_photo_desc));
        }
//        photoDesc.setText(mCurrentPhoto.getDescription());
        StringUtils.formatHtmlString(mCurrentPhoto.getDescription(), photoDesc);

        // tags
        TextView tagsText = (TextView) view.findViewById(R.id.photo_tags);
        Collection<Tag> tags = mCurrentPhoto.getTags();
        if (tags == null || tags.isEmpty()) {
            tagsText.setVisibility(View.GONE);
        } else {
            StringBuilder sb = new StringBuilder();
            sb.append(getActivity().getResources().getString(R.string.msg_tags));
            for (Tag tag : tags) {
                sb.append(tag.getValue()).append(" "); //$NON-NLS-1$
            }
            tagsText.setText(sb.toString());
            tagsText.setSelected(true);
        }

        // exif list.
        ListView list = (ListView) view.findViewById(R.id.exifList);
        mExifAdapter = new ExifAdapter(getActivity(), mExifs);
        list.setAdapter(mExifAdapter);
        mExifProgressBar = view.findViewById(R.id.exifProgressBar);

        // comment list.
        ListView commentListView = (ListView) view
                .findViewById(R.id.listComments);
        mCommentAdapter = new UserCommentAdapter(getActivity(), this.mComments);
        commentListView.setAdapter(mCommentAdapter);

        // view swithcer
        mViewSwitcher = (ViewSwitcher) view.findViewById(R.id.switcher);

        // comment progress bar
        mCommentProgressBar = view.findViewById(R.id.commentProgressBar);

        // get user information.
        PhotoDetailActionBar pBar = (PhotoDetailActionBar) view
                .findViewById(R.id.user_action_bar);
        pBar.setUser(mCurrentPhoto.getOwner());

        return view;
    }

    private GetPhotoCommentsTask mPhotoCommentTask;
    private GetPhotoExifTask mExifTask;

    @Override
    public void onStart() {
        super.onStart();
        String photoId = mCurrentPhoto.getId();
        Log.d(TAG, "Current photo id: " + photoId); //$NON-NLS-1$

        // exif
        mExifTask = new GetPhotoExifTask(this);
        mExifTask.execute(photoId);

        // comments
        mPhotoCommentTask = new GetPhotoCommentsTask(this);
        mPhotoCommentTask.execute(photoId);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(PHOTO_ID_ATTR, mCurrentPhoto.getId());
        outState.putString(PHOTO_TITLE_ATTR, mCurrentPhoto.getTitle());
        outState.putString(PHOTO_OWNER_ID, mCurrentPhoto.getOwner().getId());
        outState.putString(PHOTO_DESC_ATTR, mCurrentPhoto.getDescription());
    }

    @Override
    public void onPause() {
        if (mPhotoCommentTask != null) {
            mPhotoCommentTask.cancel(true);
        }
        super.onPause();
    }

    /**
     * Represents the adapter for the user comment list.
     */
    private static class UserCommentAdapter extends BaseAdapter {

        private List<UserComment> mComments;
        private Context mContext;

        UserCommentAdapter(Context context, List<UserComment> comments) {
            this.mComments = comments;
            this.mContext = context;
        }

        @Override
        public int getCount() {
            return mComments.size();
        }

        @Override
        public Object getItem(int arg0) {
            return mComments.get(arg0);
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
                view = li.inflate(R.layout.user_comment_item, null);
            }

            ImageView buddyIcon;
            TextView author, commentDate, comment;
            ViewHolder holder = (ViewHolder) view.getTag();
            if (holder == null) {
                buddyIcon = (ImageView) view.findViewById(R.id.buddy_icon);
                author = (TextView) view.findViewById(R.id.author);
                comment = (TextView) view.findViewById(R.id.comment);
                comment.setMovementMethod(LinkMovementMethod.getInstance());
                commentDate = (TextView) view.findViewById(R.id.commentDate);

                holder = new ViewHolder();
                holder.image = buddyIcon;
                holder.author = author;
                holder.comment = comment;
                holder.commentDate = commentDate;
                view.setTag(holder);
            } else {
                buddyIcon = holder.image;
                author = holder.author;
                commentDate = holder.commentDate;
                comment = holder.comment;
            }

            UserComment userComment = (UserComment) getItem(position);
            author.setText(userComment.getUserName());

            StringUtils.formatHtmlString(userComment.getCommentText(), comment);
            commentDate.setText(userComment.getCommentDateString());

            Drawable drawable = buddyIcon.getDrawable();
            String smallUrl = userComment.getBuddyIconUrl();
            if (drawable != null && drawable instanceof DownloadedDrawable) {
                ImageDownloadTask task = ((DownloadedDrawable) drawable)
                        .getBitmapDownloaderTask();
                if (!smallUrl.equals(task)) {
                    task.cancel(true);
                }
            }

            if (smallUrl == null) {
                buddyIcon.setImageDrawable(null);
            } else {
                Bitmap cacheBitmap = ImageCache.getFromCache(smallUrl);
                if (cacheBitmap != null) {
                    buddyIcon.setImageBitmap(cacheBitmap);
                } else {
                    ImageDownloadTask task = new ImageDownloadTask(buddyIcon);
                    drawable = new DownloadedDrawable(task);
                    buddyIcon.setImageDrawable(drawable);
                    task.execute(smallUrl);
                }
            }

            return view;
        }

    }

    private static class ViewHolder {
        ImageView image;
        TextView author;
        TextView commentDate;
        TextView comment;
    }

    /**
     * The adapter for exif list.
     */
    private static class ExifAdapter extends BaseAdapter {

        private List<Exif> mExifs;
        private Context mContext;

        ExifAdapter(Context context, List<Exif> exifs) {
            this.mExifs = exifs;
            this.mContext = context;
        }

        @Override
        public int getCount() {
            return mExifs.size();
        }

        @Override
        public Object getItem(int position) {
            return mExifs.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = convertView;
            if (view == null) {
                view = new TextView(mContext);
            }
            Exif exif = (Exif) getItem(position);
            if (exif != null) {
                ((TextView) view).setText(exif.getLabel() + " : " //$NON-NLS-1$
                        + exif.getRaw());
            }
            return view;
        }

    }

    @Override
    public void onCommentFetched(List<UserComment> comments) {
        Log.d(TAG, "comments fetched, comment size: " + comments.size()); //$NON-NLS-1$
        this.mComments.clear();
        for (UserComment comment : comments) {
            mComments.add(comment);
        }
        mCommentAdapter.notifyDataSetChanged();
        this.mCommentProgressBar.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onExifInfoFetched(Collection<Exif> exifs) {
        Log.d(TAG, "exif fetched."); //$NON-NLS-1$
        if (exifs == null) {
            mExifProgressBar.setVisibility(View.INVISIBLE);
            return;
        }
        this.mExifs.clear();
        for (Exif exif : exifs) {
            mExifs.add(exif);
        }
        mExifAdapter.notifyDataSetChanged();
        mExifProgressBar.setVisibility(View.INVISIBLE);
    }

}
