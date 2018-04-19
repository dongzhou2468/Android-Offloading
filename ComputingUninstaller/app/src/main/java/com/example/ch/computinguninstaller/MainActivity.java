package com.example.ch.computinguninstaller;

import android.content.Context;
import android.net.TrafficStats;
import android.os.BatteryManager;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.ch.offloading.EnergyUtil;
import com.ch.offloading.OffloadingJudge;
import com.ch.offloading.UninstallProxy;
import com.ch.offloading.SocketUtil;
import com.example.ch.interfaces.UninstallInterface;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private EditText    floors, eggs;
    private Button      localButton, remoteButton;
    private TextView    localResult, remoteResult, energyInfo, networkFlow;
    public static MyHandler myHandler;

    private static final String COMPUTING = "computing";
    private static final String LOCALRESULT = "LocalResult";
    private static final String REMOTERESULT = "RemoteResult";

    public static String dir;
    public static Map<String, Double> Rn = new HashMap<String, Double>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        floors          = (EditText) findViewById(R.id.floors);
        eggs            = (EditText) findViewById(R.id.eggs);
        localButton     = (Button) findViewById(R.id.localButton);
        remoteButton    = (Button) findViewById(R.id.remoteButton);
        localResult     = (TextView) findViewById(R.id.localResult);
        remoteResult    = (TextView) findViewById(R.id.remoteResult);
        energyInfo      = (TextView) findViewById(R.id.energy);

        myHandler = new MyHandler();

        localButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        long totalBeginTime = System.currentTimeMillis();

                        int n = Integer.parseInt(floors.getText().toString());
                        int m = Integer.parseInt(eggs.getText().toString());
                        Log.i(SocketUtil.LOGTAG, "floors:" + n + ", eggs: " + m);
                        myHandler.sendMessage(getMessage(LOCALRESULT, COMPUTING));

                        EnergyThread et = new EnergyThread();
                        new Thread(et).start();
//                        long scriptBeginTime = System.currentTimeMillis();
//                        printEnergy();
//                        long scriptEndTime = System.currentTimeMillis();
//                        Log.i(EnergyUtil.LOG, "printEnergy() time: " + (double)(scriptEndTime - scriptBeginTime) / 1000);

                        long beginTime = System.currentTimeMillis();
                        long beginThreadTime = SystemClock.currentThreadTimeMillis();
                        int result = DropEgg.fun(n, m);
                        long endThreadTime = SystemClock.currentThreadTimeMillis();
                        long endTime = System.currentTimeMillis();
//                        new Thread(et).start();
//                        printEnergy();

                        long btt, ett, btt1, ett1;
                        int test, test1;
                        for (int i = 12; i < 13; i++) {
                            double tmp = 0;
                            for (int j = 0; j < 10; j++) {
                                btt = SystemClock.currentThreadTimeMillis();
                                test = DropEgg.fun(20, 2);
                                ett = SystemClock.currentThreadTimeMillis();
                                btt1 = SystemClock.currentThreadTimeMillis();
                                test1 = DropEgg.fun(18 + i, 2);
                                ett1 = SystemClock.currentThreadTimeMillis();
                                tmp += Math.round((double)(ett1 - btt1) / (ett - btt) * 1000) / 1000.0;
                            }
                            Log.i("myLog", (18 + i) + ":" + Math.round(tmp / 10 * 1000) / 1000.0);
                            Rn.put(String.valueOf(18 + i), Math.round(tmp / 10 * 1000) / 1000.0);
                        }

                        myHandler.sendMessage(getMessage(LOCALRESULT, String.valueOf(result)
                                + ", time: " + (double)(endTime - beginTime) / 1000
                                + "\nthread t': " + (double)(endThreadTime - beginThreadTime) / 1000));
