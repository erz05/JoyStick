package com.erz.joystick;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.erz.joysticklibrary.JoyStick;

public class MainActivity extends AppCompatActivity implements JoyStick.JoyStickListener {

    GameView gameView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        gameView = (GameView) findViewById(R.id.game);
        ((JoyStick)findViewById(R.id.joy1)).setListener(this);
        ((JoyStick)findViewById(R.id.joy1)).setPadColor(Color.parseColor("#55ffffff"));
        ((JoyStick)findViewById(R.id.joy1)).setButtonColor(Color.parseColor("#55ff0000"));

        ((JoyStick)findViewById(R.id.joy2)).setListener(new JoyStick.JoyStickListener() {
            @Override
            public void onMove(double radians, double power) {
                gameView.rotate(radians);
            }
        });
    }

    @Override
    public void onMove(double radians, double power) {
        gameView.move(radians, power);
    }
}