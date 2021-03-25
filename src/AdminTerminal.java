import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Scanner;

public class AdminTerminal
{
    private String numero;
    private String password;
    private RmiInterface ri;

    public boolean loginUser() throws RemoteException    {
        while(true) {
            boolean check = ri.login(this.numero, this.password);
            if (!check) {
                System.err.println("Login Failed: username or password is incorrect.");
            } else {
                return true;
            }
        }

    }

    public boolean registerUser() throws RemoteException {
        System.out.println("---Criar Utilizador---\n");
        Scanner sc = new Scanner(System.in);
        System.out.println("1 - Estudante\n2 - Docente\n3 - Funcionário\n");
        String tipoS = sc.nextLine();
        int tipo = Integer.parseInt(tipoS);

        System.out.println("\nNome: ");
        String nome = sc.nextLine();

        System.out.println("\nPassword: ");
        String password = sc.nextLine();

        System.out.println("\nNúmero da Universidade: ");
        String uni = sc.nextLine();

        System.out.println("\nNúmero do Cartão de Cidadão: ");
        String cc = sc.nextLine();

        System.out.println("\nValidade do Cartão de Cidadão (dd-MM-yyyy): ");
        String validade = sc.nextLine();

        System.out.println("\nContacto Telefónico: ");
        String numeroTelefonico = sc.nextLine();

        System.out.println("\nMorada: ");
        String morada = sc.nextLine();

        System.out.println("\nDepartamento: ");
        String departamento = sc.nextLine();

        System.out.println("\nFaculdade: ");
        String faculdade = sc.nextLine();

        boolean check = ri.createUserRMI(tipo, nome, uni, departamento, faculdade, numeroTelefonico, morada, cc, validade, password);
        return check;
    }

    public boolean createEleicao() throws RemoteException {
        System.out.println("---Criar Eleição---\n");
        Scanner sc = new Scanner(System.in);

        System.out.println("\nTitulo: ");
        String titulo = sc.nextLine();

        System.out.println("\nDescrição: ");
        String descricao = sc.nextLine();

        System.out.println("\nData de Inicio (dd-MM-yyyy): ");
        String startDate = sc.nextLine();

        System.out.println("\nHora de Inicio: ");
        String startH = sc.nextLine();
        int startHour = Integer.parseInt(startH);

        System.out.println("\nMinuto de Inicio: ");
        String startM = sc.nextLine();
        int startMinute = Integer.parseInt(startH);

        System.out.println("\nData de Fim (dd-MM-yyyy): ");
        String endDate = sc.nextLine();

        System.out.println("\nHora de Fim: ");
        String endH = sc.nextLine();
        int endHour = Integer.parseInt(startH);

        System.out.println("\nMinuto de Fim: ");
        String endM = sc.nextLine();
        int endMinute = Integer.parseInt(startH);

        System.out.println("\nRestringir eleição para um único departamento?(1 - Sim | 2 - Não): ");
        String departamento;
        String tipoS = sc.nextLine();
        int tipo = Integer.parseInt(tipoS);

        switch (tipo){
            case 1:
                System.out.println("\nDepartamento: ");
                departamento = sc.nextLine();
                break;

            default:
                departamento = "none";
                break;
        }

        System.out.println("Restringir eleição para que grupo de Pessoas?(1 - Estudantes || 2 - Docentes || 3 - Funcionários): ");
        int type = 0;
        while(type != 1 || type != 2 || type != 3) {
            String typeS = sc.nextLine();
            type = Integer.parseInt(typeS);
            if(type != 1 || type != 2 || type != 3)
                System.out.println("Numero Inválido: Tente de novo\n");
        }

        boolean check = ri.createEleicaoRMI(titulo, descricao, startDate, startHour, startMinute, endDate, endHour, endMinute, departamento, type);
        return check;
    }

    public boolean gerirCandidatos(Eleicao eleicao) throws RemoteException{
        boolean check = false;
        System.out.println("---Gerir Candidatos---\n");
        Scanner sc = new Scanner(System.in);
        System.out.println("Lista que pretende gerir (1 - ");
        System.out.println(eleicao.sizeCandidatos());
        System.out.println("): ");
        String choiceS = sc.nextLine();
        int choice = Integer.parseInt(choiceS);

        System.out.println("\n1 - Adicionar candidatos || 2 - Remover candidatos:  ");
        String tipoS = sc.nextLine();
        int tipo = Integer.parseInt(tipoS);
        int size = eleicao.sizeLista(choice - 1);

        switch (tipo){
            case 1:
                System.out.println("\n---Adicionar Candidatos---\n");
                ri.showPessoas();
                System.out.println("Pessoa que pretende adicionar (1 - ");
                System.out.println(ri.sizePessoas());
                System.out.println("): ");
                String addS = sc.nextLine();
                int add = Integer.parseInt(addS);
                if(add <= ri.sizePessoas())
                    check = ri.addCandidateRMI(eleicao,choice - 1,add);
                else
                    System.out.printf("\nCandidato inválido.\n");
                break;

            case 2:
                System.out.println("\n---Remover Candidatos---\n");
                if(size == 0){
                    System.out.println("Lista Vazia - Impossivel Remover Candidatos");
                    break;
                }
                eleicao.showCandidatos(choice - 1);
                System.out.println("Candidato que pretende eliminar (1 - ");
                System.out.println(size);
                System.out.println("): ");
                String deletS = sc.nextLine();
                int delet = Integer.parseInt(deletS);
                if(delet <= size)
                    check = ri.deleteCandidateRMI(eleicao,choice - 1,delet);
                else
                    System.out.printf("\nCandidato inválido.\n");
                break;

            default:
                break;
        }

        return check;
    }

    public boolean gerirMesas() throws RemoteException{
        boolean check = false;
        System.out.println("---Gerir Mesas---\n");
        Scanner sc = new Scanner(System.in);
        System.out.println("\n1 - Adicionar mesa || 2 - Remover mesa:  ");
        String tipoS = sc.nextLine();
        int tipo = Integer.parseInt(tipoS);

        switch (tipo){
            case 1:
                System.out.println("\n---Adicionar Mesa---\n");
                System.out.println("Departamento da Mesa: ");
                String departamento = sc.nextLine();
                //verificar se o departamento já tem mesa
                check = ri.criaMesaRMI(departamento);
                break;

            case 2:
                System.out.println("\n---Remover Mesa---\n");
                ri.showMesas();
                System.out.println("Mesa que pretende remover (1 - ");
                System.out.println(ri.sizeMesas());
                System.out.println("): ");
                String delS = sc.nextLine();
                int del = Integer.parseInt(delS);
                check = ri.deleteMesaRMI(del);
                break;

            default:
                break;
        }

        return check;
    }

    public static void main(String[] args) throws MalformedURLException, RemoteException, NotBoundException {
        try {
            RmiInterface ri = (RmiInterface) Naming.lookup("rmi://localhost:7000/rmiServer");
            System.out.println("8 + 3 = " + ri.add(8, 3));
        } catch (NotBoundException | MalformedURLException | RemoteException e) {
            e.printStackTrace();
        }
    }
}
