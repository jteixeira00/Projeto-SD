import javax.swing.plaf.multi.MultiInternalFrameUI;
import java.io.*;
import java.lang.reflect.Array;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.RemoteObjectInvocationHandler;
import java.rmi.server.UnicastRemoteObject;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class RmiServer extends UnicastRemoteObject implements RmiInterface {

    private static final long serialVersionUID = 1L;
    public ArrayList<String> addressPool;
    public int addressEnd = 1;
    public String baseAddress = "224.3.2.";


    private ArrayList<Eleicao> listaEleicoes;
    private ArrayList<Pessoa> listaPessoas;
    private ArrayList<Pessoa> pessoasOnline;
    private ArrayList<MulticastServer> listaMesas;

    //implementar
    private ArrayList<Eleicao> listaEleicoesEnded;

    public RmiServer() throws RemoteException {
        super();
        this.listaPessoas = new ArrayList<>();
        this.listaEleicoes = new ArrayList<>();
        this.pessoasOnline = new ArrayList<>();
        this.listaMesas = new ArrayList<>();
        //load();
    }

    public double add(double a, double b) throws RemoteException {
        return a + b;
    }

    @Override
    public String getNewAddress() throws RemoteException {
        return baseAddress + Integer.toString(addressEnd);
    }

    public int getTableNumber() throws RemoteException {
        return addressEnd++;
    }


    public static void main(String args[]) {
        try {
            RmiInterface ri = new RmiServer();

            LocateRegistry.createRegistry(7000).rebind("rmiServer", ri);
        } catch (RemoteException ex1) {
            System.out.println("RMI SERVER EXCEPTION: " + ex1);
        }
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

    /* ================ LOGINS LOGOUTS ======================== */


    @Override
    public boolean login(String numero, String password) throws RemoteException {
        System.out.println("Procurando utilizador com nº " + numero);
        Pessoa p = getPessoabyNumber(numero);
        if (p.getPassword().equals(password)) {
            //check if user already online
            if (this.pessoasOnline.contains(p)) {
                return false;
            }
            addOnlineUser(p);
            return true;
        } else {
            return false;
        }
    }

    public void logout(String numero) throws RemoteException {
        this.pessoasOnline.remove(getPessoabyNumber(numero));
    }

    /* =============== Eleições e votar ===============*/

    public ArrayList<Eleicao> eleicoesOngoing() throws RemoteException {
        ArrayList<Eleicao> res = new ArrayList<>();
        Date date = new Date();
        for (Eleicao e : getEleicoes()) {
            if (date.after(e.getStartDate()) && date.before(e.getEndDate())) {
                res.add(e);
            }
        }
        return res;
    }


    public boolean votar(Eleicao e, Lista l, Pessoa p, String departamento) {
        Voto v = new Voto(l, p, departamento);
        try {
            if (!this.eleicoesOngoing().contains(e)) {
                return false;
            }
        } catch (RemoteException remoteException) {
            remoteException.printStackTrace();
            return false;
        }
        e.addVoto(v);
        l.addVoto();
        save();
        return true;
    }


    @Override
    public Eleicao createEleicaoRMI(String titulo, String descricao, String startDate, int startHour, int startMinute, String endDate, int endHour, int endMinute, String departamento, int type) throws RemoteException {
        Date startDate1 = null;
        try {
            startDate1 = new SimpleDateFormat("dd-MM-yyyy'T'HH:mm").parse(parseDate(startDate, startHour, startMinute));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Date endDate1 = null;
        try {
            endDate1 = new SimpleDateFormat("dd-MM-yyyy'T'HH:mm").parse(parseDate(endDate, endHour, endMinute));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Date date = new Date();
        if (startDate1.after(endDate1) || startDate1.before(date)) {

            return null;
        }

        try {
            Eleicao e = new Eleicao(titulo, descricao, startDate, startHour, startMinute, endDate, endHour, endMinute, departamento, type);
            this.listaEleicoes.add(e);
            save();
            return e;
        } catch (ParseException parseException) {
            parseException.printStackTrace();
        }

        return null;
    }


    @Override
    public boolean createUserRMI(int tipo, String nome, String numero, String dep, String fac, String contacto, String morada, String cc, String validadecc, String password) throws RemoteException {
        for (Pessoa aux : getPessoas()) {
            if (aux.getNumero().equals(numero) || aux.getCc().equals(cc)) {
                return false;
            }
        }
        Pessoa p = new Pessoa(tipo, nome, numero, dep, fac, contacto, morada, cc, validadecc, password);
        this.addPessoaLista(p);
        save();
        return true;
    }


    public void printEleicao(Eleicao e) throws RemoteException {
        System.out.println(e.getDescricao());

    }
    //to-do
    @Override
    public boolean deleteCandidateRMI(Eleicao eleicao, int choice, int delete) throws RemoteException {
        /*
        ArrayList<Lista> aux = eleicao.getListasCandidatas();
        aux.remove(delete);
        eleicao.setListasCandidatas(aux);
         */
        return false;
    }

    //to-do [copiar do eleição.showCandidatos()]
    @Override
    public String showPessoas() throws RemoteException {
        String peeps = "";
        int i = 1;
        String is;
        for(Pessoa p: listaPessoas){
            is = String.valueOf(i);
            System.out.println(p.getType());
            peeps += is + " - " + p.getNome() + " || " + p.getNumero() + " || " + p.getType() + " || " + p.getDepartamento() + '\n';
            i++;
        }
        return peeps;
    }

    @Override
    public int sizePessoas() throws RemoteException {
        return listaPessoas.size();
    }

    //to-do
    @Override
    public boolean addCandidateRMI(Eleicao eleicao, int choice, int add) throws RemoteException {
        //ArrayList<Lista> aux = eleicao.getListasCandidatas();

        return false;
    }

    //to-do
    @Override
    public boolean criaMesaRMI(String departamento) throws RemoteException {
        return false;
    }

    //to-do [copiar do eleição.showCandidatos()]
    @Override
    public void showMesas() throws RemoteException {

    }

    //to-do
    @Override
    public int sizeMesas() throws RemoteException {
        return listaMesas.size();
    }

    //to-do
    @Override
    public boolean deleteMesaRMI(int del) throws RemoteException {
        this.listaMesas.remove(del);
        return false;
    }

    @Override
    public String showEleicoesFuturas() throws RemoteException{
        ArrayList<Eleicao> res = this.getEleicoesFuturas();
        String str = "";
        int indx;
        String indxS;
        for(int i = 0; i < this.getEleicoesFuturas().size(); i++) {
            indx = i + 1;
            indxS = Integer.toString(indx);
            str += indxS + " - " + this.getEleicoesFuturas().get(i).getTitulo() + '\n';

        }
        return str;
    }

    public ArrayList<Lista> getListasCandidatas(Eleicao e) {
        return e.getListasCandidatas();
    }

    /* ====================== FILES =========================== */
    public void load() {
        ObjectInputStream is1 = null;
        ObjectInputStream is2 = null;
        ObjectInputStream is3 = null;
        try {
            FileInputStream stream = new FileInputStream("eleicoes.data");
            is1 = new ObjectInputStream(stream);
            this.listaEleicoes = (ArrayList<Eleicao>) is1.readObject();

            stream = new FileInputStream("pessoas.data");
            is2 = new ObjectInputStream(stream);
            this.listaPessoas = (ArrayList<Pessoa>) is2.readObject();

            stream = new FileInputStream("mesas.data");
            is3 = new ObjectInputStream(stream);
            this.listaMesas = (ArrayList<MulticastServer>) is3.readObject();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (is1 != null) {
                    is1.close();
                }
                if (is2 != null) {
                    is2.close();
                }
                if (is3 != null) {
                    is3.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void save() {
        ObjectOutputStream os1 = null;
        ObjectOutputStream os2 = null;
        ObjectOutputStream os3 = null;

        try {
            FileOutputStream stream = new FileOutputStream("eleicoes.data");
            os1 = new ObjectOutputStream(stream);
            os1.writeObject(this.listaEleicoes);

            stream = new FileOutputStream("pessoas.data");
            os2 = new ObjectOutputStream(stream);
            os2.writeObject(this.listaPessoas);

            stream = new FileOutputStream("mesas.data");
            os3 = new ObjectOutputStream(stream);
            os3.writeObject(this.listaMesas);

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (os1 != null) {
                    os1.close();
                }
                if (os2 != null) {
                    os2.close();
                }
                if (os3 != null) {
                    os3.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }



    /* === AUX METHODS === */

    public Pessoa getPessoabyNumber(String numero) {
        for (Pessoa p : this.listaPessoas) {
            if (p.getNumero().equals(numero)) {
                return p;
            }
        }
        return null;
    }

    public void addOnlineUser(Pessoa p) {
        this.pessoasOnline.add(p);

    }

    public void addPessoaLista(Pessoa p) {
        this.listaPessoas.add(p);
    }

    public String parseDate(String date, int hour, int minute) {
        String sHour = "" + hour;
        if (sHour.length() == 1) {
            sHour = "0" + sHour;
        }
        String sMinute = "" + minute;
        if (sMinute.length() == 1) {
            sMinute = "0" + sMinute;
        }
        return date + "T" + sHour + ":" + sMinute;
    }

    @Override
    public ArrayList<Eleicao> getEleicoesFuturas() throws RemoteException {
        Date date = new Date();
        ArrayList<Eleicao> res = new ArrayList<>();
        try {
            for (Eleicao e : getEleicoes()) {
                if (date.before(e.getStartDate())) {

                    res.add(e);
                }

            }
            return res;
        } catch (RemoteException e) {
            e.printStackTrace();
        }

        return null;

    }

    @Override
    public ArrayList<Eleicao> getEleicoesEnded() throws RemoteException {
        return listaEleicoesEnded;
    }

    @Override
    public int sizeEleicoesFuturas() throws RemoteException{
        return getEleicoesFuturas().size();
    }

    @Override
    public String showEleicoesDetalhes(int index) throws RemoteException {
        Eleicao eleicao = getEleicoesFuturas().get(index);
        return "\n1 - Titulo " + eleicao.getTitulo() + "\n2 - Descrição: " + eleicao.getDescricao() + "\n3 - Data de Inicio (dd-MM-yyyy  HH:mm): " + eleicao.dateToString(eleicao.getStartDate()) + "\n4 - Data de Fim (dd-MM-yyyy  HH:mm): " + eleicao.dateToString(eleicao.getEndDate()) + "\n5 - Restringir eleição para um único departamento: " + eleicao.getDepartamento();
    }

    //to-do
    @Override
    public boolean changeEleicoesRMI(int index, int answer, String change) throws RemoteException {
        Eleicao eleicao = getEleicoesFuturas().get(index);
        switch (answer) {
            case 1:
                //alterar titulo
                eleicao.setTitulo(change);
                break;
            case 2:
                //alterar descrição
                eleicao.setDescricao(change);
                break;
            case 3:
                //alterar data inicio
                try {
                    eleicao.setStartDate(parseDateString(change));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                break;
            case 4:
                //alterar data de fim
                try {
                    eleicao.setEndDate(parseDateString(change));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                break;

            /*case 5:
                //alterar departamento
                eleicao.
                break;

            */
            default:
                System.out.println("Input Inválido.");
                return false;
        }
        return true;
    }

    @Override
    public void showVotoDetalhesRMI(Pessoa eleitor) throws RemoteException {
        System.out.println("Local de Voto: ");
        System.out.println(eleitor.getLocalVoto());
        System.out.println("Momento de Voto: ");
        System.out.println(eleitor.getTimeVoto());
    }

    @Override
    public void showVotosRMI(Eleicao eleicao) throws RemoteException {
        int count;
        System.out.println("Resultados:\n");
        for(Lista list : eleicao.getListasCandidatas()){
            System.out.println(".................");
            System.out.printf("Lista ");
            System.out.println(list.getNome());
            count = list.getVotos();
            System.out.printf("\nVotos: %d | %f" + '%',count,count/eleicao.votosTotal());
        }
        System.out.println(".................");
        System.out.printf("\nVotos em Branco: %d | %f" + '%',eleicao.getVotosBrancos(),eleicao.getVotosBrancos()/eleicao.votosTotal());
        System.out.println(".................");
        System.out.printf("\nVotos Nulos: %d | %f" + '%',eleicao.getVotosNulos(),eleicao.getVotosNulos()/eleicao.votosTotal());
    }


    //to-do (13 - Os detalhes dessa eleição são atualizados e podem ser consultados posteriormente.)
    @Override
    public void atualizaEleicao(Eleicao eleicao) throws RemoteException {

    }

    @Override
    public void eleicoesEndedRMI() throws RemoteException {
        for(int i = 0; i < listaEleicoesEnded.size();i++){
            showEleicoesDetalhes(i);
            showVotosRMI(listaEleicoesEnded.get(i));
            System.out.println();
        }
    }

    public Date parseDateString(String string) throws RemoteException, ParseException {
        Date date = new SimpleDateFormat("dd-MM-yyyy HH:mm").parse(string);


        return date;
    }

    public void adicionarMesa(Eleicao e, MulticastServer mesa) throws RemoteException {
        mesa.setEleicao(e);
        e.addMesa(mesa);
    }

    public void addMesa(MulticastServer m) throws RemoteException{
        listaMesas.add(m);
    }


    @Override
    public Eleicao createListaRMI(Eleicao eleicao, String nome) throws RemoteException {
        Lista list = new Lista(null,nome);
        eleicao.addListasCandidatas(list);
        return eleicao;
    }

    //to-do
    @Override
    public void eliminarListaCandidatos(Eleicao eleicao, int i) throws RemoteException{

    }

}

