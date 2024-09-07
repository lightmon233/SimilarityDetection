package org.similaritydetection.backend.service;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ServiceInitializer {

    @Autowired
    private UdpServer udpServer;
    @Autowired
    private TcpServer tcpServer;

    @PostConstruct
    public void init() {
        new Thread(() -> {
            try {
                udpServer.run();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
        new Thread(() -> {
            try {
                tcpServer.run();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }
}
