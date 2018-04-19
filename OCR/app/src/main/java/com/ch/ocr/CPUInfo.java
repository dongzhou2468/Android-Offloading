package com.ch.ocr.intf;

import android.util.Log;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;

/**
 * Created by CH on 2017/11/20.
 */

public class CPUInfo {

    private static double o_idle;
    private static double o_cpu;

    public static String getCpuUsage() {
        getCpuUsage(true);
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return getCpuUsage(false);
    }

    private static String getCpuUsage(boolean initCpu) {
        double usage = 0.0;
        if (initCpu) {
            String[] toks = readProcStat().split(" ");
            o_idle = Double.parseDouble(toks[5]);
            o_cpu = Double.parseDouble(toks[2])
                    + Double.parseDouble(toks[3])
                    + Double.parseDouble(toks[4])
                    + Double.parseDouble(toks[6])
                    + Double.parseDouble(toks[7])
                    + Double.parseDouble(toks[8])
                    + Double.parseDouble(toks[9]);
        } else {
            String[] toks = readProcStat().split(" ");
            double c_idle = Double.parseDouble(toks[5]);
            double c_cpu = Double.parseDouble(toks[2])
                    + Double.parseDouble(toks[3])
                    + Double.parseDouble(toks[4])
                    + Double.parseDouble(toks[6])
                    + Double.parseDouble(toks[7])
                    + Double.parseDouble(toks[8])
                    + Double.parseDouble(toks[9]);
            if (0 != ((c_cpu + c_idle) - (o_cpu + o_idle))) {
                usage = (c_cpu - o_cpu) / (c_cpu - o_cpu + c_idle - o_idle );
                usage = Math.round(usage * 1000) / 10.0;
//                Log.i("myLog", "/proc/stat: " + usage);
            }
//            if (usage == 0)
//                return "1.0";
            if (usage < 0 || usage > 100) {
                // exception
                return dumpsysQuery();
            }
        }
        return String.valueOf(usage);
    }

    private static String readProcStat() {

        RandomAccessFile reader = null;
        String load = "";
        try {
            reader = new RandomAccessFile("/proc/stat", "r");
            load = reader.readLine();
//            Log.i("myLog", load);
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return load;
    }

    public static String dumpsysQuery() {
        // Superuser is necessary
        String COMMAND_SU       = "su";
        String COMMAND_EXIT     = "exit\n";
        String COMMAND_LINE_END = "\n";
        String query  = "dumpsys cpuinfo | grep 'TOTAL'";

        Process process;
        DataOutputStream dos;
        BufferedReader sbr;
        String result = "";
        try {
            process = Runtime.getRuntime().exec(COMMAND_SU);
            dos = new DataOutputStream(process.getOutputStream());
            dos.write(query.getBytes());
            dos.writeBytes(COMMAND_LINE_END);
            dos.writeBytes(COMMAND_EXIT);
            dos.flush();

            int exitValue = process.waitFor();
            if (exitValue != 0) {
                Log.i("myLog", exitValue + "");
                return "ERROR";
            }
            sbr = new BufferedReader(new InputStreamReader(process.getInputStream()));
            result = sbr.readLine().split(" ")[0].split("%")[0];

        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return result;
    }
}
