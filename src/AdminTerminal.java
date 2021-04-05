import java.io.Serializable;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;
import java.security.SecureRandom;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;
import java.util.jar.JarOutputStream;

public class AdminTerminal extends UnicastRemoteObject implements AdminTerminalInterface, Serializable {
    private RmiInterface ri;

    public AdminTerminal() throws RemoteException {
        super();
        for (int i = 0; i <= 6; i++) {
            try {
                ri = (RmiInterface) Naming.lookup("rmi://localhost:7000/rmiServer");
                break;
            } catch (RemoteException | NotBoundException | MalformedURLException e) {
                try {
                    ri = (RmiInterface) LocateRegistry.getRegistry("localhost", 7000).lookup("rmiServer");
                } catch (NotBoundException | RemoteException notBoundException) {
                }
                if (i == 6) {
                    System.out.println("Impossivel conectar aos servidores RMI");
                    return;
                }
            }
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
        int startHour = 0;
        int startMinute = 0;
        String startDate = "";
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
        while (true) {
            startDate = sc.nextLine();
            if(isParsableDate(startDate))
                break;
            else System.out.println("[Formato de Data Inválido: Tente de Novo]");
        }

        System.out.println("Hora de Inicio: ");
        while(true) {
            String startH = sc.nextLine();
            if (isParsable(startH)) {
                startHour = Integer.parseInt(startH);
                break;
            }
            else
                System.out.println("[Input Inválido: Tente de Novo]");
        }

        System.out.println("Minuto de Inicio: ");
        while(true) {
            String startM = sc.nextLine();
            if (isParsable(startM)) {
                startMinute = Integer.parseInt(startM);
                break;
            }
            else
                System.out.println("[Input Inválido: Tente de Novo]");
        }



        System.out.println("Data de Fim (dd-MM-yyyy): ");
        String endDate = "";
        while (true) {
            endDate = sc.nextLine();
            if(isParsableDate(endDate))
                break;
            else System.out.println("[Formato de Data Inválido: Tente de Novo]");
        }

        System.out.println("Hora de Fim: ");
        int endHour = 0;
        while(true) {
            String endH = sc.nextLine();
            if (isParsable(endH)) {
                endHour = Integer.parseInt(endH);
                break;
            }
            else
                System.out.println("[Input Inválido: Tente de Novo]");
        }

        System.out.println("Minuto de Fim: ");
        int endMinute = 0;
        while(true) {
            String endM = sc.nextLine();
            if (isParsable(endM)) {
                endMinute = Integer.parseInt(endM);
                break;
            }
            else
                System.out.println("[Input Inválido: Tente de Novo]");
        }

        System.out.println("Restringir eleição para que grupo de Pessoas?(1 - Estudantes || 2 - Docentes || 3 - Funcionários): ");
        int type = 0;
        while(type != 1 && type != 2 && type != 3) {
            while(true) {
                String typeS = sc.nextLine();
                if (isParsable(typeS)) {
                    type = Integer.parseInt(typeS);
                    break;
                }
                else
                    System.out.println("[Input Inválido: Tente de Novo]");
            }
            if(type != 1 && type != 2 && type != 3)
                System.out.println("[Input Inválido: Tente de novo]");
        }

        for (int i = 0; i <= 6; i++) {
            try {
                if(ri.createEleicaoRMI(titulo, descricao, startDate, startHour, startMinute, endDate, endHour, endMinute, "", type) == null) {
                    System.out.println("Erro: Eleição não criada - Datas Inválidas");
                    return;
                }
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
        int choice = 0;
        while(true) {
            String  choiceS = sc.nextLine();
            if (isParsable(choiceS)) {
                choice = Integer.parseInt(choiceS);
                if(choice != 1 && choice != 2)
                    System.out.println("[Input Inválido: Tente de Novo]");
                else
                    break;
            }
            else
                System.out.println("[Input Inválido: Tente de Novo]");
        }
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
        }

    }

    public boolean gerirCandidatos(Eleicao eleicao, int choice, int indx) throws RemoteException {
        boolean check = false;
        System.out.println("---Gerir Candidatos---");
        Scanner sc = new Scanner(System.in);

        System.out.println("1 - Adicionar candidatos || 2 - Remover candidatos:  ");
        int tipo = 0;
        while (true) {
            String tipoS = sc.nextLine();
            if (isParsable(tipoS)) {
                tipo = Integer.parseInt(tipoS);
                if (tipo != 1 && tipo != 2)
                    System.out.println("[Input Inválido: Tente de Novo]");
                else
                    break;
            } else
                System.out.println("[Input Inválido: Tente de Novo]");
        }

        int size = eleicao.sizeLista(choice);
        switch (tipo) {
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
                while (true) {
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
                    int add = 0;
                    while (true) {
                        String addS = sc.nextLine();
                        if (isParsable(addS)) {
                            add = Integer.parseInt(addS);
                            if (add <= sizeP && add >= 0)
                                break;
                            else
                                System.out.println("[Input Inválido: Tente de Novo]");
                        } else
                            System.out.println("[Input Inválido: Tente de Novo]");
                    }

                    if (add != 0) {
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
                        if (!check)
                            System.out.println("Erro: Candidato já adicionado.");
                        else {
                            System.out.println("Candidato adicionado");
                        }
                    } else {
                        return true;
                    }
                }


            case 2:
                System.out.println("---Remover Candidatos---");
                if (size == 0) {
                    System.out.println("Lista Vazia - Impossivel Remover Candidatos");
                }
                else {
                    while (true) {
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
                        int delet = 0;

                        while (true) {
                            String deletS = sc.nextLine();
                            if (isParsable(deletS)) {
                                delet = Integer.parseInt(deletS);
                                if (delet <= size && delet >= 0) {
                                    break;
                                } else {
                                    System.out.println("[Input Inválido: Tente de Novo]");
                                }
                            } else
                                System.out.println("[Input Inválido: Tente de Novo]");
                        }

                        if(delet != 0) {
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
                        else{
                            return true;
                        }
                    }
                }
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
            int tipo = 0;
            while (true) {
                String tipoS = sc.nextLine();
                if (isParsable(tipoS)) {
                    tipo = Integer.parseInt(tipoS);
                    if (tipo == 1 || tipo == 2) {
                        break;
                    } else {
                        System.out.println("[Input Inválido: Tente de Novo]");
                    }
                } else
                    System.out.println("[Input Inválido: Tente de Novo]");
            }


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
                    System.out.println("0 - SAIR DE ADICIONAR MESA");
                    int add = 0;
                    while (true) {
                        String addS = sc.nextLine();
                        if (isParsable(addS)) {
                            add = Integer.parseInt(addS);
                            if (add <= sizeM && add >= 0) {
                                break;
                            } else {
                                System.out.println("[Input Inválido: Tente de Novo]");
                            }
                        } else
                            System.out.println("[Input Inválido: Tente de Novo]");
                    }

                    if(add != 0) {
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
                    }
                    else{
                        return true;
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
                        System.out.printf("Mesa que pretende remover (1 - %d): \n", sizeMeleicao);
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
                        int del = 0;
                        while (true) {
                            String delS = sc.nextLine();
                            if (isParsable(delS)) {
                                del = Integer.parseInt(delS);
                                if (del <= sizeMeleicao && del >= 0) {
                                    break;
                                } else {
                                    System.out.println("[Input Inválido: Tente de Novo]");
                                }
                            } else
                                System.out.println("[Input Inválido: Tente de Novo]");
                        }

                        if(del != 0) {
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
                        else{
                            return true;
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
        String  choiceS;
        int choice = 0;
        while (true) {
            choiceS = sc.nextLine();
            if (isParsable(choiceS)) {
                choice = Integer.parseInt(choiceS);
                if (choice == 1 || choice == 2 || choice == 3 || choice == 0)
                    break;
                else
                    System.out.println("[Input Inválido: Tente de Novo]");
            } else
                System.out.println("[Input Inválido: Tente de Novo]");
        }


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
                    System.out.println("0 - SAIR ELIMINAR LISTA");
                    while (true) {
                        choiceS = sc.nextLine();
                        if (isParsable(choiceS)) {
                            choice = Integer.parseInt(choiceS);
                            if (choice <= size && choice >= 0)
                                break;
                            else
                                System.out.println("[Input Inválido: Tente de Novo]");
                        } else
                            System.out.println("[Input Inválido: Tente de Novo]");
                    }
                    if(choice != 0) {
                        for (int i = 0; i <= 6; i++) {
                            try {
                                ri.eliminarListaCandidatos(indx, choice - 1);
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
                        return;
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
                    while (true) {
                        choiceS = sc.nextLine();
                        if (isParsable(choiceS)) {
                            choice = Integer.parseInt(choiceS);
                            if (choice <= size && choice >= 0)
                                break;
                            else
                                System.out.println("[Input Inválido: Tente de Novo]");
                        } else
                            System.out.println("[Input Inválido: Tente de Novo]");
                    }
                    if(choice != 0) {
                        for (int i = 0; i <= 6; i++) {
                            try {
                                gerirCandidatos(ri.getEleicoesFuturas().get(indx), choice - 1, indx);
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
                        return;
                    }

                }
                break;
            default:
                System.out.println("Input inválido");
                break;
        }



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

        Scanner sc = new Scanner(System.in);
        if(sizeD == 0)
            System.out.println("[Sem Departamentos Adicionados: Eleição permitida a todos os votantes]");
        System.out.println("1 - Adicionar Departamento || 2 - Remover Departamento:  ");
        int tipo = 0;

        while (true) {
            String tipoS = sc.nextLine();
            if (isParsable(tipoS)) {
                tipo = Integer.parseInt(tipoS);
                if (tipo == 1 || tipo == 2 || tipo == 0)
                    break;
                else
                    System.out.println("[Input Inválido: Tente de Novo]");
            } else
                System.out.println("[Input Inválido: Tente de Novo]");
        }

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
                    System.out.printf("Departamento que pretende remover (1 - %d): \n", sizeD);
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
                    int del = 0;
                    while (true) {
                        String delS = sc.nextLine();
                        if (isParsable(delS)) {
                            del = Integer.parseInt(delS);
                            if (del <= sizeD && del >= 0)
                                break;
                            else
                                System.out.println("[Input Inválido: Tente de Novo]");
                        } else
                            System.out.println("[Input Inválido: Tente de Novo]");
                    }

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
        return true;
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

        System.out.printf("Eleição que pretende gerir (1 - %d): \n",sizeEleF);

        int choice = 0;
        while (true) {
            String choiceS = sc.nextLine();
            if (isParsable(choiceS)) {
                choice = Integer.parseInt(choiceS);
                if (choice <= sizeEleF && choice >= 0)
                    break;
                else
                    System.out.println("[Input Inválido: Tente de Novo]");
            } else
                System.out.println("[Input Inválido: Tente de Novo]");
        }

        if(choice != 0){
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
            System.out.println("\n5 - Gerir listas");
            System.out.println("6 - Gerir Mesas");
            System.out.println("7 - Gerir Departamentos");
            int answer = 0;
            while (true) {
                String answerS = sc.nextLine();
                if (isParsable(answerS)) {
                    answer  = Integer.parseInt(answerS);
                    if (answer <= 7 && answer >= 0)
                        break;
                    else
                        System.out.println("[Input Inválido: Tente de Novo]");
                } else
                    System.out.println("[Input Inválido: Tente de Novo]");
            }

            String change;
            if(answer == 5){
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
                if(answer == 3 || answer == 4){
                    while (true) {
                        change = sc.nextLine();
                        if(isParsableDate_v2(change))
                            break;
                        else System.out.println("[Formato de Data Inválido: Tente de Novo]");
                    }
                }
                else {
                    change = sc.nextLine();
                }

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
            else if (answer == 6){
                gerirMesas(choice - 1);
            }
            else if(answer == 7){
                gerirDepartamentos(choice - 1);
            }
            else {
                return true;
            }

        }
        else
            return true;

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
        System.out.println("0 - SAIR DE ADICIONAR DETALHES");
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
        int indx = 0;
        while (true) {
            String indxS = sc.nextLine();
            if (isParsable(indxS)) {
                indx = Integer.parseInt(indxS);
                if (indx <= sizeP && indx >= 0)
                    break;
                else
                    System.out.println("[Input Inválido: Tente de Novo]");
            } else
                System.out.println("[Input Inválido: Tente de Novo]");
        }

        if(indx != 0) {
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
                int add = 0;
                while (true) {
                    while (true) {
                        String addS = sc.nextLine();
                        if (isParsable(addS)) {
                            add = Integer.parseInt(addS);
                            if (add <= sizeP && add >= 0)
                                break;
                            else
                                System.out.println("[Input Inválido: Tente de Novo]");
                        } else
                            System.out.println("[Input Inválido: Tente de Novo]");
                    }


                    if (add != 0) {
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
                        return null;
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

    public static boolean isParsable(String input) {
        try {
            Integer.parseInt(input);
            return true;
        } catch (final NumberFormatException e) {
            return false;
        }
    }

    public static boolean isParsableDate(String input){
        try {
            Date startDate = new SimpleDateFormat("dd-MM-yyyy").parse(input);
            return true;
        } catch (ParseException e) {
            return  false;
        }
    }

    public static boolean isParsableDate_v2(String input){
        try {
            Date startDate = new SimpleDateFormat("dd-MM-yyyy hh:mm").parse(input);
            return true;
        } catch (ParseException e) {
            return  false;
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

                while (true) {
                    answerS = sc.nextLine();
                    if (isParsable(answerS)) {
                        answer = Integer.parseInt(answerS);
                        if (answer <= 5 && answer >= 0)
                            break;
                        else
                            System.out.println("[Input Inválido: Tente de Novo]");
                    } else
                        System.out.println("[Input Inválido: Tente de Novo]");
                }

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
