import java.net.MulticastSocket;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.Scanner;
import java.util.UUID;


public class VotingTerm extends Thread{

    private static String MULTICAST_ADDRESS = "224.3.2.";
    private int PORT = 4321;
    private static String SECONDARY_MULTICAST;
    private int PORT2 = 4322;

    private String tableNumber = "";

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
            String s = tableNumber;
            Scanner in = new Scanner(System.in);
            String numeroUc;
            if(MULTICAST_ADDRESS.length()<9 ) {
                System.out.println("A que mesa de voto deseja ligar-se?");

                s = in.nextLine();
                tableNumber = s;
                MULTICAST_ADDRESS = MULTICAST_ADDRESS + s;
                SECONDARY_MULTICAST = MULTICAST_ADDRESS;
                SECONDARY_MULTICAST = SECONDARY_MULTICAST.replace(".2.", ".3.");
            }
            String messagestr;
            MessageProtocol message;
            int eleicao;
            while (true) {

                socket = new MulticastSocket(PORT);  // create socket and bind it
                InetAddress group = InetAddress.getByName(MULTICAST_ADDRESS);
                socket.joinGroup(group);
                System.out.println("Terminal de voto conectado à mesa nº "+s);
                //aguarda uma mensagem a pedir um terminal livre
                do {
                    byte[] buffer = new byte[256];
                    DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                    socket.receive(packet);
                    messagestr = new String(packet.getData(), 0, packet.getLength());
                    System.out.println(messagestr);
                    message = new MessageProtocol(messagestr);

                }while(!message.getType().equals("request"));
                numeroUc = message.getUsername();
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
                eleicao = message.getEleicao();


                if(message.getUuid().equals(uuid.toString())){
                    System.out.println("Terminal desbloqueado");
                    System.out.println("UC Number:" +numeroUc);
                    System.out.println("Password");
                    String password = in.nextLine();

                    //conecta-se à segunda rede multicast
                    socket = new MulticastSocket(PORT2);
                    group = InetAddress.getByName(SECONDARY_MULTICAST);
                    socket.joinGroup(group);

                    //envia login info
                    messagestr = "uuid|"+uuid.toString()+";type|login;number|"+numeroUc+";password|"+password;
                    buffer = messagestr.getBytes();
                    packet = new DatagramPacket(buffer, buffer.length, group, PORT2);
                    System.out.println(messagestr);
                    socket.send(packet);

                    //recebe o status (sucesso no login ou nao)
                    do{
                        buffer = new byte[512];
                        packet = new DatagramPacket(buffer, buffer.length);
                        socket.receive(packet);
                        messagestr = new String(packet.getData(), 0, packet.getLength());
                        System.out.println(messagestr);
                        message = new MessageProtocol(messagestr);

                    }while((!message.getUuid().equals(uuid.toString())) || (!message.getType().equals("status")) || (message.getType().equals("login")));





                    if(message.getLogged().equals("on")){
                        System.out.println("Escolha a lista em que pretende votar:");

                        //pedir listas candidatas
                        messagestr = "uuid|"+uuid.toString()+";type|listas";
                        buffer = messagestr.getBytes();
                        packet = new DatagramPacket(buffer, buffer.length, group, PORT2);
                        System.out.println(messagestr);
                        socket.send(packet);

                        //recebe as listas candidatas
                        do {
                            buffer = new byte[1024];
                            packet = new DatagramPacket(buffer, buffer.length);
                            socket.receive(packet);
                            messagestr = new String(packet.getData(), 0, packet.getLength());
                            message = new MessageProtocol(messagestr);
                            System.out.println(messagestr);
                        }while (!message.getType().equals("item_list") || !message.getUuid().equals(this.uuid.toString()));

                        System.out.println(message.getCandidatos());
                        //imprime as listas candidatas

                        System.out.println("0 - Voto Nulo");
                        System.out.println("1 - Voto Branco");
                        for(Map.Entry<Integer, String> candidato : message.getCandidatos().entrySet()){
                            Integer key = candidato.getKey()+2;
                            String nome = candidato.getValue();
                            System.out.println(key + " - " + nome+"\n");
                        }
                        int choice = Integer.parseInt(in.nextLine());

                        Date date = new Date();
                        String pattern = "MM/dd/yyyy HH:mm";
                        DateFormat df = new SimpleDateFormat(pattern);
                        String dataString = df.format(date);

                        messagestr = "uuid|"+uuid.toString()+";type|voto;choice|"+choice+";time|"+dataString+";eleicao|"+eleicao+";number|"+numeroUc;
                     
                        buffer = messagestr.getBytes();
                        packet = new DatagramPacket(buffer, buffer.length, group, PORT2);
                        socket.send(packet);

                        do{
                            buffer = new byte[1024];
                            packet = new DatagramPacket(buffer, buffer.length);
                            socket.receive(packet);
                            messagestr = new String(packet.getData(), 0, packet.getLength());
                            message = new MessageProtocol(messagestr);
                        }while(!message.getType().equals("success"));

                        System.out.println("Success! Logging you off.");
                        for(int i = 0; i<20; i++){
                            System.out.println("\n\n");
                        }
                        run();
                        //somehow limpar o ecrã?


                    }
                    else{
                        //login failed
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

