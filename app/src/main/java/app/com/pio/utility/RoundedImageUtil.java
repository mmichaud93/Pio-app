package app.com.pio.utility;

import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;

/**
 * Created by pilleym on 6/8/2015.
 */
public class RoundedImageUtil implements com.squareup.picasso.Transformation{

    private final int radius;
    private final int margin;

    public RoundedImageUtil(final int radius, final int margin)
    {
        this.radius = radius;
        this.margin = margin;
    }


    //circle profile picture - Bitmap
    public Bitmap transform(final Bitmap source)
    {
        final Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setShader(new BitmapShader(source, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP));

        Bitmap output = Bitmap.createBitmap(source.getWidth(), source.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);
        canvas.drawRoundRect(new RectF(margin, margin, source.getWidth() - margin, source.getHeight() - margin), radius, radius, paint);

        if(source != output)
        {
            source.recycle();
        }

        return output;

    }

    public String key()
    {
        return "rounded";
    }


}
