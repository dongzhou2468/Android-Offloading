package com.example.ch.interfaces;

/**
 * Created by CH on 2017/4/15.
 */

/**
 * This Interface is needed and function(s) shall be added by developer.
 * The two variables are required:
 *      APKNAME      : apk file's name of this app
 *      PACKAGENAME  : value of "package" in manifest
 * But not needed if using PathClassLoader, and action in intent is specified and added to manifest.
 */

public interface UninstallInterface {

//    String APKNAME = "MyAndroid.apk";
//    String PACKAGENAME = "com.example.ch.computinguninstaller";
    int fun(int n, int m);
}
