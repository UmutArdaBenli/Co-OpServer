import java.io.*;
import java.net.*;
import java.util.Scanner;

public class ChatClient {
    private static final String HOST = "localhost";
    private static final int PORT = 12345;
    private static final String USER = "Umut";

    public static void main(String[] args) {
        Scanner s = new Scanner(System.in);
        System.out.println("Please enter the host address:");
        String ipAddress = s.nextLine();
        System.out.println("Please enter the host Port(leave empty for default port '12345') :");
        int port = Integer.parseInt(s.nextLine());


        try (Socket socket = new Socket(ipAddress, port);
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader consoleReader = new BufferedReader(new InputStreamReader(System.in))) {
            String message;
            while ((message = consoleReader.readLine()) != null) {
                writer.println(message);
                System.out.println("Server: " + reader.readLine());
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
