package com.cetcme.socketserver;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class MainActivity extends AppCompatActivity {

    private TextView mTextView;

    private static Socket mSocket;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTextView = (TextView) findViewById(R.id.textView);

        startService();
    }

    /**
     * 启动服务监听，等待客户端连接
     */
    private static void startService() {

        new Thread() {
            @Override
            public void run() {
                try {
                    // 创建ServerSocket
                    ServerSocket serverSocket = new ServerSocket(9999);
                    System.out.println("--开启服务器，监听端口 9999--");

                    // 监听端口，等待客户端连接
                    while (true) {
                        System.out.println("--等待客户端连接--");
                        Socket socket = serverSocket.accept(); //等待客户端连接
                        System.out.println("得到客户端连接：" + socket);
                        mSocket = socket;
                        startReader(socket);
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();

    }

    /**
     * 从参数的Socket里获取最新的消息
     */
    private static void startReader(final Socket socket) {

        new Thread(){
            @Override
            public void run() {
                DataInputStream reader;
                try {
                    // 获取读取流
                    reader = new DataInputStream(socket.getInputStream());
                    InetAddress address = socket.getInetAddress();

                    while (true) {
                        System.out.println("*等待客户端输入*");
                        // 读取数据
                        String msg = reader.readUTF();
                        System.out.println("获取到客户端的信息：" + address + " :"+ msg);
                        send(socket);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }


    /**
     * 发送消息
     */
    private static void send(final Socket socket) {
        new Thread() {
            @Override
            public void run() {

                try {
                    // socket.getInputStream()
                    DataOutputStream writer = new DataOutputStream(socket.getOutputStream());
                    writer.writeUTF("我是服务器.."); // 写一个UTF-8的信息
                    System.out.println("发送消息");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }


    public void clicked(View v) {
        send(mSocket);
    }
}
