package com.sd.laborator.dbManagers.concretes

import com.sd.laborator.dbManagers.interfaces.IButtonsDBManager
import com.sd.laborator.dbManagers.interfaces.IRowMapper
import dbManagers.abstractClasses.AbstractSQLiteDBManager
import java.sql.ResultSet

class ButtonsDBManager(path: String):AbstractSQLiteDBManager(path), IButtonsDBManager {
    override fun getButtonNamesAndFrequencies(): Map<String, Int> {
        val query = "SELECT button_name, clicks FROM buttons"
        val pairs = executeQuery(query, object: IRowMapper<Pair<String, Int>> {
            override fun mapRow(rs: ResultSet): Pair<String, Int> {
                return Pair(rs.getString(1), rs.getInt(2))
            }
        })
        return pairs.toMap()
    }
}