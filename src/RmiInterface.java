import java.rmi.Remote;
import java.rmi.*;
import java.util.ArrayList;

public interface RmiInterface extends Remote {
    public double add(double a, double b) throws RemoteException;

    public String getNewAddress() throws RemoteException;

    public String getSecondaryAddress() throws RemoteException;

    public int getTableNumber(String arg) throws RemoteException;

    public void printEleicao(Eleicao e) throws RemoteException;

    public ArrayList<Eleicao> getEleicoes() throws RemoteException;

    public ArrayList<Pessoa> getPessoas() throws RemoteException;

    public ArrayList<MulticastServer> getMesas() throws RemoteException;

    public ArrayList<Eleicao> getEleicoesFuturas() throws RemoteException;

    public ArrayList<Eleicao> getEleicoesEnded() throws RemoteException;

    public boolean login(String numero, String password) throws RemoteException;

    public void logout(String numero) throws RemoteException;

    public ArrayList<Eleicao> eleicoesOngoing() throws RemoteException;

    public void adicionarMesa(Eleicao e, Mesa mesa) throws RemoteException;

    public Eleicao createEleicaoRMI(String titulo, String descricao, String startDate, int startHour, int startMinute, String endDate, int endHour, int endMinute, String departamento, int type) throws RemoteException;

    public boolean createUserRMI(int tipo, String nome, String numero, String dep, String fac, String contacto, String morada, String cc, String validadecc, String password) throws RemoteException;

    public boolean deleteCandidateRMI(int indx, int index, int delete) throws RemoteException;

    public String showPessoas() throws RemoteException;

    public int sizePessoas() throws RemoteException;

    public boolean addCandidateRMI(int indx, int lista, int add) throws RemoteException;

    public boolean criaMesaRMI(int indexE, int indexM) throws RemoteException;

    public String showMesas() throws RemoteException;

    public int sizeMesas() throws RemoteException;

    public boolean deleteMesaRMI(int indexE, int indexM) throws RemoteException;

    public String showEleicoesFuturas() throws RemoteException;

    public int sizeEleicoesFuturas() throws RemoteException;

    public String showEleicoesDetalhes(int index) throws RemoteException;

    public boolean changeEleicoesRMI(int index, int answer, String change) throws RemoteException;

    public String showVotoDetalhesRMI(int indx) throws RemoteException;

    public String showVotosRMI(Eleicao eleicao) throws RemoteException;

    public String eleicoesEndedRMI() throws RemoteException;

    public void addMesa(Mesa m) throws RemoteException;

    public String identificarUser(String input) throws RemoteException;

    public boolean createListaRMI(int indx, String nome) throws RemoteException;

    public void eliminarListaCandidatos(int indx, int i) throws RemoteException;

    public String showEleicoesDetalhesEnded(int index) throws RemoteException;

    public String showMesasEleicao(int indx) throws RemoteException;

    public int sizeMesasEleicao(int indx) throws RemoteException;

    public boolean votar(int eleicao, int choiceLista, String number, String departamento, int tableCount) throws RemoteException;
    public boolean checkNomeEleicao(String titulo) throws RemoteException;

    public String showDepartamentos(int indexE) throws RemoteException;

    public void subscribe(AdminTerminalInterface admin) throws RemoteException;
    public String generateLista(int eleicaoC, String dep) throws RemoteException;
    public Mesa getMesaByName(String dep) throws RemoteException;
    public void notifyOfNewTable(String arg) throws RemoteException;
    public Pessoa getPessoabyNumber(String numero) throws RemoteException;

    public void addDepartamentos(String nome, String departamento) throws RemoteException;

    public void addDepartamentos(int indexE, String departamento) throws RemoteException;

    public void deleteDepartamentos(int indexE, int departamento) throws RemoteException;

    public int sizeDepartamentos(int indexE) throws RemoteException;

    //public String showMesasEstados() throws RemoteException;

   // public String showMesasCount() throws RemoteException;

}
