package com.changlu.No3Netty入门.No2Netty组件.demos.readwriteDemo;

import java.io.*;
import java.net.Socket;

/**
 * @ClassName Client
 * @Author ChangLu
 * @Date 2022/1/8 10:35
 * @Description 客户端：同样有读写线程，建立连接之后写线程向服务端发送数据，读线程监听服务端发来的数据
 */
public class Client {

    public static void main(String[] args) throws IOException {
        Socket s = new Socket("localhost", 8888);

        new Thread(() -> {
            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(s.getInputStream()));
                while (true) {
                    System.out.println(reader.readLine());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();

        new Thread(() -> {
            try {
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(s.getOutputStream()));
                for (int i = 0; i < 100; i++) {
                    writer.write(String.valueOf(i));
                    writer.newLine();
                    writer.flush();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

}