package com.erz.joystick;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.Random;
import java.util.Vector;

/**
 * Created by edgarramirez on 6/15/15.
 */
public class GameView extends SurfaceView implements SurfaceHolder.Callback {

    private int i;
    private int size = 20;
    private int minSpeed;
    private int maxSpeed;
    private int minRadius;
    private int maxRadius;
    private float width;
    private float height;
    private float posX;
    private float posY;
    private float radius;
    private double angle;
    private double power;
    private double angle2;
    private Bitmap droid;
    private GameLoop gameLoop;
    private Paint paint;
    private Vector<Star> stars = new Vector<>();
    private RectF rectF = new RectF();
    private Random random = new Random();

    public GameView(Context context) {
        this(context, null);
    }

    public GameView(Context context, AttributeSet attrs) {
        super(context, attrs);
        getHolder().addCallback(this);
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setFilterBitmap(true);
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.WHITE);
        droid = BitmapFactory.decodeResource(getResources(), R.drawable.droid);
    }

    @Override
    public void draw(Canvas canvas) {
        if (canvas == null) return;
        canvas.drawColor(Color.BLACK);

        if (stars != null && stars.size() > 0) {
            for (i = 0; i < size; i++) {
                stars.get(i).draw(canvas, paint, rectF, random, minSpeed, maxSpeed, minRadius, maxRadius, width, height, maxRadius);
            }
        }

        posX -= Math.cos(angle) * power;
        posY += Math.sin(-angle) * power;
        if (posX > width - radius) posX = width - radius;
        if (posX < radius) posX = radius;
        if (posY > height - radius) posY = height - radius;
        if (posY < radius) posY = radius;

        float rotate;
        if (angle2 == 0) rotate = 0;
        else rotate = (float) Math.toDegrees(angle2) - 90;
        canvas.rotate(rotate, posX, posY);
        rectF.set(posX - radius, posY - radius, posX + radius, posY + radius);
        canvas.drawBitmap(droid, null, rectF, paint);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        gameLoop = new GameLoop(this);
        gameLoop.setRunning(true);
        gameLoop.start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        this.width = width;
        this.height = height;
        float min = Math.min(width, height);

        float centerX = width / 2;
        float centerY = height / 2;
        posX = centerX;
        posY = centerY;
        radius = min / 12;
        rectF = new RectF(posX - radius, posY - radius, posX + radius, posY + radius);

        minSpeed = (int) (min / 37);
        maxSpeed = (int) (min / 12);
        minRadius = (int) (min / 250);
        maxRadius = (int) (min / 220);

        if (maxRadius == minRadius) maxRadius += minRadius;

        stars.clear();
        for (i = 0; i < size; i++) {
            stars.add(new Star(random, minSpeed, maxSpeed, minRadius, maxRadius, width, height));
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        gameLoop.setRunning(false);
        gameLoop = null;
    }

    public void move(double angle, double power) {
        this.angle = angle;
        this.power = power;
    }

    public void rotate(double angle2) {
        this.angle2 = angle2;
    }
}