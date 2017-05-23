package com.example.ch.cloudcomputor;

import android.content.Context;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private TextView clientInfo, result;
    public static MyHandler myHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        clientInfo = (TextView) findViewById(R.id.clientInfo);
        result = (TextView) findViewById(R.id.result);
//        Context context1 = this.getApplicationContext();
//        if (getApplicationContext() == context1)
//            Log.i(SocketUtil.LOGTAG, "getApplicationContext() <=> this.getApplicationContext()");
//        if (context.getFilesDir() != null)
//            clientInfo.setText(context.getFilesDir().toString());
//        if (context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS) != null)
//            result.setText(context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS).toString());
        String dexDir = this.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS).toString();
        String dexOutputDir = this.getExternalCacheDir().toString();
        DynamicClassLoader.setDir(dexDir, dexOutputDir, this.getClassLoader(), getPackageManager());

        myHandler = new MyHandler();

        new Thread(new Runnable() {
            @Override
            public void run() {
                SocketUtil.TCPRecvAndReturn();
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
                clientInfo.setText(msg.getData().getString("ClientInfo"));
            }
        }
    }
}
