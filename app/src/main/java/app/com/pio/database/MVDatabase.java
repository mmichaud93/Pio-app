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

/**
 * Created by mmichaud on 6/6/15.
 */
public class MVDatabase {

    private static MVStore mvStore;
    private static MVRTreeMap<String> mvrTreeMap;

    private static final String fileLocation = "/database.h3";

    public static void initializeDatabase(Context context) {
        mvStore = MVStore.open(context.getFilesDir()+fileLocation);
        mvrTreeMap = mvStore.openMap("data", new MVRTreeMap.Builder<String>().dimensions(2).
                valueType(StringDataType.INSTANCE));



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
        mvStore.close();
    }

    public static void storePoint(float x, float y) {
        mvrTreeMap.add(new SpatialKey((long) (Math.random() * Long.MAX_VALUE), x, x, y, y), "(" + x + ", " + y + ")");
    }

    public static List<PointF> getPoints(float minX, float minY, float maxX, float maxY) {
        List<PointF> points = new ArrayList<PointF>();

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
            storePoint((float)(from.x+Math.random()*1f), (float)(from.y+Math.random()*1f));
        }
    }


}
