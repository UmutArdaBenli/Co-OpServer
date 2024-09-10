package com.coldary;

import com.coldary.utils.CustomFontStyle;
import com.coldary.utils.FileHandler;
import com.coldary.utils.WAVPlayer;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.*;
import java.net.Socket;

public class ChatClient {
    private static String HOST;
    private static int PORT;
    private static String USER;
    private static JPanel chatArea;
    private static JTextArea inputField;

    public static void main(String[] args) throws IOException {
        FileHandler fileHandler = new FileHandler();
        HOST = fileHandler.getHost();
        PORT = fileHandler.getPort();
        USER = fileHandler.getUsername();
        JFrame frame = new JFrame("Chat Client");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1280, 720);

        // Chat area setup
        chatArea = new JPanel();
        chatArea.setLayout(new BoxLayout(chatArea, BoxLayout.Y_AXIS));
        JScrollPane scrollPane = new JScrollPane(chatArea);
        frame.add(scrollPane, BorderLayout.CENTER);

        // Input field setup
        inputField = new JTextArea(5, 50);
        frame.add(inputField, BorderLayout.SOUTH);
        inputField.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == 10 && !e.isShiftDown()) {
                    String message = inputField.getText().trim();
                    if (!message.isEmpty()) {
                        sendMessage(message);  // Send the message to the server
                        addChatBubble(USER + ": " + message, true);  // Add it to the chat area as user's message
                        inputField.setText("");  // Clear the input field
                    }
                    e.consume();  // Prevent the default action of adding a new line
                } else if (e.getKeyCode() == 10) {
                    ChatClient.inputField.append("\n");
                }
            }
        });
        new CustomFontStyle();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        // Connect to server and listen for incoming messages
        connectToServer();
    }

    private static void connectToServer() {
        try {
            Socket socket = new Socket(HOST, PORT);
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);

            new Thread(() -> {
                while (true) {
                    try {
                        String serverMessage = reader.readLine();
                        if (serverMessage != null) {
                            // Check if the message is from the current user, and skip adding it if so
                            if (!serverMessage.startsWith(USER + ":")) {
                                addChatBubble(serverMessage, false);  // Only add if it's from another user
                            }
                            // Play a sound for incoming messages from others
                            WAVPlayer player = new WAVPlayer();
                            player.play("/one_beep.wav");
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Method to send a message to the server
    private static void sendMessage(String message) {
        try {
            Socket socket = new Socket(HOST, PORT);
            PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
            writer.println(USER + ": " + message);
            writer.close();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Method to add a chat bubble to the chat area
    private static void addChatBubble(String message, boolean isSentByUser) {
        // Create a new panel for the chat bubble
        JPanel bubble = new JPanel();
        bubble.setLayout(new BorderLayout());

        // Extract the username and message
        String[] splitMessage = message.split(":", 2);
        String senderName = splitMessage[0].trim();
        String chatMessage = splitMessage.length > 1 ? splitMessage[1].trim() : message;

        // Label for the sender name (at the top)
        JLabel nameLabel = new JLabel(senderName);
        nameLabel.setFont(new Font("SF-Pro", Font.PLAIN, 14));  // Bold font for the name
        nameLabel.setBorder(new EmptyBorder(5, 5, 0, 5));  // Padding for the name

        // Label for the chat message (in the middle)
        JLabel messageLabel = new JLabel("<html><p style='width: 200px;'>" + chatMessage + "</p></html>");
        messageLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        messageLabel.setBorder(new EmptyBorder(5, 5, 5, 5));  // Padding for the message
        messageLabel.setOpaque(true);

        // Customize the background color based on the sender
        if (isSentByUser) {
            messageLabel.setBackground(new Color(173, 216, 230));  // Light blue for user's messages
            nameLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        } else {
            messageLabel.setBackground(new Color(220, 220, 220));  // Light gray for other users
            nameLabel.setHorizontalAlignment(SwingConstants.LEFT);
        }

        // Add the name label and message label to the bubble
        bubble.add(nameLabel, BorderLayout.NORTH);   // Name at the top
        bubble.add(messageLabel, BorderLayout.CENTER);  // Message in the middle

        // Align the bubble panel
        bubble.setBorder(new EmptyBorder(5, isSentByUser ? 100 : 5, 5, isSentByUser ? 5 : 100));

        // Add the bubble to the chat area
        chatArea.add(bubble);
        chatArea.revalidate();  // Refresh the chat area to show the new message
        chatArea.repaint();
    }

}
