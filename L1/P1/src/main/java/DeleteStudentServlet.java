import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;

public class DeleteStudentServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest httpServletRequest,
                         HttpServletResponse httpServletResponse)
            throws ServletException, IOException {
        if(new File("/home/student/Desktop/SD/Laboratoare/L1/P1/persistenta/student.xml").delete())
            httpServletResponse.getWriter().print("<h1 style=\"text-align:center\">Datele studentului au fost sterse cu succes!</h1>");
        else
            httpServletResponse.sendError(404, "Nu a fost gasit niciun student serializat pe disc!");
    }
}