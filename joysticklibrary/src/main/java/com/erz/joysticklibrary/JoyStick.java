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
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by edgarramirez on 10/30/15.
 */
public class JoyStick extends View {

    float width;
    float height;
    float centerX;
    float centerY;
    float min;
    float posX;
    float posY;
    float radius;
    float buttonRadius;
    double power;
    double angle;
    int padColor;
    int buttonColor;
    Paint paint;
    JoyStickListener listener;

    public interface JoyStickListener {
        void onMove(JoyStick joyStick, double angle, double power);
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

        padColor = Color.BLACK;
        buttonColor = Color.RED;

        if (attrs != null) {
            TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.JoyStick);
            if (typedArray != null) {
                padColor = typedArray.getColor(R.styleable.JoyStick_padColor, Color.BLACK);
                buttonColor = typedArray.getColor(R.styleable.JoyStick_buttonColor, Color.RED);
                typedArray.recycle();
            }
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        width = MeasureSpec.getSize(widthMeasureSpec);
        height = MeasureSpec.getSize(heightMeasureSpec);
        centerX = width/2;
        centerY = height/2;
        min = Math.min(width, height);
        posX = centerX;
        posY = centerY;
        buttonRadius = (min / 2 * 0.25f);
        radius = (min / 2 * 0.75f);
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        if (canvas == null) return;
        paint.setColor(padColor);
        canvas.drawCircle(centerX, centerY, radius, paint);
        paint.setColor(buttonColor);
        canvas.drawCircle(posX, posY, buttonRadius, paint);
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

                invalidate();
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                posX = centerX;
                posY = centerY;
                angle = 0;
                power = 0;
                invalidate();
                break;
        }

        if (listener != null) {
            listener.onMove(this, angle, power);
        }
        return true;
    }

    public void setPadColor(int padColor) {
        this.padColor = padColor;
        invalidate();
    }

    public void setButtonColor(int buttonColor) {
        this.buttonColor = buttonColor;
        invalidate();
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
}