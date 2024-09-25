import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

public class ClientManager implements Runnable {
    private Socket socket;
    private BufferedReader in;
    private BufferedWriter out;
    private String name;
    public static ArrayList<ClientManager> clients = new ArrayList<>();

    public ClientManager(Socket socket) {
        try {
            this.socket = socket;
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            name = in.readLine();
            clients.add(this);

        } catch (IOException e) {
            closeCM(socket, in, out);
        }
    }

    public void sendMessage (String message) {
        for (ClientManager client : clients) {
            try {
                if (!client.name.equals(name)) {
                    client.out.write(message);
                    client.out.newLine();
                    client.out.flush();
                }
            } catch (Exception e) {
                closeCM(socket, in, out);
            }
        }
    }

    private void closeCM(Socket socket, BufferedReader in, BufferedWriter out) {
        try {
            if (in != null) in.close();
            if (out != null) out.close();
            if (socket != null) socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        String message;

        while (socket.isConnected()) {
            try {
                message = in.readLine();
                sendMessage(message);
            } catch (IOException e) {
                closeCM(socket, in, out);
                break;
            }
        }
    }
}