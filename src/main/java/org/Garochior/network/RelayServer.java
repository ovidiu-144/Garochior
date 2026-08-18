package org.Garochior.network;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;

public class RelayServer {

    static class GameRoom {
        String code;
        List<ClientConnection> clients = new CopyOnWriteArrayList<>();

        GameRoom(String code) { this.code = code; }

        void broadcast(String message) {
            for (ClientConnection client : clients) {
                client.send(message);
            }
        }
    }

    static class ClientConnection {
        Socket socket;
        PrintWriter out;
        String roomCode;

        ClientConnection(Socket socket) throws IOException {
            this.socket = socket;
            this.out = new PrintWriter(socket.getOutputStream(), true);
        }

        void send(String message) {
            out.println(message);
        }

        void handle(Map<String, GameRoom> rooms) {
            try (BufferedReader in = new BufferedReader(
                    new InputStreamReader(socket.getInputStream()))) {

                String line;
                while ((line = in.readLine()) != null) {

                    if (roomCode == null) {
                        roomCode = line.trim();
                        rooms.computeIfAbsent(roomCode, GameRoom::new)
                                .clients.add(this);
                        System.out.println("Client joined room: " + roomCode);
                        continue;
                    }

                    GameRoom room = rooms.get(roomCode);
                    if (room != null) {
                        System.out.println("[" + roomCode + "] " + line);
                        room.broadcast(line);
                    }
                }
            } catch (IOException e) {
                System.out.println("Client disconnected from room: " + roomCode);
            } finally {
                if (roomCode != null) {
                    GameRoom room = rooms.get(roomCode);
                    if (room != null) room.clients.remove(this);
                }
            }
        }
    }

    public static void main(String[] args) throws IOException {
        Map<String, GameRoom> rooms = new ConcurrentHashMap<>();
        ServerSocket serverSocket = new ServerSocket(5000);
        System.out.println("Relay server pornit pe portul 5000");

        while (true) {
            Socket client = serverSocket.accept();
            ClientConnection conn = new ClientConnection(client);
            new Thread(() -> conn.handle(rooms)).start();
        }
    }
}