package app.com.pio.wear;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;

/**
 * Created by michaudm3 on 7/28/2015.
 */
public class SendToDataLayerThread extends Thread {
    String path;
    DataMap dataMap;
    GoogleApiClient googleClient;

    public SendToDataLayerThread(String p, DataMap data, GoogleApiClient googleClient) {
        path = p;
        dataMap = data;
        this.googleClient = googleClient;
    }

    public void run() {
        NodeApi.GetConnectedNodesResult nodes = Wearable.NodeApi.getConnectedNodes(googleClient).await();
        PutDataMapRequest putDMR = PutDataMapRequest.create(path);
        putDMR.getDataMap().putAll(dataMap);
        PutDataRequest request = putDMR.asPutDataRequest();
        Wearable.DataApi.putDataItem(googleClient,request).await();
    }
}