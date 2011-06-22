/**
 * 
 */

package com.charles.actions;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;

import com.aetrion.flickr.contacts.Contact;
import com.charles.R;
import com.charles.event.IContactsFetchedListener;
import com.charles.task.GetContactsTask;
import com.charles.ui.ContactsFragment;

/**
 * @author charles
 */
public class ShowMyContactsAction extends ActivityAwareAction {

    private IContactsFetchedListener mContactFetchedListener = null;

    public ShowMyContactsAction(Activity activity) {
        super(activity);
    }

    public ShowMyContactsAction(Activity activity,
            IContactsFetchedListener listener) {
        super(activity);
        this.mContactFetchedListener = listener;
    }

    @Override
    public void execute() {
        GetContactsTask task = new GetContactsTask(mActivity,
                mContactFetchedListener == null ? mDefaultListener
                        : mContactFetchedListener);
        task.execute((String) null);
    }

    private IContactsFetchedListener mDefaultListener = new IContactsFetchedListener() {

        @Override
        public void onContactsFetched(Collection<Contact> contacts) {
            FragmentManager fm = mActivity.getFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            List<Contact> ret = new ArrayList<Contact>();
            ret.addAll(contacts);
            ContactsFragment fragment = new ContactsFragment(ret);
            int stackCount = fm.getBackStackEntryCount();
            for (int i = 0; i < stackCount; i++) {
                fm.popBackStack();
            }
            ft.replace(R.id.main_area, fragment);
            ft.commitAllowingStateLoss();
        }
    };
}
