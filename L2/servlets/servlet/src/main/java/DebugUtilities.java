import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;


public class DebugUtilities
{
    public static void DebugLog(String message) throws IOException {
        FileWriter fw = new FileWriter("/home/petru/Debug.txt", true);
        SimpleDateFormat formatter= new SimpleDateFormat("yyyy-MM-dd 'at' HH:mm:ss z");
        Date date = new Date(System.currentTimeMillis());
        fw.append("["+formatter.format(date)+"] : "+message);
        fw.close();
    }
}
