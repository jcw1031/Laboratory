package com.woopaca.laboratory.nio;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class BlockingSocketMain {

    public static void main(String[] args) {
        try (ExecutorService executorService = Executors.newFixedThreadPool(10);
             ServerSocket serverSocket = new ServerSocket(8080)) {
            while (true) {
                Socket socket = serverSocket.accept();

                executorService.execute(() -> handleRequest(socket));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void handleRequest(Socket socket) {
        try (InputStream inputStream = socket.getInputStream();
             OutputStream outputStream = socket.getOutputStream()) {
            int data;

            while ((data = inputStream.read()) != -1) {
                data = Character.isLetter(data) ? Character.toUpperCase(data) : data;
                outputStream.write(data);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}