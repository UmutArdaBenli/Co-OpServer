import java.io.*;
import java.net.*;
import java.util.Scanner;

public class Client{
    public static void main(String args[]) throws IOException{

        Scanner Init = new Scanner(System.in);

        System.out.println("Enter Host: ");
        String hostInput = Init.nextLine();
        System.out.println("Enter Port: ");
        int portInput = Integer.parseInt(Init.nextLine());
        Init.close();

        Socket socket = new Socket(hostInput, portInput);

        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        Scanner cont = new Scanner(System.in);
        out.println("Packet from client!");

        String response = in.readLine();
        System.out.println("Server says: " + response);

        if(cont.nextLine() == "/exit"){
            socket.close();

        }
    }
}