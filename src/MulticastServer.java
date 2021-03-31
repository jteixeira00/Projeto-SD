import com.sun.org.apache.xpath.internal.operations.Mult;

import java.io.Serializable;
import java.net.*;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;


public class MulticastServer extends Thread implements Serializable {
    private static String MULTICAST_ADDRESS, SECONDARY_MULTICAST_ADDRESS;
    private int PORT = 4321;
    private static int PORT2 = 4322;
    private long SLEEP_TIME = 5000;
    private static int tableNumber;
    private static String departamento;
    private ArrayList<Eleicao> eleicaoLista;



    public static void main(String[] args) {
        try {
            RmiInterface ti = (RmiInterface) Naming.lookup("rmi://localhost:7000/rmiServer");
            MULTICAST_ADDRESS = ti.getNewAddress();
            SECONDARY_MULTICAST_ADDRESS = ti.getSecondaryAddress();
            tableNumber = ti.getTableNumber();
            MulticastServer m = new MulticastServer();
            ti.subscribe((AdminTerminalInterface) m);
        }
        catch (NotBoundException|MalformedURLException|RemoteException e) {
            e.printStackTrace();
        }
        setDepartamento(args[0]);
        MulticastServer server = new MulticastServer();
        server.start();
        client cliente = new client(SECONDARY_MULTICAST_ADDRESS, PORT2, server);
        cliente.start();
    }

    public MulticastServer() {
        super("Table Number " + tableNumber);
    }

    public void run() {
        try {
            RmiInterface ri = (RmiInterface) Naming.lookup("rmi://localhost:7000/rmiServer");
            ri.addMesa(this);
        } catch (NotBoundException | MalformedURLException | RemoteException e) {
            e.printStackTrace();
        }

        MulticastSocket socket = null;
        long counter = 0;
        System.out.println(this.getName() + " running in address " + MULTICAST_ADDRESS + " in department " + departamento);
        try {
            RmiInterface ri = (RmiInterface) Naming.lookup("rmi://localhost:7000/rmiServer");
            socket = new MulticastSocket(PORT);  // create socket without binding it (only for sending)
            Scanner sc = new Scanner(System.in);
            InetAddress group = InetAddress.getByName(MULTICAST_ADDRESS);
            socket.joinGroup(group);
            int choice;

            while(true) {
                System.out.println("Para indetificar um eleitor, insira o número da UC");
                System.out.println(ri.identificarUser(sc.nextLine())); //falta dar handle das exceptions

                System.out.println("Escolha a eleição em que quer votar:");
                displayEleicoes();
                choice = Integer.parseInt(sc.nextLine());

                String message = "type|request";
                byte[] buffer = message.getBytes();
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length, group, PORT);
                //envia mensagem a pedir um terminal livre
                socket.send(packet);
                String reply;
                do {

                    buffer = new byte[256];
                    packet = new DatagramPacket(buffer, buffer.length);

                    socket.receive(packet);
                    reply = new String(packet.getData(), 0, packet.getLength());
                    System.out.println(reply);
                    //testa se mensagem recebida tem o formato "type|available;uuid|x"

                } while (!reply.split("\\|", 0)[1].equals("available;uuid"));
                //recebe mensagem com um terminal livre
                String uuid = reply.split("\\|", 0)[2].split(";", 0)[0];
                message = "uuid|" + uuid + ";type|unlock;eleicao|"+(choice-1);
                buffer = message.getBytes();
                packet = new DatagramPacket(buffer, buffer.length, group, PORT);
                socket.send(packet);
                //desbloqueia esse terminal
            }



        } catch (IOException | NotBoundException e) {
            e.printStackTrace();
        } finally {
            socket.close();
        }
    }

    public static void setDepartamento(String s){
        departamento = s;
    }
    public String getDepartamento(){
        return departamento;
    }
    public void setEleicaoLista(ArrayList<Eleicao> e){
        this.eleicaoLista = e;
    }

    public ArrayList<Eleicao> getEleicaoLista(){
        return eleicaoLista;
    }

    public void addEleicaoLista(Eleicao eleicao){
        eleicaoLista.add(eleicao);
    }
    public void displayEleicoes(){
        int i = 1;
        for(Eleicao e: eleicaoLista){
            System.out.println(i++ + " - " + e.getTitulo());
        }
    }

}


class client extends Thread{
    private String MULTICAST_ADDRESS;
    private int PORT;
    private MulticastServer server;
    //private HashMap<String, String> usersLoggedIn = new HashMap<String,String>();
    public client(String MULTICAST_ADDRESS, int PORT, MulticastServer server){
        super();
        this.MULTICAST_ADDRESS = MULTICAST_ADDRESS;
        this.PORT = PORT;
        this.server = server;
    }

    public void run(){
        MulticastSocket socket = null;
        RmiInterface ri = null;
        try {
           ri = (RmiInterface) Naming.lookup("rmi://localhost:7000/rmiServer");

        } catch (NotBoundException|MalformedURLException|RemoteException e) {
            e.printStackTrace();
        }
        try {
            socket = new MulticastSocket(PORT);  // create socket without binding it (only for sending)
        } catch (IOException e) {
            e.printStackTrace();
        }
        Scanner sc = new Scanner(System.in);
        InetAddress group = null;

        try {
            group = InetAddress.getByName(MULTICAST_ADDRESS);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        try {
            if (socket != null) {
                socket.joinGroup(group);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        while(true){
            byte[] buffer = new byte[1024];
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
            try {
                socket.receive(packet);
            } catch (IOException e) {
                e.printStackTrace();
            }
            String messagestr = new String(packet.getData(), 0, packet.getLength());
            MessageProtocol message = new MessageProtocol(messagestr);


            if(message.getType().equals("login")){
                try {
                    if(ri.login(message.getUsername(), message.getPassword())){
                        messagestr = "uuid|"+message.getUuid()+";status|on";
                        //usersLoggedIn.put(message.getUuid(), message.getUsername());
                    }
                    else{
                        messagestr = "uuid|"+message.getUuid()+";status|failure";
                    }
                    buffer = messagestr.getBytes();
                    packet = new DatagramPacket(buffer, buffer.length, group, PORT);
                    socket.send(packet);
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
            if(message.getType().equals("voto")){

                try {
                    ri.votar(server.getEleicaoLista().get(message.getEleicao()), message.getChoice(), ri.getPessoabyNumber(message.getUsername()), server.getDepartamento());
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }

        }

    }
}



