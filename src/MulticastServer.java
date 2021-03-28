import java.io.Serializable;
import java.net.MulticastSocket;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.io.IOException;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Scanner;


public class MulticastServer extends Thread implements Serializable {
    private static String MULTICAST_ADDRESS;
    private int PORT = 4321;
    private long SLEEP_TIME = 5000;
    private static int tableNumber;
    private static String departamento;
    private Eleicao eleicao;

    public static void main(String[] args) {
        try {
            RmiInterface ti = (RmiInterface) Naming.lookup("rmi://localhost:7000/rmiServer");
            MULTICAST_ADDRESS = ti.getNewAddress();
            tableNumber = ti.getTableNumber();

        }
        catch (NotBoundException|MalformedURLException|RemoteException e) {
            e.printStackTrace();
        }
        setDepartamento(args[0]);
        MulticastServer server = new MulticastServer();
        server.start();
        client cliente = new client();
        cliente.start();
    }

    public MulticastServer() {
        super("Table Number " + tableNumber);
    }

    public void run() {
        try {
            RmiInterface ri = (RmiInterface) Naming.lookup("rmi://localhost:7000/rmiServer");
            ri.addMesa(this);
        } catch (NotBoundException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (RemoteException e) {
            e.printStackTrace();
        }

        MulticastSocket socket = null;
        long counter = 0;
        System.out.println(this.getName() + " running in address " + MULTICAST_ADDRESS + " in department " + departamento);
        try {
            RmiInterface ri = (RmiInterface) Naming.lookup("rmi://localhost:7000/rmiServer");
            socket = new MulticastSocket();  // create socket without binding it (only for sending)
            Scanner sc = new Scanner(System.in);
            InetAddress group = InetAddress.getByName(MULTICAST_ADDRESS);
            /*
            while (true) {
                String message = this.getName() + " packet " + counter++;
                byte[] buffer = message.getBytes();
                InetAddress group = InetAddress.getByName(MULTICAST_ADDRESS);
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length, group, PORT);
                socket.send(packet);
                try { sleep((long) (Math.random() * SLEEP_TIME)); } catch (InterruptedException e) { }
            }
            */

            System.out.println("Para indetificar um eleitor, insira o número da UC");
            System.out.println(ri.identificarUser(sc.nextLine()));

            String message = "type|request";
            byte[] buffer = message.getBytes();
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length, group, PORT);
            socket.send(packet);
            while(true){
                String reply = new String(packet.getData(), 0, packet.getLength());

                //testa se mensagem recebida tem o formato "type|available;uuid|x"
                if(reply.split("\\|",0)[1].equals("available;uuid")){
                    break;
                }

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
    public void setEleicao(Eleicao e){
        this.eleicao = e;
    }


}


class client extends Thread{
    public client(){
        super();
    }
    public void run(){
        try {
            RmiInterface ri = (RmiInterface) Naming.lookup("rmi://localhost:7000/rmiServer");
            System.out.println("8 + 3 = " + ri.add(8, 3));
        } catch (NotBoundException|MalformedURLException|RemoteException e) {
            e.printStackTrace();
        }
    }
}



