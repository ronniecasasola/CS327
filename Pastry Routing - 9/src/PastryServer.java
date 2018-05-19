/*
 * Ronald Casasola
 * pastry = 1302
 * IP Address = 18.188.131.2
 * 
 */
import java.net.*;
import java.io.*;
import java.util.*;

public class PastryServer {
	private static Map<String, String> leafSet;
	private static Map<String, String> routingTable;
	private final static int myPastry = 1302;
	private final static String myIPAddress = "18.188.131.2";

	public static void main(String[] args) {
		
		//initialize leafset table and routing table
		leafSet = new TreeMap<>();
		routingTable = new TreeMap<>();
		
		leafSet.put("1300", "18.219.11.198");
		leafSet.put("1220", "52.53.151.57");
		leafSet.put("1312", "54.153.51.70");
		leafSet.put("2020", "18.218.119.113");
		
		routingTable.put("0001", "54.67.23.56");
		routingTable.put("1302", "18.188.131.2");
		routingTable.put("2020", "18.218.119.113");
		routingTable.put("3001", "13.57.5.20");
		routingTable.put("1013", "18.144.32.67");
		routingTable.put("1100", "18.217.157.190");
		routingTable.put("1201", "54.183.205.102");
		routingTable.put("1312", "54.153.51.70");
		routingTable.put("132", "NULL");
		routingTable.put("1330", "NULL");
		routingTable.put("1300", "18.219.11.198");
		routingTable.put("1301", "NULL");
		routingTable.put("1303", "NULL");

		DatagramSocket aSocket = null;
		try {
			//initialize datagramsocket with port 32710
			aSocket = new DatagramSocket(32710);
			while (true) {
				byte[] buffer = new byte[1000];
				DatagramPacket request = new DatagramPacket(buffer, buffer.length);
				//recieves request from client
				aSocket.receive(request);
				String nodeStr = new String(request.getData());
				//removes whitespace
				nodeStr = nodeStr.replace(" ", "");
				nodeStr = nodeStr.trim();
				String pastry = getPastry(nodeStr);
				DatagramPacket reply = new DatagramPacket(pastry.getBytes(), pastry.getBytes().length,
						request.getAddress(), request.getPort());
				//sends reply to client
				aSocket.send(reply);
			}
		} catch (SocketException e) {
			System.out.println("Socket: " + e.getMessage());
		} catch (IOException e) {
			System.out.println("IO: " + e.getMessage());
		} finally {
			if (aSocket != null) {
				aSocket.close();
			}
		}
	}

	//searches for the node and IP address, helper method
	public static String getPastry(String nodeStr) {
		try {
			Integer.parseInt(nodeStr);
		} catch (Exception e) {
			return "Request not valid";
		}
		String reply;
		//handles any amount of digits given but sets which are valid
		switch (nodeStr.length()) {
		case 0:
			reply = "NULL";
			break;
		case 1:
			reply = getPastryReply(nodeStr, 1);
			break;
		case 2:
			reply = getPastryReply(nodeStr, 2);
			break;
		case 3:
			reply = getPastryReply(nodeStr, 3);
			break;
		case 4:
			reply = getPastryReply(nodeStr, 4);
			break;
		default:
			reply = "INVALID_REQUEST";
		}

		return reply;
	}

	public static String getPastryReply(String nodeStr, int length) {

		//checks if its own pastry
		if (String.valueOf(myPastry).startsWith(nodeStr)) {
			return String.valueOf(myPastry) + ":" + myIPAddress;
		} else {

			//checks the leafset table first
			for (String k : leafSet.keySet()) {
				if (k.compareTo(nodeStr) == 0) {
					return k + ":" + leafSet.get(k);
				}
			}
			
			//searches for nearest node
				for (int i = 0; i < length; i++) {
					String str = nodeStr.substring(0, nodeStr.length() - i);
					
					for (String k : routingTable.keySet()) {
						if (k.startsWith(str)) {
							return k + ":" + routingTable.get(k);
						}
					}
					
				}
		}
		return "NULL";
	}
}