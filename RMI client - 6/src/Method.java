import java.rmi.*;
import java.rmi.server.*;
 
public class Method extends UnicastRemoteObject implements MethodInterface {
 
		public Method () throws RemoteException {}
		//returns the fibonacci number
		public int fibonacci(int n) throws RemoteException{
			if(n <= 1){
				return n;
			}
			return fibonacci(n-1) + fibonacci(n-2);
		}
		//returns the factorial number
		public int factorial(int n) throws RemoteException{
			int factorial = 1;
			for(int i = 1; i <= n; i++){
				factorial = factorial * i;
			}
			return factorial;
		}
 }