import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;

public class RmiServer extends UnicastRemoteObject implements RmiInterface {

    private static final long serialVersionUID = 1L;
    public ArrayList<String> addressPool;
    public int addressEnd = 1;
    public String baseAddress = "224.3.2.";
    public RmiServer() throws RemoteException{
        super();
    }

    public double add(double a, double b) throws RemoteException{
        return a+b;
    }

    @Override
    public String getNewAddress() throws RemoteException {
        return baseAddress+Integer.toString(addressEnd);
    }

    public int getTableNumber() throws RemoteException{
        return addressEnd++;
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
