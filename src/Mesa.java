import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

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


    public ArrayList<Eleicao> getEleicoesEspecificas(String tipoUser){
        ArrayList<Eleicao> res = new ArrayList<>();
        Date date = new Date();
        for(Eleicao e: eleicoes){
            if(e.getTipoVoters().toString().equals(tipoUser)){
                res.add(e);
            }
        }
        return res;
    }

}
