package com.example.pdg.moodlamp;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;


/*17-06-07 MainActivity 에서 블루투스를 연결한 객체 Bluetooth 를 SelectColorActivity 에 전달 받으려는 것을 실패*/
public class SelectColorActivity extends Activity {

    private static final int BACK_CODE = 1111;

    private ImageView _colorImageView;

    private static final String ACTION_UP = "ACTION_UP";
    private static final String ACTION_DOWN = "ACTION_DOWN";
    private static final String ACTION_MOVE = "ACTION_MOVE";

    private static final String BLUETOOTH_DATA = "BLUETOOTH";

    private String _rgbValue;

    private Bluetooth _bluetooth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_color);

        _bluetooth = Bluetooth.getInstance();

        _colorImageView = (ImageView) findViewById(R.id.color_img);

        _colorImageView.setOnTouchListener(this::touchImage);

        Button back_BT1 = (Button) findViewById(R.id.back_BT1);

        back_BT1.setOnClickListener(this::backActivity);
    }


    private boolean touchImage(View v, MotionEvent event) {

        final Bitmap bitmap = ((BitmapDrawable) _colorImageView.getDrawable()).getBitmap();
        int touchX = (int) event.getX();
        int touchY = (int) event.getY();
        //|| event.actionToString(event.getAction()) == ACTION_MOVE)
        if (event.actionToString(event.getAction()) == ACTION_DOWN &&
                touchX > 0 && touchY > 0) {
            try {
                int pixel = bitmap.getPixel(touchX, touchY);

                int redValue = Color.red(pixel);
                int greenValue = Color.green(pixel);
                int blueValue = Color.blue(pixel);

                _rgbValue = String.valueOf(redValue + "," + greenValue + "," + blueValue);
                sendBluetooth(_rgbValue);
            } catch (Exception e) {
                Toast.makeText(getApplicationContext(), "이미지 범위를 벗어났습니다.", Toast.LENGTH_SHORT).show();
            }

        }
        return true;
    }

    public void sendBluetooth(String rgbValue) {
        Log.i("rgbValue", rgbValue);
        try {
            _bluetooth.sendData(rgbValue);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void backActivity(View view) {
        Intent intent = new Intent(SelectColorActivity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        startActivityForResult(intent,BACK_CODE);
    }



}
