/*
    Copyright 2015 erz05

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

     http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
 */

package com.erz.joysticklibrary;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by edgarramirez on 10/30/15.
 */
public class JoyStick extends View {

    public static final int CENTER = -1;
    public static final int LEFT = 0;
    public static final int LEFT_UP = 1;
    public static final int UP = 2;
    public static final int UP_RIGHT = 3;
    public static final int RIGHT = 4;
    public static final int RIGHT_DOWN = 5;
    public static final int DOWN = 6;
    public static final int DOWN_LEFT = 7;

    private JoyStickListener listener;
    private Paint paint;
    private int direction = CENTER;
    private float centerX;
    private float centerY;
    private float posX;
    private float posY;
    private float radius;
    private float buttonRadius;
    private double power = 0;
    private double angle = 0;
    private RectF temp;

    //Background Color
    private int padColor;

    //Stick Color
    private int buttonColor;

    //Keeps joystick in last position
    private boolean stayPut;

    //Button Size percentage of the minimum(width, height)
    private int percentage = 25;

    //Background Bitmap
    private Bitmap padBGBitmap = null;

    //Button Bitmap
    private Bitmap buttonBitmap = null;

    public interface JoyStickListener {
        void onMove(JoyStick joyStick, double angle, double power, int direction);
    }

    public JoyStick(Context context) {
        super(context);
        init(context, null);
    }

    public JoyStick(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
        paint.setAntiAlias(true);
        paint.setFilterBitmap(true);

        temp = new RectF();

        padColor = Color.WHITE;
        buttonColor = Color.RED;

        if (attrs != null) {
            TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.JoyStick);
            if (typedArray != null) {
                padColor = typedArray.getColor(R.styleable.JoyStick_padColor, Color.WHITE);
                buttonColor = typedArray.getColor(R.styleable.JoyStick_buttonColor, Color.RED);
                stayPut = typedArray.getBoolean(R.styleable.JoyStick_stayPut, false);
                percentage = typedArray.getInt(R.styleable.JoyStick_percentage, 25);
                if (percentage > 50) percentage = 50;
                if (percentage < 25) percentage = 25;

                int padResId = typedArray.getResourceId(R.styleable.JoyStick_backgroundDrawable, -1);
                int buttonResId = typedArray.getResourceId(R.styleable.JoyStick_buttonDrawable, -1);

                if (padResId > 0) {
                    padBGBitmap = BitmapFactory.decodeResource(getResources(), padResId);
                }
                if (buttonResId > 0) {
                    buttonBitmap = BitmapFactory.decodeResource(getResources(), buttonResId);
                }

                typedArray.recycle();
            }
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        float width = MeasureSpec.getSize(widthMeasureSpec);
        float height = MeasureSpec.getSize(heightMeasureSpec);
        centerX = width / 2;
        centerY = height / 2;
        float min = Math.min(width, height);
        posX = centerX;
        posY = centerY;
        buttonRadius = (min / 2f * (percentage / 100f));
        radius = (min / 2f * ((100f - percentage) / 100f));
    }

    @Override
    public void onDraw(Canvas canvas) {
        if (canvas == null) return;
        if (padBGBitmap == null) {
            paint.setColor(padColor);
            canvas.drawCircle(centerX, centerY, radius, paint);
        } else {
            temp.set(centerX - radius, centerY - radius, centerX + radius, centerY + radius);
            canvas.drawBitmap(padBGBitmap, null, temp, paint);
        }
        if (buttonBitmap == null) {
            paint.setColor(buttonColor);
            canvas.drawCircle(posX, posY, buttonRadius, paint);
        } else {
            temp.set(posX - buttonRadius, posY - buttonRadius, posX + buttonRadius, posY + buttonRadius);
            canvas.drawBitmap(buttonBitmap, null, temp, paint);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_MOVE:
                posX = event.getX();
                posY = event.getY();
                float abs = (float) Math.sqrt((posX - centerX) * (posX - centerX)
                        + (posY - centerY) * (posY - centerY));
                if (abs > radius) {
                    posX = ((posX - centerX) * radius / abs + centerX);
                    posY = ((posY - centerY) * radius / abs + centerY);
                }

                angle = Math.atan2(centerY - posY, centerX - posX);

                power = (100 * Math.sqrt((posX - centerX)
                        * (posX - centerX) + (posY - centerY)
                        * (posY - centerY)) / radius);

                double degrees = Math.toDegrees(angle);
                if ((degrees >= 0 && degrees < 22.5) || (degrees < 0 && degrees > -22.5)) direction = LEFT;
                else if (degrees >= 22.5 && degrees < 67.5) direction = LEFT_UP;
                else if (degrees >= 67.5 && degrees < 112.5) direction = UP;
                else if (degrees >= 112.5 && degrees < 157.5) direction = UP_RIGHT;
                else if ((degrees >= 157.5 && degrees <= 180) || (degrees >= -180 && degrees < -157.5)) direction = RIGHT;
                else if (degrees >= -157.5 && degrees < -112.5) direction = RIGHT_DOWN;
                else if (degrees >= -112.5 && degrees < -67.5) direction = DOWN;
                else if (degrees >= -67.5 && degrees < -22.5) direction = DOWN_LEFT;
                else direction = CENTER;

                invalidate();
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                if (!stayPut) {
                    posX = centerX;
                    posY = centerY;
                    direction = CENTER;
                    angle = 0;
                    power = 0;
                    invalidate();
                }
                break;
        }

        if (listener != null) {
            listener.onMove(this, angle, power, direction);
        }
        return true;
    }

    public void setListener(JoyStickListener listener) {
        this.listener = listener;
    }

    public double getPower() {
        return power;
    }

    public double getAngle() {
        return angle;
    }

    public double getAngleDegrees() {
        return Math.toDegrees(angle);
    }

    public int getDirection() {
        return direction;
    }

    //Customization ----------------------------------------------------------------

    public void setPadColor(int padColor) {
        this.padColor = padColor;
    }

    public void setButtonColor(int buttonColor) {
        this.buttonColor = buttonColor;
    }

    //size of button is a percentage of the minimum(width, height)
    //percentage must be between 25 - 50
    public void setButtonRadiusScale(int scale) {
        percentage = scale;
        if (percentage > 50) percentage = 50;
        if (percentage < 25) percentage = 25;
    }

    public void enableStayPut(boolean enable) {
        this.stayPut = enable;
    }

    public void setPadBackground(int resId) {
        this.padBGBitmap = BitmapFactory.decodeResource(getResources(), resId);
    }

    public void setPadBackground(Bitmap bitmap) {
        this.padBGBitmap = bitmap;
    }

    public void setButtonDrawable(int resId) {
        this.buttonBitmap = BitmapFactory.decodeResource(getResources(), resId);
    }

    public void setButtonDrawable(Bitmap bitmap) {
        this.buttonBitmap = bitmap;
    }
}