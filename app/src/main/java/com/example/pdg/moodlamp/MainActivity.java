package com.example.pdg.moodlamp;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity {


    private static final int SelectColorActivity_CODE = 0002;

    private static final int SetOffTimeActivity_CODE = 0003;

    public static final int REQUEST_ENABLE_BT = 1;// REQUEST_ENABLE_BT : 블루투스 활성 상태의 변경 결과를 App 으로 알려줄 때 식별자로 사용(0이상)

    private Bluetooth _bluetooth;

    private String _message;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button connect_bluetooth = (Button) findViewById(R.id.connectButtonClick);

        connect_bluetooth.setOnClickListener(this::connectBluetoothButtonClick);

        _bluetooth = Bluetooth.getInstance();

        Button use_lamp = (Button) findViewById(R.id.use_lamp);

        use_lamp.setOnClickListener(this::useLampButtonClick);


        Button offTimeSet = (Button) findViewById(R.id.set_offtime);
        offTimeSet.setOnClickListener(this::setOffTimeButtonClick);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode,resultCode,data);
        //RESULT_OK : -1 RESULT_CANCLE : 0

        if(requestCode == REQUEST_ENABLE_BT){
            if(resultCode == RESULT_OK){
                createDevicesDialog();
            }
        }

    }

    //블루투스 연결
    private void connectBluetoothButtonClick(View view) {

        _message = _bluetooth.setBluetooth();

        if (_message == "error1")
            Toast.makeText(getApplicationContext(), "기기가 블루투스를 지원하지 않습니다.", Toast.LENGTH_LONG).show();

        else if (_message == "error2") {
            Toast.makeText(getApplicationContext(), "현재 블루투스가 비활성 상태입니다.", Toast.LENGTH_LONG).show();
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);//startActivityForResult에  REQUEST_ENABLE_BT 전달

        } else if (_message == "hasBluetooth") {
            createDevicesDialog();
        }

    }




    private void createDevicesDialog() {

        _message = _bluetooth.findListDevice();

        if (_message == "emptyPair")
            Toast.makeText(getApplicationContext(), "페어링된 장치가 없습니다.", Toast.LENGTH_LONG).show();

        else if (_message == "hasPairDevice") {

            AlertDialog.Builder AlertDialogbuilder = new AlertDialog.Builder(this);//AlertDialog 이용 블루투스 목록표시
            AlertDialogbuilder.setTitle("블루투스 장치 선택");//목록title


            // 블루투스 목록을 표시하고 각 다른 이름을 가진 디바이스들을 표시한다.
            List<String> listItems = new ArrayList<>();//ArrayList 객체생성
            for (BluetoothDevice device : _bluetooth.getDevices()) {
                listItems.add(device.getName()); // device.getName() : 단말기의 Bluetooth Adapter 이름을 반환.
            }
            listItems.add("취소");  // 취소 항목 추가.


            // CharSequence : 변경 가능한 문자열 (객체)
            // toArray : List형태로 넘어온것 배열로 바꿔서 처리하기 위한 toArray() 함수.
            final CharSequence[] items = listItems.toArray(new CharSequence[listItems.size()]);//리스트 배열을 사용하기위한 선언

            AlertDialogbuilder.setItems(items, new DialogInterface.OnClickListener() {//클릭시 setItems 리스트추가 메소드

                @Override
                public void onClick(DialogInterface dialog, int item) {//리스트내 아이템 클릭
                    // TODO Auto-generated method stub
                    if (item == _bluetooth.getPairDeviceCount()) { // 취소 버튼을 누른경우
                        Toast.makeText(getApplicationContext(), "연결할 장치를 선택하지 않았습니다.", Toast.LENGTH_LONG).show();
                    } else { // 연결할 장치를 선택한 경우, 선택한 장치와 연결을 시도함.
                        _message = _bluetooth.connectToSelectedDevice(items[item].toString());//생성된 장치목록을 ConnectToSelectedDevice 메소드에 보냄
                        if (_message == "connectedBluetooth")
                            Toast.makeText(getApplicationContext(), "블루투스가 연결되었습니다.", Toast.LENGTH_LONG).show();

                        else if (_message == "cannotConnectedBluetooth")
                            Toast.makeText(getApplicationContext(), "블루투스 연결 중 오류가 발생했습니다.", Toast.LENGTH_LONG).show();
                    }
                }

            });//end setItems

            AlertDialogbuilder.setCancelable(false);  // 뒤로 가기 버튼 사용 금지.
            AlertDialog alert = AlertDialogbuilder.create();//알림창 객체 생성
            alert.show();//알림창 띄우기
        }
    }


    private void useLampButtonClick(View view) {

        Intent intent = new Intent(MainActivity.this, SelectColorActivity.class);

        startActivityForResult(intent, SelectColorActivity_CODE);
    }

    private void setOffTimeButtonClick(View view) {

        Intent intent = new Intent(MainActivity.this, SetOffTImeActivity.class);

        startActivityForResult(intent, SetOffTimeActivity_CODE);
    }


}
