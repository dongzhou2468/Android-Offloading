package com.ch.offloading;

import android.os.Parcelable;
import android.util.Log;

import com.ch.collector.CollectUtil;
import com.ch.collector.ResultBean;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;

/**
 * Created by CH on 2017/4/13.
 */

public class SocketUtil {

    public static final String LOGTAG = "myLog";

//    private static final String IP = "172.16.1.66";
    public static final String IP = "116.56.140.66";
    public static final int REMOTE_PORT = 34567;
//    private static final String IP = "192.168.1.121";
//    private static final int REMOTE_PORT = 4567;
    private static final int LOCAL_PORT = 4567;
    private static ServerSocket serverSocket;
    private static Socket clientSocket;

    public static void TCPRecv() {
        byte[] recvBuffer = new byte[1024];
        String recvString;
        try {
            while (true) {
                serverSocket = new ServerSocket(LOCAL_PORT);
                Socket socket = serverSocket.accept();
                SocketAddress socketAddr = socket.getRemoteSocketAddress();
                Log.i(LOGTAG, socketAddr + "");

                InputStream is = socket.getInputStream();
                while (is.read(recvBuffer)!= -1) {
                    recvString = new String(recvBuffer);
                    Log.i(LOGTAG, recvString);
                }
                serverSocket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void getCPUAndNetworkData(int monitorPort) {

        byte[] recvBuffer = new byte[1024];
        String recvString;
        try {
            Socket clientSocket = new Socket(IP, monitorPort);
            OutputStream os = clientSocket.getOutputStream();
            os.write(String.valueOf("collector").getBytes());
            os.flush();

            InputStream is = clientSocket.getInputStream();
            while (is.read(recvBuffer)!= -1) {
                recvString = new String(recvBuffer);
                Log.i("myLog", "cpu and network:" + recvString);
                CollectUtil.collectCPUAndNetwork(recvString);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (clientSocket != null)
                try {
                    clientSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }
    }

    public static Object TCPSendAndRecv(byte[] data) {
//    public static Object TCPSendAndRecv(byte[] data, double Rn) {

//        CollectUtil.tmpString = new StringBuilder(Rn + ",");
//        getCPUAndNetworkData(5678);

        long tb = 0, te = 0;
//        String result = "";
        Object result = null;
        byte[] recvBuffer = new byte[1024];
        int recv;
        try {
            Log.i(LOGTAG, "sending data to " + IP + ":" + REMOTE_PORT);
            clientSocket = new Socket(IP, REMOTE_PORT);
            OutputStream os = clientSocket.getOutputStream();
            tb = System.currentTimeMillis();
            os.write(data);
            os.flush();

            clientSocket.shutdownOutput();

//            BufferedReader br = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
//            result = br.readLine();
            Log.i(LOGTAG, "obtaining data back...");
            InputStream is = clientSocket.getInputStream();
            if ((recv = is.read(recvBuffer)) == -1) {
                // fail to obtain data back
                // some policies
                return result;
            }
            te = System.currentTimeMillis();
            ByteArrayInputStream bais = new ByteArrayInputStream(recvBuffer);
            ObjectInputStream ois = new ObjectInputStream(bais);
            result = ois.readObject();
            Log.i(LOGTAG, result.getClass() + ": " + result.toString());

        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            if (clientSocket != null)
                try {
                    clientSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }

//        ResultBean tmp = (ResultBean) result;
//        result = tmp.getResult();
//        CollectUtil.collectMemory(tmp.getMemory(), (te - tb));

        return result;
    }

    public static Object readResult(byte[] recvBuffer) {
        Object result = null;
        ByteArrayInputStream bais = new ByteArrayInputStream(recvBuffer);
        try {
            ObjectInputStream ois = new ObjectInputStream(bais);
            result = ois.readObject();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return result;
    }
}
