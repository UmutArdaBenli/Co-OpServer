package com.coldary;

import java.io.*;
import java.net.*;
import java.util.*;

public class ChatServer {
    private static final int PORT = 12345;
    private static final int TIMEOUT = 200; // milliseconds
    static final Set<PrintWriter> clientWriters = new HashSet<>();

    public static void main(String[] args) {
        String localIp = getLocalIpAddress();
        if (localIp == null) {
            System.out.println("Could not find local IP address.");
            return;
        }

        String subnet = getSubnet(localIp);
        if (subnet == null) {
            System.out.println("Could not determine subnet.");
            return;
        }

        try (ServerSocket serverSocket = new ServerSocket()) {
            serverSocket.bind(new InetSocketAddress(localIp, PORT));
            System.out.println("Server is listening on " + localIp + ":" + PORT);
            while (true) {
                Socket socket = serverSocket.accept();
                String clientIp = socket.getInetAddress().getHostAddress();
                System.out.println("New client connected: " + clientIp);
                //broadcastMessage("User joined the server!");
                new ServerThread(socket).start();
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private static String getLocalIpAddress() {
        try {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements()) {
                NetworkInterface networkInterface = interfaces.nextElement();
                if (networkInterface.isLoopback() || !networkInterface.isUp()) {
                    continue;
                }
                Enumeration<InetAddress> addresses = networkInterface.getInetAddresses();
                while (addresses.hasMoreElements()) {
                    InetAddress address = addresses.nextElement();
                    if (address instanceof Inet4Address) {
                        return address.getHostAddress();
                    }
                }
            }
        } catch (SocketException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    private static String getSubnet(String ipAddress) {
        int lastDot = ipAddress.lastIndexOf('.');
        if (lastDot == -1) {
            return null;
        }
        return ipAddress.substring(0, lastDot + 1);
    }
    public static void broadcastMessage(String message) {
        synchronized (ChatServer.clientWriters) {
            for (PrintWriter writer : ChatServer.clientWriters) {
                writer.println(message);
            }
        }
    }
}

class ServerThread extends Thread {
    private final Socket socket;
    private PrintWriter writer;

    public ServerThread(Socket socket) {
        this.socket = socket;
    }

    public void run() {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
            writer = new PrintWriter(socket.getOutputStream(), true);
            synchronized (ChatServer.clientWriters) {
                ChatServer.clientWriters.add(writer);
            }
            String message;
            while ((message = reader.readLine()) != null) {
                System.out.println(message);
                broadcastMessage(message, writer);
            }
        } catch (SocketException ex) {
            System.out.println("User has left the Server.");
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            if (writer != null) {
                synchronized (ChatServer.clientWriters) {
                    ChatServer.clientWriters.remove(writer);
                }
            }
            try {
                socket.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    private void broadcastMessage(String message, PrintWriter excludeWriter) {
        synchronized (ChatServer.clientWriters) {
            for (PrintWriter writer : ChatServer.clientWriters) {
                if (writer != excludeWriter) {
                    writer.println(message);
                }
            }
        }
    }
}
