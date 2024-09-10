package com.coldary.styles;

import javax.accessibility.Accessible;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class Essentials extends JComponent implements Accessible {

    public Essentials(String message, boolean isSentByUser) {
        JPanel bubble = new JPanel();
        bubble.setLayout(new BorderLayout());

        // Label for the message
        JLabel messageLabel = new JLabel("<html><p style='width: 200px;'>" + message + "</p></html>");
        messageLabel.setOpaque(true);

        // Customize the bubble look
        if (isSentByUser) {
            messageLabel.setBackground(new Color(173, 216, 230));  // Light blue for user's message
            messageLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        } else {
            messageLabel.setBackground(new Color(220, 220, 220));  // Light gray for other users
            messageLabel.setHorizontalAlignment(SwingConstants.LEFT);
        }

        messageLabel.setBorder(new EmptyBorder(10, 10, 10, 10));  // Padding inside the bubble
        messageLabel.setForeground(Color.BLACK);
        messageLabel.setOpaque(true);  // Make background visible

        // Add the message label to the bubble panel
        bubble.add(messageLabel, BorderLayout.CENTER);

        // Align the bubble to the left or right based on sender
        bubble.setBorder(new EmptyBorder(5, isSentByUser ? 100 : 5, 5, isSentByUser ? 5 : 100));
    }
}
