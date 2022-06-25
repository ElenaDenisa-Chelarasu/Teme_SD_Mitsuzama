<%@ page import="java.util.ArrayList" %>
<%@ page import="beans.StudentBean" %>
<html xmlns:jsp="http://java.sun.com/JSP/Page">
    <head>
        <title>Informatii student</title>
    </head>
    <body>
        <h2>Informatii studenti</h2>
        <ul type="bullet">
            <%
                ArrayList<Integer> ids = (ArrayList<Integer>) request.getAttribute("ids");
                ArrayList<StudentBean> beans = (ArrayList<StudentBean>) request.getAttribute("beans");
                String nume, prenume;
                int varsta, id;
                for(int i=0; i<ids.size();++i)
                {
                    nume=beans.get(i).getNume();
                    prenume=beans.get(i).getPrenume();
                    varsta=beans.get(i).getVarsta();
                    id=ids.get(i);
                    out.print("<li>ID: "+id+"</li>");
                    out.print("<li>Nume: "+nume+"</li>");
                    out.print("<li>Prenume: "+prenume+"</li>");
                    out.print("<li>Varsta: "+varsta+"</li>");
                    out.print("<li><a href=\"./formular.jsp?id="+id+
                            "&nume="+nume+
                            "&prenume="+prenume+
                            "&varsta="+varsta+"\">Modifica</a></li><br/>");
                }
            %>
        </ul>
    </body>
</html>
