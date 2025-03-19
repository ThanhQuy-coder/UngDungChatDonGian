package src.com.simplechat.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ChatServer {
    private static final int PORT = 1903; // Cong lang nghe
    private static List<ClientHandler> clients = new ArrayList<>();

    public static void main(String[] args){
        System.out.println("Chat Server dang khoi dong");

        try (ServerSocket serverSocket = new ServerSocket(PORT)){
            System.out.println("Server dang lang nghe tren cong " + PORT);

            while (true){
                Socket socket = serverSocket.accept();
                System.out.println("Client moi ket noi: " + socket);

                // tao mot clientHandler de xu ly client
                ClientHandler clientHandler = new ClientHandler(socket, clients);
                clients.add(clientHandler);
                new Thread(clientHandler).start();
            }
        } catch (IOException e){
            e.printStackTrace();
        }
    }
}
