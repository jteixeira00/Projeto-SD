import java.rmi.Remote;
import java.rmi.*;
import java.util.ArrayList;

public interface RmiInterface extends Remote {
    public double add(double a, double b) throws RemoteException;
    public String getNewAddress() throws RemoteException;
    public int getTableNumber() throws RemoteException;

    public void printEleicao(Eleicao e) throws RemoteException;
    public ArrayList<Eleicao> getEleicoes() throws  RemoteException;
    public ArrayList<Pessoa> getPessoas() throws  RemoteException;
    public ArrayList<MulticastServer> getMesas() throws RemoteException;
    public ArrayList<Eleicao> getEleicoesFuturas() throws RemoteException;
    public ArrayList<Eleicao> getEleicoesEnded() throws RemoteException;
    public boolean login(String numero, String password) throws RemoteException;

    public void logout(String numero) throws RemoteException;
    public ArrayList<Eleicao> eleicoesOngoing() throws RemoteException;

    public void adicionarMesa(Eleicao e, MulticastServer mesa) throws RemoteException;

    public Eleicao createEleicaoRMI(String titulo, String descricao, String startDate, int startHour, int startMinute, String endDate, int endHour, int endMinute, String departamento, int type) throws RemoteException;

    public boolean createUserRMI(int tipo, String nome, String numero, String dep, String fac, String contacto, String morada, String cc, String validadecc, String password) throws RemoteException;

    public boolean deleteCandidateRMI(Eleicao eleicao, int index, int delete) throws RemoteException;

    public String showPessoas() throws RemoteException;

    public int sizePessoas() throws RemoteException;

    public boolean addCandidateRMI(Eleicao eleicao, int lista, int add) throws RemoteException;

    public boolean criaMesaRMI(String departamento) throws RemoteException;

    public void showMesas() throws RemoteException;

    public int sizeMesas() throws RemoteException;

    public boolean deleteMesaRMI(int del) throws RemoteException;

    public String showEleicoesFuturas() throws RemoteException;

    public int sizeEleicoesFuturas() throws RemoteException;

    public String showEleicoesDetalhes(int index) throws RemoteException;

    public boolean changeEleicoesRMI(int index, int answer, String change) throws RemoteException;

    public void showVotoDetalhesRMI(Pessoa eleitor) throws RemoteException;

    public void showVotosRMI(Eleicao eleicao) throws RemoteException;

    public void atualizaEleicao(Eleicao eleicao) throws RemoteException;

    public void eleicoesEndedRMI() throws RemoteException;

    public void addMesa(MulticastServer m) throws RemoteException;

    public Eleicao createListaRMI(Eleicao eleicao, String nome) throws RemoteException;

    public void eliminarListaCandidatos(Eleicao eleicao, int i) throws RemoteException;
}
