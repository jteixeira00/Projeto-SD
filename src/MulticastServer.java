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
import java.util.Date;
import java.util.HashMap;
import java.util.Scanner;


public class MulticastServer extends Thread implements Serializable, MulticastInterface{
    private static String MULTICAST_ADDRESS, SECONDARY_MULTICAST_ADDRESS;
    private int PORT = 4321;
    private static int PORT2 = 4322;
    private long SLEEP_TIME = 5000;
    private static int tableNumber;
    private static String departamento;
    private ArrayList<Eleicao> eleicaoLista;
    private static Mesa mesa;

    //11 - estado mesas
    private boolean active = false;

    //12 - nº votos
    private int countVotos = 0;



    public static void main(String[] args) {
        setDepartamento(args[0]);
        try {
            RmiInterface ti = (RmiInterface) Naming.lookup("rmi://localhost:7000/rmiServer");
            MULTICAST_ADDRESS = ti.getNewAddress();
            SECONDARY_MULTICAST_ADDRESS = ti.getSecondaryAddress();

            tableNumber = ti.getTableNumber(args[0]);
            try {
                ti.notifyOfNewTable(args[0]);
            }
            catch (RemoteException e){}

        }
        catch (NotBoundException|MalformedURLException|RemoteException e) {
            e.printStackTrace();
        }

        MulticastServer server = new MulticastServer();
        server.start();
        mesa = new Mesa(departamento);
        try {
            RmiInterface ri = (RmiInterface) Naming.lookup("rmi://localhost:7000/rmiServer");
            ri.addMesa(mesa);
        } catch (NotBoundException | MalformedURLException | RemoteException e) {
            e.printStackTrace();
        }
        client cliente = new client(SECONDARY_MULTICAST_ADDRESS, PORT2, server, departamento);
        cliente.start();
    }

    public MulticastServer() {
        super("Table Number " + tableNumber);
    }

    public void run() {


        MulticastSocket socket = null;
        long counter = 0;
        System.out.println(this.getName() + " running in address " + MULTICAST_ADDRESS + " in department " + departamento);
        try {

            RmiInterface ri = (RmiInterface) Naming.lookup("rmi://localhost:7000/rmiServer");
            socket = new MulticastSocket(PORT);  // create socket without binding it (only for sending)
            socket.setSoTimeout(1000);
            Scanner sc = new Scanner(System.in);
            InetAddress group = InetAddress.getByName(MULTICAST_ADDRESS);
            socket.joinGroup(group);
            int choice;

            while(true) {
                System.out.println("Para identificar um eleitor, insira o número da UC");
                System.out.println(ri.identificarUser(sc.nextLine())); //falta dar handle das exceptions

                System.out.println("Escolha a eleição em que quer votar:");

                displayEleicoes(ri.getMesaByName(departamento));
                choice = Integer.parseInt(sc.nextLine());

                String message = "type|request";
                byte[] buffer = message.getBytes();
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length, group, PORT);
                //envia mensagem a pedir um terminal livre
                socket.send(packet);
                String reply = null;
                do {

                    buffer = new byte[256];
                    packet = new DatagramPacket(buffer, buffer.length);
                    try {
                        socket.receive(packet);
                        reply = new String(packet.getData(), 0, packet.getLength());
                        System.out.println(reply);
                    }
                    catch (SocketTimeoutException e){
                        System.out.println("Timeout exceeded, no available terminals!");
                        run();
                        return;
                    }
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
    @Override
    public String getDepartamento() throws RemoteException{
        return departamento;
    }

    public ArrayList<Eleicao> getEleicaoLista() throws RemoteException{
        return eleicaoLista;
    }

    public void addEleicaoLista(Eleicao eleicao){
        eleicaoLista.add(eleicao);
    }

    public void displayEleicoes(Mesa mesaByName) {
        int i = 1;
        Date date = new Date();
        for (Eleicao e : mesaByName.getEleicoes()) {
            if(e.getEndDate().after(date) && e.getStartDate().before(date)) {

                System.out.println(i++ + " - " + e.getTitulo());
            }
        }
    }

    //11 - estado mesas
    public void setMesaON(){
        active = true;
    }
    public void setMesaOFF(){
        active = false;
    }
    public boolean getEstado() throws RemoteException{
        return active;
    }

    //12 - nº votos
    public void addCountVotos(){
        countVotos++;
    }

    public int getCountVotos() throws  RemoteException{
        return countVotos;
    }


}


class client extends Thread{
    private String MULTICAST_ADDRESS;
    private int PORT;
    private MulticastServer server;
    private String departamento;
    private int tableCount =0;
    //private HashMap<String, String> usersLoggedIn = new HashMap<String,String>();
    public client(String MULTICAST_ADDRESS, int PORT, MulticastServer server, String departamento){
        super();
        this.MULTICAST_ADDRESS = MULTICAST_ADDRESS;
        this.PORT = PORT;
        this.departamento = departamento;
        this.server = server;
    }

    public void run() {
        //System.out.println(MULTICAST_ADDRESS);
        MulticastSocket socket = null;
        RmiInterface ri = null;
        try {
            ri = (RmiInterface) Naming.lookup("rmi://localhost:7000/rmiServer");

        } catch (NotBoundException | MalformedURLException | RemoteException e) {
            e.printStackTrace();
        }
        try {
            socket = new MulticastSocket(PORT);  // create socket without binding it (only for sending)
            Scanner sc = new Scanner(System.in);
            InetAddress group = null;
            group = InetAddress.getByName(MULTICAST_ADDRESS);
            socket.joinGroup(group);
            while (true) {
                byte[] buffer = new byte[1024];
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                try {
                    socket.receive(packet);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                String messagestr = new String(packet.getData(), 0, packet.getLength());
                System.out.println(messagestr);
                MessageProtocol message = new MessageProtocol(messagestr);


                if (message.getType().equals("login")) {
                    try {
                        if (ri.login(message.getUsername(), message.getPassword())) {
                            messagestr = "uuid|" + message.getUuid() + ";type|status;logged|on";

                            //usersLoggedIn.put(message.getUuid(), message.getUsername());
                        } else {
                            messagestr = "uuid|" + message.getUuid() + ";type|status;logged|failure";
                            buffer = messagestr.getBytes();
                            packet = new DatagramPacket(buffer, buffer.length, group, PORT);
                            socket.send(packet);
                            run();
                            return;
                        }
                        buffer = messagestr.getBytes();
                        packet = new DatagramPacket(buffer, buffer.length, group, PORT);
                        socket.send(packet);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
                if (message.getType().equals("voto")) {



                    try {
                        if(ri.votar(message.getEleicao(), message.getChoice(), message.getUsername(), server.getDepartamento(), ++tableCount)){
                            messagestr = "uuid|"+message.getUuid()+";type|success";
                            buffer = messagestr.getBytes();
                            packet = new DatagramPacket(buffer, buffer.length, group, PORT);
                            socket.send(packet);
                        };
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
                if(message.getType().equals("listas")){
                    messagestr = "uuid|"+message.getUuid()+";"+ri.generateLista(message.getEleicao(), departamento);
                    buffer = messagestr.getBytes();
                    packet = new DatagramPacket(buffer, buffer.length, group, PORT);
                    socket.send(packet);
                }

            }
        } catch (IOException e) {
            e.printStackTrace();


        }

    }
}



