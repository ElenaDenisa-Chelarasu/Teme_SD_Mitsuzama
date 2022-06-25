package dbManagers.abstractClasses

import java.sql.DriverManager
import java.sql.Statement

abstract class AbstractSQLiteDBManager(private val dbRelativePath: String) {
    private fun createStatement(): Statement? {
        val connection = DriverManager.getConnection("jdbc:sqlite:$dbRelativePath")
        return connection!!.createStatement()
    }

    protected fun executeUpdate(command: String)=createStatement()!!.executeUpdate(command)

    protected fun executeQuery(query: String)=createStatement()!!.executeQuery(query)
}