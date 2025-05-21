package Server;

import java.io.*;
import java.net.Socket;
import java.util.List;

public class ClientHandler implements Runnable {
    private Socket socket;
    private DataInputStream dis;
    private DataOutputStream dos;
    private List<ClientHandler> allClients;
    public String username;

    public ClientHandler(Socket socket, List<ClientHandler> allClients) throws IOException {
        this.socket = socket;
        this.allClients = allClients;
        dis = new DataInputStream(socket.getInputStream());
        dos = new DataOutputStream(socket.getOutputStream());
    }

    @Override
    public void run() {
        try {
            while (true) {
                String command = dis.readUTF();

                switch (command) {
                    case "LOGIN" -> handleLogin();
                    case "CHAT" -> broadcast(username + ": " + dis.readUTF());
                    case "UPLOAD" -> receiveFile(dis.readUTF(), dis.readInt());
                    case "GET_FILE_LIST" -> sendFileList();
                    case "DOWNLOAD" -> sendFile(dis.readUTF());
                }
            }
        } catch (Exception e) {
            System.out.println(username + " disconnected.");
        } finally {
            allClients.remove(this);
        }
    }

    private void handleLogin() throws IOException {
        String u = dis.readUTF();
        String p = dis.readUTF();

        if (Server.authenticate(u, p)) {
            username = u;
            dos.writeUTF("LOGIN_SUCCESS");

            File userDir = new File("resources/Client/" + username);
            userDir.mkdirs();
        } else {
            dos.writeUTF("LOGIN_FAIL");
        }
    }

    private void broadcast(String msg) throws IOException {
        for (ClientHandler client : allClients) {
            if (!client.username.equals(this.username)) {
                client.sendMessage(msg);
            }
        }
    }

    private void sendMessage(String msg) throws IOException {
        dos.writeUTF("CHAT");
        dos.writeUTF(msg);
    }

    private void receiveFile(String filename, int length) throws IOException {
        byte[] buffer = new byte[length];
        dis.readFully(buffer);

        File outFile = new File("resources/Server/" + filename);
        try (FileOutputStream fos = new FileOutputStream(outFile)) {
            fos.write(buffer);
        }

        System.out.println("Received file: " + filename);
        broadcastFileList();
    }

    private void sendFileList() throws IOException {
        File folder = new File("resources/Server");
        String[] files = folder.list();
        if (files == null) files = new String[0];
        String fileList = String.join(",", files);
        dos.writeUTF("FILE_LIST");
        dos.writeUTF(fileList);
    }

    private void broadcastFileList() throws IOException {
        File folder = new File("resources/Server");
        String[] files = folder.list();
        if (files == null) files = new String[0];
        String fileList = String.join(",", files);

        for (ClientHandler client : allClients) {

            if (client.username.equals(this.username)) continue;

            try {
                client.dos.writeUTF("FILE_LIST");
                client.dos.writeUTF(fileList);
            } catch (IOException e) {
                System.out.println("Couldn't send file list to " + client.username);
            }
        }
    }


    private void sendFile(String filename) throws IOException {
        File file = new File("resources/Server/" + filename);
        byte[] data = new byte[(int) file.length()];
        try (FileInputStream fis = new FileInputStream(file)) {
            fis.read(data);
        }

        dos.writeUTF("FILE_DATA");
        dos.writeUTF(filename);
        dos.writeInt(data.length);
        dos.write(data);
    }
}
