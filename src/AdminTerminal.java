import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.Scanner;
import java.util.jar.JarOutputStream;

public class AdminTerminal extends Thread
{
    private RmiInterface ri;

    public boolean loginUser() throws RemoteException    {
        while(true) {
            System.out.println("\n---Login User---\n");
            Scanner sc = new Scanner(System.in);
            System.out.println("Numero: ");
            String numero = sc.nextLine();
            System.out.println("Numero: ");
            String password = sc.nextLine();
            boolean check = ri.login(numero, password);
            if (!check) {
                System.err.println("Login Failed: numero ou password incorretas.");
            } else {
                return true;
            }
        }
    }

    public boolean registerUser() throws RemoteException {
        System.out.println("\n---Criar Utilizador---\n");
        Scanner sc = new Scanner(System.in);
        System.out.println("1 - Estudante\n2 - Docente\n3 - Funcionário");
        String tipoS = sc.nextLine();
        int tipo = Integer.parseInt(tipoS);

        System.out.println("Nome: ");
        String nome = sc.nextLine();

        System.out.println("Password: ");
        String password = sc.nextLine();

        System.out.println("Número da Universidade: ");
        String uni = sc.nextLine();

        System.out.println("Número do Cartão de Cidadão: ");
        String cc = sc.nextLine();

        System.out.println("Validade do Cartão de Cidadão (dd-MM-yyyy): ");
        String validade = sc.nextLine();

        System.out.println("Contacto Telefónico: ");
        String numeroTelefonico = sc.nextLine();

        System.out.println("Morada: ");
        String morada = sc.nextLine();

        System.out.println("Departamento: ");
        String departamento = sc.nextLine();

        System.out.println("Faculdade: ");
        String faculdade = sc.nextLine();

        boolean check = ri.createUserRMI(tipo, nome, uni, departamento, faculdade, numeroTelefonico, morada, cc, validade, password);
        return check;
    }

