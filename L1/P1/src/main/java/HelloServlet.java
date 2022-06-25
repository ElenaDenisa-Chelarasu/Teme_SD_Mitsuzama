import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class HelloServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest httpServletRequest,
                         HttpServletResponse httpServletResponse)
            throws ServletException, IOException {
        httpServletResponse.getWriter().print("<h1 style=\"text-align:center\">Helloooo!</h1>");
    }
}