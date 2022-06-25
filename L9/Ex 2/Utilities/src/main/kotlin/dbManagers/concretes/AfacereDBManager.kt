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
import java.security.InvalidParameterException

class AfacereDBManager:IAfacereDBManager, AbstractSQLiteDBManager("/home/petru/SD/L9/Ex 2/Persistence/database.db") {
    override fun getClientiList(): List<Client> {
        val result = mutableListOf<Client>()
        val rs = executeQuery("SELECT * FROM client")
        val mapper = ClientRowMapper()
        while(rs.next())
            result+=mapper.mapRow(rs)
        return result
    }

    override fun insertComanda(id_client: Int, id_produs: Int, cantitate: Int): Int {
        executeUpdate("INSERT INTO comanda(id_client, id_produs, cantitate) VALUES ($id_client, $id_produs, $cantitate)")
        val rs = executeQuery("SELECT MAX(id_comanda) FROM comanda")
        if(!rs.next())
            throw UnknownError() // :(
        return rs.getInt(0)
    }

    override fun getMateriale(id_produs: Int): List<Material> {
        val result = mutableListOf<Material>()
        val rs = executeQuery("SELECT * FROM material WHERE id_produs=$id_produs")
        val mapper = MaterialRowMapper()
        while(rs.next())
            result+=mapper.mapRow(rs)
        return result
    }

    override fun insertFactura(id_comanda: Int): Int {
        executeUpdate("INSERT INTO factura(id_comanda, data) VALUES($id_comanda, DATE())")
        val rs = executeQuery("SELECT MAX(id_factura) FROM factura")
        if(!rs.next())
            throw UnknownError() // :(
        return rs.getInt(0)
    }

    override fun addStocProdus(id_produs: Int, cantitate_noua: Int) {
        executeUpdate("UPDATE produs SET stoc_disponibil=$cantitate_noua WHERE id_produs=$id_produs")
    }

    override fun getProduseList(): List<Produs> {
        val result = mutableListOf<Produs>()
        val rs = executeQuery("SELECT * FROM produs")
        val mapper = ProdusRowMapper()
        while(rs.next())
            result+=mapper.mapRow(rs)
        return result
    }

    override fun getComanda(id_comanda: Int): Comanda {
        val rs = executeQuery("SELECT * FROM comanda WHERE id_comanda=$id_comanda")
        val mapper = ComandaRowMapper()
        if(!rs.next())
            throw InvalidParameterException()
        return mapper.mapRow(rs)
    }
}