import ejb.StudentEntity;

import javax.persistence.*;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
public class ProcessStudentServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response) throws ServletException, IOException {
        // se citesc parametrii din cererea de tip POST
        String nume = request.getParameter("nume");
        String prenume = request.getParameter("prenume");
        int varsta = Integer.parseInt(request.getParameter("varsta"));
        int id=0;
        String msg;
        boolean isUpdate=false;
        if(request.getParameter("id")!=null)
        {
            try{
                id=Integer.parseInt(request.getParameter("id"));
                isUpdate=true;
            }catch(NumberFormatException e)
            {
                response.getWriter().println("ID-ul trebuie sa fie un numar intreg!<br /><br /><a href='./'>Inapoi la meniul principal</a>");
                return;
            }
        }
        // pregatire EntityManager
        EntityManagerFactory factory =
                Persistence.createEntityManagerFactory("bazaDeDateSQLite");
        EntityManager em = factory.createEntityManager();

        EntityTransaction transaction = em.getTransaction();

        transaction.begin();
        if(isUpdate)
        {
            Query query = em.createQuery("UPDATE StudentEntity se SET se.nume = ?1, se.prenume = ?2, se.varsta = ?3 WHERE se.id = ?4");
            int updateCount = query.setParameter(1, nume).setParameter(2, prenume)
                    .setParameter(3, varsta).setParameter(4, id).executeUpdate();
            if(updateCount==0)
                msg="Nu exista nici un student cu ID-ul "+id+"!";
            else
                msg="Datele studentului cu ID-ul "+id+" au fost actualizate.";
        }else{
            StudentEntity student = new StudentEntity();
            student.setNume(nume);
            student.setPrenume(prenume);
            student.setVarsta(varsta);
            em.persist(student);
            msg="Datele au fost adaugate in baza de date.";
        }
        transaction.commit();
        // inchidere EntityManager
        em.close();
        factory.close();
        DBMonitor dbMonitor = DBMonitor.GetInstance();
        dbMonitor.Lock();
        if(dbMonitor.isMonitorizing()) {
            try {
                dbMonitor.TransactionCommited();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        dbMonitor.Unlock();
        // trimitere raspuns inapoi la client
        response.setContentType("text/html");
        response.getWriter().println(msg+"<br /><br /><a href='./'>Inapoi la meniul principal</a>");
    }
}