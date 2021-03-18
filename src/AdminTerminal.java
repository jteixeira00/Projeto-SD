import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

public class AdminTerminal
{
    public static void main(String[] args) throws MalformedURLException, RemoteException, NotBoundException {
        try {
            RmiInterface ri = (RmiInterface) Naming.lookup("rmi://localhost:7000/rmiServer");
            System.out.println("8 + 3 = " + ri.add(8, 3));
        } catch (NotBoundException | MalformedURLException | RemoteException e) {
            e.printStackTrace();
        }
    }
}
