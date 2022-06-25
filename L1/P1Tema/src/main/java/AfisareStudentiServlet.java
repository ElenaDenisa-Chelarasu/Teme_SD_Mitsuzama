import beans.StudentBean;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;

public class AfisareStudentiServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request,
                          HttpServletResponse response)
            throws ServletException, IOException {
        ArrayList<StudentBean> beans=new ArrayList<>();
        ArrayList<Integer> ids=new ArrayList<>();
        DBManager dbManager=new DBManager();
        try {
            dbManager.GetStudents(beans, ids);
        } catch (ClassNotFoundException | SQLException e) {
            response.getWriter().print(Arrays.toString(e.getStackTrace()));
            return;
        }
        request.setAttribute("beans",beans);
        request.setAttribute("ids",ids);
        request.getRequestDispatcher("./afisare-studenti.jsp").forward(request,response);
    }

}