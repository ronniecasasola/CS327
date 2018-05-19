import java.rmi.*;
import java.rmi.server.*;   
 
public class Assn6Server {
	   public static void main (String[] argv) {
			try {
				Method localMethods = new Method();
				//the correct arguments needed, rmi://, the ip address, cecs327, and the method to execute
				Naming.rebind("rmi://" + argv[0] + "/cecs327", localMethods);
				System.out.println("Server is ready...");
			}catch(Exception e) {
				System.out.println("Error " + e);
			}		
		}
}