package com.ch.collector;

import android.os.Environment;
import android.util.Log;

import com.example.ch.computinguninstaller.MainActivity;

import java.io.FileWriter;

/**
 * Created by CH on 2017/11/20.
 */

public class CollectUtil {

    public static StringBuilder tmpString;
    public static void collectMemory(String memory, long time) {

        float alcMemory  = Float.valueOf(memory.split(",")[0]);
        float freeMemory = Float.valueOf(memory.split(",")[1]);
        double m = Math.round((1 - freeMemory / alcMemory) * 1000) / 10.0;
        double t = time * 1.0 / 1000;
        writeFile("," + m + "," + t, false);
    }

    public static void collectCPUAndNetwork(String data) {

//        String cpu     = data.split(",")[0];
//        String network = data.split(",")[1];
        writeFile(data, true);
    }

    public static void writeFile(String string, boolean head) {

        if (head) {
            Log.i("myLog", "write cpu and network: " + string);
            tmpString = tmpString.append(string);
            return;
        } else {
            Log.i("myLog", "write memory and time: " + string);
            tmpString = tmpString.append(string + "\n");
        }
        FileWriter fw;
        try {
//            fw = new FileWriter("/data/system/data.txt", true);
//            fw = new FileWriter(Environment.getExternalStorageDirectory() + "/data.txt", true);
            fw = new FileWriter(MainActivity.dir + "/data.txt", true);
            fw.write(tmpString.toString());
            fw.close();
        } catch (Exception e) {
//            e.printStackTrace();
            Log.i("myLog", e.toString());
        }
    }
}
