package com.ch.offloadingjartest.activities;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.ch.offloading.*;
import com.ch.offloadingjartest.R;
import com.ch.offloadingjartest.interfaces.DropEggInt;
import com.ch.offloadingjartest.utils.DropEgg;

public class MainActivity extends AppCompatActivity {

    private Button localButton, remoteButton;
    private TextView localResult, remoteResult;
    public static MyHandler myHandler;

    private static final String COMPUTING = "computing";
    private static final String LOCALRESULT = "LocalResult";
    private static final String REMOTERESULT = "RemoteResult";
    private static final String LOGTAG = "log";

    private int n = 26;
    private int m = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        localButton     = (Button) findViewById(R.id.button);
        localResult     = (TextView) findViewById(R.id.textView);
        remoteButton    = (Button) findViewById(R.id.button2);
        remoteResult    = (TextView) findViewById(R.id.textView2);

        myHandler = new MyHandler();

        localButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        myHandler.sendMessage(getMessage(LOCALRESULT, COMPUTING));
                        long beginTime = System.currentTimeMillis();
                        int result = new DropEgg().fun(n, m);
                        long endTime = System.currentTimeMillis();
                        myHandler.sendMessage(getMessage(LOCALRESULT, String.valueOf(result)
                                + ", time: " + (double)(endTime - beginTime) / 1000));
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
                        myHandler.sendMessage(getMessage(REMOTERESULT, COMPUTING));
                        long beginTime = System.currentTimeMillis();
                        // uninstall entrance
                        Object result = ((DropEggInt) UninstallProxy.getProxy(new DropEgg())).fun(n, m);
                        long endTime = System.currentTimeMillis();
                        myHandler.sendMessage(getMessage(REMOTERESULT, String.valueOf(result)
                                + ", time: " + (double)(endTime - beginTime) / 1000));
                    }
                }).start();
            }
        });
    }

    class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            String str = msg.getData().getString(LOCALRESULT);
            if (str != null) {
//                Log.i(LOGTAG, LOCALRESULT + ": " + str);
                localResult.setText(str);
            } else {
                str = msg.getData().getString(REMOTERESULT);
//                Log.i(LOGTAG, REMOTERESULT + ": " + str);
                remoteResult.setText(str);
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
}
