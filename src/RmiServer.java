import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;

public class RmiServer extends UnicastRemoteObject implements RmiInterface {

    private static final long serialVersionUID = 1L;

    public RmiServer() throws RemoteException{
        super();
    }

    public double add(double a, double b) throws RemoteException{
        return a+b;
    }

    public static void main(String args[]){
        try {
            RmiInterface ri = new RmiServer();
            LocateRegistry.createRegistry(7000).rebind("rmiServer", ri);
        }
        catch (RemoteException ex1){
            System.out.println("RMI SERVER EXCEPTION: " + ex1);
        }
    }
}