    public Eleicao createEleicao() throws RemoteException {
        System.out.println("\n---Criar Eleição---\n");
        Scanner sc = new Scanner(System.in);

        System.out.println("Titulo: ");
        String titulo = sc.nextLine();

        System.out.println("Descrição: ");
        String descricao = sc.nextLine();

        System.out.println("Data de Inicio (dd-MM-yyyy): ");
        String startDate = sc.nextLine();

        System.out.println("Hora de Inicio: ");
        String startH = sc.nextLine();
        int startHour = Integer.parseInt(startH);

        System.out.println("Minuto de Inicio: ");
        String startM = sc.nextLine();
        int startMinute = Integer.parseInt(startM);

        System.out.println("Data de Fim (dd-MM-yyyy): ");
        String endDate = sc.nextLine();

        System.out.println("Hora de Fim: ");
        String endH = sc.nextLine();
        int endHour = Integer.parseInt(endH);

        System.out.println("Minuto de Fim: ");
        String endM = sc.nextLine();
        int endMinute = Integer.parseInt(endM);

        System.out.println("Departamento: ");
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

    public boolean gerirCandidatos(Eleicao eleicao, int choice, int indx) throws RemoteException{
        boolean check = false;
        System.out.println("---Gerir Candidatos---");
        Scanner sc = new Scanner(System.in);

        System.out.println("1 - Adicionar candidatos || 2 - Remover candidatos:  ");
        String tipoS = sc.nextLine();
        int tipo = Integer.parseInt(tipoS);
        int size = eleicao.sizeLista(choice);
        switch (tipo){
            case 1:
                System.out.println("---Adicionar Candidatos---");
                System.out.printf("\nPessoa(s) que pretende adicionar (1 - %d): \n", ri.sizePessoas());
                System.out.print(ri.showPessoas());
                System.out.println("0 - SAIR DE ADICIONAR CANDIDATOS");
                while(true){
                    String addS = sc.nextLine();
                    int add = Integer.parseInt(addS);
                    if (add <= ri.sizePessoas() && add > 0)
                        check = ri.addCandidateRMI(indx, 0,add - 1);
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
                    while(true) {
                        size = ri.getEleicoesFuturas().get(indx).sizeLista(choice);
                        System.out.printf("\nCandidato que pretende eliminar (1 - %d): \n", size);
                        System.out.print(ri.getEleicoesFuturas().get(indx).getListasCandidatas().get(choice).showCandidatos());
                        System.out.println("0 - SAIR DE REMOVER CANDIDATOS");
                        String deletS = sc.nextLine();
                        int delet = Integer.parseInt(deletS);
                        if (delet <= size && delet > 0)
                            check = ri.deleteCandidateRMI(indx, 0, delet - 1);
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


    public boolean gerirMesas(int indexE) throws RemoteException{
        boolean check = false;
        System.out.println("---Gerir Mesas---\n");
        if(ri.sizeMesas() != 0) {
            Scanner sc = new Scanner(System.in);
            System.out.println("\n1 - Adicionar mesa || 2 - Remover mesa:  ");
            String tipoS = sc.nextLine();
            int tipo = Integer.parseInt(tipoS);

            switch (tipo) {
                case 1:
                    //mostrar lista de mesas e associar eleicao + mesa & mesa + eleicao || gerirMesas(index)
                    //ri.criaMesaRMI(indexE,indexM);
                    System.out.printf("\n---Adicionar Mesa---\n");
                    System.out.printf("Mesa que pretende adicionar (1 - %d): \n",ri.sizeMesas());
                    System.out.println(ri.showMesas());
                    String addS = sc.nextLine();
                    int add = Integer.parseInt(addS);
                    check = ri.criaMesaRMI(indexE, add - 1);
                    break;

                case 2:
                    System.out.printf("\n---Remover Mesa---\n");
                    if(ri.sizeMesasEleicao(indexE) != 0) {
                        System.out.printf("Mesa que pretende remover (1 - %d): \n", ri.sizeMesasEleicao(indexE));
                        System.out.println(ri.showMesasEleicao(indexE));
                        String delS = sc.nextLine();
                        int del = Integer.parseInt(delS);
                        check = ri.deleteMesaRMI(indexE, del - 1);
                    }
                    else System.out.println("Impossivel Remover Mesas: Sem mesas adicionadas");
                    break;

                default:
                    break;
            }
        }
        else System.out.println("Impossivel Gerir Mesas: Não existem mesas criadas.");
        return check;
    }


    public void gerirListas(Eleicao eleicao, int indx) throws RemoteException{
        System.out.println("---Gerir Listas---");
        Scanner sc = new Scanner(System.in);
        System.out.println("1 - Criar lista || 2 - Remover Lista || 3 - Gerir Candidatos: ");
        String choiceS = sc.nextLine();
        int choice = Integer.parseInt(choiceS);
        switch (choice){
            case  1:
                createLista(eleicao,indx);
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
                    ri.eliminarListaCandidatos(indx,choice - 1);
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
                    gerirCandidatos(ri.getEleicoesFuturas().get(indx), choice - 1,indx);

                }
                break;
            default:
                System.out.println("Input inválido");
                break;
        }



    }

    public boolean gerirEleicao() throws RemoteException{
        boolean check = false;
        System.out.println("\n---Gerir Eleições---\n");
        Scanner sc = new Scanner(System.in);
        System.out.print(ri.showEleicoesFuturas());
        System.out.printf("Eleição que pretende gerir (1 - %d): ",ri.sizeEleicoesFuturas());
        String choiceS = sc.nextLine();
        int choice = Integer.parseInt(choiceS);

        if(choice <= ri.sizeEleicoesFuturas()){
            System.out.println("\n---Alterar propriedade da Eleição---");
            System.out.printf(ri.showEleicoesDetalhes(choice - 1));
            System.out.println("\n6 - Gerir listas");
            System.out.println("7 - Gerir Mesas");
            String answerS = sc.nextLine();
            int answer  = Integer.parseInt(answerS);
            if(answer == 6)
                gerirListas(ri.getEleicoesFuturas().get(choice - 1),choice - 1);
            else if (answer > 0 && answer <= 4){
                System.out.println("Alterar para: ");
                String change = sc.nextLine();
                //to-do pedir a data ao utilizador no formato dd-MM-yyyy HH:mm ou entao pedir data hora e minuto separado
                check = ri.changeEleicoesRMI(choice - 1, answer, change);
            }
            else if (answer == 7){
                gerirMesas(choice - 1);
            }
            else {
                System.out.printf("\nInput inválido.\n");
            }


        }
        else
            System.out.printf("\nInput inválido.\n");

        return check;
    }

    public void votoDetalhes() throws RemoteException{
        System.out.println("---Eleitor Local & Momento de Voto---");
        Scanner sc = new Scanner(System.in);
        System.out.printf("\nEscolha um eleitor (1 - %d): \n", ri.sizePessoas());
        System.out.print(ri.showPessoas());
        System.out.println("0 - SAIR DE ADICIONAR CANDIDATOS");
        String indxS = sc.nextLine();
        int indx = Integer.parseInt(indxS);
        if (indx <= ri.sizePessoas() && indx > 0)
            System.out.print(ri.showVotoDetalhesRMI(indx - 1));
        else {
            if(indx == 0)
                System.out.print("");
            else
                System.out.println("Candidato inválido.");

            }


    }



    public Lista createLista(Eleicao eleicao, int indx) throws RemoteException{
        System.out.println("---Criar Lista---");
        Scanner sc = new Scanner(System.in);

        System.out.println("Nome da lista: ");
        String nome = sc.nextLine();
        ri.createListaRMI(indx,nome);


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
                        ri.addCandidateRMI(indx, 0, add-1);
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

    //(11) ?
    /*
    public boolean mesasEstado() throws RemoteException{
        System.out.print(ri.showMesasEstados());
        return true;
    }

    //(12) ?
    public boolean numeroEleitores() throws RemoteException{
        System.out.print(ri.showMesasCount());
        return true;
    }
       */
    public void eleicaoEndedDetalhes() throws RemoteException{
        System.out.println("\n---Detalhes Eleicoes Terminadas---\n");
        System.out.print(ri.eleicoesEndedRMI());
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
            Scanner sc = new Scanner(System.in);
            int answer = -1;
            String answerS;

            while(answer != 0){
                System.out.println("\n---Menu Admin---\n");
                System.out.println("1 - Criar Eleição");
                System.out.println("2 - Criar Utilizador");
                System.out.println("3 - Gerir Eleições");
                System.out.println("4 - Eleições Passadas");
                System.out.println("5 - Local e Momento de Voto de um Eleitor");
                System.out.println("0 - SAIR");
                answerS = sc.nextLine();
                answer = Integer.parseInt(answerS);
                switch (answer){
                    case 1:
                        createEleicao();
                        break;
                    case 2:
                        registerUser();
                        break;
                    case 3:
                        if(ri.getEleicoes().size() == 0)
                            System.out.println("Não existem eleições que possam ser geridas.");
                        else
                            gerirEleicao();
                        break;
                    case 4:
                        eleicaoEndedDetalhes();
                        break;
                    case 5:
                        votoDetalhes();
                        break;
                    case 0:
                        break;
                    default:
                        System.out.println("Input Inválido.");
                }

            }

        } catch (RemoteException e) {
            e.printStackTrace();
        }

    }
}
