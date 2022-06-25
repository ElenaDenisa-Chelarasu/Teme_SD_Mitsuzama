package dbManagers.concretes

import dbManagers.abstractClasses.AbstractSQLiteDBManager
import dbManagers.interfaces.IAfacereDBManager
import pojo.Client
import pojo.Comanda
import pojo.Material
import pojo.Produs
import rowMappers.concretes.ClientRowMapper
import rowMappers.concretes.ComandaRowMapper
import rowMappers.concretes.MaterialRowMapper
import rowMappers.concretes.ProdusRowMapper
import rowMappers.interfaces.IRowMapper
import java.sql.ResultSet

class AfacereDBManager:IAfacereDBManager, AbstractSQLiteDBManager("/home/petru/SD/L9/Ex 2/Persistence/database.db") {
    override fun getClientiList(): List<Client> {
        return executeQuery("SELECT * FROM client", ClientRowMapper())
    }

    override fun insertComanda(id_client: Int, id_produs: Int, cantitate: Int): Int {
        executeNonQuery("INSERT INTO comanda(id_client, id_produs, cantitate) VALUES ($id_client, $id_produs, $cantitate)")

        val results = executeQuery("SELECT MAX(id_comanda) FROM comanda", object : IRowMapper<Int> {
            override fun mapRow(rs: ResultSet): Int {
                return rs.getInt(1)
            }
        })
        return results[0]
    }

    override fun getMateriale(id_produs: Int): List<Material> {
        val query = "SELECT * FROM material WHERE id_produs=$id_produs"
        return executeQuery(query, MaterialRowMapper())
    }

    override fun insertFactura(id_comanda: Int): Int {
        executeNonQuery("INSERT INTO factura(id_comanda, data) VALUES($id_comanda, DATE())")
        val results = executeQuery("SELECT MAX(id_factura) FROM factura", object : IRowMapper<Int> {
            override fun mapRow(rs: ResultSet): Int {
                return rs.getInt(1)
            }
        })
        return results[0]
    }

    override fun addStocProdus(id_produs: Int, cantitate_noua: Int) {
        executeNonQuery("UPDATE produs SET stoc_disponibil=$cantitate_noua WHERE id_produs=$id_produs")
    }

    override fun getProduseList(): List<Produs> {
        return executeQuery("SELECT * FROM produs", ProdusRowMapper())
    }

    override fun getComanda(id_comanda: Int): Comanda {
        val result = executeQuery("SELECT * FROM comanda WHERE id_comanda=$id_comanda", ComandaRowMapper())
        return result[0]
    }
}