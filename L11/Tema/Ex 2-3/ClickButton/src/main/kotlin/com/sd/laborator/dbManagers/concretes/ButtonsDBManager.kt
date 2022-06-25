package com.sd.laborator.dbManagers.concretes

import com.sd.laborator.dbManagers.interfaces.IButtonsDBManager
import com.sd.laborator.dbManagers.interfaces.IRowMapper
import dbManagers.abstractClasses.AbstractSQLiteDBManager
import java.sql.ResultSet

class ButtonsDBManager(path: String):AbstractSQLiteDBManager(path), IButtonsDBManager {
    override fun incrementButtonClicks(buttonName: String) {
        executeNonQuery("UPDATE buttons SET clicks = clicks + 1 WHERE button_name=\"$buttonName\"")
    }
}