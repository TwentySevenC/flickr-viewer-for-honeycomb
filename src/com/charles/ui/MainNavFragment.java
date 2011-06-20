/**
 * 
 */

package com.charles.ui;

import com.charles.FlickrViewerApplication;
import com.charles.R;
import com.charles.actions.ShowAuthDialogAction;
import com.charles.actions.ShowFavoritesAction;
import com.charles.actions.ShowInterestingPhotosAction;
import com.charles.actions.ShowPeoplePhotosAction;
import com.charles.event.IImageDownloadDoneListener;
import com.charles.task.GetUserInfoTask;
import com.charles.utils.Constants;
import com.charles.utils.ImageUtils;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents the fragment to be shown at the left side of the screen, which
 * acts as the main menu.
 * 
 * @author charles
 */
public class MainNavFragment extends Fragment {

    /**
     * the handler.
     */
    private Handler mHandler = new Handler();

    /**
     * The item click listner to handle the main menus.
     */
    private OnItemClickListener mItemClickListener = new OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position,
                long id) {
            ListView list = (ListView) parent;
            list.setItemChecked(position, true);

            FlickrViewerApplication app = (FlickrViewerApplication) getActivity()
                    .getApplication();
            String token = app.getFlickrToken();

            switch (position) {
                case 0:
                ShowInterestingPhotosAction action = new ShowInterestingPhotosAction(
                        getActivity());
                action.execute();
                break;
            case 1:
                ShowPeoplePhotosAction photosAction = new ShowPeoplePhotosAction(
                        getActivity(), null);
                if (token == null) {
                    ShowAuthDialogAction ia = new ShowAuthDialogAction(
                            getActivity(), photosAction);
                    ia.execute();
                } else {
                    photosAction.execute();
                }

                break;
            case 2:
                ContactsFragment fragment = new ContactsFragment();
                FragmentManager fm = getFragmentManager();
                FragmentTransaction ft = fm.beginTransaction();
                ft.replace(R.id.main_area, fragment);
                ft.commitAllowingStateLoss();
                break;
            case 3:
                ShowFavoritesAction favAction = new ShowFavoritesAction(
                        getActivity(), null);
                if (token == null) {
                    ShowAuthDialogAction showAuthAction = new ShowAuthDialogAction(
                            getActivity(), favAction);
                    showAuthAction.execute();
                } else {
                    favAction.execute();
                }
                break;
        }
    }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View view = inflater.inflate(com.charles.R.layout.main_nav, null);

        // main menu
        final ListView list = (ListView) view.findViewById(R.id.list_menu);
        NavMenuAdapter adapter = new NavMenuAdapter(getActivity(),
                createNavCommandItems());
        list.setAdapter(adapter);
        list.setOnItemClickListener(mItemClickListener);

        handleUserPanel(view);

        return view;
    }

    /**
     * Deals with the user info panel in the main navigation page, showing the
     * user information, and fetch the buddy icons.
     * 
     * @param view the root view of this fragment, that is, the view returned
     *            from <code>onCreateView</code>.
     */
    protected void handleUserPanel(View view) {

        // the user panel.
        final View userPanel = view.findViewById(R.id.user_panel);
        final FlickrViewerApplication app = (FlickrViewerApplication) getActivity()
                .getApplication();
        String token = app.getFlickrToken();
        userPanel.setVisibility(token == null ? View.INVISIBLE : View.VISIBLE);
        if (token != null) {
            TextView userText = (TextView) view.findViewById(R.id.user_name);
            userText.setText(app.getUserName());
        }

        // logout button
        ImageView button = (ImageView) view.findViewById(R.id.buttonLogout);
        button.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(
                        getActivity());
                builder
                        .setMessage(
                                "Some features will not work after logout, are you sure to logout?")
                        .setCancelable(false).setPositiveButton("Yes",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,
                                            int id) {
                                        app.logout();
                                        userPanel.setVisibility(View.INVISIBLE);
                                    }
                                }).setNegativeButton("No",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,
                                            int id) {
                                        dialog.cancel();
                                    }
                                });
                AlertDialog alert = builder.create();
                alert.show();
            }
        });

        // buddy icon
        ImageView iconImage = (ImageView) view.findViewById(R.id.user_icon);
        Bitmap cachedIcon = getBuddyIconFromCache();
        if (cachedIcon != null) {
            iconImage.setImageBitmap(cachedIcon);
        } else {
            String userId = app.getUserId();
            GetUserInfoTask task = new GetUserInfoTask(iconImage, null,
                    mImageDownloadedListener);
            task.execute(userId);
        }
    }

    /**
     * Gets the buddy icon from the sd card, which was cached before.
     * 
     * @return
     */
    private Bitmap getBuddyIconFromCache() {
        File root = new File(Environment.getExternalStorageDirectory(),
                Constants.SD_CARD_FOLDER_NAME);
        File buddyIconFile = new File(root,
                Constants.FLICKR_BUDDY_IMAGE_FILE_NAME);
        if (!buddyIconFile.exists()) {
            return null;
        }
        return BitmapFactory.decodeFile(buddyIconFile.getAbsolutePath());
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_main_nav, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_setting:
                Fragment frag = new SettingsFragment();
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.replace(R.id.main_area, frag);
                ft.addToBackStack("Settings");
                ft.commit();

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * The image download listener to save the buddy icons.
     */
    private IImageDownloadDoneListener mImageDownloadedListener = new IImageDownloadDoneListener() {

        @Override
        public void onImageDownloaded(final Bitmap bitmap) {
            File root = new File(Environment.getExternalStorageDirectory(),
                    Constants.SD_CARD_FOLDER_NAME);
            if (!root.exists() && !root.mkdirs()) {
                return;
            }

            final File buddyIconFile = new File(root,
                    Constants.FLICKR_BUDDY_IMAGE_FILE_NAME);
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    ImageUtils.saveImageToFile(buddyIconFile, bitmap);
                }
            });
        }
    };

    /**
     * The main menu adapter.
     */
    private static class NavMenuAdapter extends BaseAdapter {

        private List<CommandItem> commands;
        private Context context;

        public NavMenuAdapter(Context context, List<CommandItem> commands) {
            this.commands = commands;
            this.context = context;
        }

        @Override
        public int getCount() {
            return commands.size();
        }

        @Override
        public Object getItem(int arg0) {
            return commands.get(arg0);
        }

        @Override
        public long getItemId(int arg0) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = convertView;
            if (view == null) {
                LayoutInflater li = LayoutInflater.from(context);
                view = li.inflate(R.layout.main_nav_item, null);
            }
            ViewHolder holder = (ViewHolder) view.getTag();
            CommandItem command = (CommandItem) getItem(position);

            ImageView image;
            TextView text;
            if (holder == null) {
                image = (ImageView) view.findViewById(R.id.nav_item_image);
                text = (TextView) view.findViewById(R.id.nav_item_title);
                holder = new ViewHolder();
                holder.image = image;
                holder.text = text;
                view.setTag(holder);
            } else {
                image = holder.image;
                text = holder.text;
            }
            image.setImageResource(command.imageResId);
            text.setText(command.title);

            return view;
        }

    }

    private static class ViewHolder {
        ImageView image;
        TextView text;
    }

    private static class CommandItem {
        int imageResId;
        String title;
    }

    private List<CommandItem> createNavCommandItems() {
        List<CommandItem> list = new ArrayList<CommandItem>();
        CommandItem item = new CommandItem();
        item.imageResId = R.drawable.interesting;
        item.title = "Interesting Photos";
        list.add(item);

        item = new CommandItem();
        item.imageResId = R.drawable.photos;
        item.title = "My Photos";
        list.add(item);

        item = new CommandItem();
        item.imageResId = R.drawable.contacts;
        item.title = "My Contacts";
        list.add(item);

        item = new CommandItem();
        item.imageResId = R.drawable.myfavorite;
        item.title = "My Favorites";
        list.add(item);

        return list;
    }

}
