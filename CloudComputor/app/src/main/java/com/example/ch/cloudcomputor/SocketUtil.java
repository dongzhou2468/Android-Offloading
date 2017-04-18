package com.example.ch.cloudcomputor;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.Map;

/**
 * Created by CH on 2017/4/16.
 */

public class SocketUtil {

    public static final String LOGTAG = "myLog";

    private static final int LOCAL_PORT = 4567;
    private static ServerSocket serverSocket;
    //private static Socket clientSocket;

    private static byte[] recvBuffer = new byte[1024];
    private static Map<String, Object> computeInfo;

    public static void TCPRecv() {
        try {
            while (true) {
                serverSocket = new ServerSocket(LOCAL_PORT);
                Socket socket = serverSocket.accept();
                SocketAddress socketAddr = socket.getRemoteSocketAddress();
                Log.i(LOGTAG, socketAddr + "");

                InputStream is = socket.getInputStream();
                if (is.read(recvBuffer)!= -1) {
                    ReadComputeInfo(recvBuffer);
                }

                Log.i(LOGTAG, "start computing in cloud...");
                int result = DropEgg.fun((Integer)computeInfo.get("Arg0"), (Integer)computeInfo.get("Arg1"));
                HandlerSend(result);
                Log.i(LOGTAG, "sending back result...");

                BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));       // or ByteArrayOutputStream
                bw.write(String.valueOf(result) + "\n");       // int to char might lost; add "\n" and readLine()
                bw.flush();
                Log.i(LOGTAG, "task finished...");

                serverSocket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

//    public static void TCPSend(byte[] data) {
//
//        try {
//            clientSocket = new Socket(IP, REMOTE_PORT);
//            OutputStream os = clientSocket.getOutputStream();
//            os.write(data);
//        } catch (IOException e) {
//            e.printStackTrace();
//        } finally {
//            if (clientSocket != null)
//                try {
//                    clientSocket.close();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//        }
//
//    }

    private static void ReadComputeInfo(byte[] recvBuffer) {

        ByteArrayInputStream bais = new ByteArrayInputStream(recvBuffer);
        try {
            ObjectInputStream ois = new ObjectInputStream(bais);
            computeInfo = (Map<String, Object>) ois.readObject();
            Log.i(LOGTAG, computeInfo.size() + "");
            Log.i(LOGTAG, (String) computeInfo.get("ApkName"));
            Log.i(LOGTAG, (String) computeInfo.get("PackageName"));
            Log.i(LOGTAG, (String) computeInfo.get("ClassName"));
            Log.i(LOGTAG, (String) computeInfo.get("MethodName"));
            for (int i = 0; i < computeInfo.size() - 4; i++) {
                Log.i(LOGTAG, computeInfo.get("Arg" + i).toString());
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private static void HandlerSend(int result) {
        Bundle bundle = new Bundle();
        bundle.putString("Result", String.valueOf(result));
        Message msg = new Message();
        msg.setData(bundle);
        MainActivity.myHandler.sendMessage(msg);
    }
}
