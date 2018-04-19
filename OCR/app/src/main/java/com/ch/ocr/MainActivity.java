package com.ch.ocr;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.ch.ocr.intf.CPUInfo;
import com.ch.ocr.intf.OCRUtilIntf;
import com.ch.offloading.Offloading;
import com.ch.offloading.UninstallProxy;
import com.googlecode.tesseract.android.TessBaseAPI;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private static final String DATAPATH = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator;
    private static final String DEFAULT_LANGUAGE = "chi_sim";
//    private static final String DEFAULT_LANGUAGE = "eng";

    public static final String LOGTAG = "OCR";

    private TextView localResult, cloudResult;
    private Button localRecBtn, cloudRecBtn;

    private ProgressBar pb_local, pb_cloud;

//    private static boolean loading = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Offloading.init(this, 1400);

        localResult = (TextView) findViewById(R.id.text_local_result);
        localRecBtn = (Button) findViewById(R.id.button_local_recognize);
        localRecBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        loadingState(1);
                        textRecognize(1);
                    }
                }).start();
            }
        });
        cloudResult = (TextView) findViewById(R.id.text_cloud_result);
        cloudRecBtn = (Button) findViewById(R.id.button_cloud_recognize);
        cloudRecBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        loadingState(2);
                        textRecognize(2);
                    }
                }).start();
            }
        });

        pb_local = (ProgressBar) findViewById(R.id.loading_prgbar_local);
        pb_cloud = (ProgressBar) findViewById(R.id.loading_prgbar_cloud);
        pb_local.setVisibility(View.INVISIBLE);
        pb_cloud.setVisibility(View.INVISIBLE);

        final TextView cpu = (TextView) findViewById(R.id.text_cpu_data);
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            cpu.setText(CPUInfo.getCpuUsage());
                        }
                    });
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    public void localRecognize(byte[] data) {
        long t = System.currentTimeMillis();
//        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.test2);
//        Bitmap bitmap = BitmapFactory.decodeFile(DATAPATH + "tessdata" + File.separator + "images" + File.separator + "0.png");
        Bitmap bitmap = BitmapFactory.decodeStream(new ByteArrayInputStream(data));
        Log.i(LOGTAG, "get bitmap: " + (System.currentTimeMillis() - t) / 1000.0 + "s");
        TessBaseAPI tessBaseAPI = new TessBaseAPI();
        Log.i(LOGTAG, "new tessbase api: " + (System.currentTimeMillis() - t) / 1000.0 + "s");
        tessBaseAPI.init(DATAPATH, DEFAULT_LANGUAGE);
        Log.i(LOGTAG, "tessbase api init: " + (System.currentTimeMillis() - t) / 1000.0 + "s");
        tessBaseAPI.setImage(bitmap);
        Log.i(LOGTAG, "set image: " + (System.currentTimeMillis() - t) / 1000.0 + "s");
        final String result = tessBaseAPI.getUTF8Text();
        Log.i(LOGTAG, "get result: " + result + ", " + (System.currentTimeMillis() - t) / 1000.0 + "s");
        //loading = false;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //loading = false;      //why not here?
                localResult.setVisibility(View.VISIBLE);
                pb_local.setVisibility(View.INVISIBLE);
                localResult.setText(result);
            }
        });
        tessBaseAPI.end();
    }

    public void cloudRecognize(byte[] data) {
//        final String result = new OCRUtil().OLAbleRecognize(bitmap, DATAPATH, DEFAULT_LANGUAGE);
        // 返回的是Object对象，如果返回类型是基本数据类型，需要转换为包装类
        final String result = ((OCRUtilIntf) UninstallProxy.getProxy(new OCRUtil())).OLAbleRecognize(data, DATAPATH, DEFAULT_LANGUAGE);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                cloudResult.setVisibility(View.VISIBLE);
                pb_cloud.setVisibility(View.INVISIBLE);
                cloudResult.setText(result);
            }
        });
    }

    public void textRecognize(int i) {

        String imageName = "test2.png";
        String imagePath = DATAPATH + "tessdata" + File.separator + imageName;
        // Bitmap和FileInputStream都不能序列化，选择decodeStream方法，传输文件流字节数组
        FileInputStream fis = null;
        byte[] buffer = null;
        try {
            fis = new FileInputStream(imagePath);
            buffer = new byte[fis.available()];
            fis.read(buffer);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (i == 1)
            localRecognize(buffer);
        else
            cloudRecognize(buffer);

    }



    private void loadingState(final int i) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(i == 1) {
                    localResult.setVisibility(View.INVISIBLE);
                    pb_local.setVisibility(View.VISIBLE);
                } else {
                    cloudResult.setVisibility(View.INVISIBLE);
                    pb_cloud.setVisibility(View.VISIBLE);
                }
                /*
                int i = 1;
                while (loading) {
                    if (i == 1) {
                        Log.i(LOGTAG, "loading state..." + i);
                        resultTv.setText("正在识别.");          // why not work?
                    }
                    else if (i == 2) {
                        Log.i(LOGTAG, "loading state..." + i);
                        resultTv.setText("正在识别..");
                    }
                    else if (i == 3) {
                        Log.i(LOGTAG, "loading state..." + i);
                        resultTv.setText("正在识别...");
                    }
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    if (i == 3)
                        i = 1;
                    else
                        i++;
                }
                */
            }
        });
    }
}
