import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.Scanner;

public class AdminTerminal extends Thread
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

    public Eleicao createEleicao() throws RemoteException {
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

        System.out.println("\nDepartamento: ");
        String departamento = sc.nextLine();

        System.out.println("Restringir eleição para que grupo de Pessoas?(1 - Estudantes || 2 - Docentes || 3 - Funcionários): ");
        int type = 0;
        while(type != 1 && type != 2 && type != 3) {
            String typeS = sc.nextLine();
            type = Integer.parseInt(typeS);
            if(type != 1 && type != 2 && type != 3)
                System.out.println("Numero Inválido: Tente de novo\n");
        }

        return ri.createEleicaoRMI(titulo, descricao, startDate, startHour, startMinute, endDate, endHour, endMinute, departamento, type);
    }

    public boolean gerirCandidatos(Eleicao eleicao, int choice) throws RemoteException{
        boolean check = false;
        System.out.println("---Gerir Candidatos---");
        Scanner sc = new Scanner(System.in);

        System.out.println("1 - Adicionar candidatos || 2 - Remover candidatos:  ");
        String tipoS = sc.nextLine();
        int tipo = Integer.parseInt(tipoS);
        int size = eleicao.sizeLista(choice - 1);
        switch (tipo){
            case 1:
                System.out.println("---Adicionar Candidatos---");
                System.out.printf("\nPessoa(s) que pretende adicionar (1 - %d): \n", ri.sizePessoas());
                System.out.printf(ri.showPessoas());
                System.out.println("0 - SAIR DE ADICIONAR CANDIDATOS");
                while(true){
                    String addS = sc.nextLine();
                    int add = Integer.parseInt(addS);
                    if (add <= ri.sizePessoas() && add > 0)
                        check = ri.addCandidateRMI(eleicao, choice - 1, add);
                    else {
                        if(add == 0)
                            break;
                        System.out.println("Candidato inválido.");
                        break;
                    }
                }
                break;

            case 2:
                System.out.println("---Remover Candidatos---");
                if(size == 0){
                    System.out.println("Lista Vazia - Impossivel Remover Candidatos");
                }
                else {
                    System.out.printf("\nCandidato que pretende eliminar (1 - %d): \n", size);
                    System.out.printf(eleicao.getListasCandidatas().get(choice - 1).showCandidatos());
                    System.out.println("0 - SAIR DE REMOVER CANDIDATOS");
                    while(true) {
                        String deletS = sc.nextLine();
                        int delet = Integer.parseInt(deletS);
                        if (delet <= size)
                            check = ri.deleteCandidateRMI(eleicao, choice - 1, delet);
                        else {
                            if(delet == 0)
                                break;
                            System.out.println("Candidato inválido.");
                            break;
                        }
                    }
                }
                break;

            default:
                System.out.println("Opção Inválida");
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

    //mandar as op para o RMI
    public void gerirListas(Eleicao eleicao) throws RemoteException{
        System.out.println("---Gerir Listas---");
        Scanner sc = new Scanner(System.in);
        System.out.println("1 - Criar lista || 2 - Remover Lista || 3 - Gerir Candidatos: ");
        String choiceS = sc.nextLine();
        int choice = Integer.parseInt(choiceS);
        switch (choice){
            case  1:
                createLista(eleicao);
                break;
            case 2:
                if(eleicao.sizeCandidatos() == 0)
                    System.out.println("Eleição sem listas.");
                else{
                    int size = eleicao.sizeCandidatos();
                    System.out.printf("\nLista que pretende eliminar (1 - %d): \n",size);
                    System.out.print(eleicao.showListasCandidatas());
                    System.out.println();
                    choiceS = sc.nextLine();
                    choice = Integer.parseInt(choiceS);
                    ri.eliminarListaCandidatos(eleicao,choice - 1);
                }
                break;
            case 3:
                if(eleicao.sizeCandidatos() == 0)
                    System.out.println("Eleição sem listas.");
                else {
                    int size = eleicao.sizeCandidatos();
                    System.out.printf("\nLista que pretende gerir (1 - %d): \n", size);
                    System.out.print(eleicao.showListasCandidatas());
                    System.out.println();
                    choiceS = sc.nextLine();
                    choice = Integer.parseInt(choiceS);
                    gerirCandidatos(eleicao, choice - 1);

                }
                break;
            default:
                System.out.println("Input inválido");
                break;
        }



    }

    public boolean gerirEleicao() throws RemoteException{
        boolean check = false;
        System.out.println("---Gerir Eleições---\n");
        Scanner sc = new Scanner(System.in);
        System.out.print(ri.showEleicoesFuturas());
        System.out.printf("Eleição que pretende gerir (1 - %d): ",ri.sizeEleicoesFuturas());
        String choiceS = sc.nextLine();
        int choice = Integer.parseInt(choiceS);

        if(choice <= ri.sizeEleicoesFuturas()){
            System.out.println("\n---Alterar propriedade da Eleição---");
            System.out.printf(ri.showEleicoesDetalhes(choice - 1));
            System.out.println("\n6 - Gerir listas");
            String answerS = sc.nextLine();
            int answer  = Integer.parseInt(answerS);
            if(answer == 6)
                gerirListas(ri.getEleicoesFuturas().get(choice - 1));
            else if (answer > 0 && answer <= 4){
                System.out.println("Alterar para: ");
                String change = sc.nextLine();
                //to-do pedir a data ao utilizador no formato dd-MM-yyyy HH:mm ou entao pedir data hora e minuto separado
                check = ri.changeEleicoesRMI(choice - 1, answer, change);
            }
            else {
                System.out.printf("\nInput inválido.\n");
            }


        }
        else
            System.out.printf("\nInput inválido.\n");

        return check;
    }

    public void votoDetalhes(Pessoa eleitor) throws RemoteException{
        System.out.println("---Eleitor Local & Momento de Voto---");
        ri.showVotoDetalhesRMI(eleitor);
    }

    public Lista createLista(Eleicao eleicao) throws RemoteException{
        System.out.println("---Criar Lista---");
        Scanner sc = new Scanner(System.in);

        System.out.println("Nome da lista: ");
        String nome = sc.nextLine();
        ri.createListaRMI(eleicao,nome);


        System.out.println("Adicionar candidatos: Sim - 1 || Não - 2");
        String choiceS = sc.nextLine();
        int choice = Integer.parseInt(choiceS);
        switch (choice){
            case 1:
                System.out.printf("\nPessoa(s) que pretende adicionar (1 - %d): \n", ri.sizePessoas());
                System.out.printf(ri.showPessoas());
                System.out.println("0 - SAIR DE ADICIONAR CANDIDATOS");
                while(true){
                    String addS = sc.nextLine();
                    int add = Integer.parseInt(addS);
                    if (add <= ri.sizePessoas() && add > 0)
                        ri.addCandidateRMI(eleicao, -1, add);
                    else {
                        if(add == 0)
                            break;
                        System.out.println("Candidato inválido.");
                        break;
                    }
                }
                break;
            default:
                System.out.println("Lista criada com 0 candidatos");
        }
        return null;
    }

    //to-do (11)
    public boolean mesasEstado() throws RemoteException{

        return true;
    }

    //to-do (12)
    public boolean numeroEleitores() throws RemoteException{

        return true;
    }

    //n esquecer: end Eleicao
    public void resultadosEleicao(Eleicao eleicao) throws RemoteException{
        System.out.println("---Eleicao Terminada---");
        ri.showVotosRMI(eleicao);
        ri.atualizaEleicao(eleicao);
    }

    public void EleicaoEndedDetalhes() throws RemoteException{
        System.out.println("---Detalhes Eleicoes Terminadas---");
        ri.eleicoesEndedRMI();
    }



    public static void main(String[] args) throws MalformedURLException, RemoteException, NotBoundException {
        /*try {

            //System.out.println("8 + 3 = " + ri.add(8, 3));

        } catch (NotBoundException | MalformedURLException | RemoteException e) {
            e.printStackTrace();
        }

         */
        AdminTerminal terminal = new AdminTerminal();
        terminal.start();
    }
    public void run(){
        try {
            this.ri = (RmiInterface) Naming.lookup("rmi://localhost:7000/rmiServer");
        } catch (NotBoundException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (RemoteException e) {
            e.printStackTrace();
        }


        try {

            //ri.printEleicao(createEleicao());
            //System.out.printf(ri.showEleicoesDetalhes(0));

            createEleicao();
            gerirEleicao();
            gerirEleicao();

        } catch (RemoteException e) {
            e.printStackTrace();
        }

    }
}
