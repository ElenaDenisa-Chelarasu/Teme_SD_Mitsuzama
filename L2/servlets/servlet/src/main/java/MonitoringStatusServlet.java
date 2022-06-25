import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

public class MonitoringStatusServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response) throws ServletException, IOException {
        DBMonitor dbMonitor = DBMonitor.GetInstance();
        dbMonitor.Lock();
        response.setContentType("text/html");
        if(!dbMonitor.isMonitorizing())
        {
            response.getWriter().println("Monitorizarea este oprita!");
            dbMonitor.Unlock();
            return;
        }
        List<String> depasiri = dbMonitor.GetDepasiri();
        if(depasiri.size()==0)
            response.getWriter().println("<h3>Toate campurile monitorizate sunt intre intervalele dorite</h3>");
        else
        {
            StringBuilder responseText = new StringBuilder();
            responseText.append("<h3>Depasiri:</h3><ol>");
            for(String s:depasiri)
                responseText.append("<li>").append(s).append("</li>");
            responseText.append("</ol>");
            response.getWriter().print(responseText);
        }
        dbMonitor.Unlock();
    }
}