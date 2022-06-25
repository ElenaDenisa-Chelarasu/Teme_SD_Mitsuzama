import beans.StudentBean;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;

public class ExportServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request,
                          HttpServletResponse response)
            throws ServletException, IOException {
        ArrayList<StudentBean> beans=new ArrayList<>();
        ArrayList<Integer> ids=new ArrayList<>();
        DBManager dbManager=new DBManager();
        File exportFile=new File("/home/petru/SD/L1/P1Tema/studenti.xml");
        FileWriter fileWriter;
        StringBuilder stringBuilder=new StringBuilder();
        XmlMapper mapper = new XmlMapper();

        try {
            dbManager.GetStudents(beans, ids);
        } catch (ClassNotFoundException | SQLException e) {
            response.getWriter().print(Arrays.toString(e.getStackTrace()));
            return;
        }
        exportFile.delete();
        if(beans.size()==0)
        {
            response.getWriter().print("Baza de date nu contine nici un student!");
            return;
        }
        exportFile.createNewFile();
        stringBuilder.append("<studenti>\n");
        for(StudentBean studentBean: beans)
            stringBuilder
                    .append('\t')
                    .append(mapper.writeValueAsString(studentBean))
                    .append('\n');
        stringBuilder.append("</studenti>\n");
        fileWriter=new FileWriter(exportFile);
        fileWriter.write(stringBuilder.toString());
        response.getWriter().print("Baza de date a fost exportata cu succes!");
        fileWriter.close();
    }
}