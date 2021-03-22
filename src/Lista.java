import java.io.Serializable;
import java.util.ArrayList;

public class Lista implements Serializable {
    private ArrayList<Pessoa> membros;
    private String nome;

    public Lista(ArrayList<Pessoa> membros, String nome) {
        this.membros = membros;
        this.nome = nome;
    }

    public ArrayList<Pessoa> getMembros() {
        return membros;
    }

    public void setMembros(ArrayList<Pessoa> membros) {
        this.membros = membros;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public void addPessoa(Pessoa pessoa){
        //to do
    }
}
