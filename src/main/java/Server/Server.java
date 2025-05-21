    package Server;

    import java.net.ServerSocket;
    import java.net.Socket;
    import java.util.ArrayList;

    public class Server {
        public static ArrayList<ClientHandler> clients = new ArrayList<>();

        public static void main(String[] args) throws Exception {
            ServerSocket serverSocket = new ServerSocket(12345);
            System.out.println("Server started on port 12345...");

            while (true) {
                Socket clientSocket = serverSocket.accept();
                ClientHandler handler = new ClientHandler(clientSocket, clients);
                clients.add(handler);
                new Thread(handler).start();
            }
        }

        public static boolean authenticate(String username, String password) {
            return password.equals("1234") &&
                    (username.equals("user1") || username.equals("user2") ||
                            username.equals("user3") || username.equals("user4") ||
                            username.equals("user5"));
        }
    }
