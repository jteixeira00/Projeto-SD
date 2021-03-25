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

    public boolean createEleicaoRMI(String titulo, String descricao, String startDate, int startHour, int startMinute, String endDate, int endHour, int endMinute, String departamento, int type) throws RemoteException;

    public boolean createUserRMI(int tipo, String nome, String numero, String dep, String fac, String contacto, String morada, String cc, String validadecc, String password) throws RemoteException;

    public boolean deleteCandidateRMI(Eleicao eleicao, int index, int delete) throws RemoteException;

    public void showPessoas() throws RemoteException;

    public int sizePessoas() throws RemoteException;

    public boolean addCandidateRMI(Eleicao eleicao, int lista, int add) throws RemoteException;

    public boolean criaMesaRMI(String departamento) throws RemoteException;

    public void showMesas() throws RemoteException;

    public int sizeMesas() throws RemoteException;

    public boolean deleteMesaRMI(int del) throws RemoteException;
}
