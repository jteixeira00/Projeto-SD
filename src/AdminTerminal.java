import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.text.ParseException;
import java.util.Scanner;

public class AdminTerminal
{
    private String numero;
    private String password;
    private RmiInterface ri;

    public boolean loginUser() throws RemoteException
    {
        while(true) {
            boolean check = ri.login(this.numero, this.password);
            if (!check) {
                System.err.println("Login Failed: username or password is incorrect.");
            } else {
                return true;
            }
        }

    }

    public boolean registerUser(Pessoa pessoa) throws RemoteException{
       return true;
    }

    private Eleicao createEleicao() throws ParseException {
        System.out.println("---Criar Eleição---\n");
        Scanner sc = new Scanner(System.in);
        System.out.println("Titulo: ");
        String titulo = sc.nextLine();
        System.out.println("Descrição");
        String descricao = sc.nextLine();
        System.out.println("Data de Inicio (dd-MM-yyyy): \n");
        String startDate = sc.nextLine();
        System.out.println("Hora de Inicio: \n");
        String startH = sc.nextLine();
        int startHour = Integer.parseInt(startH);
        System.out.println("Minuto de Inicio: \n");
        String startM = sc.nextLine();
        int startMinute = Integer.parseInt(startH);
        System.out.println("Data de Fim (dd-MM-yyyy): \n");
        String endDate = sc.nextLine();
        System.out.println("Hora de Fim: \n");
        String endH = sc.nextLine();
        int endHour = Integer.parseInt(startH);
        System.out.println("Minuto de Fim: \n");
        String endM = sc.nextLine();
        int endMinute = Integer.parseInt(startH);
        System.out.println("Departamento: ");
        String departamento = sc.nextLine();
        Eleicao election = new Eleicao(titulo, descricao, startDate, startHour, startMinute, endDate, endHour, endMinute, departamento);
        return election;
    }



    public static void main(String[] args) throws MalformedURLException, RemoteException, NotBoundException {
        try {
            RmiInterface ri = (RmiInterface) Naming.lookup("rmi://localhost:7000/rmiServer");
            System.out.println("8 + 3 = " + ri.add(8, 3));
        } catch (NotBoundException | MalformedURLException | RemoteException e) {
            e.printStackTrace();
        }
    }
}
