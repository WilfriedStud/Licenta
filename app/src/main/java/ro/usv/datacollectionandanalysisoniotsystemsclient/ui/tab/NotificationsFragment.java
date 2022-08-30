package ro.usv.datacollectionandanalysisoniotsystemsclient.ui.tab;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import ro.usv.datacollectionandanalysisoniotsystemsclient.R;
import ro.usv.datacollectionandanalysisoniotsystemsclient.communication.AzureIotHubConnection;
import ro.usv.datacollectionandanalysisoniotsystemsclient.communication.c2d.NotificationCallback;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link NotificationsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NotificationsFragment extends Fragment {

    private static final int NOTIF_SIZE = 5;

    private final String[] cache = new String[NOTIF_SIZE];
    private final TextView[] notificationsList = new TextView[NOTIF_SIZE];
    private boolean hasStarted = false;

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

        if (hasStarted) {
            if (cache[NOTIF_SIZE - 1] != null) {
                for (int i = 0; i < NOTIF_SIZE; i++) {
                    notificationsList[i].setText(cache[i]);
                    cache[i] = null;
                }
            }

            for (int i = 1; i < NOTIF_SIZE; i++) {
                notificationsList[i - 1].setText(notificationsList[i].getText());
            }
            notificationsList[NOTIF_SIZE - 1].setText(message);
        } else {
            for (int i = 1; i < NOTIF_SIZE; i++) {
                cache[i - 1] = cache[i];
            }
            cache[NOTIF_SIZE - 1] = message;

        }

    }

    @Override
    public void onStart() {
        super.onStart();
        hasStarted = true;
        notificationsList[0] =  requireView().findViewById(R.id.textView);
        notificationsList[1] =  requireView().findViewById(R.id.textView2);
        notificationsList[2] =  requireView().findViewById(R.id.textView3);
        notificationsList[3] =  requireView().findViewById(R.id.textView4);
        notificationsList[4] =  requireView().findViewById(R.id.textView5);
        notificationsList[5] =  requireView().findViewById(R.id.textView6);
        notificationsList[6] =  requireView().findViewById(R.id.textView7);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_notifications, container, false);
    }
}