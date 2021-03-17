import java.net.MulticastSocket;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.io.IOException;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;


public class MulticastServer extends Thread {
    private static String MULTICAST_ADDRESS;
    private int PORT = 4321;
    private long SLEEP_TIME = 5000;
    private static int tableNumber;

    public static void main(String[] args) {
        try {
            RmiInterface ti = (RmiInterface) Naming.lookup("rmi://localhost:7000/rmiServer");
            MULTICAST_ADDRESS = ti.getNewAddress();
            tableNumber = ti.getTableNumber();

        }
        catch (NotBoundException|MalformedURLException|RemoteException e) {
            e.printStackTrace();
        }
        MulticastServer server = new MulticastServer();
        server.start();
        client cliente = new client();
        cliente.start();
    }

    public MulticastServer() {
        super("Table Number " + tableNumber);
    }

    public void run() {
        MulticastSocket socket = null;
        long counter = 0;
        System.out.println(this.getName() + " running in address " + MULTICAST_ADDRESS);
        try {
            socket = new MulticastSocket();  // create socket without binding it (only for sending)
            while (true) {
                String message = this.getName() + " packet " + counter++;
                byte[] buffer = message.getBytes();

                InetAddress group = InetAddress.getByName(MULTICAST_ADDRESS);
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length, group, PORT);
                socket.send(packet);
                try { sleep((long) (Math.random() * SLEEP_TIME)); } catch (InterruptedException e) { }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            socket.close();
        }
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



