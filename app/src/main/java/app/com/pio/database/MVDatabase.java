package app.com.pio.database;

import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.graphics.PointF;
import android.util.Log;

import org.h2.mvstore.MVStore;
import org.h2.mvstore.rtree.MVRTreeMap;
import org.h2.mvstore.rtree.SpatialKey;
import org.h2.mvstore.type.StringDataType;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import app.com.pio.ui.map.MaskTileProvider;

/**
 * Created by mmichaud on 6/6/15.
 */
public class MVDatabase {

    private static String TAG = "MVDatabase";

    private static MVStore mvStore;
    private static MVRTreeMap<String> mvrTreeMap;

    private static final String fileLocation = "/database.h3";
    private static Context context;

    public static void initializeDatabase(Context context) {
        MVDatabase.context = context;
        if (mvStore == null) {
            mvStore = MVStore.open(context.getFilesDir()+fileLocation);
        }
        if (mvrTreeMap == null) {
            mvrTreeMap = mvStore.openMap("data", new MVRTreeMap.Builder<String>().dimensions(2).
                    valueType(StringDataType.INSTANCE));
        }




//        eraseDatabase(true);

//        addTestPoints(new PointF(42.345400f, -71.098260f));
//        addTestPoints(new PointF(41.345400f, -72.098260f));
//        addTestPoints(new PointF(43.345400f, -71.098260f));
//        addTestPoints(new PointF(41.345400f, -71.098260f));
//        addTestPoints(new PointF(42.345400f, -72.098260f));
//        addTestPoints(new PointF(43.345400f, -72.098260f));
//
//        storePoint(42.345400f, -71.098260f);
//        storePoint(42.345439f, -71.098090f);
//        storePoint(42.345478f, -71.097920f);
//        storePoint(42.345517f, -71.097750f);
//        storePoint(42.345556f, -71.097580f);
//        storePoint(42.345595f, -71.097410f);
//        storePoint(42.345634f, -71.097240f);
//        storePoint(42.345673f, -71.097070f);

        //getPoints(4,4,6,6);
    }

    public static void closeDatabase() {
        Log.d(TAG, "closing database");
        mvStore.close();
    }

    public static boolean storePoint(float x, float y, boolean checkForDuplicates) {
        if(checkForDuplicates) {
            //Log.d(TAG, "0.0001f to lat: "+(Math.cos(Math.toRadians(y))*0.0001));
            float xDiff = ((float)(Math.cos(Math.toRadians(x))*0.00005f));
            List<PointF> points = getPoints(x-xDiff, y- 0.00005f, x+xDiff, y+ 0.00005f);
            if(points.size()>0) {
                return false;
            }
        }
        if(mvrTreeMap.isClosed()) {
            mvrTreeMap = mvStore.openMap("data", new MVRTreeMap.Builder<String>().dimensions(2).
                    valueType(StringDataType.INSTANCE));
        }
        if(!mvrTreeMap.isClosed()) {
            //Log.d("MVDatabase", "storing point, x: " + x + ", y: " + y);
            mvrTreeMap.add(new SpatialKey((long) (Math.random() * Long.MAX_VALUE), x, x, y, y), "(" + x + ", " + y + ")");
        }
        return true;
    }

    public static List<PointF> getPoints(float minX, float minY, float maxX, float maxY) {
        List<PointF> points = new ArrayList<PointF>();
        if(mvrTreeMap.isClosed()) {
            if(mvStore.isClosed() && context!=null) {
                mvStore = MVStore.open(context.getFilesDir()+fileLocation);
            }
            mvrTreeMap = mvStore.openMap("data", new MVRTreeMap.Builder<String>().dimensions(2).
                    valueType(StringDataType.INSTANCE));
        }
        Iterator<SpatialKey> it = mvrTreeMap.findContainedKeys(new SpatialKey(0, minX, maxX, minY, maxY));
        while(it.hasNext()) {
            SpatialKey k = it.next();
            points.add(new PointF(k.max(0), k.max(1)));
        }

        return points;
    }

    private static void eraseDatabase(boolean areYouSure) {
        if(areYouSure) {
            mvrTreeMap.clear();
        }
    }

    private static void addTestPoints(PointF from) {
        for(int i = 0; i < 50000; i++) {
            storePoint((float)(from.x+Math.random()*1f), (float)(from.y+Math.random()*1f), false);
        }
    }


}
