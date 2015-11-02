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
        ((JoyStick)findViewById(R.id.joy2)).setListener(this);
        ((JoyStick)findViewById(R.id.joy2)).enableStayPut(true);
        ((JoyStick)findViewById(R.id.joy2)).setButtonSize(50);
        ((JoyStick)findViewById(R.id.joy2)).setButtonDrawable(R.drawable.droid);
        ((JoyStick)findViewById(R.id.joy2)).setButtonAlpha(100);
    }

    @Override
    public void onMove(JoyStick joyStick, double angle, double power) {
        switch (joyStick.getId()) {
            case R.id.joy1:
                gameView.move(angle, power);
                break;
            case R.id.joy2:
                gameView.rotate(angle);
                break;
        }
    }
}