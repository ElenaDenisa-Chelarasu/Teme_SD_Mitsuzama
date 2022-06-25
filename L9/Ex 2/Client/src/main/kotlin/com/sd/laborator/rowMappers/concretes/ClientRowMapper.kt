package rowMappers.concretes

import pojo.Client
import rowMappers.interfaces.IRowMapper
import java.sql.ResultSet

class ClientRowMapper: IRowMapper<Client> {
    override fun mapRow(rs: ResultSet): Client {
        return Client(
            rs.getInt("id_client"),
            rs.getString("nume"),
            rs.getString("prenume"),
            rs.getString("email"),
            rs.getString("adresa_livrare"),
            rs.getString("adresa_facturare"),
            rs.getString("telefon")
        )
    }
}