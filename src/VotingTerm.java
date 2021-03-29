import java.net.MulticastSocket;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;
import java.util.UUID;


public class VotingTerm extends Thread{

    private static String MULTICAST_ADDRESS = "224.3.2.";
    private int PORT = 4321;
    private UUID uuid;
    public void setUuid() {
        this.uuid = UUID.randomUUID();
    }


    public static void main(String[] args){
        VotingTerm client = new VotingTerm();
        client.start();
    }

    public void run() {
        MulticastSocket socket = null;
        setUuid();
        try {
            System.out.println( "A que mesa de voto deseja ligar-se?");
            Scanner in = new Scanner(System.in);
            String s = in.nextLine();
            MULTICAST_ADDRESS = MULTICAST_ADDRESS+s;
            socket = new MulticastSocket(PORT);  // create socket and bind it
            InetAddress group = InetAddress.getByName(MULTICAST_ADDRESS);
            socket.joinGroup(group);
            System.out.println("Terminal de voto conectado à mesa nº "+s);
            String message;
            while (true) {

                //aguarda uma mensagem a pedir um terminal livre
                do {
                    byte[] buffer = new byte[256];
                    DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                    socket.receive(packet);
                    message = new String(packet.getData(), 0, packet.getLength());
                    System.out.println(message);
                }while(!message.equals("type|request"));

                //responde a informar que está disponivel
                message = "type|available;uuid|"+ uuid.toString();
                byte[] buffer = message.getBytes();
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length, group, PORT);
                socket.send(packet);

                //aguarda mensagem a desbloquear
                do{
                    buffer = new byte[256];
                    packet = new DatagramPacket(buffer, buffer.length);
                    socket.receive(packet);
                    message = new String(packet.getData(), 0, packet.getLength());
                    System.out.println(message);
                }while(!message.split(";")[1].equals("type|unlock"));

                if(message.split("\\|")[1].equals(uuid.toString()+";type"){
                    System.out.println("UC Number:");
                    String ucnumber = in.nextLine();
                    System.out.println("Password");
                    String password = in.nextLine();

                    //envia login info
                    message = "uuid|"+uuid.toString()+";type|login;number|"+ucnumber+";password|"+password;
                    buffer = message.getBytes();
                    packet = new DatagramPacket(buffer, buffer.length, group, PORT);
                    socket.send(packet);

                    //recebe o status (sucesso no login ou nao)
                    do{
                        buffer = new byte[512];
                        packet = new DatagramPacket(buffer, buffer.length);
                        socket.receive(packet);
                        message = new String(packet.getData(), 0, packet.getLength());
                    }while(!message.split(";")[0].equals("uuid|"+uuid.toString()) && !message.split(";")[1].equals("type|status"));

                    if(message.split(";")[0].equals("logged|on")){

                    }
                    else{
                        //login failed«
                    }

                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            socket.close();
        }
    }


}

