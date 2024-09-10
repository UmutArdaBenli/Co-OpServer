package com.coldary.styles;

import java.awt.*;
import javax.swing.*;

public class RoundedTriangleBubble extends JPanel {
    private String message;
    private boolean isSentByUser;

    public RoundedTriangleBubble(String message, boolean isSentByUser) {
        this.message = message;
        this.isSentByUser = isSentByUser;
        setOpaque(false);  // Make the panel transparent to show custom background
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);  // Enable anti-aliasing

        int width = getWidth();
        int height = getHeight();
        int arcSize = 30;

        // Create a custom rounded triangle shape
        Polygon triangle = new Polygon();
        if (isSentByUser) {
            triangle.addPoint(width - 20, height - 20);
            triangle.addPoint(width - 60, height - 40);
            triangle.addPoint(width - 60, height - 20);
        } else {
            triangle.addPoint(20, height - 20);
            triangle.addPoint(60, height - 40);
            triangle.addPoint(60, height - 20);
        }

        // Set the color for the bubble
        g2d.setColor(isSentByUser ? new Color(173, 216, 230) : new Color(220, 220, 220));
        g2d.fillRoundRect(0, 0, width - 1, height - 1, arcSize, arcSize);  // Draw the rounded rectangle
        g2d.fillPolygon(triangle);  // Draw the triangle tail

        // Draw the border (optional)
        g2d.setColor(Color.GRAY);
        g2d.drawRoundRect(0, 0, width - 1, height - 1, arcSize, arcSize);
        g2d.drawPolygon(triangle);

        // Draw the text
        g2d.setColor(Color.BLACK);
        g2d.drawString(message, 15, 25);  // Adjust text position based on your layout
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(250, 50);  // Adjust size based on content
    }
}
