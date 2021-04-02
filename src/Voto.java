import java.io.Serializable;
import java.util.Date;


public class Voto implements Serializable {
    private Pessoa eleitor;

    private Date data;
    private String local;

    Voto(Pessoa p, String departamento){
        this.eleitor = p;

        this.data = new Date();
        this.local = departamento;
    }

    public Pessoa getEleitor() {
        return eleitor;
    }


    public Date getData() {
        return data;
    }

    public String getLocal() {
        return local;
    }

}
