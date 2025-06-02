package com.woopaca.laboratory.nio;

import org.springframework.core.task.SimpleAsyncTaskExecutor;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class BlockingSocketApplication {

    public static void main(String[] args) {
        try (ExecutorService executorService = Executors.newCachedThreadPool();
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
                Thread.sleep(20);
                data = Character.isLetter(data) ? Character.toUpperCase(data) : data;
                System.out.print(data);
                outputStream.write(data);
            }
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
