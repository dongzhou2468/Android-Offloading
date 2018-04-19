package com.ch.ocr.intf;

import java.io.FileInputStream;

/**
 * Created by CH on 2018/2/28.
 */

public interface OCRUtilIntf {

    String OLAbleRecognize(byte[] buffer, String dataPath, String defaultLanguage);
}
