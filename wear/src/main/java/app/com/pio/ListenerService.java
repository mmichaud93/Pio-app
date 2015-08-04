package app.com.pio;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.support.v4.content.LocalBroadcastManager;

import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.WearableListenerService;

/**
 * Created by mmichaud on 8/3/15.
 */
public class ListenerService extends WearableListenerService {

    private static final String WEARABLE_DATA_PATH = "/pio";

    @Override
    public void onDataChanged(DataEventBuffer dataEvents) {

        DataMap dataMap;
        for (DataEvent event : dataEvents) {

            // Check the data type
            if (event.getType() == DataEvent.TYPE_CHANGED) {
                // Check the data path
                String path = event.getDataItem().getUri().getPath();
                if (path.equals(WEARABLE_DATA_PATH)) {
                    dataMap = DataMapItem.fromDataItem(event.getDataItem()).getDataMap();
                    Intent messageIntent = new Intent();
                    messageIntent.setAction(Intent.ACTION_SEND);
                    LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(messageIntent);

                    buildWearableOnlyNotification("Exploring", dataMap.getString("xp")+" XP gained");
                }
            }
        }
    }

    private void buildWearableOnlyNotification(String title, String content) {
        Intent viewIntent = new Intent(this, MainActivity.class);
        PendingIntent viewPendingIntent =
                PendingIntent.getActivity(this, 0, viewIntent, 0);
        Notification.Builder builder = new Notification.Builder(this)
                .setSmallIcon(R.drawable.ic_launcher)
                .setContentTitle(title)
                .setContentText(content)
                .setContentIntent(viewPendingIntent)
                .extend(new Notification.WearableExtender().setBackground(BitmapFactory.decodeResource(getResources(), R.drawable.london_map)));

        ((NotificationManager) getSystemService(NOTIFICATION_SERVICE))
                .notify(001, builder.build());
    }
}