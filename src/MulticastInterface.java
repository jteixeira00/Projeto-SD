import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Iterator;

public interface MulticastInterface extends Remote {
    public String getDepartamento() throws RemoteException;
    public ArrayList<Eleicao> getEleicaoLista() throws RemoteException;
}
