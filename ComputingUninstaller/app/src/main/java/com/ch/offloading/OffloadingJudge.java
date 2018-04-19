package com.ch.offloading;

import android.app.ActivityManager;
import android.app.ActivityManager.MemoryInfo;
import android.content.Context;
import android.text.format.Formatter;
import android.util.Log;

/**
 * Created by CH on 2017/11/17.
 */

public class OffloadingJudge {

    public static String getAvailMemory(Context context) {

        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        MemoryInfo mi = new MemoryInfo();
        am.getMemoryInfo(mi);
        long freeMem = mi.availMem;
        String result = Formatter.formatFileSize(context, freeMem);
        Log.i(EnergyUtil.LOG, "free memory: " + freeMem + ", " + result);
        return result;
    }
}
