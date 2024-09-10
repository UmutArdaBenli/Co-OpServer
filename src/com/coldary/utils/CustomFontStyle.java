package com.coldary.utils;

import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import javax.swing.JLabel;

public class CustomFontStyle {
    public CustomFontStyle() {
        try {
            // Load the font from the resources folder
            InputStream fontStream = CustomFontStyle.class.getResourceAsStream("/fonts/SF-Pro.ttf");
            Font customFont = Font.createFont(Font.TRUETYPE_FONT, fontStream);

            // Register the font with the GraphicsEnvironment
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            ge.registerFont(customFont);

            // Create a font object with a specific size
            Font newFont = customFont.deriveFont(12f);

            // Set the font to a JLabel or any other component
            JLabel messageLabel = new JLabel("Hello with custom font!");
            messageLabel.setFont(newFont);

            // You can now add messageLabel to your JFrame or JPanel as usual
        } catch (FontFormatException | IOException e) {
            e.printStackTrace();
        }
    }
}
