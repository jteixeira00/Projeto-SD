import java.util.HashMap;

public class MessageProtocol {


    String uuid, type, logged, username, password, msg;
    int item_count;
    HashMap<Integer, String> candidatos = new HashMap<Integer, String>();


    MessageProtocol(String string){
        for(String s: string.split(";")){
            switch(s.split("\\|")[0]){
                case "uuid":
                    this.uuid = s.split("\\|")[1];
                    break;
                case "type":
                    this.type = s.split("\\|")[1];
                    break;
                case "logged":
                    this.logged = s.split("\\|")[1];
                    break;
                case "username":
                    this.username = s.split("\\|")[1];
                    break;
                case "password":
                    this.password = s.split("\\|")[1];
                    break;
                case "msg":
                    this.msg  = s.split("\\|")[1];
                    break;
                case "item_count":
                    this.item_count = Integer.parseInt(s.split("\\|")[1]);
            }

        }
        if(this.type.equals("item_list")){
            for(String s:string.split(";")){
                String ss = s.split("\\|")[0];
                if(s.split("\\|")[0].matches("item_%d_name")){
                    candidatos.put(Integer.parseInt(s.split("\\|")[0].replaceAll("[^0-9]", "")), s.split("\\|")[1]);
                }
            }
        }
    }
    public HashMap<Integer, String> getCandidatos(){
        return candidatos;
    }
    public String getUuid() {
        return uuid;
    }

    public String getType() {
        return type;
    }

    public String getLogged() {
        return logged;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getMsg() {
        return msg;
    }

}
