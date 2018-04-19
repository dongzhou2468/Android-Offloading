package com.ch.ocr;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.ch.ocr.intf.OCRUtilIntf;
import com.googlecode.tesseract.android.TessBaseAPI;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;

/**
 * Created by CH on 2018/2/28.
 */

public class OCRUtil implements OCRUtilIntf {

    private static final String LOGTAG = "OCR";

    // 目标函数声明为public
    @Override
    public String OLAbleRecognize(byte[] data, String dataPath, String defaultLanguage) {

        long t = System.currentTimeMillis();
//        Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
//        Bitmap bitmap = BitmapFactory.decodeStream(fis);
        Bitmap bitmap = BitmapFactory.decodeStream(new ByteArrayInputStream(data));
        Log.i(LOGTAG, "get bitmap: " + (System.currentTimeMillis() - t) / 1000.0 + "s");
        // 需要storage权限，计算代理值得拥有
        TessBaseAPI tessBaseAPI = new TessBaseAPI();
        Log.i(LOGTAG, "new tessbase api: " + (System.currentTimeMillis() - t) / 1000.0 + "s");
        tessBaseAPI.init(dataPath, defaultLanguage);
        Log.i(LOGTAG, "tessbase api init: " + (System.currentTimeMillis() - t) / 1000.0 + "s");
        tessBaseAPI.setImage(bitmap);
        Log.i(LOGTAG, "set image: " + (System.currentTimeMillis() - t) / 1000.0 + "s");
        String result = tessBaseAPI.getUTF8Text();
        Log.i(LOGTAG, "get result: " + result + ", " + (System.currentTimeMillis() - t) / 1000.0 + "s");
        tessBaseAPI.end();
        return result;
    }
}
