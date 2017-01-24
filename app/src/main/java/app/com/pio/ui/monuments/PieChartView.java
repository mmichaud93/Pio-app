package app.com.pio.ui.monuments;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import app.com.pio.R;

/**
 * Created by mmichaud on 3/21/16.
 */
public class PieChartView extends View {

    private double percentFilled;
    private int viewWidth;
    private int viewHeight;

    Paint basePaint;
    Paint fillPaint;

    public PieChartView(Context context, AttributeSet attrs) {
        super(context, attrs);

        basePaint = new Paint();
        basePaint.setColor(getContext().getResources().getColor(R.color.app_primary_light));

        fillPaint = new Paint();
        fillPaint.setColor(getContext().getResources().getColor(R.color.app_accent));
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        this.viewHeight = h;
        this.viewWidth = w;
    }

    public double getPercentFilled() {
        return percentFilled;
    }

    public void setPercentFilled(double percentFilled) {
        this.percentFilled = percentFilled;
    }

    public void setBaseColor(int color) {
        basePaint.setColor(color);
    }

    public void setFillColor(int color) {
        fillPaint.setColor(color);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int radius = viewWidth / 2;
        if (radius > viewHeight / 2) {
            radius = viewHeight / 2;
        }
        canvas.drawCircle(viewWidth / 2, viewHeight / 2, radius, basePaint);

        canvas.drawArc(new RectF(0, 0, viewWidth, viewHeight), 0, (float) (-360f * percentFilled), true, fillPaint);
    }
}
