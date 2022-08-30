package ro.usv.datacollectionandanalysisoniotsystemsclient.ui.main;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import ro.usv.datacollectionandanalysisoniotsystemsclient.R;
import ro.usv.datacollectionandanalysisoniotsystemsclient.communication.AzureIotHubConnection;
import ro.usv.datacollectionandanalysisoniotsystemsclient.communication.c2d.NotificationCallback;
import ro.usv.datacollectionandanalysisoniotsystemsclient.ui.tab.CloudDataFragment;
import ro.usv.datacollectionandanalysisoniotsystemsclient.ui.tab.LocalDataFragment;
import ro.usv.datacollectionandanalysisoniotsystemsclient.ui.tab.NotificationsFragment;

/**
 * A [FragmentPagerAdapter] that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
public class SectionsPagerAdapter extends FragmentPagerAdapter {

    @StringRes
    private static final int[] TAB_TITLES = new int[]{R.string.tab_text_1, R.string.tab_text_2, R.string.tab_text_3};
    private final Context context;
    private final LocalDataFragment localData;
    private final CloudDataFragment cloudData;
    private final NotificationsFragment notifications;

    public SectionsPagerAdapter(Context context, FragmentManager fm) {
        super(fm);
        this.context = context;
        this.localData = LocalDataFragment.newInstance();
        this.cloudData = CloudDataFragment.newInstance();
        this.notifications = NotificationsFragment.newInstance();
        AzureIotHubConnection.init(this.context);
        AzureIotHubConnection.getInstance().addNotificationCallback(new NotificationCallback(notifications, cloudData));
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return localData;
            case 1:
                return cloudData;
            case 2:
                return notifications;
            default:
                throw new IllegalArgumentException("Cannot instantiate not existing tab");
        }
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return context.getResources().getString(TAB_TITLES[position]);
    }

    @Override
    public int getCount() {
        // Show 3 total pages.
        return 3;
    }
}