package org.similaritydetection.backend.service;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

import static org.similaritydetection.backend.utils.CalculateSimilarity.calculateSimilarity;
import static org.similaritydetection.backend.utils.JsonParser.jsonToList;

@Component
public class TcpServer {

    private static final int PORT = 3001;

    public void run(String... args) throws Exception {
        ServerSocket serverSocket = new ServerSocket(PORT);
        System.out.println("TCP服务器运行在端口号：" + PORT);
        while (true) {
            Socket clientSocket = serverSocket.accept();
            new Thread(() -> handleClient(clientSocket)).start();
        }
    }

    private void handleClient(Socket clientSocket) {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        OutputStream out = clientSocket.getOutputStream()) {

            System.out.println("客户端连接成功，开始接收数据……");

            // 接受客户端发来的数据
            StringBuilder receivedData = new StringBuilder();
            char[] buffer = new char[1024];
            int charsRead;
            while (in.ready() && (charsRead = in.read(buffer)) != -1) {
                System.out.println("读取到的字符数量：" + charsRead);
                receivedData.append(buffer, 0, charsRead);
            }

            // 处理数据（假设数据是json格式
            System.out.println("接收到的数据：" + receivedData.toString());

            // 响应客户端
            String file1 = jsonToList(receivedData.toString()).get(0);
            String file2 = jsonToList(receivedData.toString()).get(1);
            Double response = calculateSimilarity(file1, file2);
            out.write(response.toString().getBytes());

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                clientSocket.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