//                                + "\nt't'(25,22): " + (ett1 - btt1) / 1000.0 + ", " + (ett - btt) / 1000.0
//                                + "\n(25/22): " + Math.round((double)(ett1 - btt1) / (ett - btt) * 1000) / 1000.0));

                        long totalEndTime = System.currentTimeMillis();
                        Log.i(EnergyUtil.LOG, "run() time: " + (double)(totalEndTime - totalBeginTime) / 1000);
                    }
                }).start();
            }
        });

        remoteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        int n = Integer.parseInt(floors.getText().toString());
                        int m = Integer.parseInt(eggs.getText().toString());
                        Log.i(SocketUtil.LOGTAG, "floors:" + n + ", eggs: " + m);
                        myHandler.sendMessage(getMessage(REMOTERESULT, COMPUTING));
//                        printEnergy();
                        long beginTime = System.currentTimeMillis();
                        long beginThreadTime = SystemClock.currentThreadTimeMillis();
                        // uninstall entrance
                        Object result = ((UninstallInterface) UninstallProxy.getProxy(new RemoteDropEgg())).fun(n, m);
                        long endThreadTime = SystemClock.currentThreadTimeMillis();
                        long endTime = System.currentTimeMillis();
//                        printEnergy();
                        myHandler.sendMessage(getMessage(REMOTERESULT, String.valueOf(result)
                                + ", time: " + (double)(endTime - beginTime) / 1000
                                + "\nthread t': " + (double)(endThreadTime - beginThreadTime) / 1000));
                    }
                }).start();
            }
        });

        networkFlow = (TextView) findViewById(R.id.networkFlow);
        new Thread(new NetworkThread()).start();

        dir = getFilesDir().getAbsolutePath();
    }

    class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            String str = msg.getData().getString(LOCALRESULT);
            if (str != null) {
                Log.i(SocketUtil.LOGTAG, LOCALRESULT + ": " + str);
                localResult.setText(str);
            } else {
                str = msg.getData().getString(REMOTERESULT);
                if (str != null) {
                    Log.i(SocketUtil.LOGTAG, REMOTERESULT + ": " + str);
                    remoteResult.setText(str);
                } else {
                    str = msg.getData().getString("EnergyInfo");
                    if (str != null) {
                        energyInfo.setText(str);
                    } else {
                        str = msg.getData().getString("NetworkInfo");
                        networkFlow.setText(str);
                    }
                }
            }
        }
    }

    Message getMessage(String key, String value) {
        Bundle bundle = new Bundle();
        bundle.putString(key, value);
        Message msg = new Message();
        msg.setData(bundle);
        return msg;
    }

    class EnergyThread implements Runnable {

        @Override
        public void run() {
             long scriptBeginTime = System.currentTimeMillis();
             printEnergy();
             long scriptEndTime = System.currentTimeMillis();
             Log.i(EnergyUtil.LOG, "printEnergy() time: " + (double)(scriptEndTime - scriptBeginTime) / 1000);

        }
    }

    void printEnergy() {

        /*
        myHandler.sendMessage(getMessage("EnergyInfo",
                EnergyUtil.PrintEnergy((BatteryManager) getSystemService(BATTERY_SERVICE))));
        myHandler.sendMessage(getMessage("EnergyInfo",
                EnergyUtil.getBatteryCapacity(MainActivity.this)));
                */
//        myHandler.sendMessage(getMessage("EnergyInfo", EnergyUtil.getBatteryCost()));
        myHandler.sendMessage(getMessage("EnergyInfo", OffloadingJudge.getAvailMemory(this)));
    }

    class NetworkThread implements Runnable {

        @Override
        public void run() {

            /*
            while (true) {
                long tx = TrafficStats.getTotalTxBytes();
                long rx = TrafficStats.getTotalRxBytes();
                int t = 500;
                try {
                    Thread.sleep(t);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                long tx1 = TrafficStats.getTotalTxBytes();
                long rx1 = TrafficStats.getTotalRxBytes();
                String networkFlow = "Tx: " + (tx1 - tx) * 1000 / t + "B/s\n" +
                        "Rx: " + (rx1 - rx) * 1000 / t + "B/s";
                myHandler.sendMessage(getMessage("NetworkInfo", networkFlow));
            }
            */
            while(true) {


            }
        }
    }

}
