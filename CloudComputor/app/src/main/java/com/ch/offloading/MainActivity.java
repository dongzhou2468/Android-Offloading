package com.ch.offloading;

import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.ch.offloading.R;

public class MainActivity extends AppCompatActivity {

    private TextView clientInfo, result, memoryStat;
    public static MyHandler myHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        clientInfo = (TextView) findViewById(R.id.clientInfo);
        result = (TextView) findViewById(R.id.result);
        /*
        Context context1 = this.getApplicationContext();
        if (getApplicationContext() == context1)
            Log.i(SocketUtil.LOGTAG, "getApplicationContext() <=> this.getApplicationContext()");
        if (context.getFilesDir() != null)
            clientInfo.setText(context.getFilesDir().toString());
        if (context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS) != null)
            result.setText(context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS).toString());
        */
        String dexDir = this.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS).toString();
        String dexOutputDir = this.getExternalCacheDir().toString();
        DynamicClassLoader.setDir(dexDir, dexOutputDir, this.getClassLoader(), getPackageManager());

        myHandler = new MyHandler();

        new Thread(new Runnable() {
            @Override
            public void run() {
                SocketUtil.createThread();
            }
        }).start();

//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                TimeCostMeasure.createThread();
//            }
//        }).start();

        memoryStat = (TextView) findViewById(R.id.memoryStat);
        new Thread(new Runnable() {
            @Override
            public void run() {
                setMemoryStat();
            }
        }).start();
    }

    class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            String str = msg.getData().getString("Result");
            if (str != null)
                result.setText(str);
            else {
                str = msg.getData().getString("ClientInfo");
                if (str != null)
                    clientInfo.setText(str);
                else
                    memoryStat.setText(msg.getData().getString("MemoryStat"));
            }
        }
    }

    public void setMemoryStat() {

        while (true) {
            myHandler.sendMessage(getMessage("MemoryStat", getMemoryStat()));
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static String getMemoryStat() {

        float maxMemory;
        float alcMemory;
        float freeMemory;

        maxMemory  = (float) (Runtime.getRuntime().maxMemory() * 1.0 / (1024 * 1024));
        alcMemory  = (float) (Runtime.getRuntime().totalMemory() * 1.0 / (1024 * 1024));
        freeMemory = (float) (Runtime.getRuntime().freeMemory() * 1.0 / (1024 * 1024));
        /*
        String memoryStatStr = "Max memory: " + maxMemory +
                               "\nAllocated memory: " + alcMemory +
                               "\nFree memory: " + freeMemory;
        */
        String memoryStatStr = alcMemory + "," + freeMemory;
        return  memoryStatStr;
    }

    public Message getMessage(String key, String value) {
        Bundle bundle = new Bundle();
        bundle.putString(key, value);
        Message msg = new Message();
        msg.setData(bundle);
        return msg;
    }
}
