package rowMappers.concretes

import pojo.Comanda
import rowMappers.interfaces.IRowMapper
import java.sql.ResultSet

class ComandaRowMapper: IRowMapper<Comanda> {
    override fun mapRow(rs: ResultSet): Comanda {
        return Comanda(
            rs.getInt("id_comanda"),
            rs.getInt("id_client"),
            rs.getInt("id_produs"),
            rs.getInt("cantitate"),
        )
    }
}