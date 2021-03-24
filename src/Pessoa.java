import java.io.Serializable;

public class Pessoa implements Serializable {

    private String departamento;
    private String numero;
    private String faculdade;
    private String contacto;
    private String morada;
    private String cc;
    private String validade;
    private String password;
    private String nome;
    public enum voterType{
        ALUNO,
        DOCENTE,
        FUNCIONARIO,
        NONE
    }
    private voterType type;



    Pessoa(int tipo, String nome, String numero, String dep, String fac, String contacto, String morada, String cc, String validadecc, String password){
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
        this.nome = nome;
        this.numero = numero;
        this.departamento = dep;
        this.faculdade = fac;
        this.contacto = contacto;
        this.morada = morada;
        this.cc = cc;
        this.validade = validadecc;
        this.password = password;
    }

    public String getDepartamento() {
        return departamento;
    }

    public String getNumero() {
        return numero;
    }

    public String getFaculdade() {
        return faculdade;
    }

    public String getContacto() {
        return contacto;
    }

    public String getMorada() {
        return morada;
    }

    public String getCc() {
        return cc;
    }

    public String getValidade() {
        return validade;
    }

    public String getPassword() {
        return password;
    }

    public String getNome() {
        return nome;
    }

    public voterType getType() {
        return type;
    }

    public void setDepartamento(String departamento) {
        this.departamento = departamento;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public void setFaculdade(String faculdade) {
        this.faculdade = faculdade;
    }

    public void setContacto(String contacto) {
        this.contacto = contacto;
    }

    public void setMorada(String morada) {
        this.morada = morada;
    }

    public void setCc(String cc) {
        this.cc = cc;
    }

    public void setValidade(String validade) {
        this.validade = validade;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public void setType(voterType type) {
        this.type = type;
    }
}
