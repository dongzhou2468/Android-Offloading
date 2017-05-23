package com.example.ch.cloudcomputor;

import android.os.Bundle;
import android.os.Message;
import android.util.Log;

import com.example.ch.bean.InfoBean;

import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;

/**
 * Created by CH on 2017/4/16.
 */

public class SocketUtil {

    public static final String LOGTAG = "myLog";

    private static final int LOCAL_PORT = 4567;

    private static InfoBean computeInfo;

    public static void TCPRecvAndReturn() {

        ServerSocket serverSocket;
        byte[] recvBuffer = new byte[1024];
        Object result = null;
        try {
            while (true) {
                serverSocket = new ServerSocket(LOCAL_PORT);
                Socket socket = serverSocket.accept();
                SocketAddress socketAddr = socket.getRemoteSocketAddress();
                Log.i(LOGTAG, socketAddr + "");

                InputStream is = socket.getInputStream();
                if (is.read(recvBuffer) != -1) {
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
                byte[] bytes = baos.toByteArray();
                Log.i(LOGTAG, bytes.length + "");
//                bw.write(String.valueOf(result) + "\n");       // int to char might lost; add "\n" and readLine()
//                bw.flush();
                os.write(bytes);
                os.flush();
                Log.i(LOGTAG, "task finished...");

                serverSocket.close();
                result = null;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void Display(Object result, int time) {

        HandlerSend("ClientInfo", computeInfo.toString());
        HandlerSend("Result", "Computing...");
//        Log.i(LOGTAG, "start computing in cloud...");
//        Object[] params = computeInfo.getParams();
//        int result = DropEgg.fun((Integer)params[0], (Integer)params[1]);
        if (result != null)
            HandlerSend("Result", "result: " + result.toString() + ", time: " + (double)time / 1000);
//        return result;
    }

    private static void ReadComputeInfo(byte[] recvBuffer) {

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
}
