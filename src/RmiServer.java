import java.io.*;
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

    private ArrayList<Eleicao> listaEleicoes;
    private ArrayList<Pessoa> listaPessoas;
    private ArrayList<Pessoa> pessoasOnline;

    public RmiServer() throws RemoteException{
        super();
        this.listaPessoas = new ArrayList<>();
        this.listaEleicoes = new ArrayList<>();
        this.pessoasOnline = new ArrayList<>();
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
        return listaEleicoes;
    }

    @Override
    public ArrayList<Pessoa> getPessoas() throws RemoteException {
        return listaPessoas;
    }

    @Override
    public ArrayList<MulticastServer> getMesas() throws RemoteException {
        return null;
    }

//  ====================== FILES =============================
    public void load(){
        ObjectInputStream is1 = null;
        ObjectInputStream is2 = null;
        try{
            FileInputStream stream = new FileInputStream("eleicoes.data");
            is1 = new ObjectInputStream(stream);
            this.listaEleicoes = (ArrayList<Eleicao>) is1.readObject();

            stream = new FileInputStream("pessoas.data");
            is2 = new ObjectInputStream(stream);
            this.listaPessoas = (ArrayList<Pessoa>) is2.readObject();

        }
        catch(Exception e){
            e.printStackTrace();
        }
        finally {
            try {
                if (is1 != null) {
                    is1.close();
                }
                if (is2 != null) {
                    is2.close();
                }
            }
            catch(IOException e){
                e.printStackTrace();
            }
        }
    }

    public void save(){
        ObjectOutputStream os1 = null;
        ObjectOutputStream os2 = null;

        try{
            FileOutputStream stream = new FileOutputStream("eleicoes.data");
            os1 = new ObjectOutputStream(stream);
            os1.writeObject(this.listaEleicoes);

            stream = new FileOutputStream("pessoas.data");
            os2 = new ObjectOutputStream(stream);
            os2.writeObject(this.listaPessoas);
        }
        catch(IOException e){
            e.printStackTrace();
        }
        finally {
            try {
                if (os1 != null) {
                    os1.close();
                }
                if (os2 != null) {
                    os2.close();
                }
            }
            catch(IOException e){
                e.printStackTrace();
            }
        }
    }


}

