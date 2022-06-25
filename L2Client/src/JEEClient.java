import interfaces.StatelessSessionBeanRemote;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
public class JEEClient {
    public static void main(String[] args) throws NamingException {
        Context ctx = new InitialContext();
        StatelessSessionBeanRemote ssb = (StatelessSessionBeanRemote)
                ctx.lookup("ssb-example#interfaces.StatelessSessionBeanRemote");
        System.out.println(ssb.getCurrentTime());
        System.out.println("1 + 3 = " + ssb.addNumbers(1, 3));
    }
}