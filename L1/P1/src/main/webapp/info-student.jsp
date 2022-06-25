<html xmlns:jsp="http://java.sun.com/JSP/Page">
    <head>
        <title>Informatii student</title>
    </head>
    <body>
        <h3>Informatii student</h3>
        <!-- populare bean cu informatii din cererea HTTP -->
        <jsp:useBean id="studentBean" class="beans.StudentBean" />
        <jsp:setProperty name="studentBean" property="nume" value='<%=request.getAttribute("nume") %>'/>
        <jsp:setProperty name="studentBean" property="prenume" value='<%=request.getAttribute("prenume") %>'/>
        <jsp:setProperty name="studentBean" property="varsta" value='<%=request.getAttribute("varsta") %>'/>
        <!-- folosirea bean-ului pentru afisarea informatiilor -->
        <p>Urmatoarele informatii au fost introduse:</p>
        <ul type="bullet">
            <li>Nume: <jsp:getProperty name="studentBean" property="nume" /></li>
            <li>Prenume: <jsp:getProperty name="studentBean" property="prenume" /></li>
            <li>Varsta: <jsp:getProperty name="studentBean" property="varsta" /></li>
            <li>Anul nasterii:
                <%
                    Object anNastere = request.getAttribute("anNastere");
                    if (anNastere != null) {
                    out.print(anNastere);
                    } else {
                    out.print("necunoscut");
                    }
                %>
            </li>
        </ul>
    </body>
</html>