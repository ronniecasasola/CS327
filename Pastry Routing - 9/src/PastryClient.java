/*
 * Ronald Casasola
 * pastry = 1302
 * IP Address = 18.188.131.2
 * 
 */
import java.net.*;
import java.io.*;

public class PastryClient {
    public static void main(String [] args){
        if(args.length < 2){
            System.out.println("Needs more arguments " +
                    "\n Enter: java PastryClient [pastry id in quotes] [IP address]");
            System.exit(1);
        }else{
            DatagramSocket aSocket = null;
            try{
                aSocket = new DatagramSocket();
                byte [] m = "13".getBytes();
                InetAddress aHost = InetAddress.getByName("18.188.131.2");
                int serverPort = 32710;
                DatagramPacket request = new DatagramPacket(m, m.length, aHost, serverPort);
                aSocket.send(request);
                byte [] buffer = new byte[300];
                DatagramPacket reply = new DatagramPacket(buffer, buffer.length);
                aSocket.receive(reply);
                System.out.println(" " + args[0] + " - " + new String(reply.getData()));
            }catch(SocketException e){
                System.out.println("Socket: " + e.getMessage());
            }catch (IOException e){
                System.out.println("IO: " + e.getMessage());
            }finally {
                if(aSocket != null){
                    aSocket.close();
                }
            }
        }
    }
}