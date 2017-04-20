package com.example.ch.dynamicproxy;

/**
 * Created by CH on 2017/4/15.
 */

/**
 * This Interface is needed and function(s) shall be added by developer.
 * The two variables are required:
 *      APKNAME      : apk file's name of this app
 *      PACKAGENAME  : value of "package" in manifest
 */
public interface UninstallInterface {

    String APKNAME = "CloudComputor.apk";
    String PACKAGENAME = "com.example.ch.computinguninstaller";
    int fun(int n, int m);
}
