import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Arrays;

public class DeleteStudentsServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response)
            throws ServletException, IOException {
        String fieldName=request.getParameter("fieldName");
        String fieldValue=request.getParameter("fieldValue");

        DBManager dbManager=new DBManager();
        if(fieldValue==null || fieldName==null)
        {
            response.getWriter().print("Completati, va rog, campurile!");
            return;
        }
        if(IsNumericField(fieldName) && !IsInteger(fieldValue))
        {
            response.getWriter().print("Campul "+fieldName+" trebuie sa fie un numar!");
            return;
        }
        try {
            if(!IsNumericField(fieldName))
                fieldValue="\""+fieldValue+"\"";
            dbManager.Delete(fieldName+"="+fieldValue);
            response.getWriter().print("Studentii au fost stersi cu succes!");
        } catch (ClassNotFoundException | SQLException e) {
            response.getWriter().print(Arrays.toString(e.getStackTrace()));
        }
    }

    private boolean IsNumericField(String fieldName)
    {
        return fieldName.equals("varsta") || fieldName.equals("id");
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