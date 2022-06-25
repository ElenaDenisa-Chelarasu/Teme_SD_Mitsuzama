package dbManagers.interfaces

import pojo.Client
import pojo.Comanda
import pojo.Material
import pojo.Produs

interface IAfacereDBManager {
    fun getClientiList(): List<Client>
    fun insertComanda(id_client: Int, id_produs: Int, cantitate: Int): Int
    fun getMateriale(id_produs: Int): List<Material>
    fun insertFactura(id_comanda: Int): Int
    fun addStocProdus(id_produs: Int, cantitate_noua: Int)
    fun getProduseList(): List<Produs>
    fun getComanda(id_comanda: Int): Comanda
}