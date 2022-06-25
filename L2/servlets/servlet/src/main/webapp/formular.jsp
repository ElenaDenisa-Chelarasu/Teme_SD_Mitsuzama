<html xmlns:jsp="http://java.sun.com/JSP/Page">
  <head>
    <title>Formular student</title>
    <meta charset="UTF-8" />
  </head>
  <body>
    <h3>Formular student</h3>
    Introduceti datele despre student:
    <form action="./process-student" method="post">
      <%
        Object id=request.getParameter("id");
        if(id!=null)
          out.print("ID: <input type=\"number\" name=\"id\" value=\""+id+"\" readonly><br/>");
      %>
      Nume: <input type="text" name="nume"
        <%
          Object nume=request.getParameter("nume");
          if(nume!=null)
            out.print("value=\""+nume+"\"");
        %>
      />
      <br />
      Prenume: <input type="text" name="prenume"
        <%
          Object prenume=request.getParameter("prenume");
          if(prenume!=null)
            out.print("value=\""+prenume+"\"");
        %>
      />
      <br />
      Varsta: <input type="number" name="varsta"
        <%
          Object varsta=request.getParameter("varsta");
          if(varsta!=null)
            out.print("value=\""+varsta+"\"");
        %>
      />
      <br />
      <br />
      <button type="submit" name="submit">Trimite</button>
    </form>
  </body>
</html>