package org.Garochior.network;

import com.google.gson.JsonObject;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;

import java.io.*;
import java.lang.reflect.Array;
import java.net.*;

public class RelayConnection {
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private MessageListener listener;
    private int playerId;

    public BooleanProperty isDisconnected = new javafx.beans.property.SimpleBooleanProperty(false);

    public RelayConnection() {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("Se închide aplicatia, deconectare de la relay...");
            disconnect();
        }));
    }


    public interface MessageListener {
        void onMessage(JsonObject message);
    }

    public void connectAsHost(String roomCode) throws IOException {
        connect();
        playerId = 0; // Host is always player 0
        out.println("CREATE:" + roomCode);
        waitForConfirmation();
    }

    public void connectAsClient(String roomCode, int playerId) throws IOException {
        this.playerId = playerId;
        connect();
//        out.println("GET_PLAYERS:" + roomCode); //doar pentru test ase
//        waitForConfirmation();
        out.println("JOIN:" + roomCode + ":" + playerId);
        waitForConfirmation();
//        String playerIdStr = waitForConfirmation();
//
//        return Integer.parseInt(playerIdStr);
    }

    public int[] connectForPlayers(String roomCode) throws IOException {
        try (Socket tempSocket = new Socket(NetworkConfig.RELAY_IP, NetworkConfig.RELAY_PORT);
             PrintWriter tempOut = new PrintWriter(tempSocket.getOutputStream(), true);
             BufferedReader tempIn = new BufferedReader(new InputStreamReader(tempSocket.getInputStream()))) {

            tempOut.println("GET_PLAYERS:" + roomCode);
            String response = tempIn.readLine();

            String playersIds = response.split(":")[2];
            String[] parts = playersIds.split(",");
            int[] players = new int[parts.length];
            for (int i = 0; i < parts.length; i++) {
                players[i] = Integer.parseInt(parts[i].trim());
            }
            return players;
        }
    }

    private void connect() throws IOException {
        socket = new Socket(NetworkConfig.RELAY_IP, NetworkConfig.RELAY_PORT);
        out = new PrintWriter(socket.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    }

    private void waitForConfirmation() throws IOException {
        String response = in.readLine();
        System.out.println(response);

        if (response.startsWith("ERROR")) {
            throw new IOException("Relay error: " + response);
        }
        if (response.startsWith("OK")) {
//            String playerIdStr = response.split(":")[2];
            //int playerId = Integer.parseInt(response.split(":")[2]);

            System.out.println("Relay confirmation: " + response);
            // Pornește listener-ul abia după confirmare

            Thread listenerThread = new Thread(this::listenLoop);
            listenerThread.setDaemon(true);
            listenerThread.start();

            //return playerIdStr;
        }
        //return "0";
    }

    private void listenLoop() {
        try {
            String line;
            while ((line = in.readLine()) != null) {
                if (line.startsWith("{\"type\":\"PING\"")) {
                    out.println("{\"type\":\"PING2\",\"Player\":" + playerId + "}");
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