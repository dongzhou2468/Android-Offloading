package com.example.ch.cloudcomputor;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private TextView receive;
    public static MyHandler myHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        receive = (TextView) findViewById(R.id.receive);
        myHandler = new MyHandler();

        new Thread(new Runnable() {
            @Override
            public void run() {
                SocketUtil.TCPRecv();
            }
        }).start();
    }

    class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            String str = msg.getData().getString("Result");
            receive.setText(str);
        }
    }
}
