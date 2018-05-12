package com.example.user.harlop_xiaomi;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.zhaoxiaodan.miband.model.BatteryInfo;
import com.zhaoxiaodan.miband.ActionCallback;
import com.zhaoxiaodan.miband.MiBand;
import com.zhaoxiaodan.miband.model.LedColor;
import com.zhaoxiaodan.miband.model.UserInfo;
import com.zhaoxiaodan.miband.model.VibrationMode;


public class MainActivity extends ActionBarActivity {

    MiBand miband;
    Button bt_connect, bt_set, bt_vibration, bt_display;
    TextView tv_add, tv_energy, tv_rssi;

    Handler handler;

    private Runnable update_runnable = new Runnable() {
        @Override
        public void run() {
            miband.readRssi(new ActionCallback() {
                @Override
                public void onSuccess(Object data) {
                    rssi = (int) data;
                }

                @Override
                public void onFail(int errorCode, String msg) {
                    Log.d(TAG, "readRssi fail");
                }
            });
            tv_rssi.setText(Integer.toString(rssi));
            if (rssi < -90) {
                //取得Notification服務
                NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

                //設定當按下這個通知之後要執行的ACTIVITY
                Intent notifyIntent = new Intent(MainActivity.this, MainActivity.class);
                notifyIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                PendingIntent appIntent = PendingIntent.getActivity(MainActivity.this, 0,
                        notifyIntent, 0);
                Notification notification = new Notification();
                //設定出現在狀態列的圖示
                notification.icon = R.drawable.miss2;
                //顯示再狀態列的文字
                notification.tickerText = "notification on status bar.";
                //會友通知預設的鈴聲、震動、LED
                notification.defaults = Notification.DEFAULT_VIBRATE;
                notification.defaults |= Notification.DEFAULT_LIGHTS;
                notification.defaults = Notification.DEFAULT_SOUND;
                //設定通知的標題、內容
                notification.setLatestEventInfo(MainActivity.this, "Child missed", "call police!", appIntent);
                //送出Notification
                notificationManager.notify(0, notification);

                miband.startVibration(VibrationMode.VIBRATION_WITH_LED);
            }
            handler.postDelayed(update_runnable, 500);
        }
    };
    String address;
    int energy, rssi;


    private static final String TAG = "==[mibandtest]==";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        miband = new MiBand(this);
        bt_connect = (Button) findViewById(R.id.bt_connect);
        bt_set = (Button) findViewById(R.id.bt_set);
        bt_display = (Button) findViewById(R.id.bt_display);
        bt_vibration = (Button) findViewById(R.id.bt_vibration);
        tv_add = (TextView) findViewById(R.id.tv_add);
        tv_energy = (TextView) findViewById(R.id.tv_energy);
        tv_rssi = (TextView) findViewById(R.id.tv_rssi);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void set(View view) {
        UserInfo userInfo = new UserInfo(19940604, 1, 21, 153, 48, "RUBY", 1);
        miband.setUserInfo(userInfo);
    }

    public void connect(View view) {
        try{
        miband.connect(new ActionCallback() {

            @Override
            public void onSuccess(Object data) {
                Log.d(TAG, "connect success");
            }

            @Override
            public void onFail(int errorCode, String msg) {
                Log.d(TAG, "connect fail, code:" + errorCode + ",mgs:" + msg);
            }
        });
        }
        catch(Exception ex1){

        }

    }

    //顯示ADDRESS 配對
    public void display(View view) {
        try{
        address = miband.getDevice().getAddress().toString();
        tv_add.setText(address);
        if (address == "88:0F:10:2B:D1:C8") {

            miband.pair(new ActionCallback() {
                @Override
                public void onSuccess(Object data) {
                    Log.d(TAG, "Success");
                }

                @Override
                public void onFail(int errorCode, String msg) {
                    Log.d(TAG, "Failed");
                }
            });
        }
        }
        catch(Exception ex2){

        }
    }

    //震動
    public void vibration(View view) {
        try {
            miband.startVibration(VibrationMode.VIBRATION_WITH_LED);
        }
        catch(Exception ex2){

        }
    }

    //取得電量
    public void battery(View view) {
        try{
        miband.getBatteryInfo(new ActionCallback() {

            @Override
            public void onSuccess(Object data) {
                BatteryInfo info = (BatteryInfo) data;
                energy = info.getLevel();

                //cycles:4,level:44,status:unknow,last:2015-04-15 03:37:55
            }

            @Override
            public void onFail(int errorCode, String msg) {
                Log.d(TAG, "Battery fail");
            }
        });
        tv_energy.setText(energy + "%");
        }
        catch(Exception ex3){

        }
    }

    //RSSI
    public void rssi(View view) {
        try {
            handler = new Handler();
            handler.postDelayed(update_runnable, 500);
        }
        catch(Exception ex4){

        }
    }

    //LED
    public void LED(View view) {
        try {
            miband.setLedColor(LedColor.BLUE);
        }
        catch(Exception ex6){

        }
    }
}
