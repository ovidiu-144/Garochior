package org.Garochior.network;

import com.google.gson.JsonObject;

import java.io.*;
import java.net.*;

public class RelayConnection {
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private MessageListener listener;

    public interface MessageListener {
        void onMessage(JsonObject message);
    }

    public void connect(String roomCode) throws IOException {
        socket = new Socket(NetworkConfig.RELAY_IP, NetworkConfig.RELAY_PORT);
        out = new PrintWriter(socket.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        // Primul mesaj e mereu codul camerei
        out.println(roomCode);
        System.out.println("Conectat la relay, camera: " + roomCode);

        // Thread separat care ascultă mesaje primite
        new Thread(this::listenLoop).start();
    }

    private void listenLoop() {
        try {
            String line;
            while ((line = in.readLine()) != null) {
                if (listener != null) {
                    listener.onMessage(NetworkMessage.parse(line));
                }
            }
        } catch (IOException e) {
            System.out.println("Conexiunea la relay s-a închis.");
        }
    }

    public void send(String message) {
        if (out != null) {
            out.println(message);
        }
    }

    public void setMessageListener(MessageListener listener) {
        this.listener = listener;
    }

    public void disconnect() {
        try {
            if (socket != null) socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}