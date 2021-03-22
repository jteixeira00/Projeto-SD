import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;

public class RmiServer extends UnicastRemoteObject implements RmiInterface {

    private static final long serialVersionUID = 1L;
    public ArrayList<String> addressPool;
    public int addressEnd = 1;
    public String baseAddress = "224.3.2.";
    public static int idCounter = 1;
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

    @Override
    public boolean login(String numero, String password) throws RemoteException {
        System.out.println("Procurando utilizador com nº " + numero);
        boolean result = false;
        return result;
    }
    @Override
    public ArrayList<Eleicao> getEleicoes() throws RemoteException {

        return null;
    }

    @Override
    public ArrayList<Pessoa> getPessoas() throws RemoteException {
        return null;
    }

    @Override
    public ArrayList<MulticastServer> getMesas() throws RemoteException {
        return null;
    }
}
