import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class StartMonitoringServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request,
                          HttpServletResponse response) throws ServletException, IOException {
        DBMonitor dbMonitor = DBMonitor.GetInstance();
        dbMonitor.Lock();
        response.setContentType("text/html");
        if(dbMonitor.isMonitorizing())
        {
            response.getWriter().println("Monitorizarea este deja activa!");
            dbMonitor.Unlock();
            return;
        }
        StringBuilder responseText = new StringBuilder();
        responseText.append("<h3>Atribute monitorizabile:</h3>");
        responseText.append("<form method=\"post\">");
        responseText.append("<input type=\"checkbox\" name=\"se.varsta\"> Varsta: ");
        responseText.append("<input type=\"number\" name=\"a_se.varsta\">");
        responseText.append("<input type=\"number\" name=\"b_se.varsta\"><br/>");
        responseText.append("<input type=\"checkbox\" name=\"SUM(se.varsta)\"> Suma varstelor: ");
        responseText.append("<input type=\"number\" name=\"a_SUM(se.varsta)\">");
        responseText.append("<input type=\"number\" name=\"b_SUM(se.varsta)\"><br/>");
        responseText.append("<input type=\"checkbox\" name=\"COUNT(se.id)\"> Nr. total de studenti: ");
        responseText.append("<input type=\"number\" name=\"a_COUNT(se.id)\">");
        responseText.append("<input type=\"number\" name=\"b_COUNT(se.id)\"><br/>");
        responseText.append("<button type=\"submit\">Porneste</button></form>");
        response.getWriter().print(responseText);
        dbMonitor.Unlock();
    }

    protected void doPost(HttpServletRequest request,
                         HttpServletResponse response) throws ServletException, IOException {
        DBMonitor dbMonitor = DBMonitor.GetInstance();
        dbMonitor.Lock();
        response.setContentType("text/html");
        if(dbMonitor.isMonitorizing())
        {
            response.getWriter().println("Monitorizarea este deja activa!");
            return;
        }
        ArrayList<Integer> a = new ArrayList<Integer>();
        ArrayList<Integer> b = new ArrayList<Integer>();
        ArrayList<String> fieldNames = new ArrayList<String>();
        List<String> _fieldNames = Arrays.asList("se.varsta", "SUM(se.varsta)", "COUNT(se.id)");
        List<String> _aliases = Arrays.asList("\"varsta\"", "\"suma varstelor\"", "\"nr. studenti\"");
        ArrayList<String> aliases = new ArrayList<String>();
        String s;
        try
        {
            for(int i=0; i<_fieldNames.size();++i)
            {
                s=_fieldNames.get(i);
                if("on".equals(request.getParameter(s)))
                {
                    a.add(Integer.parseInt(request.getParameter("a_"+s)));
                    b.add(Integer.parseInt(request.getParameter("b_"+s)));
                    if(a.get(a.size()-1)>b.get(b.size()-1))
                    {
                        response.getWriter().println("Unul din intervale a avut limita inferioara mai mare decat cea superioara!");
                        return;
                    }
                    aliases.add(_aliases.get(i));
                    fieldNames.add(s);
                }
            }
            dbMonitor.SetParameteres(fieldNames,a,b,aliases);
            dbMonitor.StartMonitoring();
            dbMonitor.TransactionCommited();
            response.getWriter().println("Monitorizare pornita cu succes!");
        }catch(NumberFormatException e) {
            response.getWriter().println("Textul introdus pentru capetele intervalelor nu este numeric!");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }catch (NullPointerException e) {
            response.getWriter().println("Unul dintre capetele intervalelor lipseste!");
        }finally {
            dbMonitor.Unlock();
        }
    }
}