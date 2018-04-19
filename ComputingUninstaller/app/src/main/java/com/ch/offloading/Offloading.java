package com.ch.offloading;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by CH on 2018/3/5.
 */

public class Offloading {

    public static final String LOGTAG = "myLog";
    private final static String IP = "116.56.140.66";
    private final static int MEASURE_PORT = 10001;
//    private final static String IP = "192.168.1.108";
//    private final static int MEASURE_PORT = 10000;
    private static Socket clientSocket;
    private final static int MAX_SIZE = 1380;

    private static List<Long> listTime = new ArrayList<Long>();
    public int average;

    private static Map<Integer, String> mapTimeSize = new HashMap<Integer, String>();

    public static void init(Context context, final int size) {

        new Thread(new Runnable() {
            @Override
            public void run() {
//                noDataTimeCost();
                for (int i = 0; i < 5; i++) {
                    relationTimeSize();
                }
//                MTUTest(size);
            }
        }).start();

    }

    public static void MTUTest(int size) {

        char[] data = new char[size];
        for (int i = 0; i < data.length; i++) {
            data[i] = 'a';
        }
        String result = (String) TCPSendAndRecv(String.valueOf(data).getBytes());

    }

    public static void relationTimeSize() {
        int size = 100, i = 0;
        long t1, t2;
        while (i < 100) {
            byte[] buffer = new byte[size];
            t1 = System.currentTimeMillis();
            String corresTime = (String) TCPSendAndRecv(buffer);
            t2 = System.currentTimeMillis();
//            corresTime = String.valueOf(Long.valueOf(corresTime) - t1);        // time is different
//            mapTimeSize.put(size, corresTime);
            if (corresTime.equals("ok")) {
                String t = String.valueOf(t2 - t1);
                Log.i(LOGTAG, "time cost: " + t);
                mapTimeSize.put(size, t);
            }
            else if (corresTime.equals("timeout"))
                mapTimeSize.put(size, "null");
            size += 200;
            i++;
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        writeToFile("time_size_relation_1.txt", 2);
    }

    public static void noDataTimeCost() {
        int size = 1, i = 0;
        long t1, t2;
        while (i < 20) {
            byte[] buffer = new byte[size];
            t1 = System.currentTimeMillis();
            String corresTime = (String) TCPSendAndRecv(buffer);
            t2 = System.currentTimeMillis();
            listTime.add(t2 - t1);
            Log.i(LOGTAG, "i: " + i);
            i++;
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        writeToFile("noDataTimeCost.txt", 1);
    }

    public static Object directlySend(byte[] data) {

        Object result = null;
        byte[] recvBuffer = new byte[MAX_SIZE];
        int recv;
        Log.i(LOGTAG, "sending " + data.length + " data to " + IP + ":" + MEASURE_PORT);
        try {
            clientSocket = new Socket(IP, MEASURE_PORT);
            OutputStream os = clientSocket.getOutputStream();

            os.write(data);
            os.flush();

            clientSocket.shutdownOutput();

            Log.i(LOGTAG, "obtaining data back...");
            InputStream is = clientSocket.getInputStream();
            if ((recv = is.read(recvBuffer)) != -1) {
                Log.i(LOGTAG, "receive data size: " + recv);
                result = new String(recvBuffer, 0, recv);
                Log.i(LOGTAG, "result: " + result);
            }


        } catch (IOException e) {
            e.printStackTrace();
        }  finally {
            if (clientSocket != null)
                try {
                    clientSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }
        return result;
    }

    public static Object TCPSendAndRecv(byte[] data) {

//        String result = null;
        Object result = null;
        byte[] recvBuffer = new byte[MAX_SIZE];
        int recv;
        Log.i(LOGTAG, "sending " + data.length + " data to " + IP + ":" + MEASURE_PORT);
        InetSocketAddress endpoint = new InetSocketAddress(IP , MEASURE_PORT);
        try {
            clientSocket = new Socket();
//            Log.i(LOGTAG, "getSendBufferSize(): " + clientSocket.getSendBufferSize());
            // 对于Socket和SeverSocket如果需要指定缓冲区大小，必须在连接之前完成缓冲区的设定。
            // 设置socket发包缓冲为100k；
//            clientSocket.setSendBufferSize(100*1024);
            // 设置socket底层接收缓冲为100k
//            clientSocket.setReceiveBufferSize(100*1024);
            // 关闭Nagle算法.立即发包
//            clientSocket.setTcpNoDelay(true);
            clientSocket.connect(endpoint);
            OutputStream os = clientSocket.getOutputStream();

            if (data.length <= MAX_SIZE) {
                os.write(data);
                os.flush();
            }
            // 超过MTU，分包发送
            else {
                byte[] temp;
                int size = data.length;
                // 先发送消息（12bytes）告知数据大小，格式：size:_______
                String sizeMsg = firstMsgFront(size) + String.valueOf(size);
                os.write(sizeMsg.getBytes());
                os.flush();
                for (int i = 0; i <= size / MAX_SIZE; i++) {
                    if (i != size / MAX_SIZE) {
                        temp = new byte[MAX_SIZE];
                        System.arraycopy(data, MAX_SIZE * i, temp, 0, MAX_SIZE);
                    } else {
                        int rest = size - MAX_SIZE * i;
                        temp = new byte[rest];
                        System.arraycopy(data, MAX_SIZE * i, temp, 0, rest);
                    }
                    Log.i(LOGTAG, i + 1 + ". send: " + temp.length);
                    Thread.sleep(30);
                    os.write(temp);
                    os.flush();
                }
//                os.flush();
            }
            // ?
            // Socket在未关闭之前是不会关闭流的。所以read()方法就不知道什么时候到了流的末尾，就会一直阻塞。
            // read(byte[] b)当流里的字节数不为b的长度整数倍，在最后一次读取时由于流里所剩的字节数小于b的长度，流就认为到了流的末尾。如果为整数的话阻塞原因同上。
            clientSocket.shutdownOutput();
//            os.write(data);
//            os.flush();
            Log.i(LOGTAG, "obtaining data back...");
            InputStream is = clientSocket.getInputStream();
            // 清空接收缓冲区
//            recvBuffer = new byte[MAX_SIZE];
            if ((recv = is.read(recvBuffer)) != -1) {
                Log.i(LOGTAG, "receive data size: " + recv);
                // 返回的是ByteArrayOutputStream，需要读取出Object
//                result = SocketUtil.readResult(recvBuffer);
                result = new String(recvBuffer, 0, recv);
                Log.i(LOGTAG, "result: " + result);
            }


        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            if (clientSocket != null)
                try {
                    clientSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }
        return result;
    }

    public static void writeToFile(String fileName, int i) {
        String filePath = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + fileName;
        File file = new File(filePath);
        FileWriter fw = null;
        String comma = ",";
        try {
            if (!file.exists())
                file.createNewFile();
            fw = new FileWriter(file, true);

            if (i == 1) {
                for (int j = 0; j < listTime.size(); j++) {
                    fw.write(listTime.get(j).toString());
                    if (j != listTime.size()) fw.write(comma);
                }
            } else {
                for (Map.Entry<Integer, String> entry : mapTimeSize.entrySet()) {
                    String str = entry.getKey() + ":" + entry.getValue() + comma;
                    fw.write(str);
                }
            }
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String firstMsgFront(int size) {

        if (size < 100)
            return "size:00000";
        else if (size < 1000)
            return "size:0000";
        else if (size < 10000)
            return "size:000";
        else if (size < 100000)
            return "size:00";
        else if (size < 1000000)
            return "size:0";
        else
            return "size:";
    }
}
