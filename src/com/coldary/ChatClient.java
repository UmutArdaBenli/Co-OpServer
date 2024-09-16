package com.coldary;

import com.coldary.utils.FileHandler;
import com.coldary.utils.WAVPlayer;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
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
    private static JScrollPane scrollPane;

    private static Socket socket;
    private static PrintWriter writer;
    private static BufferedReader reader;


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
        scrollPane = new JScrollPane(chatArea);
        frame.add(scrollPane, BorderLayout.CENTER);

        inputField = new JTextArea(1, 50);  // Start with a single row height
        inputField.setLineWrap(true);       // Enable wrapping of text
        inputField.setWrapStyleWord(true);  // Break lines at word boundaries

// Create a JScrollPane for the input field to add scrolling functionality
        JScrollPane inputScrollPane = new JScrollPane(inputField);
        inputScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED); // Add scrollbar when needed
        inputScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        inputScrollPane.setPreferredSize(new Dimension(50, 100));  // Set a preferred size

        frame.add(inputScrollPane, BorderLayout.SOUTH);  // Add the input scroll pane to the frame

// Adjust size dynamically as the user types, but limit to a maximum number of rows
        inputField.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { adjustTextAreaSize(); }
            public void removeUpdate(DocumentEvent e) { adjustTextAreaSize(); }
            public void changedUpdate(DocumentEvent e) { adjustTextAreaSize(); }

            private void adjustTextAreaSize() {
                int lineCount = inputField.getLineCount();
                int maxRows = 5;  // Maximum rows before the scroll bar appears
                inputField.setRows(Math.min(lineCount, maxRows));  // Limit to maxRows
                inputField.revalidate();  // Refresh the input area without resizing the frame
            }
        });

        inputField.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER && !e.isShiftDown()) {
                    String message = inputField.getText().trim();
                    if (!message.isEmpty()) {
                        sendMessage(message);  // Send the message to the server
                        addChatBubble(USER + ": " + message, true);  // Add it to the chat area as user's message
                        inputField.setText("");  // Clear the input field
                        inputField.setRows(1);  // Reset to single line after message is sent
                    }
                    e.consume();  // Prevent the default action of adding a new line
                } else if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    inputField.append("\n");
                }
            }
        });


        //new CustomFontStyle();


        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        // Connect to server and listen for incoming messages
        connectToServer();
    }

    private static void connectToServer() {
        try {
            socket = new Socket(HOST, PORT);
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            writer = new PrintWriter(socket.getOutputStream(), true);

            // Thread to listen for incoming messages
            new Thread(() -> {
                StringBuilder messageBuilder = new StringBuilder();  // To accumulate multiline messages
                while (true) {
                    try {
                        String line = reader.readLine();
                        if (line != null) {
                            // Accumulate lines until we hit the "END_OF_MESSAGE" delimiter
                            if (line.contains("END_OF_MESSAGE")) {
                                messageBuilder.append(line.replace("END_OF_MESSAGE", "").trim());  // Remove the delimiter
                                String fullMessage = messageBuilder.toString();
                                // Add chat bubble with the full message
                                if (!fullMessage.startsWith(USER + ":")) {
                                    addChatBubble(fullMessage, false);
                                }
                                WAVPlayer player = new WAVPlayer();
                                player.play("/sounds/message.wav");
                                messageBuilder.setLength(0);  // Clear the builder for the next message
                            } else {
                                messageBuilder.append(line).append("\n");
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                        break;
                    }
                }
            }).start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private static void sendMessage(String message) {
        if (writer != null) {
            // Append a custom delimiter to the message to signify the end of a message block
            writer.println(USER + ": " + message + "END_OF_MESSAGE");
            WAVPlayer player = new WAVPlayer();
            player.play("/sounds/message.wav");

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
        nameLabel.setFont(new Font("Arial", Font.PLAIN, 14));  // Bold font for the name
        nameLabel.setBorder(new EmptyBorder(5, 5, 0, 5));  // Padding for the name

        String formattedMessage = chatMessage
                .replace(" ", "&nbsp;")        // Preserve spaces
                .replace("\n", "<br>");        // Preserve line breaks

        JLabel messageLabel = new JLabel("<html><div style='width: 200px; word-wrap: break-word;'>" + formattedMessage + "</div></html>");
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
        // Scroll to the bottom of the chat area
        SwingUtilities.invokeLater(() -> {
            JScrollBar verticalScrollBar = scrollPane.getVerticalScrollBar();
            verticalScrollBar.setValue(verticalScrollBar.getMaximum());
        });
    }

}
