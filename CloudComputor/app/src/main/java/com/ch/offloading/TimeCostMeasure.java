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
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;

/**
 * Created by CH on 2017/4/16.
 */

public class TimeCostMeasure {

    public static final String LOGTAG = "myLog";

    public static final int MEASURE_PORT = 10001;

    public static void createThread() {

        ServerSocket serverSocket;
        try {
            serverSocket = new ServerSocket(MEASURE_PORT);
//            Log.i(LOGTAG, "getReceiveBufferSize()" + serverSocket.getReceiveBufferSize() + "");
//            serverSocket.setReceiveBufferSize(100*1024);
            while (true) {
                final Socket socket = serverSocket.accept();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        TCPRecvAndReturn(socket);
                    }
                }).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void TCPRecvAndReturn(Socket socket) {

        byte[] recvBuffer;
        byte[] recvTemp = new byte[1401];
        try {
            // 100ms读超时，异常返回超时提示
            socket.setSoTimeout(10000);
            SocketAddress socketAddr = socket.getRemoteSocketAddress();
            Log.i(LOGTAG, "receive from: " + socketAddr);

            InputStream is = socket.getInputStream();
            int readCode = is.read(recvTemp);
            String sizeMsg = new String(recvTemp, 0, readCode);
            int i = 0;
            if (sizeMsg.startsWith("size:")) {
                // 大小信息和数据一起读取，拆分
                if (readCode > 12) {
                    Log.i(LOGTAG, "read message and data together, size: " + readCode);
                    sizeMsg = new String(recvTemp, 0, 12);
                    i = 12;
                }
                Log.i(LOGTAG, "first message: " + sizeMsg);
                int size = Integer.parseInt(sizeMsg.split(":")[1]);
                recvBuffer = new byte[size];
                // 拆分后
                if (i != 0) {
                    Log.i(LOGTAG, "save data, size: " + (readCode - i));
                    System.arraycopy(recvTemp, i, recvBuffer, 0, readCode - i);
                    i = readCode - i;
                }
                while ((readCode = is.read(recvTemp)) != -1) {
//                    Log.i(LOGTAG, "receive data size: " + readCode);
                    System.arraycopy(recvTemp, 0, recvBuffer, i, readCode);
                    i += readCode;
                }
            } else
                recvBuffer = recvTemp;
            Log.i(LOGTAG, "total: " + recvBuffer.length);

            /*
            readCode = is.read(recvBuffer);
            long t = System.currentTimeMillis();    // time is different from client
            if (readCode != -1) {
                Log.i(LOGTAG, "receive data size: " + readCode);
                String data = new String(recvBuffer, 0, readCode);
                Log.i(LOGTAG, data);
            }
            Log.i(LOGTAG, "sending back result at " + t);
            */
/*
            SocketUtil.ReadComputeInfo(recvBuffer);
            Object result = null;
            SocketUtil.Display(result, 0);
            long beginTime = System.currentTimeMillis();
            result = DynamicClassLoader.PathClassLoaderWay(SocketUtil.computeInfo);
            long endTime = System.currentTimeMillis();
            SocketUtil.Display(result, (int)(endTime - beginTime));

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(result);
            byte[] bytesResult = baos.toByteArray();
*/
            OutputStream os = socket.getOutputStream();
//            os.write(String.valueOf(t).getBytes());
            os.write("ok".getBytes());
//            os.write(bytesResult);
            os.flush();
            Log.i(LOGTAG, "task finished...");

            socket.close();
        } catch (SocketTimeoutException se) {
            Log.i(LOGTAG, "read timeout");
            OutputStream os = null;
            try {
                os = socket.getOutputStream();
                os.write("timeout".getBytes());
                os.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
