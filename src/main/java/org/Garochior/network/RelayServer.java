package org.Garochior.network;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;

public class RelayServer {

    static class GameRoom {
        String code;
        ClientConnection host;
        List<ClientConnection> clients = new CopyOnWriteArrayList<>();

        GameRoom(String code) { this.code = code; }

        void sendToHost(String message) {
            if (host != null) host.send(message);
        }

        void sendToClients(String message) {
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
                        String line2 = line.trim();

                        if (line2.startsWith("CREATE:")) {
                            roomCode = line2.substring(7);
                            GameRoom room = rooms.get(roomCode);

                            if (room != null) {
                                send("ERROR:ROOM_ALREADY_EXISTS");
                                roomCode = null;
                                System.out.println("Room already exists: " + roomCode);
                                continue;
                            }

                            GameRoom newRoom = new GameRoom(roomCode);
                            newRoom.host = this;
                            rooms.put(roomCode, newRoom);

                            send("OK:CREATED:0"); // este player 0 pentru host
                            System.out.println("Room created: " + roomCode);
                        }
                        else if (line2.startsWith("JOIN:")) {
                            roomCode = line2.substring(5);
                            GameRoom room = rooms.get(roomCode);
                            if (room == null) {
                                send("ERROR:ROOM_NOT_FOUND");
                                roomCode = null;
                                System.out.println("Room not found: " + roomCode);
                            } else {
                                room.clients.add(this);
                                int playerId = room.clients.size();
                                send("OK:JOINED:" + playerId);
                                System.out.println("Client joined room: " + roomCode);
                            }
                        }
                        continue;
                    }

                    GameRoom room = rooms.get(roomCode);
                    if (room != null) {
                        System.out.println("[" + roomCode + "] " + line);
                        //Clientii trimit doar la Server, iar Serverul trimite doar la clienti
                        if (this == room.host) {
                            room.sendToClients(line);
                        } else {
                            room.sendToHost(line);
                        }
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