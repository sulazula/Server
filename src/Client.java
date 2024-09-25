import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    private Socket socket;
    private BufferedReader in;
    private BufferedWriter out;
    private String clientName;

    public Client(Socket socket, String clientName) {
        this.socket = socket;
        this.clientName = clientName;
        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        } catch (IOException e) {
            closeClient(socket, in, out);
        }
    }

    public void sendMessage() {
        try {
            out.write(clientName);
            out.newLine();
            out.flush();

            Scanner scanner = new Scanner(System.in);
            while (socket.isConnected()) {
                String message = scanner.nextLine();
                out.write(clientName + ": " + message);
                out.newLine();
                out.flush();
            }
        } catch (IOException e) {
            closeClient(socket, in, out);
        }
    }

    public void listenFor() {
        new Thread(new Runnable() {
            public void run() {
                String messageFromServer;
                while (socket.isConnected()) {
                    try {
                        messageFromServer = in.readLine();
                        System.out.println(messageFromServer);
                    } catch (IOException e) {
                        closeClient(socket, in, out);
                    }
                }
            }
        }).start();
    }

    private void closeClient(Socket socket, BufferedReader in, BufferedWriter out) {
        try {
            if (in != null) in.close();
            if (out != null) out.close();
            if (socket != null) socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Введите имя пользователя: ");
        String clientName = scanner.nextLine();
        try (Socket socket = new Socket("localhost", 1488);){
            Client client = new Client(socket, clientName);
            client.listenFor();
            client.sendMessage();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}