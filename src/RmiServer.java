import javax.swing.plaf.multi.MultiInternalFrameUI;
import java.io.*;
import java.lang.reflect.Array;
import java.rmi.RMISecurityManager;
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
    public String secondaryAddress = "224.3.3.";


    private ArrayList<Eleicao> listaEleicoes;
    private ArrayList<Pessoa> listaPessoas;
    private ArrayList<Pessoa> pessoasOnline;
    private ArrayList<MulticastServer> listaMesas;

    private ArrayList<AdminTerminalInterface> terminais = new ArrayList<>();
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
        return baseAddress + addressEnd;
    }
    public String getSecondaryAddress() throws RemoteException{
        return secondaryAddress + addressEnd;
    }

    public int getTableNumber() throws RemoteException {
        return addressEnd++;
    }


    public static void main(String args[]) {
        System.getProperties().put("java.security.policy", "policy.all");
        System.setSecurityManager(new RMISecurityManager());
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


    public boolean votar(Eleicao e, int choiceLista, Pessoa p, String departamento) throws RemoteException{
        if(choiceLista == 0){
            e.addVotoNulo();
        }

        if(choiceLista== 1){
            e.addVotoBranco();
        }
        else {
            Voto v = new Voto(e.getListasCandidatas().get(choiceLista), p, departamento);
            try {
                if (!this.eleicoesOngoing().contains(e)) {
                    return false;
                }
            } catch (RemoteException remoteException) {
                remoteException.printStackTrace();
                return false;
            }
            e.addVoto(v);
            e.getListasCandidatas().get(choiceLista).addVoto();
            save();
        }
        return true;
    }

    /*public boolean checkData(Date start, Date end, Date now) throws RemoteException{
        if(start.after(end)) {
            return false;
        }
        if (start.getYear() == now.getYear()){
            System.out.println("Year");
            if(start.getMonth() == now.getMonth()){
                System.out.println("Month");
                if(start.getDay() == now.getDay()) {
                    System.out.println("Day");
                    if (start.getHours() == now.getHours()) {
                        System.out.println("Hours");
                        System.out.println(start.getHours());
                        System.out.println(now.getMinutes());
                        return start.getMinutes() >= now.getMinutes();
                    } else return start.getHours() > now.getHours();
                }
                else return start.getDay() > now.getDay();
            }
            else return start.getMonth() > now.getMonth();
        }
        else return start.getYear() > now.getYear();
    }*/

    /*public boolean dataAfter(Date end, Date now) throws RemoteException {
        if (end.getYear() == now.getYear()){
            if(end.getMonth() == now.getMonth()){
                if(end.getDay() == now.getDay()) {
                    if (end.getHours() == now.getHours()) {
                        return end.getMinutes() > now.getMinutes();
                    } else return end.getHours() > now.getHours();
                }
                else return end.getDay() > now.getDay();
            }
            else return end.getMonth() > now.getMonth();
        }
        else return end.getYear() > now.getYear();
    }*/


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

    @Override
    public boolean deleteCandidateRMI(int indx, int choice, int delete) throws RemoteException {
        ArrayList<Lista> aux = getEleicoesFuturas().get(indx).getListasCandidatas();
        aux.get(choice).getMembros().remove(delete);
        getEleicoesFuturas().get(indx).setListasCandidatas(aux);
        return true;
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

    @Override
    public boolean addCandidateRMI(int indx, int choice, int addm) throws RemoteException {
        ArrayList<Lista> aux = getEleicoesFuturas().get(indx).getListasCandidatas();
        Pessoa p = listaPessoas.get(addm);
        aux.get(choice).getMembros().add(p);
        getEleicoesFuturas().get(indx).setListasCandidatas(aux);
        return true;
    }

    //to-do
    @Override
    public boolean criaMesaRMI(int indexE, int indexM) throws RemoteException {
        getEleicoesFuturas().get(indexE).addMesa(getMesas().get(indexM));
        getMesas().get(indexM).addEleicaoLista(getEleicoesFuturas().get(indexE));
        return true;
    }


    @Override
    public String showMesas() throws RemoteException {
        String str = "";
        String number;
        int i = 0;
        for(MulticastServer m: listaMesas){
            i++;
            number = String.valueOf(i);
            str += number + " - " + m.getDepartamento() + "\n";
        }
        return str;
    }

    @Override
    public String showMesasEleicao(int indx) throws RemoteException {
        String str = "";
        String number;
        int i = 0;
        for(MulticastServer m: getEleicoesFuturas().get(indx).getMesas()){
            i++;
            number = String.valueOf(i);
            str += number + " - " + m.getDepartamento() + "\n";
        }
        return str;
    }


    @Override
    public int sizeMesas() throws RemoteException {
        if(listaMesas == null)
            return 0;
        return listaMesas.size();
    }

    @Override
    public int sizeMesasEleicao(int indx) throws RemoteException {
        if(getEleicoesFuturas().get(indx).getMesas() == null)
            return 0;
        return getEleicoesFuturas().get(indx).getMesas().size();
    }


    @Override
    public boolean deleteMesaRMI(int indexE,int indexM) throws RemoteException {

        for(MulticastServer m: listaMesas){
            if(m.getDepartamento().equals(getEleicoesFuturas().get(indexE).getMesas().get(indexM).getDepartamento())){
                m.getEleicaoLista().remove(getEleicoesFuturas().get(indexE));
            }
        }

        getEleicoesFuturas().get(indexE).getMesas().remove(indexM);

        return true;
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

    public Pessoa getPessoabyNumber(String numero) throws RemoteException{
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
            Date date = new Date();
            ArrayList<Eleicao> res = new ArrayList<>();
            try {
                for (Eleicao e : getEleicoes()) {
                    if (e.getEndDate().before(date) || e.getEndDate().equals(date)) {
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
    public int sizeEleicoesFuturas() throws RemoteException{
        return getEleicoesFuturas().size();
    }

    @Override
    public String showEleicoesDetalhes(int index) throws RemoteException {
        Eleicao eleicao = getEleicoesFuturas().get(index);
        return "\n1 - Titulo: " + eleicao.getTitulo() + "\n2 - Descrição: " + eleicao.getDescricao() + "\n3 - Data de Inicio (dd-MM-yyyy  HH:mm): " + eleicao.dateToString(eleicao.getStartDate()) + "\n4 - Data de Fim (dd-MM-yyyy  HH:mm): " + eleicao.dateToString(eleicao.getEndDate()) + "\n5 - Restringir eleição para um único departamento: " + eleicao.getDepartamento();
    }

    @Override
    public String showEleicoesDetalhesEnded(int index) throws RemoteException {
        Eleicao eleicao = getEleicoesEnded().get(index);
        return "\n1 - Titulo: " + eleicao.getTitulo() + "\n2 - Descrição: " + eleicao.getDescricao() + "\n3 - Data de Inicio (dd-MM-yyyy  HH:mm): " + eleicao.dateToString(eleicao.getStartDate()) + "\n4 - Data de Fim (dd-MM-yyyy  HH:mm): " + eleicao.dateToString(eleicao.getEndDate()) + "\n5 - Restringir eleição para um único departamento: " + eleicao.getDepartamento();
    }

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

            case 5:
                //alterar departamento
                eleicao.setDepartamento(change);
                break;

            default:
                System.out.println("Input Inválido.");
                return false;
        }
        return true;
    }

    @Override
    public String showVotoDetalhesRMI(Pessoa eleitor) throws RemoteException {
        return "Local de Voto: " + eleitor.getLocalVoto() + "\nMomento de Voto: " + eleitor.getTimeVoto();
    }

    @Override
    public String showVotosRMI(Eleicao eleicao) throws RemoteException {
        int count;
        int percent;
        String countS;
        String percentS;
        String str = "\nResultados:\n";
        if(eleicao.getListasCandidatas().size() != 0) {
            for (Lista list : eleicao.getListasCandidatas()) {
                count = list.getVotos();
                if (eleicao.votosTotal() == 0)
                    percent = 0;
                else
                    percent = count / eleicao.votosTotal();
                countS = Integer.toString(count);
                percentS = Integer.toString(percent);
                str += ".................\nLista " + list.getNome() + "\nVotos: " + countS + " | " + percentS + "%\n.................\n";
            }
            count = eleicao.getVotosBrancos();
            if (eleicao.votosTotal() == 0)
                percent = 0;
            else
                percent = count / eleicao.votosTotal();
            countS = Integer.toString(count);
            percentS = Integer.toString(percent);
            str += "\n................." + "\nVotos em Branco: " + countS + " | " + percentS + "%";

            count = eleicao.getVotosNulos();
            if (eleicao.votosTotal() == 0)
                percent = 0;
            else
                percent = count / eleicao.votosTotal();
            countS = Integer.toString(count);
            percentS = Integer.toString(percent);
            str += "\n................." + "\nVotos Nulos: " + countS + " | " + percentS + "%";
        }
        else
            str = "\nResultados:\nSem Listas Candidatas\n";
        return str;
    }


    //to-do (13 - Os detalhes dessa eleição são atualizados e podem ser consultados posteriormente.)
    @Override
    public void atualizaEleicao(Eleicao eleicao) throws RemoteException {

    }

    @Override
    public String eleicoesEndedRMI() throws RemoteException {
        String str = "";
        if(getEleicoesEnded().size() != 0) {
            for (int i = 0; i < getEleicoesEnded().size(); i++) {
                str += showEleicoesDetalhesEnded(i) + "\n" + showVotosRMI(getEleicoesEnded().get(i));
            }
        }
        else{
            return "Impossivel Realizar Operação: Eleições Passadas Inexistentes.\n";
        }
        return str;
    }

    public Date parseDateString(String string) throws RemoteException, ParseException {
        Date date = new SimpleDateFormat("dd-MM-yyyy HH:mm").parse(string);
        return date;
    }

    public void adicionarMesa(Eleicao e, MulticastServer mesa) throws RemoteException {
        mesa.addEleicaoLista(e);
        e.addMesa(mesa);
    }

    public void addMesa(MulticastServer m) throws RemoteException{
        listaMesas.add(m);
    }

    public String identificarUser(String input){
        return "";
    }

    @Override
    public Eleicao createListaRMI(int inx, String nome) throws RemoteException {
        Eleicao eleicao = getEleicoesFuturas().get(inx);
        Lista list = new Lista(null,nome);
        eleicao.addListasCandidatas(list);
        return eleicao;
    }

    //to-do
    @Override
    public void eliminarListaCandidatos(int indx, int i) throws RemoteException{
        getEleicoesFuturas().get(indx).getListasCandidatas().remove(i);
    }


    public void subscribe(AdminTerminalInterface admin) throws RemoteException{
        terminais.add(admin);
    }
}

