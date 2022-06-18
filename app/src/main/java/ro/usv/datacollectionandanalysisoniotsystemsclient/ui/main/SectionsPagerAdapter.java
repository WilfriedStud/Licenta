package ro.usv.datacollectionandanalysisoniotsystemsclient.ui.main;

import android.content.Context;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import ro.usv.datacollectionandanalysisoniotsystemsclient.CloudData;
import ro.usv.datacollectionandanalysisoniotsystemsclient.LocalData;
import ro.usv.datacollectionandanalysisoniotsystemsclient.Notifications;
import ro.usv.datacollectionandanalysisoniotsystemsclient.R;

/**
 * A [FragmentPagerAdapter] that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
public class SectionsPagerAdapter extends FragmentPagerAdapter {

    @StringRes
    private static final int[] TAB_TITLES = new int[] {R.string.tab_text_1, R.string.tab_text_2, R.string.tab_text_3};
    private final Context mContext;

    public SectionsPagerAdapter(Context context, FragmentManager fm) {
        super(fm);
        mContext = context;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new LocalData();
            case 1:
                return new CloudData();
            case 2:
                return new Notifications();
            default:
                throw new IllegalArgumentException("Cannot instantiate not existing tab");
        }
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return mContext.getResources().getString(TAB_TITLES[position]);
    }

    @Override
    public int getCount() {
        // Show 3 total pages.
        return 3;
    }
}