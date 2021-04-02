import java.rmi.Remote;
import java.rmi.RemoteException;

public interface AdminTerminalInterface extends Remote {
    public void placeholder() throws RemoteException;
    public void voteUpdate(String departamento, int count) throws RemoteException;
    public void tableUpdate(String dep) throws RemoteException;
    public void tableDisconnectedUpdate(String dep) throws RemoteException;
}
