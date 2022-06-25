<html xmlns:jsp="http://java.sun.com/JSP/Page">
    <head>
        <title>Formular student</title>
        <meta charset="UTF-8">
    </head>
    <body>
        <h3>Formular student</h3>
        Introduceti datele despre student:
        <form action="./process-student" method="post">
            Nume: <input type="text" name="nume"
                <%
                    Object nume = request.getAttribute("nume");
                    if (nume != null)
                        out.print(" value=\""+nume+"\"");
                %>
            />
            <br/>
            Prenume: <input type="text" name="prenume"
                <%
                    Object prenume = request.getAttribute("prenume");
                    if (prenume != null)
                        out.print(" value=\""+prenume+"\"");
                %>
            />
            <br/>
            Varsta: <input type="number" name="varsta"
                <%
                    Object varsta = request.getAttribute("varsta");
                    if (varsta != null)
                        out.print(" value=\""+varsta+"\"");
                %>
            />
            <br/>
            <br/>
            <button type="submit" name="submit">Trimite</button>
        </form>
    </body>
</html>