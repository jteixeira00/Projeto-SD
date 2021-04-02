import java.io.*;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
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
    private ArrayList<Mesa> listaMesas;
    private ArrayList<AdminTerminalInterface> terminais = new ArrayList<>();

    public RmiServer() throws RemoteException {
        super();
        this.listaPessoas = new ArrayList<>();
        this.listaEleicoes = new ArrayList<>();
        this.pessoasOnline = new ArrayList<>();
        this.listaMesas = new ArrayList<>();
        load();
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

    public int getTableNumber(String arg) throws RemoteException {

        return addressEnd++;
    }

    public void notifyOfNewTable(String arg) throws RemoteException{
        for(AdminTerminalInterface a:getTerminais()){
            try{
                a.tableUpdate(arg);}
            catch(RemoteException e){
                //ignore
            }
        }

    }
    public static void main(String args[]) {

        try {
            RmiInterface ri = new RmiServer();
            LocateRegistry.createRegistry(7000).rebind("rmiServer", ri);
        } catch (RemoteException ex1) {
            System.out.println("RMI SERVER EXCEPTION: " + ex1);
        }
    }

    public ArrayList<AdminTerminalInterface> getTerminais(){
        return terminais;
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
        System.out.println(p.getNumero());
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




    public boolean votar(int eleicao, int choiceLista, String number, String departamento, int tableCount) throws RemoteException{
        ArrayList<Eleicao> eArray = new ArrayList<>();
        Date date = new Date();
        Pessoa p = getPessoabyNumber(number);

        for (Eleicao e1 : getMesaByName(departamento).getEleicoes()) {
            if(e1.getEndDate().after(date) && e1.getStartDate().before(date)) {
                System.out.println(e1.getTitulo());
                System.out.println(e1.getListasCandidatas().get(0).getNome());
                eArray.add(e1);
            }
        }

        System.out.println("----------");
        System.out.println(eArray);
        System.out.println("----------");

        Eleicao e = eArray.get(eleicao);

        if(choiceLista == 0){
            e.addVotoNulo();
        }

        if(choiceLista== 1){
            e.addVotoBranco();
        }
        else {
            Voto v = new Voto(p, departamento);
            e.getListasCandidatas().get(choiceLista-2).addVoto();
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

        }

        for(AdminTerminalInterface ad: terminais){

            try{
                System.out.println("at least i tried");
                ad.voteUpdate(departamento, tableCount);}
            catch(RemoteException e1){
                //ignore
            }
        }
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

    @Override
    public boolean deleteCandidateRMI(int indx, int choice, int delete) throws RemoteException {
        ArrayList<Lista> aux = getEleicoesFuturas().get(indx).getListasCandidatas();
        aux.get(choice).getMembros().remove(delete);
        getEleicoesFuturas().get(indx).setListasCandidatas(aux);
        save();
        return true;
    }


    @Override
    public String showPessoas() throws RemoteException {
        String peeps = "";
        int i = 1;
        String is;
        for(Pessoa p: listaPessoas){
            is = String.valueOf(i);
            peeps += is + " - " + p.getNome() + " || " + p.getNumero() + " || " + p.getType() + " || " + p.getDepartamento() + '\n';
            i++;
        }
        return peeps;
    }

    @Override
    public int sizePessoas() throws RemoteException {
        return listaPessoas.size();
    }

    //indx - Eleicao || choice - lista || addm - pessoa
    @Override
    public boolean addCandidateRMI(int indx, int choice, int addm) throws RemoteException {
        ArrayList<Lista> aux = getEleicoesFuturas().get(indx).getListasCandidatas();
        Pessoa p = listaPessoas.get(addm);
        for(Pessoa pep : getEleicoesFuturas().get(indx).getListasCandidatas().get(choice).getMembros()){
            if(pep == p)
                return false;
        }
        aux.get(choice).getMembros().add(p);
        getEleicoesFuturas().get(indx).setListasCandidatas(aux);
        save();
        return true;
    }

    //adiciona mesa a eleição e vice-versa
    @Override
    public boolean criaMesaRMI(int indexE, int indexM) throws RemoteException {
        for(Mesa m: getEleicoesFuturas().get(indexE).getMesas()){
            if(m.getDepartamento().equals(listaMesas.get(indexM).getDepartamento())){
                return false;
            }
        }
        getEleicoesFuturas().get(indexE).addMesa(listaMesas.get(indexM));
        listaMesas.get(indexM).addEleicao(getEleicoesFuturas().get(indexE));
        save();
        return true;
    }


    @Override
    public String showMesas() throws RemoteException {
        String str = "";
        String number;
        int i = 0;
        for(Mesa m: listaMesas){
            i++;
            number = String.valueOf(i);
            str += number + " - " + m.getDepartamento() + "\n";
        }
        return str;
    }
    public Mesa getMesaByName(String dep) throws RemoteException{
        for (Mesa m: listaMesas){
            if( m.getDepartamento().equals(dep)){
                System.out.println(m.getDepartamento());
                return m;
            }
        }
        return  null;
    }
    @Override
    public String showMesasEleicao(int indx) throws RemoteException {
        String str = "";
        String number;
        int i = 0;
        for(Mesa m: getEleicoesFuturas().get(indx).getMesas()){
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

        for(Mesa m: listaMesas){
            if(m.getDepartamento().equals(getEleicoesFuturas().get(indexE).getMesas().get(indexM).getDepartamento())){
                m.getEleicoes().remove(getEleicoesFuturas().get(indexE));
            }
        }

        getEleicoesFuturas().get(indexE).getMesas().remove(indexM);
        save();
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
    public  void load() {
        ObjectInputStream is1 = null;
        ObjectInputStream is2 = null;
        ObjectInputStream is3 = null;
        File f = new File("eleicoes.ser");
        File f2 = new File("pessoas.ser");
        File f3 = new File("mesas.ser");

        if(f2.exists() && !f2.isDirectory()){
            try {
                FileInputStream stream = new FileInputStream("pessoas.ser");
                is2 = new ObjectInputStream(stream);
                this.listaPessoas = (ArrayList<Pessoa>) is2.readObject();
            }catch(Exception e){
                e.printStackTrace();
            }finally {
                if (is2 != null) {
                    try {
                        is2.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        if(f3.exists() && !f3.isDirectory()){
            try {
                FileInputStream stream = new FileInputStream("mesas.ser");
                is3 = new ObjectInputStream(stream);
                this.listaMesas = (ArrayList<Mesa>) is3.readObject();
            } catch(Exception e){
                e.printStackTrace();

            } finally {
                if (is2 != null) {
                    try {
                        is2.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        if(f.exists() && !f.isDirectory()  ) {
            try {
                FileInputStream stream = new FileInputStream("eleicoes.ser");
                is1 = new ObjectInputStream(stream);
                this.listaEleicoes = (ArrayList<Eleicao>) is1.readObject();





            } catch (Exception e) {

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
    }

    synchronized public void save() {
        ObjectOutputStream os1 = null;
        ObjectOutputStream os2 = null;
        ObjectOutputStream os3 = null;

        try {
            FileOutputStream stream = new FileOutputStream("eleicoes.ser");
            os1 = new ObjectOutputStream(stream);
            os1.writeObject(this.listaEleicoes);

            stream = new FileOutputStream("pessoas.ser");
            os2 = new ObjectOutputStream(stream);
            os2.writeObject(this.listaPessoas);

            stream = new FileOutputStream("mesas.ser");
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
        save();
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
                return false;
        }
        save();
        return true;
    }

    @Override
    public String showVotoDetalhesRMI(int indx) throws RemoteException {
        Pessoa eleitor = listaPessoas.get(indx);
        if(eleitor.getTimeVoto() == null){
            return "O eleitor ainda não votou.";
        }
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
                    percent = (count / eleicao.votosTotal())*100;
                countS = Integer.toString(count);
                percentS = Integer.toString(percent);
                str += ".................\nLista " + list.getNome() + "\nVotos: " + countS + " | " + percentS + "%\n.................\n";
            }
            count = eleicao.getVotosBrancos();
            if (eleicao.votosTotal() == 0)
                percent = 0;
            else
                percent = (count / eleicao.votosTotal())*100;
            countS = Integer.toString(count);
            percentS = Integer.toString(percent);
            str += "\n................." + "\nVotos em Branco: " + countS + " | " + percentS + "%";

            count = eleicao.getVotosNulos();
            if (eleicao.votosTotal() == 0)
                percent = 0;
            else
                percent = (count / eleicao.votosTotal())*100;
            countS = Integer.toString(count);
            percentS = Integer.toString(percent);
            str += "\n................." + "\nVotos Nulos: " + countS + " | " + percentS + "%";
        }
        else
            str = "\nResultados:\nSem Listas Candidatas\n";
        return str;
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

    public void adicionarMesa(Eleicao e, Mesa mesa) throws RemoteException {
        mesa.addEleicao(e);
        e.addMesa(mesa);
        save();
    }


    public void addMesa(Mesa m) throws RemoteException{
        for(Mesa m2: listaMesas){
            if(m2.getDepartamento().equals(m.getDepartamento())){
                return;
            }
        }
        listaMesas.add(m);
        save();
    }

    //to-do
    public String identificarUser(String input){
        return "";
    }

    @Override
    public boolean createListaRMI(int inx, String nome) throws RemoteException {
        Eleicao eleicao = getEleicoesFuturas().get(inx);
        for(Lista l : eleicao.getListasCandidatas()){
            if(l.getNome().equals(nome)){
                return false;
            }
        }
        Lista list = new Lista(null,nome);
        eleicao.addListasCandidatas(list);
        save();
        return true;
    }

    @Override
    public void eliminarListaCandidatos(int indx, int i) throws RemoteException{
        getEleicoesFuturas().get(indx).getListasCandidatas().remove(i);
        save();
    }


    public void subscribe(AdminTerminalInterface admin) throws RemoteException{
        terminais.add(admin);
    }

    public String generateLista(int eleicaoC, String dep) throws RemoteException{
        ArrayList<Eleicao> eArray = new ArrayList<>();
        Date date = new Date();

        for (Eleicao e1 : getMesaByName(dep).getEleicoes()) {
            if(e1.getEndDate().after(date) && e1.getStartDate().before(date)) {
                System.out.println(e1.getTitulo());
                System.out.println(e1.getListasCandidatas().get(0).getNome());
                eArray.add(e1);
            }
        }

        Eleicao eleicao = eArray.get(eleicaoC);

        for(Eleicao e: getMesaByName(dep).getEleicoes()){
            System.out.println("eleição: " + e.getTitulo());
            for(Lista l: e.getListasCandidatas()){
                System.out.println("lista "+ l.getNome());
            }
        }
        StringBuilder str = new StringBuilder("type|item_list;item_count|" + eleicao.getListasCandidatas().size());
        int i = 0;
        for(Lista l : eleicao.getListasCandidatas()){
            System.out.println(l.getNome());
            str.append(";item_").append(i).append("_name|");
            for(Pessoa p : l.getMembros()){
                System.out.println(p.getNome());
                str.append(p.getNome()).append(",");
            }
            i++;
        }
        System.out.println(str);
        return str.toString();
    }

}

