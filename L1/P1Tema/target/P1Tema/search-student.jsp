<html xmlns:jsp="http://java.sun.com/JSP/Page">
<head>
    <title>Formular student</title>
    <meta charset="UTF-8">
</head>
<body>
<h3>Formular student</h3>
Introduceti datele despre student:
<br/>
<br/>
<form method="post" action="
    <%
        if(request.getParameter("purpose").equals("delete"))
            out.print("./delete-students");
        else
            out.print("./search-students");
    %>">
    <label for="fieldName">Numele atributului:</label>
    <select name="fieldName" id="fieldName">
        <option value="id">ID</option>
        <option value="nume">Nume</option>
        <option value="prenume">Prenume</option>
        <option value="varsta">Varsta</option>
    </select>
    <br/>
    <br/>
    <label for="fieldValue">Valoare atribut:</label>
    <input type="text" id="fieldValue" name="fieldValue">
    <br/>
    <br/>
    <button type="submit" name="submit">Trimite</button>
</form>
</body>
</html>