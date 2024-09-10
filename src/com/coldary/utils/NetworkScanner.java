package com.coldary.utils;

import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

public class NetworkScanner {
    private static final int PORT = 12345;
    private static final int TIMEOUT = 200; // milliseconds

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

        List<String> activeHosts = scanNetwork(subnet, PORT);
        if (activeHosts.isEmpty()) {
            System.out.println("No active hosts found with port " + PORT + " open.");
        } else {
            System.out.println("Active hosts with port " + PORT + " open: " + activeHosts);
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

    private static List<String> scanNetwork(String subnet, int port) {
        List<String> activeHosts = new ArrayList<>();
        for (int i = 1; i < 255; i++) {
            String host = subnet + i;
            try (Socket socket = new Socket()) {
                socket.connect(new InetSocketAddress(host, port), TIMEOUT);
                activeHosts.add(host);
                System.out.println("Port " + port + " is open on " + host);
            } catch (IOException e) {
                // Port is not open on this host
            }
        }
        return activeHosts;
    }
}
