import javax.lang.model.element.NestingKind;
import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class Eleicao implements Serializable{
    private final String titulo;
    private final String descricao;
    private final Date startDate;
    private final Date endDate;
    private final int id;
    Pessoa.voterType tipoVoters;
    ArrayList<String> orgVoters;

    Eleicao(String titulo, String descricao, String startDate, int startHour, int startMinute, String endDate, int endHour, int endMinute) throws ParseException {
        this.titulo = titulo;
        this.descricao = descricao;
        this.startDate = new SimpleDateFormat("dd-MM-yyyy'T'HH:mm").parse(parseDate(startDate, startHour,startMinute));
        this.endDate = new SimpleDateFormat("dd-MM-yyyy'T'HH:mm").parse(parseDate(endDate, endHour, endMinute));
        this.id = RmiServer.idCounter++;
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
        return date+"T"+sHour+":"+sMinute;
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

    public String getDescricao() {
        return descricao;
    }

    public Date getStartDate() {
        return startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public int getId() {
        return id;
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
}
