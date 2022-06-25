package dbManagers.abstractClasses

import rowMappers.interfaces.IRowMapper
import java.sql.DriverManager
import java.sql.Statement

abstract class AbstractSQLiteDBManager(private val absolutePath: String) {

    protected fun executeNonQuery(command: String) {
        val connection = DriverManager.getConnection("jdbc:sqlite:$absolutePath")
        val statement = connection.createStatement()
        try{
            statement.execute(command)
        }finally {
            statement.close()
            connection.close()
        }
    }

    protected fun <T>executeQuery(query: String, mapper: IRowMapper<T>): List<T>{
        val connection = DriverManager.getConnection("jdbc:sqlite:$absolutePath")
        val statement = connection.createStatement()
        try {
            val rs = statement.executeQuery(query)
            val result = mutableListOf<T>()
            while(rs.next())
                result+=mapper.mapRow(rs)
            return result
        } finally {
            statement.close()
            connection.close()
        }
    }
}