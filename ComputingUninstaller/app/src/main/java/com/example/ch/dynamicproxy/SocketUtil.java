package com.example.ch.dynamicproxy;

import android.util.Log;

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

//    private static final String IP = "116.56.140.66";
//    private static final int REMOTE_PORT = 34567;
    private static final String IP = "192.168.1.150";
    private static final int REMOTE_PORT = 4567;
    private static final int LOCAL_PORT = 4567;
    private static ServerSocket serverSocket;
    private static Socket clientSocket;

    private static byte[] recvBuffer = new byte[1024];
    private static String recvString;

    public static void TCPRecv() {
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

    public static void TCPSend(byte[] data) {

        try {
            clientSocket = new Socket(IP, REMOTE_PORT);
            OutputStream os = clientSocket.getOutputStream();
            os.write(data);
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

//        String result = "";
        Object result = null;
        byte[] recvBuffer = new byte[1024];
        int recv;
        try {
            Log.i(LOGTAG, "sending data...");
            clientSocket = new Socket(IP, REMOTE_PORT);
            OutputStream os = clientSocket.getOutputStream();
            os.write(data);
            os.flush();
//            BufferedReader br = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
//            result = br.readLine();
            Log.i(LOGTAG, "obtaining data back...");
            InputStream is = clientSocket.getInputStream();
            if ((recv = is.read(recvBuffer)) == -1) {
                // fail to obtain data back
                // some policies
                return result;
            }
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
        return result;
    }
}
