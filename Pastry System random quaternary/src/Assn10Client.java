
/*
 * Ronald Casasola
 * pastry = 1302
 * IP Address = 18.188.131.2
 * 
 */
import java.net.*;
import java.util.ArrayList;
import java.util.Random;
import java.io.*;

public class Assn10Client {

	public static final int size = 4;
	public static final int MAX_HOPS = 6;
	public static final int NUM_OF_TRIALS = 100;
	public static final int SERVER_PORT = 32710;
	public static final String MY_IP = "18.188.131.2";
	public static final String ERROR = "error";
	public static final int TIMEOUT_MS = 500;

	public int[] histogram;
	
	public static void main (String[] args) {
		Assn10Client pC = new Assn10Client();
		pC.histogram = new int[MAX_HOPS];
		pC.Start(NUM_OF_TRIALS);
	}
	
	public String returnRandNum(int size) {
		Random gen = new Random();
		String rand = "";
		int iter = 0;
		while (iter < size) {
			rand += String.valueOf(gen.nextInt(size));
			iter++;
		}
		return rand;
	}

	private String getRandNum(String reply) {
		if (reply.charAt(4) == ':'){
			return reply.split(":")[0];
		} else {
			return ERROR;
		}
	}

	private String getIP(String reply) {
		if (reply.length() > 4 && reply.charAt(4) == ':'){
			return reply.split(":")[1];
		} else if (reply.equals("null")) {
			return reply;
		} else {
			return ERROR;
		}
	}

	private void findNode (String startIP, int serverPort, String startGUID) {
		DatagramSocket aSocket = null;
		try {
			int hops = 1;
			String iP = startIP;
			String gUID = startGUID;
			System.out.println("\nMy IP: " + startIP);
			System.out.println("Rand Num: " + startGUID + "\n");

			// Init Socket
			aSocket = new DatagramSocket();
			aSocket.setSoTimeout(TIMEOUT_MS);

			while (hops < MAX_HOPS) {
				// Construct message
				byte [] m = gUID.getBytes();
				System.out.println("Hop " + hops + " - " + gUID + ":" + iP);
				InetAddress aHost = InetAddress.getByName(iP);
				DatagramPacket request = new DatagramPacket(m, m.length, aHost, serverPort);
				aSocket.send(request);
				
				// Receive Reply
				byte[] buffer = new byte[1000];
				DatagramPacket reply = new DatagramPacket(buffer, buffer.length);
				aSocket.receive(reply);

				// Process Reply
				String stringReply = new String(reply.getData());
				stringReply = stringReply.trim();
				System.out.println("Server Reply: " + stringReply + "\n");
				iP = getIP(stringReply);

				if (iP.equals(ERROR)) {
					System.out.println("ERROR");
					break;
				}
				if (iP.equals("null")) {
					System.out.println("NULL");
					// Add to the list of found nulls.
					break;
				}
				if (getRandNum(stringReply).equals(gUID.toString())){
					System.out.println("Reached destination");
					// Add to the list of found IPs.
					break;
				}
				hops++;
			}
			if (hops < MAX_HOPS) {
				histogram[hops] += 1;
			}
			if(hops == MAX_HOPS) {
				System.out.println("Max hops reached");
				// Error finding the proper node. Discard the Run.
			}
			
		} 
		catch (SocketException e) {
			System.out.println("Socket: " + e.getMessage()); 
		} catch (IOException e) {
			System.out.println("IO: " + e.getMessage());
		} finally { 
			if(aSocket != null) {
				aSocket.close();
			}	
		}
	}

	private void Start(int iterations) {
		int count = 0;
		while (count < iterations) {
			System.out.println("-------------------------------");
			System.out.println("ITERATION: " + count);
			String gUID = returnRandNum(size);
			findNode(MY_IP, SERVER_PORT, gUID);
			count++;
		}
		System.out.println("Hops histogram:");
		for (int hop : histogram) {
			System.out.print(hop + " ");
		}
		System.out.println();
	}
}
