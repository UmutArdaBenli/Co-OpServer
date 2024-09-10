package com.coldary;

import com.coldary.utils.FileHandler;

import javax.sound.sampled.*;
import java.io.*;
import java.net.*;
import java.util.Scanner;

public class ChatClient {
    private static String HOST;
    private static int PORT;
    private static String USER;

    public static void main(String[] args) throws IOException {
        FileHandler fileHandler = new FileHandler();
        HOST = fileHandler.getHost();
        PORT = fileHandler.getPort();
        USER = fileHandler.getUsername();

        try (Socket socket = new Socket(HOST, PORT);
             BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader consoleReader = new BufferedReader(new InputStreamReader(System.in))) {

            // Thread to handle incoming messages from the server
            new Thread(() -> {
                try {
                    String serverMessage;
                    while ((serverMessage = reader.readLine()) != null) {
                        System.out.println(serverMessage);
                        //playSound();
                    }
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }).start();

            // Main thread to handle user input
            String message;
            while ((message = consoleReader.readLine()) != null) {
                writer.println(USER + ": " + message);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    static void playSound(String soundFile) throws LineUnavailableException, IOException, UnsupportedAudioFileException {
        File f = new File("./" + soundFile);
        AudioInputStream audioIn = AudioSystem.getAudioInputStream(f.toURI().toURL());
        Clip clip = AudioSystem.getClip();
        clip.open(audioIn);
        clip.start();
    }

}