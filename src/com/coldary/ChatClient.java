package com.coldary;

import com.coldary.utils.FileHandler;
import com.coldary.utils.WAVPlayer;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class ChatClient {
   private static String HOST;
   private static int PORT;
   private static String USER;
   private static JTextArea chatArea;
   private static JTextArea inputField;

   public static void main(String[] args) throws IOException {
      FileHandler fileHandler = new FileHandler();
      HOST = fileHandler.getHost();
      PORT = fileHandler.getPort();
      USER = fileHandler.getUsername();
      JFrame frame = new JFrame("Chat Client");
      frame.setDefaultCloseOperation(3);
      frame.setSize(400, 400);
      chatArea = new JTextArea();
      chatArea.setEditable(false);
      frame.add(new JScrollPane(chatArea), "Center");
      inputField = new JTextArea();
      frame.add(inputField, "South");
      inputField.addKeyListener(new KeyAdapter() {
         public void keyPressed(KeyEvent e) {
            if (e.getKeyCode() == 10 && !e.isShiftDown()) {
               String message = ChatClient.inputField.getText();
               if (!message.isEmpty()) {
                  ChatClient.sendMessage(message);
                  ChatClient.inputField.setText("");
               }

               e.consume();
            } else if (e.getKeyCode() == 10) {
               ChatClient.inputField.append("\n");
            }

         }
      });
      frame.setVisible(true);

      try {
         Socket socket = new Socket(HOST, PORT);

         try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            try {
               PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);

               try {
                  (new Thread(() -> {
                     while(true) {
                        try {
                           String serverMessage;
                           if ((serverMessage = reader.readLine()) != null) {
                              chatArea.append(serverMessage + "\n");
                              WAVPlayer player = new WAVPlayer();
                              player.play("/one_beep.wav");
                              continue;
                           }
                        } catch (IOException var3) {
                           var3.printStackTrace();
                        }

                        return;
                     }
                  })).start();
               } catch (Throwable var11) {
                  try {
                     writer.close();
                  } catch (Throwable var10) {
                     var11.addSuppressed(var10);
                  }

                  throw var11;
               }
            } catch (Throwable var12) {
               try {
                  reader.close();
               } catch (Throwable var9) {
                  var12.addSuppressed(var9);
               }

               throw var12;
            }
         } catch (Throwable var13) {
            try {
               socket.close();
            } catch (Throwable var8) {
               var13.addSuppressed(var8);
            }

            throw var13;
         }
      } catch (IOException var14) {
         var14.printStackTrace();
      }
   }

   private static void sendMessage(String message) {
      try {
         Socket socket = new Socket(HOST, PORT);

         try {
            PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);

            try {
               writer.println(USER + ": " + message);
            } catch (Throwable var7) {
               try {
                  writer.close();
               } catch (Throwable var6) {
                  var7.addSuppressed(var6);
               }

               throw var7;
            }

            writer.close();
         } catch (Throwable var8) {
            try {
               socket.close();
            } catch (Throwable var5) {
               var8.addSuppressed(var5);
            }

            throw var8;
         }

         socket.close();
      } catch (IOException var9) {
         var9.printStackTrace();
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
