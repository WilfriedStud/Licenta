package ro.usv.datacollectionandanalysisoniotsystemsclient.ui.tab;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import ro.usv.datacollectionandanalysisoniotsystemsclient.R;
import ro.usv.datacollectionandanalysisoniotsystemsclient.communication.AzureIotHubConnection;
import ro.usv.datacollectionandanalysisoniotsystemsclient.communication.c2d.NotificationCallback;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link NotificationsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NotificationsFragment extends Fragment {

    private TextView[] notificationsList;
    private int index = 0;

    public NotificationsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment Notifications.
     */
    public static NotificationsFragment newInstance() {
        NotificationsFragment fragment = new NotificationsFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public void addNotification(String message) {
        for (int i = 1; i < notificationsList.length; i++) {
            notificationsList[i - 1].setText(notificationsList[i].getText());
        }
        notificationsList[notificationsList.length - 1].setText(message);
    }

    @Override
    public void onStart() {
        super.onStart();
        AzureIotHubConnection.getInstance().addNotificationCallback(new NotificationCallback(this));
        notificationsList = new TextView[]{
                requireView().findViewById(R.id.textView),
                requireView().findViewById(R.id.textView2),
                requireView().findViewById(R.id.textView3),
                requireView().findViewById(R.id.textView4),
                requireView().findViewById(R.id.textView5),
                requireView().findViewById(R.id.textView6),
                requireView().findViewById(R.id.textView7)
        };
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_notifications, container, false);
    }
}