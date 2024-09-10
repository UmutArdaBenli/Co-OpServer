package com.coldary.utils;

import java.io.*;
import java.util.Scanner;

public class FileHandler {

    File configFile = new File("config.txt");

    public FileHandler() throws IOException {
        System.out.println("hello");
        if(configFile.createNewFile()){
            System.out.println("Created File 'Config.txt'");
            FileWriter w = new FileWriter("config.txt");
            w.append("USERNAME=\n");
            w.append("DEFAULT_HOST=\n");
            w.append("DEFAULT_PORT=\n");
            w.close();
            System.out.println("Please Fill out the config.txt file that has been created in the folder.");
            System.exit(0);
        }
    }

    public String getUsername(){
        try{
            Scanner s = new Scanner(configFile);
            while(s.hasNextLine()){
                String data = s.nextLine();
                if (data.startsWith("USERNAME=")) {
                    return data.split("=")[1];
                }
            }
            s.close();
        }catch (FileNotFoundException fn){
            System.out.println("File has not been found");
        }
        return "Chat-User";
    }
    public String getHost(){
        try{
            Scanner s = new Scanner(configFile);
            while(s.hasNextLine()){
                String data = s.nextLine();
                if (data.startsWith("DEFAULT_HOST=")) {
                    return data.split("=")[1];
                }
            }
            s.close();
        }catch (FileNotFoundException fn){
            System.out.println("File has not been found");
        }
        return "localhost";
    }

    public Integer getPort(){
        try{
            Scanner s = new Scanner(configFile);
            while(s.hasNextLine()){
                String data = s.nextLine();
                if (data.startsWith("DEFAULT_PORT=")) {
                    return Integer.valueOf(data.split("=")[1]);
                }
            }
            s.close();
        }catch (FileNotFoundException fn){
            System.out.println("File has not been found");
        }
        return Integer.valueOf("12345");
    }

}