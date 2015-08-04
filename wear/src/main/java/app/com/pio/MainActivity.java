package app.com.pio;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.wearable.view.WatchViewStub;

/**
 * Created by mmichaud on 8/3/15.
 */
public class MainActivity extends Activity {

    IntentFilter messageFilter;
    MessageReceiver messageReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);
        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub stub) {

            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(getApplicationContext()).unregisterReceiver(messageReceiver);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(messageFilter == null || messageReceiver == null) {
            messageFilter = new IntentFilter(Intent.ACTION_SEND);
            messageReceiver = new MessageReceiver();
        }
        LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(messageReceiver, messageFilter);
    }

    public class MessageReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
//            String xp = intent.getStringExtra("xp");
        }
    }
}
