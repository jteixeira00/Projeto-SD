import java.rmi.Remote;
import java.rmi.*;

public interface RmiInterface extends Remote {
    public double add(double a, double b) throws RemoteException;
    public String getNewAddress() throws RemoteException;
    public int getTableNumber() throws RemoteException;
}
