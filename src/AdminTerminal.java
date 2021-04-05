import java.io.IOException;
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
    private String rmiport;
    private String rminame;
    private String registry;
    private String callbackip;
    //construtor do admin terminal - lookup do rmi server
    public AdminTerminal() throws RemoteException {
        super();

        
        //linha 23 - 27: tenta 6 vezes dar lookup no RmiServer
        // Caso não encontre, o Rmi seguinte passa a principal
        //Este pedaço de código é utilizado ao longo do programa quando é necessário chamadas ao RmiServer
        GetPropertyValues properties = new GetPropertyValues();
        try {
            properties.setPropValues();
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.rmiport = properties.getRmiport();
        this.rminame = properties.getRminame();
        this.registry = properties.getRegistry();
        this.callbackip = properties.getCallbackip();
        System.out.println(registry);


        for (int i = 0; i <= 6; i++) {
            try {
                System.setProperty("java.rmi.server.hostname", callbackip);
                ri = (RmiInterface) LocateRegistry.getRegistry(registry, Integer.parseInt(rmiport)).lookup(rminame);

                System.out.println(ri.toString());
                break;
            } catch (RemoteException | NotBoundException e) {

                try {
                    ri = (RmiInterface) LocateRegistry.getRegistry(registry, Integer.parseInt(rmiport)).lookup(rminame);
                } catch (NotBoundException | RemoteException notBoundException) {
                    System.out.println("2");
                }
                if (i == 6) {
                    System.out.println("Impossivel conectar aos servidores RMI");
                    return;
                }
            }
        }
    }

    //cria utilizador a partir dos inputs do admin
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
                    ri = (RmiInterface) LocateRegistry.getRegistry(registry, Integer.parseInt(rmiport)).lookup(rminame);
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

    //cria eleição a partir dos inputs do admin
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
                        ri = (RmiInterface) LocateRegistry.getRegistry(registry, Integer.parseInt(rmiport)).lookup(rminame);
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

        //cria eleição
        for (int i = 0; i <= 6; i++) {
            try {
                if(ri.createEleicaoRMI(titulo, descricao, startDate, startHour, startMinute, endDate, endHour, endMinute, "", type) == null) {
                    System.out.println("Erro: Eleição não criada - Datas Inválidas");
                    return;
                }
                break;
            } catch (RemoteException e) {
                try {
                    ri = (RmiInterface) LocateRegistry.getRegistry(registry, Integer.parseInt(rmiport)).lookup(rminame);
                } catch (NotBoundException | RemoteException ignored) {

                }
                if (i == 6) {
                    System.out.println("Impossivel conectar aos servidores RMI");
                    return;
                }
            }
        }


        //Depois de criar a eleição, pergunta ao admin se quer adicionar departamentos à eleição
        //Caso afirmativo: adiciona à lista de departamentos da eleição os inputs do admin
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
                                    ri = (RmiInterface) LocateRegistry.getRegistry(registry, Integer.parseInt(rmiport)).lookup(rminame);
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

    //Gerir candidatos (adicionar/remover) de uma lista
    //-eleicao: Eleicao que pretendemos aceder
    //-choice: index da lista nessa eleição
    //-indx: index da Eleicao na lista de eleições que ainda não começaram, presente no RmiServer
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
            //apresenta a lista de todas as pessoas presentes no RmiServer
            //admin escolhe por index
            case 1:
                System.out.println("---Adicionar Candidatos---");
                for (int i = 0; i <= 6; i++) {
                    try {
                        System.out.printf("\nPessoa(s) que pretende adicionar (1 - %d): \n", ri.sizePessoas());
                        break;
                    } catch (RemoteException e) {
                        try {
                            ri = (RmiInterface) LocateRegistry.getRegistry(registry, Integer.parseInt(rmiport)).lookup(rminame);
                        } catch (NotBoundException | RemoteException ignored) {

                        }
                        if (i == 6) {
                            System.out.println("Impossivel conectar aos servidores RMI");
                            return false;
                        }
                    }
                }
                //De modo a minimizar a criação de objetos no AdminTerminal, ao longo do programa criamos chamadas ao RmiServer
                //que devolvem uma String com o conteúdo que queremos apresentar. Neste caso, uma String
                //com todas a pessoas presentes no RmiServer.
                for (int i = 0; i <= 6; i++) {
                    try {
                        System.out.print(ri.showPessoas());
                        break;
                    } catch (RemoteException e) {
                        try {
                            ri = (RmiInterface) LocateRegistry.getRegistry(registry, Integer.parseInt(rmiport)).lookup(rminame);
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
                                ri = (RmiInterface) LocateRegistry.getRegistry(registry, Integer.parseInt(rmiport)).lookup(rminame);
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
                                    ri = (RmiInterface) LocateRegistry.getRegistry(registry, Integer.parseInt(rmiport)).lookup(rminame);
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
                //remove candidatos presentes na Lista escolhida
                //por cada candidato removido volta a apresentar a lista de candidatos
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
                                    ri = (RmiInterface) LocateRegistry.getRegistry(registry, Integer.parseInt(rmiport)).lookup(rminame);
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
                                    ri = (RmiInterface) LocateRegistry.getRegistry(registry, Integer.parseInt(rmiport)).lookup(rminame);
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
                                        ri = (RmiInterface) LocateRegistry.getRegistry(registry, Integer.parseInt(rmiport)).lookup(rminame);
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

    //Gerir mesas (adicionar/remover) de uma eleição
    //-indexE:index da Eleicao na lista de eleições que ainda não começaram, presente no RmiServer
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
                    ri = (RmiInterface) LocateRegistry.getRegistry(registry, Integer.parseInt(rmiport)).lookup(rminame);
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
                    //Adiciona mesa por index a partir da lista de Mesas no RmiServer
                    System.out.print("\n---Adicionar Mesa---\n");
                    System.out.println("Mesas atualmente adicionadas:");
                    for (int i = 0; i <= 6; i++) {
                        try {
                            System.out.println(ri.showMesasEleicao(indexE));
                            break;
                        } catch (RemoteException e) {
                            try {
                                ri = (RmiInterface) LocateRegistry.getRegistry(registry, Integer.parseInt(rmiport)).lookup(rminame);
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
                                ri = (RmiInterface) LocateRegistry.getRegistry(registry, Integer.parseInt(rmiport)).lookup(rminame);
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
                                ri = (RmiInterface) LocateRegistry.getRegistry(registry, Integer.parseInt(rmiport)).lookup(rminame);
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
                                    ri = (RmiInterface) LocateRegistry.getRegistry(registry, Integer.parseInt(rmiport)).lookup(rminame);
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
                    //remove mesas de uma eleição por index
                    System.out.print("\n---Remover Mesa---\n");
                    int sizeMeleicao = 0;
                    for (int i = 0; i <= 6; i++) {
                        try {
                            sizeMeleicao = ri.sizeMesasEleicao(indexE);
                            break;
                        } catch (RemoteException e) {
                            try {
                                ri = (RmiInterface) LocateRegistry.getRegistry(registry, Integer.parseInt(rmiport)).lookup(rminame);
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
                                    ri = (RmiInterface) LocateRegistry.getRegistry(registry, Integer.parseInt(rmiport)).lookup(rminame);
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
                                        ri = (RmiInterface) LocateRegistry.getRegistry(registry, Integer.parseInt(rmiport)).lookup(rminame);
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

    //Gerir listas(criar/adicionar/remover) de uma eleição
    //eleicao: Eleicao que pretendemos aceder
    //-indx: index da Eleicao na lista de eleições que ainda não começaram, presente no RmiServer
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
                //cria lista
                createLista(eleicao,indx);
                break;
            case 2:
                //remover lista da eleição por index
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
                                    ri = (RmiInterface) LocateRegistry.getRegistry(registry, Integer.parseInt(rmiport)).lookup(rminame);
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
                //Gerir candidatos de uma lista (adicionar/remover)
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
                                    ri = (RmiInterface) LocateRegistry.getRegistry(registry, Integer.parseInt(rmiport)).lookup(rminame);
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

    //Gerir departamentos (adicionar/remover) de uma eleição
    //-indexE:index da Eleicao na lista de eleições que ainda não começaram, presente no RmiServer
    public boolean gerirDepartamentos(int indexE) throws RemoteException{
        System.out.println("---Gerir Departamentos---\n");
        int sizeD = 0;
        for (int i = 0; i <= 6; i++) {
            try {
                sizeD = ri.getEleicoesFuturas().get(indexE).sizeDepartamentos();
                break;
            } catch (RemoteException e) {
                try {
                    ri = (RmiInterface) LocateRegistry.getRegistry(registry, Integer.parseInt(rmiport)).lookup(rminame);
                } catch (NotBoundException | RemoteException ignored) {

                }
                if (i == 6) {
                    System.out.println("Impossivel conectar aos servidores RMI");
                    return false;
                }
            }
        }

        Scanner sc = new Scanner(System.in);
        if(sizeD == 0) {
            //Avisa o admin em caso de não haver departamentos adicionados à eleição
            //esta irá decorrer sem restrições
            System.out.println("[Sem Departamentos Adicionados: Eleição permitida a todos os votantes]");
        }
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
            //Adiciona departamento a partir do input do admin
            case 1:
                System.out.print("\n---Adicionar Departamento---\n");
                String addS = sc.nextLine();
                for (int i = 0; i <= 6; i++) {
                    try {
                        ri.addDepartamentos(indexE,addS);
                        break;
                    } catch (RemoteException e) {
                        try {
                            ri = (RmiInterface) LocateRegistry.getRegistry(registry, Integer.parseInt(rmiport)).lookup(rminame);
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
                //Remove departamentos por index
                System.out.print("\n---Remover Departamento---\n");
                for (int i = 0; i <= 6; i++) {
                    try {
                        sizeD = ri.sizeDepartamentos(indexE);
                        break;
                    } catch (RemoteException e) {
                        try {
                            ri = (RmiInterface) LocateRegistry.getRegistry(registry, Integer.parseInt(rmiport)).lookup(rminame);
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
                                ri = (RmiInterface) LocateRegistry.getRegistry(registry, Integer.parseInt(rmiport)).lookup(rminame);
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
                                    ri = (RmiInterface) LocateRegistry.getRegistry(registry, Integer.parseInt(rmiport)).lookup(rminame);
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

    //Gerir eleicao: Modificar propriedades textuais/Gerir Listas/Gerir Mesas/Gerir Departamentos
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
                    ri = (RmiInterface) LocateRegistry.getRegistry(registry, Integer.parseInt(rmiport)).lookup(rminame);
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
                    ri = (RmiInterface) LocateRegistry.getRegistry(registry, Integer.parseInt(rmiport)).lookup(rminame);
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
                        ri = (RmiInterface) LocateRegistry.getRegistry(registry, Integer.parseInt(rmiport)).lookup(rminame);
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
            //gerirListas da eleicao
            if(answer == 5){
                for (int i = 0; i <= 6; i++) {
                    try {
                        gerirListas(ri.getEleicoesFuturas().get(choice - 1),choice - 1);
                        break;
                    } catch (RemoteException e) {
                        try {
                            ri = (RmiInterface) LocateRegistry.getRegistry(registry, Integer.parseInt(rmiport)).lookup(rminame);
                        } catch (NotBoundException | RemoteException ignored) {

                        }
                        if (i == 6) {
                            System.out.println("Impossivel conectar aos servidores RMI");
                            return false;
                        }
                    }
                }
            }
            //Modificar propriedades textuais
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

                for (int i = 0; i <= 6; i++) {
                    try {
                        check = ri.changeEleicoesRMI(choice - 1, answer, change);
                        break;
                    } catch (RemoteException e) {
                        try {
                            ri = (RmiInterface) LocateRegistry.getRegistry(registry, Integer.parseInt(rmiport)).lookup(rminame);
                        } catch (NotBoundException | RemoteException ignored) {

                        }
                        if (i == 6) {
                            System.out.println("Impossivel conectar aos servidores RMI");
                            return false;
                        }
                    }
                }
            }
            //Geir mesas
            else if (answer == 6){
                gerirMesas(choice - 1);
            }
            //Gerir Departamentos
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

    //Apresenta local e momento de voto das eleições em que um user escolhido pelo o admin votou
    public void votoDetalhes() throws RemoteException{
        System.out.println("---Eleitor Local & Momento de Voto---");
        Scanner sc = new Scanner(System.in);
        for (int i = 0; i <= 6; i++) {
            try {
                System.out.printf("\nEscolha um eleitor (1 - %d): \n", ri.sizePessoas());
                break;
            } catch (RemoteException e) {
                try {
                    ri = (RmiInterface) LocateRegistry.getRegistry(registry, Integer.parseInt(rmiport)).lookup(rminame);
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
                    ri = (RmiInterface) LocateRegistry.getRegistry(registry, Integer.parseInt(rmiport)).lookup(rminame);
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
                    ri = (RmiInterface) LocateRegistry.getRegistry(registry, Integer.parseInt(rmiport)).lookup(rminame);
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
                        ri = (RmiInterface) LocateRegistry.getRegistry(registry, Integer.parseInt(rmiport)).lookup(rminame);
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

    //Cria lista: admin insere o nome da lista e escolher candidatos por index a partir da lista de pessoas presente no RmiServer
    //-eleicao: Eleicao que pretendemos aceder
    //-indx: index da Eleicao na lista de eleições que ainda não começaram, presente no RmiServer
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
                    ri = (RmiInterface) LocateRegistry.getRegistry(registry, Integer.parseInt(rmiport)).lookup(rminame);
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
            //Adiciona candidatos à lista atualmente criada
            for (int i = 0; i <= 6; i++) {
                try {
                    System.out.printf("Pessoa(s) que pretende adicionar (1 - %d): \n", ri.sizePessoas());
                    break;
                } catch (RemoteException e) {
                    try {
                        ri = (RmiInterface) LocateRegistry.getRegistry(registry, Integer.parseInt(rmiport)).lookup(rminame);
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
                        ri = (RmiInterface) LocateRegistry.getRegistry(registry, Integer.parseInt(rmiport)).lookup(rminame);
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
                            //Ao criar a lista adicionamos à primeira posição da listasCandidatas da eleição
                            //facilitando a adição de candidatos
                            check = ri.addCandidateRMI(indx, 0, add - 1);
                            break;
                        } catch (RemoteException e) {
                            try {
                                ri = (RmiInterface) LocateRegistry.getRegistry(registry, Integer.parseInt(rmiport)).lookup(rminame);
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

    //Apresenta detalhes das eleições terminadas
    public void eleicaoEndedDetalhes() throws RemoteException{
        System.out.println("\n---Detalhes Eleicoes Terminadas---\n");
        for (int i = 0; i <= 6; i++) {
            try {
                System.out.print(ri.eleicoesEndedRMI());
                break;
            } catch (RemoteException e) {
                try {
                    ri = (RmiInterface) LocateRegistry.getRegistry(registry, Integer.parseInt(rmiport)).lookup(rminame);
                } catch (NotBoundException | RemoteException ignored) {

                }
                if (i == 6) {
                    System.out.println("Impossivel conectar aos servidores RMI");
                    return;
                }
            }
        }
    }

    //Função auxiliar que determina se uma String é Parsable para Int
    public static boolean isParsable(String input) {
        try {
            Integer.parseInt(input);
            return true;
        } catch (final NumberFormatException e) {
            return false;
        }
    }

    //Função auxiliar que determina se uma String é Parsable para Data no formato "dd-MM-yyyy"
    public static boolean isParsableDate(String input){
        try {
            Date startDate = new SimpleDateFormat("dd-MM-yyyy").parse(input);
            return true;
        } catch (ParseException e) {
            return  false;
        }
    }

    //Função auxiliar que determina se uma String é Parsable para Data no formato "dd-MM-yyyy hh:mm"
    public static boolean isParsableDate_v2(String input){
        try {
            Date startDate = new SimpleDateFormat("dd-MM-yyyy hh:mm").parse(input);
            return true;
        } catch (ParseException e) {
            return  false;
        }
    }

    //Callback: callback no AdminTerminal quando um voto é a adicionado a um departamento
    //Apresenta o departamento e o número de votos na mesa
    public void voteUpdate(String departamento, int count) throws RemoteException{
        System.out.println("[UPDATE] New Vote in department "+ departamento + ", total count in that table: " + count);
    }

    //Callback: callback no AdminTerminal quando uma mesa se liga ao RmiServer
    //Apresenta o departamento
    public void tableUpdate(String dep) throws RemoteException{
        System.out.println("[UPDATE] Table at "+dep+" connected");
    }

    //Callback: callback no AdminTerminal quando uma mesa se desconecta ao RmiServer
    //Apresenta o departamento
    public void tableDisconnectedUpdate(String dep) throws RemoteException{
        System.out.println("[UPDATE] Table at "+ dep+" exited gracefully");
    }

    //Callback: callback no AdminTerminal quando um terminal de voto se liga a um MulticastServer
    //Apresenta o departamento
    public void terminalUpdate(String departamento) throws RemoteException{
        System.out.println("[UPDATE] New terminal connected at the table at " + departamento);
    }


    public static void main(String[] args) throws MalformedURLException, RemoteException, NotBoundException {




        AdminTerminal terminal = new AdminTerminal();

        terminal.ri.subscribe((AdminTerminalInterface) terminal );


        try {
            Scanner sc = new Scanner(System.in);
            int answer = -1;
            String answerS;
            //Menu admin
            while(true){
                System.out.println("\n---Menu Admin---\n");
                System.out.println("1 - Criar Eleição");
                System.out.println("2 - Criar Utilizador");
                System.out.println("3 - Gerir Eleições");
                System.out.println("4 - Eleições Passadas");
                System.out.println("5 - Local e Momento de Voto de um Eleitor");
                System.out.println();

                while (true) {
                    answerS = sc.nextLine();
                    if (isParsable(answerS)) {
                        answer = Integer.parseInt(answerS);
                        if (answer <= 5 && answer > 0)
                            break;
                        else
                            System.out.println("[Input Inválido: Tente de Novo]");
                    } else
                        System.out.println("[Input Inválido: Tente de Novo]");
                }

                switch (answer){
                    //Cria eleição & adiciona-a à lista de Eleicoes no RmiServer
                    case 1:
                        terminal.createEleicao();
                        break;
                    //Cria user & adiciona-o à lista de Pessoas no RmiServer
                    case 2:
                        terminal.registerUser();
                        break;
                    //Gerir eleições
                    case 3:
                        if(terminal.ri.getEleicoes().size() == 0)
                            System.out.println("Não existem eleições que possam ser geridas.");
                        else
                            terminal.gerirEleicao();
                        break;
                    //Apresenta detalhes de todas as eleições terminadas
                    case 4:
                        terminal.eleicaoEndedDetalhes();
                        break;
                    //Detalhes dos votos de um user
                    case 5:
                        terminal.votoDetalhes();
                        break;
                    default:
                        System.out.println("[Input Inválido: Tente de Novo]");
                        break;
                }

            }

        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
}
