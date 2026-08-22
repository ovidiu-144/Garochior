package org.Garochior.network;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;

public class RelayServer {
    static class GameRoom {
        String code;
        ClientConnection host;
        ClientConnection[] clients = new ClientConnection[3];
        volatile boolean active = true;

        GameRoom(String code) {
            this.code = code;
            startHeartbeat();
        }

        void stop() {
            active = false;
        }

        private void startHeartbeat() {
            new Thread(() -> {
                while (active) {
                    try {
                        Thread.sleep(5000); // ping la fiecare 5 secunde
                        if (!active) break;
                        if (host != null) {
                            if (!host.pingReceived) {
                                System.out.println("Host timeout, closing connection.");
                                host.socket.close();
                            } else {
                                host.pingReceived = false;
                                long timestamp = System.currentTimeMillis();
                                host.send("{\"type\":\"PING\",\"ts\":" + timestamp + "}");
                            }

                        }
                        for (ClientConnection client : clients) {
                            if (client == null) continue;
                            if (!client.pingReceived) {
                                System.out.println("Client timeout, closing connection.");
                                client.socket.close();
                            } else {
                                client.pingReceived = false;
                                long timestamp = System.currentTimeMillis();
                                client.send("{\"type\":\"PING\",\"ts\":" + timestamp + "}");
                            }
                        }
                    } catch (InterruptedException | IOException e) {
                        break;
                    }
                }
            }).start();
        }

        void sendToHost(String message) {
            if (host != null) host.send(message);
        }

        void sendToClients(String message) {
            for (ClientConnection client : clients) {
                if (client == null) continue;
                client.send(message);
            }
        }

        public synchronized void addClient (ClientConnection client, int playerId) {
            clients[playerId - 1] = client;
        }

        public synchronized int removeClient(ClientConnection client) {
            for (int i = 0; i < 3; i++) {
                if (clients[i] == client) {
                    clients[i] = null;
                    return i + 1;
                }
            }
            return -1;
        }

    }

    public static String getPlayers(GameRoom room) {

        StringBuilder builder = new StringBuilder();
        builder.append("OK:PLAYERS:");

        if (room == null){
            builder.append("0,0,0,0");
            return builder.toString();
        }

        builder.append("1,"); //hostul este mereu prezent

        for (int i = 0; i < 3; i++) {
            if (room.clients[i] == null) {
                builder.append("0"); // este liber locul
            } else {
                builder.append("1"); // este ocupat locul
            }
            if (i < 2) {
                builder.append(",");
            }
        }
//            builder.append("]}");
        return builder.toString();
    }

    static class ClientConnection {
        Socket socket;
        PrintWriter out;
        String roomCode;

        public volatile boolean pingReceived = true;


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

                        String[] parts = line2.split(":");
                        // out.println("JOIN:" + roomCode + ":" + playerId);
                        String command = parts[0];

                        if (Objects.equals(command, "CREATE")) {
                            roomCode = parts[1];
                            GameRoom room = rooms.get(roomCode);

                            if (room != null) {
                                send("{\"type\":\"ROOM_EXISTS\"}");
                                roomCode = null;
                                System.out.println("Room already exists: " + roomCode);
                                continue;
                            }

                            GameRoom newRoom = new GameRoom(roomCode);
                            newRoom.host = this;
                            rooms.put(roomCode, newRoom);

                            send("OK:CREATED"); // este player 0 pentru host
                            System.out.println("Room created: " + roomCode);
                        }
                        else if (Objects.equals(command, "GET_PLAYERS")) {
                            String code  = parts[1];
                            GameRoom room = rooms.get(code);
                            send(getPlayers(room));
                            socket.close();
                            System.out.println("Sent players info for room: " + roomCode);
                        }
                        else if (Objects.equals(command, "JOIN")) {
                            roomCode = parts[1];
                            GameRoom room = rooms.get(roomCode);
                            if (room == null) {
                                send("{\"type\":\"ROOM_NOT_FOUND\"}");
                                roomCode = null;
                                System.out.println("Room not found: " + roomCode);
                            } else {
                                int playerId = Integer.parseInt(parts[2]);
                                room.addClient(this, playerId);
                                send("OK:JOINED:" + playerId);
                                System.out.println("Client joined room: " + roomCode);
                            }
                        }
                        continue;
                    }

                    GameRoom room = rooms.get(roomCode);

                    //partea de heartbeat, daca nu primeste ping2 de la client, il inchide
                    if (room != null) {
                        if (line.startsWith("{\"type\":\"PING2\"")) {
                            pingReceived = true;
                            continue;
                        }
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
                    if (room != null) {
                        if (this == room.host) {
                            // Host disconnected, remove the room
                            room.sendToClients("{\"type\":\"HOST_DISCONNECTED\"}");
                            room.stop();
                            rooms.remove(roomCode);
                            System.out.println("Host disconnected, room removed: " + roomCode);
                        } else {
                            int playerId = room.removeClient(this);
                            // Client disconnected, remove from clients list
                            room.sendToHost("{\"type\":\"CLIENT_DISCONNECTED\", \"playerId\":" + playerId + "}");
//                            room.sendToClients("{\"type\":\"CLIENT_DISCONNECTED\"}");

                            System.out.println("Client disconnected from room: " + roomCode);
                        }
                    }
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