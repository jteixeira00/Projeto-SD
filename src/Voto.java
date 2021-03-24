import java.io.Serializable;
import java.util.Date;


public class Voto implements Serializable {
    private Pessoa eleitor;
    private Lista lista;
    private Date data;
    private String local;

    Voto(Lista l, Pessoa p, String departamento){
        this.eleitor = p;
        this.lista = l;
        this.data = new Date();
        this.local = departamento;
    }

    public Pessoa getEleitor() {
        return eleitor;
    }

    public Lista getLista() {
        return lista;
    }

    public Date getData() {
        return data;
    }

    public String getLocal() {
        return local;
    }

}
