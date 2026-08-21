package org.Garochior.network;

import com.google.gson.JsonObject;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;

import java.io.*;
import java.net.*;

public class RelayConnection {
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private MessageListener listener;
    private int player;

    public BooleanProperty isDisconnected = new javafx.beans.property.SimpleBooleanProperty(false);


    public void setPlayer(int player) {
        this.player = player;
    }

    public interface MessageListener {
        void onMessage(JsonObject message);
    }

    public void connectAsHost(String roomCode) throws IOException {
        connect();
        out.println("CREATE:" + roomCode);
        waitForConfirmation();
    }

    public int connectAsClient(String roomCode) throws IOException {
        connect();
        out.println("JOIN:" + roomCode);
        return waitForConfirmation();
    }


    private void connect() throws IOException {
        socket = new Socket(NetworkConfig.RELAY_IP, NetworkConfig.RELAY_PORT);
        out = new PrintWriter(socket.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("Se închide aplicatia, deconectare de la relay...");
            disconnect();
        }));
    }

    private int waitForConfirmation() throws IOException {
        String response = in.readLine();
        if (response.startsWith("ERROR")) {
            throw new IOException("Relay error: " + response);
        }
        int playerId = Integer.parseInt(response.split(":")[2]);
        System.out.println("Relay confirmation: " + response);
        // Pornește listener-ul abia după confirmare

        Thread listenerThread = new Thread(this::listenLoop);
        listenerThread.setDaemon(true);
        listenerThread.start();

        return playerId;
    }

    private void listenLoop() {
        try {
            String line;
            while ((line = in.readLine()) != null) {
                if (line.startsWith("{\"type\":\"PING\"")) {
                    out.println("{\"type\":\"PING2\",\"Player\":" + player + "}");
                    continue;
                }
                if (listener != null) {
                    listener.onMessage(NetworkMessage.parse(line));
                }
            }
        } catch (IOException e) {
            System.out.println("Eroare la citire: " + e.getMessage());
        }
        finally {
            System.out.println("Conexiunea la relay s-a inchis.");
            disconnect();
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
            if (out != null) out.close();
            if (in != null) in.close();
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            Platform.runLater(() -> {
                isDisconnected.set(true);
            });
        }
    }
}