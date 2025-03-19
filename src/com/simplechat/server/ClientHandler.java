package src.com.simplechat.server;

import java.io.*;
import java.net.Socket;
import java.util.List;

public class ClientHandler implements Runnable {
    private Socket socket;
    private BufferedReader reader;
    private BufferedWriter writer;
    private List<ClientHandler> clients;
    private String clientName;

    public ClientHandler(Socket socket, List<ClientHandler> clients) {
        this.socket = socket;
        this.clients = clients;

        try {
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

            // Nhận tên từ Client
            this.clientName = reader.readLine();
            broadcastMessage("🔵 " + clientName + " đã tham gia phòng chat!");
        } catch (IOException e) {
            closeEverything();
        }
    }

    @Override
    public void run() {
        String message;
        try {
            while ((message = reader.readLine()) != null) {
                System.out.println(clientName + ": " + message);
                broadcastMessage(clientName + ": " + message);
            }
        } catch (IOException e) {
            closeEverything();
        }
    }

    private void broadcastMessage(String message) {
        for (ClientHandler client : clients) {
            try {
                client.writer.write(message);
                client.writer.newLine();
                client.writer.flush();
            } catch (IOException e) {
                closeEverything();
            }
        }
    }

    private void closeEverything() {
        try {
            clients.remove(this);
            if (socket != null) socket.close();
            if (reader != null) reader.close();
            if (writer != null) writer.close();
            System.out.println(clientName + " đã rời phòng chat.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
