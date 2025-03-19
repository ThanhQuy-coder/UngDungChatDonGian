package src.com.simplechat.client;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class ChatClient {
    private Socket socket;
    private BufferedReader reader;
    private BufferedWriter writer;
    private String username;

    public ChatClient(String serverAddress, int port) {
        try {
            socket = new Socket(serverAddress, port);
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

            Scanner scanner = new Scanner(System.in);
            System.out.print("Nhập tên của bạn: ");
            username = scanner.nextLine();
            writer.write(username);
            writer.newLine();
            writer.flush();

            // Luồng để lắng nghe tin nhắn từ server
            new Thread(() -> {
                String message;
                try {
                    while ((message = reader.readLine()) != null) {
                        System.out.println(message);
                    }
                } catch (IOException e) {
                    closeEverything();
                }
            }).start();

            // Gửi tin nhắn từ người dùng
            while (scanner.hasNextLine()) {
                String message = scanner.nextLine();
                writer.write(message);
                writer.newLine();
                writer.flush();
            }

        } catch (IOException e) {
            closeEverything();
        }
    }

    private void closeEverything() {
        try {
            if (socket != null) socket.close();
            if (reader != null) reader.close();
            if (writer != null) writer.close();
            System.out.println("Ngắt kết nối.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new ChatClient("192.168.1.9", 1903);
    }
}
