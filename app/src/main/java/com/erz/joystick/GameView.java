package com.erz.joystick;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.Random;
import java.util.Vector;

/**
 * Created by edgarramirez on 6/15/15.
 */
public class GameView extends SurfaceView implements SurfaceHolder.Callback {

    private float width;
    private float height;
    private float posX;
    private float posY;
    private float radius;
    private GameLoop gameLoop;
    private Paint paint;
    private Random random = new Random();
    private int i;
    private int size = 20;
    private int maxRadius;
    private Bitmap droid;
    private RectF rectF;

    private double angle;
    private double power;

    private double angle2;

    private Vector<Star> stars;

    public GameView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public GameView(Context context) {
        super(context);
        init();
    }

    private void init() {
        getHolder().addCallback(this);
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.WHITE);
        droid = BitmapFactory.decodeResource(getResources(), R.drawable.droid);
        rectF = new RectF();
    }

    @Override
    public void draw(@NonNull Canvas canvas) {
        if (canvas == null) return;
        super.draw(canvas);
        canvas.drawColor(Color.BLACK);

        if (stars != null && stars.size() > 0) {
            for (i = 0; i < size; i++) {
                stars.get(i).draw(canvas, paint, height, maxRadius);
            }
        }

        posX -= Math.cos(angle) * (power / 2);
        posY += Math.sin(-angle) * (power / 2);
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

        int minSpeed = (int) (min / 75);
        int maxSpeed = (int) (min / 25);

        int minRadius = (int) (min / 250);
        maxRadius = (int) (min / 220);

        if (maxRadius == minRadius) maxRadius += minRadius;

        stars = new Vector<>();
        for (i = 0; i < size; i++) {
            int tmpRadius = random.nextInt(maxRadius - minRadius) + minRadius;
            int maxX = width - tmpRadius;
            int maxY = height - tmpRadius;
            stars.add(new Star(random.nextInt(maxX - tmpRadius + (maxRadius * 4)) + (tmpRadius - (maxRadius * 4)),
                    random.nextInt(maxY - tmpRadius + (maxRadius * 4)) + (tmpRadius - (maxRadius * 4)),
                    random.nextInt(maxSpeed - minSpeed) + minSpeed,
                    tmpRadius));
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