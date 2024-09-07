package org.similaritydetection.backend.service;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

@Component
public class UdpServer{

    private static final int PORT = 3002;
    private static final int BUFFER_SIZE = 1024;

    public void run(String... args) throws Exception {
        DatagramSocket serverSocket = new DatagramSocket(PORT);
        System.out.println("UDP服务器运行在端口号：" + PORT);

        while (true) {
            try {
                byte[] receiveBuffer = new byte[BUFFER_SIZE];
                DatagramPacket receivePacket = new DatagramPacket(receiveBuffer, receiveBuffer.length);

                // 接收数据包
                serverSocket.receive(receivePacket);
                String receivedData = new String (receivePacket.getData(), 0, receivePacket.getLength());
                System.out.println("接收到的数据：" + receivedData);

                // 处理数据
                // 这里可以用一个json库来解析数据

                // 响应客户端
                String responseData = "数据获取成功！";
                byte[] responseBuffer = responseData.getBytes();
                InetAddress clientAddress = receivePacket.getAddress();
                int clientPort = receivePacket.getPort();
                DatagramPacket responsePacket = new DatagramPacket(responseBuffer, responseBuffer.length, clientAddress, clientPort);
                serverSocket.send(responsePacket);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
