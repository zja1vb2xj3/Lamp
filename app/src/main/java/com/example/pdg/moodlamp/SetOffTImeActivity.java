package com.example.pdg.moodlamp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.text.SimpleDateFormat;
import java.util.Date;


public class SetOffTImeActivity extends Activity {

    private static final int BACK_CODE = 1111;

    private TimeThread _timeThread;

    private String _strCurrentTime;
    private String _strOffTime;

    private EditText _offTimeET, _offMinuteET;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_offtime);

        _timeThread = new TimeThread(getApplicationContext());

        Button setOffTimeBT = (Button) findViewById(R.id.setOffTime);

        setOffTimeBT.setOnClickListener(this::setOffTimeButtonClick);

        Button back_BT = (Button) findViewById(R.id.back_BT2);

        back_BT.setOnClickListener(this::backActivityButtonClick);


    }

    private void setOffTimeButtonClick(View view) {

        _timeThread.startThread();


        _offTimeET = (EditText) findViewById(R.id.offTime);
        _offMinuteET = (EditText) findViewById(R.id.offMinute);

        if (_offTimeET.getText().toString() != null && _offMinuteET.getText().toString() != null) {
            String time = _offTimeET.getText().toString();
            String minute = _offMinuteET.getText().toString();
            _strOffTime = time + minute + "00";
        }

        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        startActivityForResult(intent, BACK_CODE);

    }


    private void backActivityButtonClick(View view) {
        Intent intent = new Intent(SetOffTImeActivity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        startActivityForResult(intent, BACK_CODE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


    private class TimeThread extends Thread {
        private boolean _runSign = false;

        private SimpleDateFormat _sdf;
        private Context _context;

        private Bluetooth _bluetooth;


        public TimeThread(Context context) {

            _bluetooth = Bluetooth.getInstance();

            _context = context;

            _sdf = new SimpleDateFormat("kkmmss");

        }


        public void startThread() {
            try {
                this.start();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void run() {

            while (!_runSign) {
                try {
                    this.sleep(1000);
                    Date _currentDate = new Date();

                    _strCurrentTime = _sdf.format(_currentDate.getTime());


//                ((TextView)((Activity)_context).findViewById(R.id.remainTime)).setText(String.valueOf(offTime-currentTime));

                    Log.i("현재시간", String.valueOf(_strCurrentTime));
                    Log.i("종료시간", String.valueOf(_strOffTime));

                    checkOffTIme();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }


        private void checkOffTIme() {
            int currentTime = Integer.parseInt(_strCurrentTime);
            int offTime = Integer.parseInt(_strOffTime);
            if (_strCurrentTime.equals(_strOffTime)) {
                offLight();
            }
        }

        private void offLight() {

            String colorValue = "0,0,0";

            _bluetooth.sendData(colorValue);

            _runSign = true;
        }

    }

}
