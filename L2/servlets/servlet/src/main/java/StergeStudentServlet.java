import ejb.StudentEntity;

import javax.persistence.*;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
public class StergeStudentServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request,
                          HttpServletResponse response) throws ServletException, IOException {
        int id, count;
        String msg;
        try{
            id = Integer.parseInt(request.getParameter("id"));
        }
        catch(Exception e)
        {
            response.getWriter().print("Eroare la procesarea comenzii de stergere!");
            return;
        }
        // pregatire EntityManager
        EntityManagerFactory factory =
                Persistence.createEntityManagerFactory("bazaDeDateSQLite");
        EntityManager em = factory.createEntityManager();
        EntityTransaction transaction = em.getTransaction();
        Query query = em.createQuery("DELETE FROM StudentEntity se WHERE se.id=?1");

        transaction.begin();
        count=query.setParameter(1, id).executeUpdate();
        transaction.commit();

        em.close();
        factory.close();
        // trimitere raspuns inapoi la client
        response.setContentType("text/html");
        if(count==0)
            msg="Nici un student nu are ID-ul "+id;
        else
            msg="Studentul cu ID-ul "+id+" a fost sters.";
        response.getWriter().println(msg+"<br /><br /><a href='./'>Inapoi la meniul principal</a>");
    }
}