package com.erz.joystick;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;

import java.util.Random;

/**
 * Created by edgarramirez on 6/16/15.
 */
public class Star {
    private float x;
    private float y;
    private float speedY;
    private float radius;
    private boolean ready = false;

    public Star(Random random, int minSpeed, int maxSpeed, int minRadius, int maxRadius, int width, int height) {
        reset(random, minSpeed, maxSpeed, minRadius, maxRadius, width, height);
    }

    private void reset(Random random, int minSpeed, int maxSpeed, int minRadius, int maxRadius, int width, int height) {
        int tmpRadius = random.nextInt(maxRadius - minRadius) + minRadius;
        int maxX = width - tmpRadius;
        int maxY = height - tmpRadius;
        this.x = random.nextInt(maxX - tmpRadius + (maxRadius * 4)) + (tmpRadius - (maxRadius * 4));
        this.y = random.nextInt(maxY - tmpRadius + (maxRadius * 4)) + (tmpRadius - (maxRadius * 4));
        this.speedY = random.nextInt(maxSpeed - minSpeed) + minSpeed;
        this.radius = tmpRadius;
        ready = true;
    }

    public void draw(Canvas canvas, Paint paint, RectF rectF, Random random, int minSpeed,
                     int maxSpeed, int minRadius, int maxRadius, float width, float height, int max) {
        if (!ready) return;

        y += speedY;

        if ((y + radius) > (height + (max * 4))) {
            reset(random, minSpeed, maxSpeed, minRadius, maxRadius, (int) width, (int) height);
        }

        rectF.set(x - radius, y - radius, x + radius, y + radius);

        paint.setStrokeWidth(radius / 10);
        canvas.drawOval(rectF, paint);
    }
}