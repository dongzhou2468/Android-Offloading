package com.ch.offloading;

import android.content.Context;
import android.os.BatteryManager;
import android.util.Log;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by CH on 2017/6/12.
 */

public class EnergyUtil {

    public static final String LOG = "ENERGYLOG";

    public static String PrintEnergy(BatteryManager batteryManager) {

        Log.i(LOG, "print energy...");
        String energyInfo = "BATTERY_PROPERTY_CHARGE_COUNTER: "
                + batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CHARGE_COUNTER) + "\n"
                + "BATTERY_PROPERTY_CURRENT_NOW: "
                + batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CURRENT_NOW) + "\n"
                + "BATTERY_PROPERTY_CURRENT_AVERAGE: "
                + batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CURRENT_AVERAGE) + "\n"
                + "BATTERY_PROPERTY_CAPACITY: "
                + batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY) + "\n"
                + "BATTERY_PROPERTY_ENERGY_COUNTER: "
                + batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_ENERGY_COUNTER);
        Log.i(LOG, energyInfo);
        return energyInfo;
//        Log.i(LOG, "BATTERY_PROPERTY_CHARGE_COUNTER: "
//                + batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CHARGE_COUNTER));
//        Log.i(LOG, "BATTERY_PROPERTY_CURRENT_NOW: "
//                + batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CURRENT_NOW));
//        Log.i(LOG, "BATTERY_PROPERTY_CURRENT_AVERAGE: "
//                + batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CURRENT_AVERAGE));
//        Log.i(LOG, "BATTERY_PROPERTY_CAPACITY: "
//                + batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY));
//        Log.i(LOG, "BATTERY_PROPERTY_ENERGY_COUNTER: "
//                + batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_ENERGY_COUNTER));
    }

    public static String getBatteryCapacity(Context context) {
        Object mPowerProfile_ = null;
        double batteryCapacity = 0;

        final String POWER_PROFILE_CLASS = "com.android.internal.os.PowerProfile";

        try {
            mPowerProfile_ = Class.forName(POWER_PROFILE_CLASS)
                    .getConstructor(Context.class).newInstance(context);
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            batteryCapacity = (Double) Class
                    .forName(POWER_PROFILE_CLASS)
                    .getMethod("getAveragePower", java.lang.String.class)
                    .invoke(mPowerProfile_, "battery.capacity");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return batteryCapacity + "";
    }

    public static String getBatteryCost() {

        String COMMAND_SU       = "su";
        String COMMAND_SH       = "sh";
        String COMMAND_EXIT     = "exit\n";
        String COMMAND_LINE_END = "\n";

//        String query  = "dumpsys batterystats --unplugged com.example.ch.computinguninstaller | grep 'Uid u0a311'";
        String query  = "dumpsys batterystats com.example.ch.computinguninstaller | grep 'System starts'";

        Process process;
        DataOutputStream dos;
        BufferedReader sbr, ebr;
        StringBuilder susMsg = new StringBuilder();
//        StringBuilder errMsg = new StringBuilder();
        try {
            process = Runtime.getRuntime().exec(COMMAND_SU);
            dos = new DataOutputStream(process.getOutputStream());
            dos.write(query.getBytes());
            dos.writeBytes(COMMAND_LINE_END);
            dos.writeBytes(COMMAND_EXIT);
            dos.flush();

            int result = process.waitFor();
            sbr = new BufferedReader(new InputStreamReader(process.getInputStream()));
//            ebr = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            String s;
            while ((s = sbr.readLine()) != null) susMsg.append(s);
//            while ((s = ebr.readLine()) != null) errMsg.append(s);
            Log.i(LOG, "getBatteryCost: " + result + ": " + susMsg);

        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return susMsg.toString();
    }
}