import java.io.Serializable;
import java.util.ArrayList;

public class Mesa implements Serializable {
    public String getDepartamento() {
        return departamento;
    }

    public ArrayList<Eleicao> getEleicoes() {
        return eleicoes;
    }
    public void addEleicao(Eleicao e){
        this.eleicoes.add(e);
    }
    private String departamento;
    private ArrayList<Eleicao> eleicoes;

    Mesa(String departamento){
        this.departamento = departamento;
        eleicoes = new ArrayList<>();
    }

}
