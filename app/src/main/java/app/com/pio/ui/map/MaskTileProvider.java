package app.com.pio.ui.map;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RadialGradient;
import android.graphics.Shader;
import android.util.Log;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Tile;
import com.google.android.gms.maps.model.TileProvider;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import app.com.pio.database.MVDatabase;

/**
 * Created by michaudm3 on 5/12/2014.
 */
public class MaskTileProvider implements TileProvider {
    Bitmap.Config conf = Bitmap.Config.ARGB_8888;
    Canvas resultCanvas;
    Bitmap result;
    ByteArrayOutputStream stream;
    byte[] byteArray;

    Paint keepPaint;
    Paint clearAwayPaint;
    Paint maskPaint;
    Paint fogPaint;
    ArrayList<PointF> points;
    Bitmap mask;
    Canvas maskCanvas;

    boolean lock  = false;

    GoogleMap map;

    int bitmapWidth = 256;
    int bitmapHeight = 256;
    public static final double radiusConstant = 0.0004;//423902130126953;

//    int width = 180/4;
//    int height = 180/4;

    public MaskTileProvider (GoogleMap map) {
        this.map = map;
        // this paint is transparent, anything in the mask drawn with this paint will not affect the fog
        keepPaint = new Paint();
        keepPaint.setStyle(Paint.Style.FILL);
        keepPaint.setColor(Color.WHITE);
        keepPaint.setAlpha(0);

        // this paint is black, anything in black will clear the fog
        clearAwayPaint = new Paint();
        clearAwayPaint.setStyle(Paint.Style.FILL);
        clearAwayPaint.setColor(Color.BLACK);
        clearAwayPaint.setAlpha(255);

        // this paint is used to draw the mask onto the fog
        maskPaint = new Paint();
        maskPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_OUT));

        // this paint is the fog
        fogPaint = new Paint();
        fogPaint.setColor(Color.argb(212, 255, 255, 255));

        // the mask/result canvas and bitmap are global variables to save on some memory, thus we have
        // to lock out some tiles so that only one is being created at a time
        points = new ArrayList<PointF>();

    }
    public void setPoints(ArrayList<PointF> points) {
        this.points = points;
    }
    @Override
    public Tile getTile(int x, int y, int zoom) {
        // we only want to do one tile at a time, if we don't we will mess up the Bitmap creation stuff
        if(!lock) {
            // no one is making a tile so we should start and lock everyone out
            lock = true;
            try {
                long start = System.currentTimeMillis();
                LatLngBounds bounds = boundsOfTile(x,y,zoom);
                // generate the mask
                mask = Bitmap.createBitmap(bitmapWidth, bitmapHeight, conf);
                maskCanvas = new Canvas(mask);
                List<PointF> ps = MVDatabase.getPoints((float) (bounds.southwest.latitude - 0.01), (float)(bounds.southwest.longitude - 0.01),
                        (float)(bounds.northeast.latitude + 0.01), (float)(bounds.northeast.longitude + 0.01));
                // draw the points on the mask as black circles
                int pointsOnTile = 0;
                int incrementer = ps.size()/5000 + 1;
                if(ps.size() > 0 ) {

                    for (int i = 0; i < ps.size(); i+=incrementer) {
                        PointF point = ps.get(i);
                        double thisTileWidth = bounds.northeast.longitude - bounds.southwest.longitude;
                        double thisTileHeight = bounds.northeast.latitude - bounds.southwest.latitude;

                        float radius = (float) (radiusConstant * (float) bitmapWidth / thisTileWidth);
                        clearAwayPaint.setShader(new RadialGradient(
                                (float) ((((point.y - bounds.southwest.longitude) / thisTileWidth) * bitmapWidth)),
                                (float) (bitmapHeight - (((point.x - bounds.southwest.latitude) / thisTileHeight) * bitmapHeight)),
                                radius, Color.BLACK, Color.TRANSPARENT, Shader.TileMode.MIRROR));
                        maskCanvas.drawCircle(
                                (float) ((((point.y - bounds.southwest.longitude) / thisTileWidth) * bitmapWidth)),
                                (float) (bitmapHeight - (((point.x - bounds.southwest.latitude) / thisTileHeight) * bitmapHeight)),
                                radius, clearAwayPaint);
                        pointsOnTile++;
                    }
                }
                // create the bitmap that will eventually become the tile
                result = Bitmap.createBitmap(bitmapWidth, bitmapHeight, conf);

                resultCanvas = new Canvas(result);
                // draw the fog onto the result
                resultCanvas.drawRect(0, 0, bitmapWidth, bitmapHeight, fogPaint);
                // apply the mask, wherever there is a black circle the fog will be cleared
                if(pointsOnTile>0) {
                    resultCanvas.drawBitmap(mask, 0, 0, maskPaint);
                }

                // stream the bitmap to a byte array
                stream = new ByteArrayOutputStream();
                result.compress(Bitmap.CompressFormat.PNG, 100, stream);
                byteArray = stream.toByteArray();
                // use the bytearray to create the tile
                Tile tile = new Tile(bitmapWidth, bitmapHeight, byteArray);

                // release the lock
                lock = false;
                Log.d("MaskTileProvider","time: "+(System.currentTimeMillis()-start)+", ps.size(): "+ps.size());
                // return the tile
                return tile;
            } catch(Exception e) {
                lock = false;
                e.printStackTrace();
                return TileProvider.NO_TILE;
            }
        }
        // it seems like if we return null here then the map will try again later to make the tile. This works nicely so far
        return null;
    }
    public static double toLatitude(double y) {
        double radians = Math.atan(Math.exp(Math.toRadians(y)));
        return Math.toDegrees(2 * radians)-90;
    }
    private LatLngBounds boundsOfTile(int x, int y, int zoom) {
        int noTiles = (1 << zoom);
        double longitudeSpan = 360.0 / noTiles;
        double longitudeMin = -180.0 + x * longitudeSpan;

        double mercatorMax = 180 - (((double) y) / noTiles) * 360;
        double mercatorMin = 180 - (((double) y + 1) / noTiles) * 360;
        double latitudeMax = toLatitude(mercatorMax);
        double latitudeMin = toLatitude(mercatorMin);

        LatLngBounds bounds = new LatLngBounds(new LatLng(latitudeMin, longitudeMin), new LatLng(latitudeMax, longitudeMin + longitudeSpan));
        return bounds;
    }
}
