import java.io.Serializable;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Scanner;
import java.util.jar.JarOutputStream;

public class AdminTerminal extends UnicastRemoteObject implements AdminTerminalInterface, Serializable {
    private RmiInterface ri;

    public AdminTerminal() throws RemoteException {
        super();
        try {
            this.ri = (RmiInterface) Naming.lookup("rmi://localhost:7000/rmiServer");
        } catch (NotBoundException | MalformedURLException | RemoteException e) {
            e.printStackTrace();
        }
    }

    public boolean registerUser() throws RemoteException {
        boolean check = false;
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

        for (int i = 0; i <= 6; i++) {
            try {
                check = ri.createUserRMI(tipo, nome, uni, departamento, faculdade, numeroTelefonico, morada, cc, validade, password);
                break;
            } catch (RemoteException e) {
                try {
                    ri = (RmiInterface) LocateRegistry.getRegistry("localhost", 7000).lookup("rmiServer");
                } catch (NotBoundException | RemoteException ignored) {

                }
                if (i == 6) {
                    System.out.println("Impossivel conectar aos servidores RMI");
                    return false;
                }
            }
        }
        if (!check)
            System.out.println("Impossivel Criar Pessoa: Número de UC ou Número de CC já registado");
        return check;
    }


    public void createEleicao() throws RemoteException {
        System.out.println("\n---Criar Eleição---\n");
        Scanner sc = new Scanner(System.in);
        String titulo;
        boolean titulobool = false;
        do {
            System.out.println("Titulo: ");
            titulo = sc.nextLine();
            for (int i = 0; i <= 6; i++) {
                try {
                    titulobool=ri.checkNomeEleicao(titulo);
                    break;
                } catch (RemoteException e) {
                    try {
                        ri = (RmiInterface) LocateRegistry.getRegistry("localhost", 7000).lookup("rmiServer");
                    } catch (NotBoundException | RemoteException ignored) {

                    }
                    if (i == 6) {
                        System.out.println("Impossivel conectar aos servidores RMI");
                        return;
                    }
                }
            }
            if(!titulobool){
                System.out.println("Titulo de eleiçao já existe, por favor insira outro");
            }
        }while(!titulobool);

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

        System.out.println("Restringir eleição para que grupo de Pessoas?(1 - Estudantes || 2 - Docentes || 3 - Funcionários): ");
        int type = 0;
        while(type != 1 && type != 2 && type != 3) {
            String typeS = sc.nextLine();
            type = Integer.parseInt(typeS);
            if(type != 1 && type != 2 && type != 3)
                System.out.println("Numero Inválido: Tente de novo\n");
        }

        for (int i = 0; i <= 6; i++) {
            try {
                ri.createEleicaoRMI(titulo, descricao, startDate, startHour, startMinute, endDate, endHour, endMinute, "", type);
                break;
            } catch (RemoteException e) {
                try {
                    ri = (RmiInterface) LocateRegistry.getRegistry("localhost", 7000).lookup("rmiServer");
                } catch (NotBoundException | RemoteException ignored) {

                }
                if (i == 6) {
                    System.out.println("Impossivel conectar aos servidores RMI");
                    return;
                }
            }
        }



        System.out.println("Restringir departamentos que podem votar? 1 - Sim || 2 - Não");
        String  choiceS = sc.nextLine();
        int choice = Integer.parseInt(choiceS);
        boolean goOn = true;
        switch (choice){
            case 1:
                System.out.println("Digite os departamentos que quer adicionar:\nSAIR - SAIR DE ADICIONAR DEPARTAMENTOS\n");
                while (goOn){
                    String departamento = sc.nextLine();
                    if(!departamento.equals("SAIR") && !departamento.equals("sair")){
                        for (int i = 0; i <= 6; i++) {
                            try {
                                ri.addDepartamentos(titulo,departamento);
                                break;
                            } catch (RemoteException e) {
                                try {
                                    ri = (RmiInterface) LocateRegistry.getRegistry("localhost", 7000).lookup("rmiServer");
                                } catch (NotBoundException | RemoteException ignored) {

                                }
                                if (i == 6) {
                                    System.out.println("Impossivel conectar aos servidores RMI");
                                    return;
                                }
                            }
                        }
                    }
                    else{
                        goOn = false;
                    }
                }
                break;
            case 2:
                break;
            default:
                System.out.println("Input Inválido: Eleição criada sem departamentos.");
        }

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
                for (int i = 0; i <= 6; i++) {
                    try {
                        System.out.printf("\nPessoa(s) que pretende adicionar (1 - %d): \n", ri.sizePessoas());
                        break;
                    } catch (RemoteException e) {
                        try {
                            ri = (RmiInterface) LocateRegistry.getRegistry("localhost", 7000).lookup("rmiServer");
                        } catch (NotBoundException | RemoteException ignored) {

                        }
                        if (i == 6) {
                            System.out.println("Impossivel conectar aos servidores RMI");
                            return false;
                        }
                    }
                }
                for (int i = 0; i <= 6; i++) {
                    try {
                        System.out.print(ri.showPessoas());
                        break;
                    } catch (RemoteException e) {
                        try {
                            ri = (RmiInterface) LocateRegistry.getRegistry("localhost", 7000).lookup("rmiServer");
                        } catch (NotBoundException | RemoteException ignored) {

                        }
                        if (i == 6) {
                            System.out.println("Impossivel conectar aos servidores RMI");
                            return false;
                        }
                    }
                }
                System.out.println("0 - SAIR DE ADICIONAR CANDIDATOS");
                int sizeP = 0;
                while(true){
                    for (int i = 0; i <= 6; i++) {
                        try {
                            sizeP = ri.sizePessoas();
                            break;
                        } catch (RemoteException e) {
                            try {
                                ri = (RmiInterface) LocateRegistry.getRegistry("localhost", 7000).lookup("rmiServer");
                            } catch (NotBoundException | RemoteException ignored) {

                            }
                            if (i == 6) {
                                System.out.println("Impossivel conectar aos servidores RMI");
                                return false;
                            }
                        }
                    }
                    String addS = sc.nextLine();
                    int add = Integer.parseInt(addS);
                    if (add <= sizeP && add > 0) {
                        for (int i = 0; i <= 6; i++) {
                            try {
                                check = ri.addCandidateRMI(indx, choice, add - 1);
                                break;
                            } catch (RemoteException e) {
                                try {
                                    ri = (RmiInterface) LocateRegistry.getRegistry("localhost", 7000).lookup("rmiServer");
                                } catch (NotBoundException | RemoteException ignored) {

                                }
                                if (i == 6) {
                                    System.out.println("Impossivel conectar aos servidores RMI");
                                    return false;
                                }
                            }
                        }
                        if(!check)
                            System.out.println("Erro: Candidato já adicionado.");
                        else{
                            System.out.println("Candidato adicionado");
                        }
                    }

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
                        for (int i = 0; i <= 6; i++) {
                            try {
                                size = ri.getEleicoesFuturas().get(indx).sizeLista(choice);
                                break;
                            } catch (RemoteException e) {
                                try {
                                    ri = (RmiInterface) LocateRegistry.getRegistry("localhost", 7000).lookup("rmiServer");
                                } catch (NotBoundException | RemoteException ignored) {

                                }
                                if (i == 6) {
                                    System.out.println("Impossivel conectar aos servidores RMI");
                                    return false;
                                }
                            }
                        }
                        System.out.printf("\nCandidato que pretende eliminar (1 - %d): \n", size);
                        for (int i = 0; i <= 6; i++) {
                            try {
                                System.out.print(ri.getEleicoesFuturas().get(indx).getListasCandidatas().get(choice).showCandidatos());
                                break;
                            } catch (RemoteException e) {
                                try {
                                    ri = (RmiInterface) LocateRegistry.getRegistry("localhost", 7000).lookup("rmiServer");
                                } catch (NotBoundException | RemoteException ignored) {

                                }
                                if (i == 6) {
                                    System.out.println("Impossivel conectar aos servidores RMI");
                                    return false;
                                }
                            }
                        }
                        System.out.println("0 - SAIR DE REMOVER CANDIDATOS");
                        String deletS = sc.nextLine();
                        int delet = Integer.parseInt(deletS);
                        if (delet <= size && delet > 0) {
                            for (int i = 0; i <= 6; i++) {
                                try {
                                    check = ri.deleteCandidateRMI(indx, choice, delet - 1);
                                    break;
                                } catch (RemoteException e) {
                                    try {
                                        ri = (RmiInterface) LocateRegistry.getRegistry("localhost", 7000).lookup("rmiServer");
                                    } catch (NotBoundException | RemoteException ignored) {

                                    }
                                    if (i == 6) {
                                        System.out.println("Impossivel conectar aos servidores RMI");
                                        return false;
                                    }
                                }
                            }
                        }
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
        int sizeM = 0;
        for (int i = 0; i <= 6; i++) {
            try {
                sizeM = ri.sizeMesas();
                break;
            } catch (RemoteException e) {
                try {
                    ri = (RmiInterface) LocateRegistry.getRegistry("localhost", 7000).lookup("rmiServer");
                } catch (NotBoundException | RemoteException ignored) {

                }
                if (i == 6) {
                    System.out.println("Impossivel conectar aos servidores RMI");
                    return false;
                }
            }
        }
        if(sizeM != 0) {
            Scanner sc = new Scanner(System.in);
            System.out.println("\n1 - Adicionar mesa || 2 - Remover mesa:  ");
            String tipoS = sc.nextLine();
            int tipo = Integer.parseInt(tipoS);
            switch (tipo) {
                case 1:
                    System.out.print("\n---Adicionar Mesa---\n");
                    System.out.println("Mesas atualmente adicionadas:");
                    for (int i = 0; i <= 6; i++) {
                        try {
                            System.out.println(ri.showMesasEleicao(indexE));
                            break;
                        } catch (RemoteException e) {
                            try {
                                ri = (RmiInterface) LocateRegistry.getRegistry("localhost", 7000).lookup("rmiServer");
                            } catch (NotBoundException | RemoteException ignored) {

                            }
                            if (i == 6) {
                                System.out.println("Impossivel conectar aos servidores RMI");
                                return false;
                            }
                        }
                    }
                    for (int i = 0; i <= 6; i++) {
                        try {
                            System.out.printf("Mesa que pretende adicionar (1 - %d): \n",ri.sizeMesas());
                            break;
                        } catch (RemoteException e) {
                            try {
                                ri = (RmiInterface) LocateRegistry.getRegistry("localhost", 7000).lookup("rmiServer");
                            } catch (NotBoundException | RemoteException ignored) {

                            }
                            if (i == 6) {
                                System.out.println("Impossivel conectar aos servidores RMI");
                                return false;
                            }
                        }
                    }
                    for (int i = 0; i <= 6; i++) {
                        try {
                            System.out.println(ri.showMesas());
                            break;
                        } catch (RemoteException e) {
                            try {
                                ri = (RmiInterface) LocateRegistry.getRegistry("localhost", 7000).lookup("rmiServer");
                            } catch (NotBoundException | RemoteException ignored) {

                            }
                            if (i == 6) {
                                System.out.println("Impossivel conectar aos servidores RMI");
                                return false;
                            }
                        }
                    }
                    String addS = sc.nextLine();
                    int add = Integer.parseInt(addS);
                    for (int i = 0; i <= 6; i++) {
                        try {
                            check = ri.criaMesaRMI(indexE, add - 1);
                            break;
                        } catch (RemoteException e) {
                            try {
                                ri = (RmiInterface) LocateRegistry.getRegistry("localhost", 7000).lookup("rmiServer");
                            } catch (NotBoundException | RemoteException ignored) {

                            }
                            if (i == 6) {
                                System.out.println("Impossivel conectar aos servidores RMI");
                                return false;
                            }
                        }
                    }
                    break;

                case 2:
                    System.out.print("\n---Remover Mesa---\n");
                    int sizeMeleicao = 0;
                    for (int i = 0; i <= 6; i++) {
                        try {
                            sizeMeleicao = ri.sizeMesasEleicao(indexE);
                            break;
                        } catch (RemoteException e) {
                            try {
                                ri = (RmiInterface) LocateRegistry.getRegistry("localhost", 7000).lookup("rmiServer");
                            } catch (NotBoundException | RemoteException ignored) {

                            }
                            if (i == 6) {
                                System.out.println("Impossivel conectar aos servidores RMI");
                                return false;
                            }
                        }
                    }
                    if(sizeMeleicao != 0) {
                        System.out.printf("Mesa que pretende remover (1 - %d): \n", ri.sizeMesasEleicao(indexE));
                        for (int i = 0; i <= 6; i++) {
                            try {
                                System.out.println(ri.showMesasEleicao(indexE));
                                break;
                            } catch (RemoteException e) {
                                try {
                                    ri = (RmiInterface) LocateRegistry.getRegistry("localhost", 7000).lookup("rmiServer");
                                } catch (NotBoundException | RemoteException ignored) {

                                }
                                if (i == 6) {
                                    System.out.println("Impossivel conectar aos servidores RMI");
                                    return false;
                                }
                            }
                        }
                        String delS = sc.nextLine();
                        int del = Integer.parseInt(delS);
                        for (int i = 0; i <= 6; i++) {
                            try {
                                check = ri.deleteMesaRMI(indexE, del - 1);
                                break;
                            } catch (RemoteException e) {
                                try {
                                    ri = (RmiInterface) LocateRegistry.getRegistry("localhost", 7000).lookup("rmiServer");
                                } catch (NotBoundException | RemoteException ignored) {

                                }
                                if (i == 6) {
                                    System.out.println("Impossivel conectar aos servidores RMI");
                                    return false;
                                }
                            }
                        }
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
                    for (int i = 0; i <= 6; i++) {
                        try {
                            ri.eliminarListaCandidatos(indx,choice - 1);
                            break;
                        } catch (RemoteException e) {
                            try {
                                ri = (RmiInterface) LocateRegistry.getRegistry("localhost", 7000).lookup("rmiServer");
                            } catch (NotBoundException | RemoteException ignored) {

                            }
                            if (i == 6) {
                                System.out.println("Impossivel conectar aos servidores RMI");
                                return;
                            }
                        }
                    }
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
                    for (int i = 0; i <= 6; i++) {
                        try {
                            gerirCandidatos(ri.getEleicoesFuturas().get(indx), choice - 1,indx);
                            break;
                        } catch (RemoteException e) {
                            try {
                                ri = (RmiInterface) LocateRegistry.getRegistry("localhost", 7000).lookup("rmiServer");
                            } catch (NotBoundException | RemoteException ignored) {

                            }
                            if (i == 6) {
                                System.out.println("Impossivel conectar aos servidores RMI");
                                return;
                            }
                        }
                    }

                }
                break;
            default:
                System.out.println("Input inválido");
                break;
        }



    }

    public String nomeHere(Eleicao eleicao){
        String str = "type|item_list;item_count|" + eleicao.getListasCandidatas().size();
        int i = 0;
        for(Lista l : eleicao.getListasCandidatas()){
            str += ";item_" + i + "_name|";
            for(Pessoa p : l.getMembros()){
                str += p.getNome() + "\n";
            }
            i++;
        }
        return str;
    }

    public boolean gerirDepartamentos(int indexE) throws RemoteException{
        System.out.println("---Gerir Departamentos---\n");
        int sizeD = 0;
        for (int i = 0; i <= 6; i++) {
            try {
                sizeD = ri.getEleicoesFuturas().get(indexE).sizeDepartamentos();
                break;
            } catch (RemoteException e) {
                try {
                    ri = (RmiInterface) LocateRegistry.getRegistry("localhost", 7000).lookup("rmiServer");
                } catch (NotBoundException | RemoteException ignored) {

                }
                if (i == 6) {
                    System.out.println("Impossivel conectar aos servidores RMI");
                    return false;
                }
            }
        }
        if(sizeD != 0) {
            Scanner sc = new Scanner(System.in);
            System.out.println("\n1 - Adicionar Departamento || 2 - Remover Departamento:  ");
            String tipoS = sc.nextLine();
            int tipo = Integer.parseInt(tipoS);

            switch (tipo) {
                case 1:
                    System.out.print("\n---Adicionar Departamento---\n");
                    String addS = sc.nextLine();
                    for (int i = 0; i <= 6; i++) {
                        try {
                            ri.addDepartamentos(indexE,addS);
                            break;
                        } catch (RemoteException e) {
                            try {
                                ri = (RmiInterface) LocateRegistry.getRegistry("localhost", 7000).lookup("rmiServer");
                            } catch (NotBoundException | RemoteException ignored) {

                            }
                            if (i == 6) {
                                System.out.println("Impossivel conectar aos servidores RMI");
                                return false;
                            }
                        }
                    }
                    break;

                case 2:
                    System.out.print("\n---Remover Departamento---\n");
                    for (int i = 0; i <= 6; i++) {
                        try {
                            sizeD = ri.sizeDepartamentos(indexE);
                            break;
                        } catch (RemoteException e) {
                            try {
                                ri = (RmiInterface) LocateRegistry.getRegistry("localhost", 7000).lookup("rmiServer");
                            } catch (NotBoundException | RemoteException ignored) {

                            }
                            if (i == 6) {
                                System.out.println("Impossivel conectar aos servidores RMI");
                                return false;
                            }
                        }
                    }
                    if(sizeD != 0) {
                        System.out.printf("Departamento que pretende remover (1 - %d): \n", ri.sizeDepartamentos(indexE));
                        for (int i = 0; i <= 6; i++) {
                            try {
                                System.out.println(ri.showDepartamentos(indexE));
                                break;
                            } catch (RemoteException e) {
                                try {
                                    ri = (RmiInterface) LocateRegistry.getRegistry("localhost", 7000).lookup("rmiServer");
                                } catch (NotBoundException | RemoteException ignored) {

                                }
                                if (i == 6) {
                                    System.out.println("Impossivel conectar aos servidores RMI");
                                    return false;
                                }
                            }
                        }
                        System.out.println("0 - SAIR DE REMOVER DEPARTAMENTOS");
                        String delS = sc.nextLine();
                        int del = Integer.parseInt(delS);
                        if(del == 0)
                            break;
                        else{
                            for (int i = 0; i <= 6; i++) {
                                try {
                                    ri.deleteDepartamentos(indexE, del - 1);
                                    break;
                                } catch (RemoteException e) {
                                    try {
                                        ri = (RmiInterface) LocateRegistry.getRegistry("localhost", 7000).lookup("rmiServer");
                                    } catch (NotBoundException | RemoteException ignored) {

                                    }
                                    if (i == 6) {
                                        System.out.println("Impossivel conectar aos servidores RMI");
                                        return false;
                                    }
                                }
                            }
                        }

                    }
                    else System.out.println("Impossivel Remover Departamento: Sem Departamentos adicionados");
                    break;

                default:
                    break;
            }
        }
        else System.out.println("Impossivel Gerir Departamento: Não existem Departamentos criados.");
        return false;
    }

    public boolean gerirEleicao() throws RemoteException{
        boolean check = false;
        System.out.println("\n---Gerir Eleições---\n");
        Scanner sc = new Scanner(System.in);
        for (int i = 0; i <= 6; i++) {
            try {
                System.out.print(ri.showEleicoesFuturas());
                break;
            } catch (RemoteException e) {
                try {
                    ri = (RmiInterface) LocateRegistry.getRegistry("localhost", 7000).lookup("rmiServer");
                } catch (NotBoundException | RemoteException ignored) {

                }
                if (i == 6) {
                    System.out.println("Impossivel conectar aos servidores RMI");
                    return false;
                }
            }
        }
        int sizeEleF = 0;
        for (int i = 0; i <= 6; i++) {
            try {
                sizeEleF = ri.sizeEleicoesFuturas();
                break;
            } catch (RemoteException e) {
                try {
                    ri = (RmiInterface) LocateRegistry.getRegistry("localhost", 7000).lookup("rmiServer");
                } catch (NotBoundException | RemoteException ignored) {

                }
                if (i == 6) {
                    System.out.println("Impossivel conectar aos servidores RMI");
                    return false;
                }
            }
        }
        if(sizeEleF == 0){
            System.out.println("Não existem eleições para gerir.");
            return  false;
        }
        for (int i = 0; i <= 6; i++) {
            try {
                System.out.printf("Eleição que pretende gerir (1 - %d): ",ri.sizeEleicoesFuturas());
                break;
            } catch (RemoteException e) {
                try {
                    ri = (RmiInterface) LocateRegistry.getRegistry("localhost", 7000).lookup("rmiServer");
                } catch (NotBoundException | RemoteException ignored) {

                }
                if (i == 6) {
                    System.out.println("Impossivel conectar aos servidores RMI");
                    return false;
                }
            }
        }
        String choiceS = sc.nextLine();
        int choice = Integer.parseInt(choiceS);
        for (int i = 0; i <= 6; i++) {
            try {
                sizeEleF = ri.sizeEleicoesFuturas();
                break;
            } catch (RemoteException e) {
                try {
                    ri = (RmiInterface) LocateRegistry.getRegistry("localhost", 7000).lookup("rmiServer");
                } catch (NotBoundException | RemoteException ignored) {

                }
                if (i == 6) {
                    System.out.println("Impossivel conectar aos servidores RMI");
                    return false;
                }
            }
        }
        if(choice <= sizeEleF){
            System.out.println("\n---Alterar propriedade da Eleição---");
            for (int i = 0; i <= 6; i++) {
                try {
                    System.out.print(ri.showEleicoesDetalhes(choice - 1));
                    break;
                } catch (RemoteException e) {
                    try {
                        ri = (RmiInterface) LocateRegistry.getRegistry("localhost", 7000).lookup("rmiServer");
                    } catch (NotBoundException | RemoteException ignored) {

                    }
                    if (i == 6) {
                        System.out.println("Impossivel conectar aos servidores RMI");
                        return false;
                    }
                }
            }
            System.out.println("\n6 - Gerir listas");
            System.out.println("7 - Gerir Mesas");
            System.out.println("8 - Gerir Departamentos");
            String answerS = sc.nextLine();
            int answer  = Integer.parseInt(answerS);
            if(answer == 6){
                for (int i = 0; i <= 6; i++) {
                    try {
                        gerirListas(ri.getEleicoesFuturas().get(choice - 1),choice - 1);
                        break;
                    } catch (RemoteException e) {
                        try {
                            ri = (RmiInterface) LocateRegistry.getRegistry("localhost", 7000).lookup("rmiServer");
                        } catch (NotBoundException | RemoteException ignored) {

                        }
                        if (i == 6) {
                            System.out.println("Impossivel conectar aos servidores RMI");
                            return false;
                        }
                    }
                }
            }
            else if (answer > 0 && answer <= 4){
                System.out.println("Alterar para: ");
                String change = sc.nextLine();
                //to-do pedir a data ao utilizador no formato dd-MM-yyyy HH:mm ou entao pedir data hora e minuto separado
                for (int i = 0; i <= 6; i++) {
                    try {
                        check = ri.changeEleicoesRMI(choice - 1, answer, change);
                        break;
                    } catch (RemoteException e) {
                        try {
                            ri = (RmiInterface) LocateRegistry.getRegistry("localhost", 7000).lookup("rmiServer");
                        } catch (NotBoundException | RemoteException ignored) {

                        }
                        if (i == 6) {
                            System.out.println("Impossivel conectar aos servidores RMI");
                            return false;
                        }
                    }
                }
            }
            else if (answer == 7){
                gerirMesas(choice - 1);
            }
            else if(answer == 8){
                gerirDepartamentos(choice - 1);
            }
            else {
                System.out.print("\nInput inválido.\n");
            }

        }
        else
            System.out.printf("\nInput inválido.\n");

        return check;
    }

    public void votoDetalhes() throws RemoteException{
        System.out.println("---Eleitor Local & Momento de Voto---");
        Scanner sc = new Scanner(System.in);
        for (int i = 0; i <= 6; i++) {
            try {
                System.out.printf("\nEscolha um eleitor (1 - %d): \n", ri.sizePessoas());
                break;
            } catch (RemoteException e) {
                try {
                    ri = (RmiInterface) LocateRegistry.getRegistry("localhost", 7000).lookup("rmiServer");
                } catch (NotBoundException | RemoteException ignored) {

                }
                if (i == 6) {
                    System.out.println("Impossivel conectar aos servidores RMI");
                    return;
                }
            }
        }
        for (int i = 0; i <= 6; i++) {
            try {
                System.out.print(ri.showPessoas());
                break;
            } catch (RemoteException e) {
                try {
                    ri = (RmiInterface) LocateRegistry.getRegistry("localhost", 7000).lookup("rmiServer");
                } catch (NotBoundException | RemoteException ignored) {

                }
                if (i == 6) {
                    System.out.println("Impossivel conectar aos servidores RMI");
                    return;
                }
            }
        }
        System.out.println("0 - SAIR DE ADICIONAR CANDIDATOS");
        String indxS = sc.nextLine();
        int indx = Integer.parseInt(indxS);
        int sizeP = 0;
        for (int i = 0; i <= 6; i++) {
            try {
                sizeP = ri.sizePessoas();
                break;
            } catch (RemoteException e) {
                try {
                    ri = (RmiInterface) LocateRegistry.getRegistry("localhost", 7000).lookup("rmiServer");
                } catch (NotBoundException | RemoteException ignored) {

                }
                if (i == 6) {
                    System.out.println("Impossivel conectar aos servidores RMI");
                    return;
                }
            }
        }
        if (indx <= sizeP && indx > 0){
            for (int i = 0; i <= 6; i++) {
                try {
                    System.out.print(ri.showVotoDetalhesRMI(indx - 1));
                    break;
                } catch (RemoteException e) {
                    try {
                        ri = (RmiInterface) LocateRegistry.getRegistry("localhost", 7000).lookup("rmiServer");
                    } catch (NotBoundException | RemoteException ignored) {

                    }
                    if (i == 6) {
                        System.out.println("Impossivel conectar aos servidores RMI");
                        return;
                    }
                }
            }
        }
        else {
            if(indx == 0)
                System.out.print("");
            else
                System.out.println("Candidato inválido.");

            }


    }


    public Lista createLista(Eleicao eleicao, int indx) throws RemoteException {
        System.out.println("---Criar Lista---");
        Scanner sc = new Scanner(System.in);
        System.out.println("Nome da lista: ");
        String nome = sc.nextLine();
        boolean check = false;
        for (int i = 0; i <= 6; i++) {
            try {
                check = ri.createListaRMI(indx, nome);
                break;
            } catch (RemoteException e) {
                try {
                    ri = (RmiInterface) LocateRegistry.getRegistry("localhost", 7000).lookup("rmiServer");
                } catch (NotBoundException | RemoteException ignored) {

                }
                if (i == 6) {
                    System.out.println("Impossivel conectar aos servidores RMI");
                    return null;
                }
            }
        }
        if (!check) {
            System.out.println("Erro: Lista já criada com esse nome");
        } else {
                for (int i = 0; i <= 6; i++) {
                    try {
                        System.out.printf("Pessoa(s) que pretende adicionar (1 - %d): \n", ri.sizePessoas());
                        break;
                    } catch (RemoteException e) {
                        try {
                            ri = (RmiInterface) LocateRegistry.getRegistry("localhost", 7000).lookup("rmiServer");
                        } catch (NotBoundException | RemoteException ignored) {

                        }
                        if (i == 6) {
                            System.out.println("Impossivel conectar aos servidores RMI");
                            return null;
                        }
                    }
                }
                for (int i = 0; i <= 6; i++) {
                    try {
                        System.out.print(ri.showPessoas());
                        break;
                    } catch (RemoteException e) {
                        try {
                            ri = (RmiInterface) LocateRegistry.getRegistry("localhost", 7000).lookup("rmiServer");
                        } catch (NotBoundException | RemoteException ignored) {

                        }
                        if (i == 6) {
                            System.out.println("Impossivel conectar aos servidores RMI");
                            return null;
                        }
                    }
                }
                System.out.println("0 - SAIR DE ADICIONAR CANDIDATOS");
                while (true) {
                    String addS = sc.nextLine();
                    int add = Integer.parseInt(addS);
                    int sizeP = 0;
                    for (int i = 0; i <= 6; i++) {
                        try {
                            sizeP = ri.sizePessoas();
                            break;
                        } catch (RemoteException e) {
                            try {
                                ri = (RmiInterface) LocateRegistry.getRegistry("localhost", 7000).lookup("rmiServer");
                            } catch (NotBoundException | RemoteException ignored) {

                            }
                            if (i == 6) {
                                System.out.println("Impossivel conectar aos servidores RMI");
                                return null;
                            }
                        }
                    }
                    if (add <= sizeP && add > 0) {
                        for (int i = 0; i <= 6; i++) {
                            try {
                                check = ri.addCandidateRMI(indx, 0, add - 1);
                                break;
                            } catch (RemoteException e) {
                                try {
                                    ri = (RmiInterface) LocateRegistry.getRegistry("localhost", 7000).lookup("rmiServer");
                                } catch (NotBoundException | RemoteException ignored) {

                                }
                                if (i == 6) {
                                    System.out.println("Impossivel conectar aos servidores RMI");
                                    return null;
                                }
                            }
                        }
                        if(check)
                            System.out.println("Candidato adicionado.");
                        else
                            System.out.println("Erro: Candidato já adicionado");

                    } else {
                        if (add == 0)
                            break;
                        System.out.println("Candidato inválido.");
                        break;
                    }
                }
        }
        return null;
    }


    public void eleicaoEndedDetalhes() throws RemoteException{
        System.out.println("\n---Detalhes Eleicoes Terminadas---\n");
        for (int i = 0; i <= 6; i++) {
            try {
                System.out.print(ri.eleicoesEndedRMI());
                break;
            } catch (RemoteException e) {
                try {
                    ri = (RmiInterface) LocateRegistry.getRegistry("localhost", 7000).lookup("rmiServer");
                } catch (NotBoundException | RemoteException ignored) {

                }
                if (i == 6) {
                    System.out.println("Impossivel conectar aos servidores RMI");
                    return;
                }
            }
        }
    }



    public static void main(String[] args) throws MalformedURLException, RemoteException, NotBoundException {

        AdminTerminal terminal = new AdminTerminal();


        terminal.ri.subscribe((AdminTerminalInterface) terminal );


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
                        terminal.createEleicao();
                        break;
                    case 2:
                        terminal.registerUser();
                        break;
                    case 3:
                        if(terminal.ri.getEleicoes().size() == 0)
                            System.out.println("Não existem eleições que possam ser geridas.");
                        else
                            terminal.gerirEleicao();
                        break;
                    case 4:
                        terminal.eleicaoEndedDetalhes();
                        break;
                    case 5:
                        terminal.votoDetalhes();
                        break;
                    case 0:
                        return;
                    default:
                        System.out.println("Input Inválido.");
                }

            }

        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }


    public void voteUpdate(String departamento, int count) throws RemoteException{
        System.out.println("[UPDATE] New Vote in department "+ departamento + ", total count in that table: " + count);
    }

    public void tableUpdate(String dep) throws RemoteException{
        System.out.println("[UPDATE] Table at "+dep+" connected");
    }

    public void tableDisconnectedUpdate(String dep) throws RemoteException{
        System.out.println("[UPDATE] Table at "+ dep+" exited gracefully");
    }

    public void terminalUpdate(String departamento) throws RemoteException{
        System.out.println("[UPDATE] New terminal connected at the table at " + departamento);
    }
}
