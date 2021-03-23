import java.rmi.Remote;
import java.rmi.*;
import java.util.ArrayList;

public interface RmiInterface extends Remote {
    public double add(double a, double b) throws RemoteException;
    public String getNewAddress() throws RemoteException;
    public int getTableNumber() throws RemoteException;


    public ArrayList<Eleicao> getEleicoes() throws  RemoteException;
    public ArrayList<Pessoa> getPessoas() throws  RemoteException;
    public ArrayList<MulticastServer> getMesas() throws RemoteException;

    public boolean login(String numero, String password) throws RemoteException;
    public void logout(String numero) throws RemoteException;
    public ArrayList<Eleicao> eleicoesOngoing() throws RemoteException;
}
