import beans.StudentBean;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;

public class ProcessStudentServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request,
                         HttpServletResponse response)
            throws ServletException, IOException {
        String nume = request.getParameter("nume");
        String prenume = request.getParameter("prenume");
        int varsta = Integer.parseInt(request.getParameter("varsta"));
        StudentBean s = new StudentBean();
        s.setNume(nume);
        s.setPrenume(prenume);
        s.setVarsta(varsta);
        DBManager dbManager = new DBManager();
        try
        {
            if(IsInteger(request.getParameter("id")))
            {
                int id = Integer.parseInt(request.getParameter("id"));
                dbManager.Update(s, id);
                response.getWriter().print("Datele studentului cu ID-ul "+id+" au fost modificate!");
            }
            else
            {
                if(!dbManager.Exists())
                    dbManager.Create();
                dbManager.Insert(s);
                response.getWriter().print("Datele studentului au fost inserate!");
            }
        }
        catch(Exception e)
        {
            response.getWriter().print(Arrays.toString(e.getStackTrace()));
        }
    }

    private Boolean IsInteger(String s)
    {
        if(s==null)
            return false;
        else
        {
            try
            {
                Integer.parseInt(s);
                return true;
            }
            catch(NumberFormatException e)
            {
                return false;
            }
        }
    }
}