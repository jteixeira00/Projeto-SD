import java.io.Serializable;

public class Pessoa implements Serializable {

    private final String departamento;
    private final String faculdade;
    private final String contacto;
    private final String morada;
    private final String cc;
    private final String validade;
    private final String password;
    public enum voterType{
        ALUNO,
        DOCENTE,
        FUNCIONARIO
    }
    private voterType type;

    Pessoa(int tipo, String dep, String fac, String contacto, String morada, String cc, String validadecc, String password){
        switch(tipo){
            case 1:
                this.type = voterType.ALUNO;
                break;
            case 2:
                this.type = voterType.DOCENTE;
                break;
            case 3:
                this.type = voterType.FUNCIONARIO;
                break;
        }
        this.departamento = dep;
        this.faculdade = fac;
        this.contacto = contacto;
        this.morada = morada;
        this.cc = cc;
        this.validade = validadecc;
        this.password = password;
    }
}
