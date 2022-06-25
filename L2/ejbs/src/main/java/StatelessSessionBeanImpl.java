package ejb;
import interfaces.StatelessSessionBeanRemote;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
public class StatelessSessionBeanImpl implements
        StatelessSessionBeanRemote, Serializable {
    public StatelessSessionBeanImpl() {
        System.out.println("[Glassfish] S-a instanţiat un stateless session bean: " +
        StatelessSessionBeanImpl.class.getName());
    }
    public String getCurrentTime() {
        System.out.println("[Glassfish] S-a apelat metoda getCurrentTime()");
                Date date = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        return formatter.format(date);
    }
    public Integer addNumbers(Integer a, Integer b) {
        System.out.println("[Glassfish] S-a apelat metoda addNumbers(" + a + ", " + b + ")");
        return a + b;
    }
}