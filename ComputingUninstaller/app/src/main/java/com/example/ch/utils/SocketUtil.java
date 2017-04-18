package com.example.ch.utils;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;

/**
 * Created by CH on 2017/4/13.
 */

public class SocketUtil {

    public static final String LOGTAG = "myLog";

    private static final String IP = "116.56.140.66";
    private static final int REMOTE_PORT = 34567;
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

    public static String TCPSendAndRecv(byte[] data) {

        String result = "";
        try {
            clientSocket = new Socket(IP, REMOTE_PORT);
            OutputStream os = clientSocket.getOutputStream();
            os.write(data);
            os.flush();
            BufferedReader br = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            result = br.readLine();
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
        return result;
    }
}
