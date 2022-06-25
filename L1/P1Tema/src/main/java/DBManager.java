import beans.StudentBean;

import java.io.File;
import java.sql.*;
import java.util.ArrayList;

public class DBManager {
    private final String dbName;

    public DBManager(String dbName)
    {
        this.dbName=dbName;
    }

    public DBManager()
    {
        this.dbName="/home/petru/SD/L1/P1Tema/studenti.db";
    }

    public boolean Exists()
    {
        return new File(dbName).isFile();
    }

    public void Create() throws ClassNotFoundException, SQLException {
        Connection c;
        Statement stmt;
        new File(dbName).delete();
        Class.forName("org.sqlite.JDBC");
        c = DriverManager.getConnection("jdbc:sqlite:"+dbName);
        stmt = c.createStatement();
        String command="CREATE TABLE studenti("+
                "id INT PRIMARY KEY NOT NULL,"+
                "nume TEXT,"+
                "prenume TEXT,"+
                "varsta INT)";
        stmt.execute(command);
        stmt.execute("INSERT INTO studenti VALUES(1, \"AA\", \"BB\", 22)");
        stmt.execute("INSERT INTO studenti VALUES(6, \"CC\", \"DD\", 25)");
        stmt.close();
        c.close();
    }

    public void Insert(StudentBean s) throws ClassNotFoundException, SQLException {
        int count;
        Connection c;
        Statement stmt;
        Class.forName("org.sqlite.JDBC");
        c = DriverManager.getConnection("jdbc:sqlite:"+dbName);
        stmt = c.createStatement();
        ResultSet rs=stmt.executeQuery("SELECT COUNT(*) FROM studenti;");
        count = rs.getInt("COUNT(*)");//Obtin nr. studenti din DB
        if(count==0)//Daca nr. studenti din DB e 0, inserez cu ID=1
            Insert(s, stmt, 1);
        else
        {
            int minId, maxId;
            rs=stmt.executeQuery("SELECT MIN(id) FROM studenti;");
            minId=rs.getInt("MIN(id)");//Obtin ID minim
            if(minId>1)//Daca ID minim e mai mare ca 1, atunci inserez cu ID=minId-1
            {
                Insert(s, stmt, minId-1);
                CloseConnection(c, stmt);
                return;
            }
            rs= stmt.executeQuery("SELECT MAX(id) FROM studenti;");
            maxId=rs.getInt("MAX(id)");//Obtin ID maxim
            if(maxId-minId+1==count)//Daca exista student cu ID=i, oricare ar fi i de la minId la maxId
                Insert(s, stmt, maxId+1);//Inserez student cu id=maxId+1
            else
                //Am ajuns in cazul in care nu exista toti studentii cu id de la minId la maxId
                //Probabil au fost stersi anumiti studenti
                for(int i=minId+1;i<maxId;++i)
                {
                    //Verific daca exista un student cu ID-ul i
                    rs=stmt.executeQuery(String.format("SELECT COUNT(*) FROM studenti WHERE id=%d;",i));
                    if(rs.getInt("COUNT(*)")==0)//Daca nu exista, inserez noul student avand ID-ul i
                    {
                        Insert(s, stmt, i);
                        break;
                    }
                }
        }
        CloseConnection(c, stmt);
    }

    private void Insert(StudentBean s, Statement stmt, int id) throws SQLException {
        String nume, prenume;
        int varsta;
        nume=s.getNume();
        prenume=s.getPrenume();
        varsta=s.getVarsta();
        stmt.execute(String.format("INSERT INTO studenti VALUES(%d, \"%s\", \"%s\", %d)",id, nume,prenume,varsta));
    }

    private void CloseConnection(Connection c, Statement s) throws SQLException {
        s.close();
        c.close();
    }

    public void Update(StudentBean s, int id) throws ClassNotFoundException, SQLException {
        Connection c;
        Statement stmt;
        String nume, prenume;
        int varsta;

        Class.forName("org.sqlite.JDBC");
        c = DriverManager.getConnection("jdbc:sqlite:"+dbName);
        stmt = c.createStatement();

        nume=s.getNume();
        prenume=s.getPrenume();
        varsta=s.getVarsta();

        stmt.execute(String.format("UPDATE studenti SET nume=\"%s\", prenume=\"%s\", varsta=%d WHERE id=%d",nume, prenume, varsta, id));
        CloseConnection(c, stmt);
    }

    public void GetStudents(ArrayList<StudentBean> beans, ArrayList<Integer> ids) throws ClassNotFoundException, SQLException {
        ExecuteQuery(beans, ids, "SELECT * FROM studenti ORDER BY id;");
    }

    public void SearchStudents(ArrayList<StudentBean> beans, ArrayList<Integer> ids, String fieldName, String fieldValue, boolean isString) throws ClassNotFoundException, SQLException {
        if(isString)
            fieldValue="\""+fieldValue+"\"";
        ExecuteQuery(beans, ids, String.format("SELECT * FROM studenti WHERE %s=%s ORDER BY id;",fieldName,fieldValue));
    }

    private void ExecuteQuery(ArrayList<StudentBean> beans, ArrayList<Integer> ids, String query) throws SQLException, ClassNotFoundException {
        StudentBean s;
        Connection c;
        Statement stmt;
        Class.forName("org.sqlite.JDBC");
        c = DriverManager.getConnection("jdbc:sqlite:"+dbName);
        stmt = c.createStatement();

        ResultSet rs= stmt.executeQuery(query);

        while(rs.next())
        {
            s=new StudentBean();
            s.setPrenume(rs.getString("prenume"));
            s.setVarsta(rs.getInt("varsta"));
            s.setNume(rs.getString("nume"));
            beans.add(s);
            ids.add(rs.getInt("id"));
        }
    }

    public void Delete(String condition) throws ClassNotFoundException, SQLException {
        StudentBean s;
        Connection c;
        Statement stmt;
        Class.forName("org.sqlite.JDBC");
        c = DriverManager.getConnection("jdbc:sqlite:"+dbName);
        stmt = c.createStatement();

        stmt.execute("DELETE FROM studenti WHERE "+condition+";");
        CloseConnection(c, stmt);
    }
}
