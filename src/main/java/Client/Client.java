package Client;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    public static String username;
    private static DataInputStream dis;
    private static DataOutputStream dos;

    public static void main(String[] args) {
        try (Socket socket = new Socket("localhost", 12345)) {
            dis = new DataInputStream(socket.getInputStream());
            dos = new DataOutputStream(socket.getOutputStream());
            Scanner scanner = new Scanner(System.in);


            System.out.println("===== Welcome to CS Music Room =====");
            boolean loggedIn = false;
            while (!loggedIn) {
                System.out.print("Username: ");
                username = scanner.nextLine();
                System.out.print("Password: ");
                String password = scanner.nextLine();

                sendLoginRequest(username, password);
                String response = dis.readUTF();

                if (response.equals("LOGIN_SUCCESS")) {
                    System.out.println("Logged in successfully!");
                    loggedIn = true;
                } else {
                    System.out.println("Invalid credentials. Try again.");
                }
            }


            new Thread(new ClientReceiver(socket)).start();


            while (true) {
                printMenu();
                System.out.print("Enter choice: ");
                String choice = scanner.nextLine();

                switch (choice) {
                    case "1" -> enterChat(scanner);
                    case "2" -> uploadFile(scanner);
                    case "3" -> requestDownload(scanner);
                    case "0" -> {
                        System.out.println("Exiting...");
                        return;
                    }
                    default -> System.out.println("Invalid choice.");
                }
            }

        } catch (IOException e) {
            System.out.println("Connection error: " + e.getMessage());
        }
    }

    private static void printMenu() {
        System.out.println("\n--- Main Menu ---");
        System.out.println("1. Enter chat box");
        System.out.println("2. Upload a file");
        System.out.println("3. Download a file");
        System.out.println("0. Exit");
    }

    private static void sendLoginRequest(String username, String password) throws IOException {
        dos.writeUTF("LOGIN");
        dos.writeUTF(username);
        dos.writeUTF(password);
    }

    private static void enterChat(Scanner scanner) throws IOException {
        System.out.println("You have entered the chat. Type /exit to leave.");

        String msg;
        while (true) {
            msg = scanner.nextLine();
            if (msg.equalsIgnoreCase("/exit")) {
                break;
            }

            dos.writeUTF("CHAT");
            dos.writeUTF(msg);
        }
    }

    private static void uploadFile(Scanner scanner) throws IOException {
        File folder = new File("resources/Client/" + username);
        folder.mkdirs();
        File[] files = folder.listFiles();

        if (files == null || files.length == 0) {
            System.out.println("No files to upload.");
            return;
        }

        System.out.println("Select a file to upload:");
        for (int i = 0; i < files.length; i++) {
            System.out.println((i + 1) + ". " + files[i].getName());
        }

        System.out.print("Enter file number: ");
        int choice;
        try {
            choice = Integer.parseInt(scanner.nextLine()) - 1;
        } catch (NumberFormatException e) {
            System.out.println("Invalid input.");
            return;
        }

        if (choice < 0 || choice >= files.length) {
            System.out.println("Invalid choice.");
            return;
        }

        File file = files[choice];
        byte[] fileBytes = new byte[(int) file.length()];
        try (FileInputStream fis = new FileInputStream(file)) {
            fis.read(fileBytes);
        }

        dos.writeUTF("UPLOAD");
        dos.writeUTF(file.getName());
        dos.writeInt(fileBytes.length);
        dos.write(fileBytes);
        System.out.println("File uploaded: " + file.getName());
    }

    private static void requestDownload(Scanner scanner) throws IOException {
        dos.writeUTF("GET_FILE_LIST");
        System.out.println("Waiting for file list...");

        try {
            Thread.sleep(1000); // Wait a bit to receive file list before user types
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        System.out.print("Enter file name to download: ");
        String fileName = scanner.nextLine();

        dos.writeUTF("DOWNLOAD");
        dos.writeUTF(fileName);
    }
}
