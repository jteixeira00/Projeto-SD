import java.net.MulticastSocket;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;
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
            String messagestr;
            MessageProtocol message;
            while (true) {

                //aguarda uma mensagem a pedir um terminal livre
                do {
                    byte[] buffer = new byte[256];
                    DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                    socket.receive(packet);
                    messagestr = new String(packet.getData(), 0, packet.getLength());
                    System.out.println(messagestr);
                    message = new MessageProtocol(messagestr);

                }while(!message.getType().equals("request"));

                //responde a informar que está disponivel
                messagestr = "type|available;uuid|"+ uuid.toString();
                byte[] buffer = messagestr.getBytes();
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length, group, PORT);
                socket.send(packet);

                //aguarda mensagem a desbloquear
                do{
                    buffer = new byte[256];
                    packet = new DatagramPacket(buffer, buffer.length);
                    socket.receive(packet);
                    messagestr = new String(packet.getData(), 0, packet.getLength());
                    System.out.println(messagestr);
                    message = new MessageProtocol(messagestr);
                }while(!message.getType().equals("unlock"));

                if(message.getUuid().equals(uuid.toString())){
                    System.out.println("UC Number:");
                    String ucnumber = in.nextLine();
                    System.out.println("Password");
                    String password = in.nextLine();

                    //envia login info
                    messagestr = "uuid|"+uuid.toString()+";type|login;number|"+ucnumber+";password|"+password;
                    buffer = messagestr.getBytes();
                    packet = new DatagramPacket(buffer, buffer.length, group, PORT);
                    socket.send(packet);

                    //recebe o status (sucesso no login ou nao)
                    do{
                        buffer = new byte[512];
                        packet = new DatagramPacket(buffer, buffer.length);
                        socket.receive(packet);
                        messagestr = new String(packet.getData(), 0, packet.getLength());
                        message = new MessageProtocol(messagestr);
                    }while(!message.getUuid().equals(uuid.toString()) && !message.getType().equals("status"));

                    if(message.getLogged().equals("on")){
                        System.out.println("Escolha a lista em que pretende votar:");
                        //recebe as listas candidatas
                        do {
                            buffer = new byte[1024];
                            packet = new DatagramPacket(buffer, buffer.length);
                            socket.receive(packet);
                            messagestr = new String(packet.getData(), 0, packet.getLength());
                            message = new MessageProtocol(messagestr);
                        }while (!message.getType().equals("item_list") && !message.getUuid().equals(this.uuid.toString()));
                        //imprime as listas candidatas
                        if(message.getCandidatos().size()>0){
                            System.out.println("0 - Voto Nulo");
                            System.out.println("1 - Voto Branco");
                            for(Map.Entry<Integer, String> candidato : message.getCandidatos().entrySet()){
                                Integer key = candidato.getKey()+1;
                                String nome = candidato.getValue();
                                System.out.println(key + " - " + nome+"\n");
                            }
                        }
                        int choice = Integer.parseInt(in.nextLine());
                        messagestr = "id|"+uuid.toString()+";type|voto;choice|"+choice+";time|";



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

