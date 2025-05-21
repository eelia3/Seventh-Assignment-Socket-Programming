package Client;

import java.io.*;
import java.net.Socket;

public class ClientReceiver implements Runnable {
    private DataInputStream dis;
    private Socket socket;

    public ClientReceiver(Socket socket) throws IOException {
        this.socket = socket;
        this.dis = new DataInputStream(socket.getInputStream());
    }

    @Override
    public void run() {
        try {
            while (true) {
                String command = dis.readUTF();

                switch (command) {
                    case "CHAT" -> {
                        String msg = dis.readUTF();
                        System.out.println(msg);
                    }
                    case "FILE_LIST" -> {
                        String fileList = dis.readUTF();
                        String[] files = fileList.split(",");
                        System.out.println("Available files:");
                        for (int i = 0; i < files.length; i++) {
                            System.out.println((i + 1) + ". " + files[i]);
                        }
                    }
                    case "FILE_DATA" -> {
                        String filename = dis.readUTF();
                        int len = dis.readInt();
                        byte[] data = new byte[len];
                        dis.readFully(data);

                        File dir = new File("resources/Client/" + Client.username);
                        dir.mkdirs();
                        File outFile = new File(dir, filename);
                        try (FileOutputStream fos = new FileOutputStream(outFile)) {
                            fos.write(data);
                        }

                        System.out.println("Downloaded " + filename);
                    }
                    case "LOGIN_SUCCESS" -> {
                        System.out.println("Login successful.");
                    }
                    case "LOGIN_FAIL" -> {
                        System.out.println("Login failed. Try again.");
                    }
                    default -> {
                        System.out.println("Unknown command from server: " + command);
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("Disconnected from server.");
        }
    }
}
