package perse.clientFitness;

import java.io.FileOutputStream;

/**
 * Created by admin on 13/02/2016.
 */




import java.io.*;
import java.net.Socket;
import java.net.InetAddress;

public class Fitness implements Runnable {

    Socket connection;
    public ObjectOutputStream writer;
    public ObjectInputStream objectIn;
    public OutputStream out;
    public InputStream in;
    public DataOutputStream dataOut;
    public DataInputStream dataIn;
    Thread t;

    final String  HOST_NAME = "178.62.31.174";
    final int PORT_NUMBER = 2000;

    final int DELAY = 1000;

    public boolean hostGame = false;

    ServerFlag flag = ServerFlag.STRING;

    public int fileSize = 0;

    public static void main(String[] args){
        new Fitness();
    }
    public Fitness(){
        try {
            System.out.println(InetAddress.getLocalHost().getHostAddress());
            connection = new Socket(HOST_NAME, PORT_NUMBER);
            out = connection.getOutputStream();
            in = connection.getInputStream();
            dataIn = new DataInputStream(new BufferedInputStream(in));
            dataOut = new DataOutputStream(new BufferedOutputStream(out));
            InputStream stream = connection.getInputStream();
            objectIn = new ObjectInputStream(stream);
            writer = new ObjectOutputStream(connection.getOutputStream());
            t = new Thread(this);
            t.start();

            sendFile("send.txt");
        }catch(Exception e){
            System.out.println("Could not connect to server");
            System.exit(0);
        }
    }
    public void run() {
        String command;
        try {
            while(true){
                if(flag==ServerFlag.STRING){
                    Object commandObj = objectIn.readObject();
                    command = (String) commandObj;
                    fileSize=Integer.parseInt(command);
                    flag=ServerFlag.MAP;
                    System.out.println("size");
                }
                else if(flag==ServerFlag.MAP){
                    getFile();
                    flag = ServerFlag.STRING;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void getFile(){
        System.out.println("File received");

        try{
            byte [] mybytearray  = new byte [fileSize];
            FileOutputStream fos = new FileOutputStream("received.txt" );

            int n = 0;
            while (fileSize > 0 && (n = dataIn.read(mybytearray, 0, (int)Math.min(mybytearray.length, fileSize))) != -1) {
                fos.write(mybytearray,0,n);
                fileSize -= n;
            }
            fos.flush();
        }
        catch(Exception e){
            System.out.println("crash");
            e.printStackTrace();
        }

    }
    public void sendFile(String location){
        File myFile = new File(location);
        String message = Long.toString(myFile.length());
        sendMessage(message);
        byte [] mybytearray  = new byte [(int)myFile.length()];
        try {
            FileInputStream fis = new FileInputStream(myFile);
            BufferedInputStream bis = new BufferedInputStream(fis);
            bis.read(mybytearray, 0, mybytearray.length);
            out.write(mybytearray, 0, mybytearray.length);
            out.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    public void sendMessage(String command){
        try {
            System.out.println(command);
            writer.writeObject(command);
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
