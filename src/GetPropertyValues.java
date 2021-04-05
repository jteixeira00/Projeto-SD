import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

//Busca os valores de configs.properties para ser usado em todo o programa
public class GetPropertyValues {

    InputStream inputStream;
    String main_multicast_pool = "";
    String secondary_multicast_pool = "";
    String port1 = "";
    String port2 ="";
    String rmiport="";
    String rminame = "";
    String registry = "";
    String callbackip = "";

    public String getCallbackip() {
        return callbackip;
    }

    public InputStream getInputStream() {
        return inputStream;
    }

    public String getMain_multicast_pool() {
        return main_multicast_pool;
    }

    public String getSecondary_multicast_pool() {
        return secondary_multicast_pool;
    }

    public int getPort1() {
        return Integer.parseInt(port1);
    }

    public int getPort2() {
        return Integer.parseInt(port2);
    }

    public String getRmiport() {
        return rmiport;
    }

    public String getRminame() {
        return rminame;
    }

    public String getRegistry() {
        return registry;
    }

    public void setPropValues() throws IOException{
        try{
            Properties prop = new Properties();
            String propFileName = "configs.properties";
            inputStream = getClass().getClassLoader().getResourceAsStream(propFileName);

            if (inputStream!=null){
                prop.load(inputStream);
            }else{
                throw new FileNotFoundException("property file " + propFileName + "not found" );
            }
            this.main_multicast_pool = prop.getProperty("main_multicast_pool");
            this.secondary_multicast_pool=prop.getProperty("secondary_multicast_pool");
            this.port1 = prop.getProperty("port1");
            this.port2 = prop.getProperty("port2");
            this.rmiport = prop.getProperty("rmiport");
            this.rminame = prop.getProperty("rminame");
            this.registry = prop.getProperty("registry");
            this.callbackip = prop.getProperty("callbackip");

        }catch (Exception e){
            System.out.println(e);
        }finally{
            inputStream.close();
        }

    }
}
