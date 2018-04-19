package com.ch.offloading;

import android.os.Bundle;
import android.os.Message;
import android.util.Log;

import com.ch.collector.ResultBean;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;

/**
 * Created by CH on 2017/4/16.
 */

public class SocketUtil {

    public static final String LOGTAG = "myLog";

    private static final int LOCAL_PORT = 4567;

    public static InfoBean computeInfo;

    public static void createThread() {

        ServerSocket serverSocket;
        try {
            serverSocket = new ServerSocket(TimeCostMeasure.MEASURE_PORT);
            while (true) {
                final Socket socket = serverSocket.accept();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        TimeCostMeasure.TCPRecvAndReturn(socket);
                    }
                }).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void TCPRecvAndReturn(Socket socket) {

//        String memory = MainActivity.getMemoryStat();
        byte[] recvBuffer = new byte[1024 * 100];
        Object result = null;
        try {
                SocketAddress socketAddr = socket.getRemoteSocketAddress();
                Log.i(LOGTAG, "receive from: " + socketAddr);

                InputStream is = socket.getInputStream();
                int readCode = 0;
                readCode = is.read(recvBuffer);
                Log.i(LOGTAG, "inputstream.read(): " + readCode);
                if (readCode != -1) {
                    Log.i(LOGTAG, recvBuffer.length + "");
                    ReadComputeInfo(recvBuffer);
                }

                Display(result, 0);
                long beginTime = System.currentTimeMillis();
                result = DynamicClassLoader.PathClassLoaderWay(computeInfo);
//                result = DynamicClassLoader.DexClassLoaderWay(computeInfo);
//                result = DynamicClassLoader.ReflectTest(computeInfo);
                long endTime = System.currentTimeMillis();
                Display(result, (int)(endTime - beginTime));
//                int result = Computing();

                Log.i(LOGTAG, "sending back result...");
//                BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
                OutputStream os = socket.getOutputStream();
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                ObjectOutputStream oos = new ObjectOutputStream(baos);
                oos.writeObject(result);
//                oos.writeObject(setResultBean(result, memory));
                byte[] bytes = baos.toByteArray();
                Log.i(LOGTAG,  "result size: " + bytes.length);
//                bw.write(String.valueOf(result) + "\n");       // int to char might lost; add "\n" and readLine()
//                bw.flush();
                os.write(bytes);
                os.flush();
                Log.i(LOGTAG, "task finished...");

                socket.close();
                result = null;

        } catch (IOException e) {
            e.printStackTrace();
            Log.i(LOGTAG, e.toString());
        }
    }

    public static void Display(Object result, int time) {

        if (result == null)
            HandlerSend("ClientInfo", computeInfo.toString());
        HandlerSend("Result", "Computing...");
//        Log.i(LOGTAG, "start computing in cloud...");
//        Object[] params = computeInfo.getParams();
//        int result = DropEgg.fun((Integer)params[0], (Integer)params[1]);
        if (result != null)
            HandlerSend("Result", "result: " + result.toString() + ", time: " + (double)time / 1000);
//        return result;
    }

    public static void ReadComputeInfo(byte[] recvBuffer) {

        ByteArrayInputStream bais = new ByteArrayInputStream(recvBuffer);
        try {
            ObjectInputStream ois = new ObjectInputStream(bais);
            computeInfo = (InfoBean) ois.readObject();
            Log.i(SocketUtil.LOGTAG, computeInfo.toString());
            /*computeInfo = (Map<String, Object>) ois.readObject();
            Log.i(LOGTAG, computeInfo.size() + "");
            Log.i(LOGTAG, (String) computeInfo.get("ApkName"));
            Log.i(LOGTAG, (String) computeInfo.get("PackageName"));
            Log.i(LOGTAG, (String) computeInfo.get("ClassName"));
            Log.i(LOGTAG, (String) computeInfo.get("MethodName"));
            for (int i = 0; i < computeInfo.size() - 4; i++) {
                Log.i(LOGTAG, computeInfo.get("Arg" + i).toString());
            }*/
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private static void HandlerSend(String key, String result) {
        Bundle bundle = new Bundle();
        bundle.putString(key, result);
        Message msg = new Message();
        msg.setData(bundle);
        MainActivity.myHandler.sendMessage(msg);
    }

    public static ResultBean setResultBean(Object result, String memory) {
        ResultBean rb = new ResultBean();
        rb.setResult(result);
        rb.setMemory(memory);
        return rb;
    }

}
