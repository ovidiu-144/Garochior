package org.Garochior.network;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketTimeoutException;

public class TestRelay {
    public static void main(String[] args) throws Exception {
        Socket socket = new Socket(NetworkConfig.RELAY_IP, NetworkConfig.RELAY_PORT);

        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        //Trimite codul camerei
        out.println("ROOM_CODE_123");
        System.out.println("Conectat! Am trimis codul camerei");

        //Trimite un mesaj
        out.println("Hello from client!");
        System.out.println("Mesaj trimis!");

        // Așteaptă răspuns 3 secunde
        socket.setSoTimeout(3000);
        try {
            String response = in.readLine();
            System.out.println("Răspuns primit: " + response);
        } catch (SocketTimeoutException e) {
            System.out.println("Niciun răspuns (normal dacă ești singurul client în cameră).");
        }

        socket.close();
        System.out.println("Test terminat cu succes!");
    }
}
