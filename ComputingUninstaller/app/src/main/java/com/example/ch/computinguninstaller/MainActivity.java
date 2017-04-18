package com.example.ch.computinguninstaller;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.ch.dynamicproxy.UninstallProxy;
import com.example.ch.utils.SocketUtil;

public class MainActivity extends AppCompatActivity {

    private EditText    floors, eggs;
    private Button      localButton, remoteButton;
    private TextView    localResult, remoteResult;
    public static MyHandler myHandler;

    private static final String COMPUTING = "computing";
    private static final String LOCALRESULT = "LocalResult";
    private static final String REMOTERESULT = "RemoteResult";

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

        myHandler = new MyHandler();

        localButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        int n = Integer.parseInt(floors.getText().toString());
                        int m = Integer.parseInt(eggs.getText().toString());
                        Log.i(SocketUtil.LOGTAG, "floors:" + n + ", eggs: " + m);
                        myHandler.sendMessage(getMessage(LOCALRESULT, COMPUTING));
                        long beginTime = System.currentTimeMillis();
                        int result = DropEgg.fun(n, m);
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
                        int n = Integer.parseInt(floors.getText().toString());
                        int m = Integer.parseInt(eggs.getText().toString());
                        Log.i(SocketUtil.LOGTAG, "floors:" + n + ", eggs: " + m);
                        myHandler.sendMessage(getMessage(REMOTERESULT, COMPUTING));
                        long beginTime = System.currentTimeMillis();
                        Object result = UninstallProxy.getProxy(new RemoteDropEgg()).fun(n, m);
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
                Log.i(SocketUtil.LOGTAG, LOCALRESULT + ": " + str);
                localResult.setText(str);
            } else {
                str = msg.getData().getString(REMOTERESULT);
                Log.i(SocketUtil.LOGTAG, REMOTERESULT + ": " + str);
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
