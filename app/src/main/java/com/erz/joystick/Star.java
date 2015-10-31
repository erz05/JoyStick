package com.erz.joystick;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;

/**
 * Created by edgarramirez on 6/16/15.
 */
public class Star {
    public float x;
    public float y;
    public float speedY;
    public float radius;
    public boolean ready = false;
    RectF rectF;

    public Star(float x, float y, float speedY, float radius) {
        this.x = x;
        this.y = y;
        this.speedY = speedY;
        this.radius = radius;
        ready = true;
        rectF = new RectF();
    }

    public void draw(Canvas canvas, Paint paint, float width, float height, float max) {
        if (!ready) return;
        update(width, height, max);

        rectF.set(x - radius, y - radius, x + radius, y + radius);

        paint.setStrokeWidth(radius / 10);
        canvas.drawOval(rectF, paint);
    }

    public void update(float width, float height, float max) {
        if (!ready) return;
        y += speedY;

        if ((y + radius) > (height + (max * 4))) {
            y = - (max * 4) - radius;
        }
    }
}
