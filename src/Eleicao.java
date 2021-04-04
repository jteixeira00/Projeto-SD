import javax.lang.model.element.NestingKind;
import java.io.Serializable;
import java.rmi.RemoteException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class Eleicao implements Serializable{
    private String titulo;
    private String descricao;
    private String departamento;
    private Date startDate;
    private Date endDate;
    private ArrayList<Lista> listasCandidatas;
    Pessoa.voterType tipoVoters;
    ArrayList<String> orgVoters;

    ArrayList<Mesa> mesas = new ArrayList<>();


    private int votosNulos;
    private int votosBrancos;
    private ArrayList<Voto> votos;
    private ArrayList<String> departamentos;

    Eleicao(String titulo, String descricao, String startDate, int startHour, int startMinute, String endDate, int endHour, int endMinute, String departamento, int type) throws ParseException {

        switch(type){
            case 1:
                this.tipoVoters = Pessoa.voterType.ALUNO;
                break;
            case 2:
                this.tipoVoters = Pessoa.voterType.DOCENTE;
                break;
            case 3:
                this.tipoVoters = Pessoa.voterType.FUNCIONARIO;
                break;
        }

        this.titulo = titulo;
        this.descricao = descricao;
        this.startDate = new SimpleDateFormat("dd-MM-yyyy HH:mm").parse(parseDate(startDate, startHour,startMinute));
        this.endDate = new SimpleDateFormat("dd-MM-yyyy HH:mm").parse(parseDate(endDate, endHour, endMinute));
        Date date = new Date();
        this.votosBrancos = 0;
        this.votosNulos = 0;
        this.listasCandidatas = new ArrayList<>();
        this.votos = new ArrayList<>();
        this.departamento = "";
        this.departamentos = new ArrayList<>();

    }

    public ArrayList<Voto> getVotos(){
        return this.votos;
    }
    public String parseDate(String date, int hour, int minute){
        String sHour = ""+hour;
        if (sHour.length()==1){
            sHour = "0"+sHour;
        }
        String sMinute = ""+minute;
        if (sMinute.length()==1){
            sMinute = "0"+sMinute;
        }
        return date+" "+sHour+":"+sMinute;
    }

    public void addOrg(String org){
        this.orgVoters.add(org);
    }

    public void setType(int tipo){
        switch(tipo){
            case 1:
                this.tipoVoters = Pessoa.voterType.ALUNO;
                break;
            case 2:
                this.tipoVoters = Pessoa.voterType.DOCENTE;
                break;
            case 3:
                this.tipoVoters = Pessoa.voterType.FUNCIONARIO;
                break;
        }
    }

    public String getTitulo() {
        return titulo;
    }

    public void addDepartamento(String nome) {
        departamentos.add(nome);
    }

    public void setTitulo(String newTitle){
        this.titulo = newTitle;
    }


    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public void setDepartamento(String departamento) {
        this.departamento = departamento;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public String getDescricao() {
        return descricao;
    }

    public Date getStartDate() {
        return startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void addVotoNulo(){
        this.votosNulos++;
    }

    public void addVotoBranco(){
        this.votosBrancos++;
    }


    public Pessoa.voterType getTipoVoters() {
        return tipoVoters;
    }

    public void setTipoVoters(Pessoa.voterType tipoVoters) {
        this.tipoVoters = tipoVoters;
    }

    public ArrayList<String> getOrgVoters() {
        return orgVoters;
    }

    public void setOrgVoters(ArrayList<String> orgVoters) {
        this.orgVoters = orgVoters;
    }

    public ArrayList<Lista> getListasCandidatas() {
            return listasCandidatas;
    }

    public void addListasCandidatas(Lista lista) {
        listasCandidatas.add(0, lista);
    }

    public void setListasCandidatas(ArrayList<Lista> listasCandidatas) {
        this.listasCandidatas = listasCandidatas;
    }

    public int getVotosBrancos() {
        return votosBrancos;
    }

    public int getVotosNulos() {
        return votosNulos;
    }

    public void addVoto(Voto v){
        this.votos.add(v);}

    public String showListasCandidatas() throws RemoteException {
        String lista = "";
        int indx;
        String indxS;
        for(int i = 0; i < listasCandidatas.size(); i++) {
            indx = i + 1;
            indxS = Integer.toString(indx);
            lista += indxS + " - " + listasCandidatas.get(i).getNome() + '\n';

        }
        return lista;
    }

    public int sizeCandidatos() throws RemoteException{
        if (listasCandidatas == null)
            return 0;
        return listasCandidatas.size();
    }

    public int sizeLista(int index) throws RemoteException{
        if (listasCandidatas.get(index) == null)
            return 0;
        return listasCandidatas.get(index).getSize();
    }

    public int sizeDepartamentos() throws RemoteException{
        if (departamentos == null)
            return 0;
        return departamentos.size();
    }

    public String getDepartamento(){
        return departamento;
    }

    public ArrayList<String> getDepartamentos(){
        return departamentos;
    }

    public String dateToString(Date date){
        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy'T'HH:mm");
        String strDate = dateFormat.format(date);
        return strDate;

    }

    public void deleteDepartamento(String dep){
        departamentos.remove(dep);
    }

    public int votosTotal(){
        return votos.size();
    }

    public void addMesa(Mesa mesa){
        mesas.add(mesa);
    }

    public ArrayList<Mesa> getMesas() {
        return mesas;
    }

    public void setMesas(ArrayList<Mesa> mesas) {
        this.mesas = mesas;
    }


}
