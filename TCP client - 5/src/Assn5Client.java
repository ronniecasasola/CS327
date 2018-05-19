import java.net.*;
import java.io.*;
public class Assn5Client {
    public static void main(String args[]){
        //args[0] = DNS or IP of the targeted server
        //args[1] = the message being passed
        Socket s = null;
        try{
            int serverPort = 7896;
            s = new Socket(args[0], serverPort);
            DataInputStream in = new DataInputStream(s.getInputStream());
            DataOutputStream out = new DataOutputStream(s.getOutputStream());
            out.writeUTF(args[1]);
            String data = in.readUTF();
            System.out.println("Server Reply: " + data);
        }catch(UnknownHostException e){
            System.out.println("Sock:" + e.getMessage());
        }catch(EOFException e){
            System.out.println("EOF:" + e.getMessage());
        }catch (IOException e){
            System.out.println("IO:" + e.getMessage());
        }finally {
            if(s!=null){
                try{
                    s.close();
                }catch (IOException e){
                    //close fails
                    System.out.println("Socket close:" + e.getMessage());
                }
            }
        }
    }
}