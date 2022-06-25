package rowMappers.concretes

import pojo.Produs
import rowMappers.interfaces.IRowMapper
import java.sql.ResultSet

class ProdusRowMapper: IRowMapper<Produs> {
    override fun mapRow(rs: ResultSet): Produs {
        return Produs(
            rs.getInt("id_produs"),
            rs.getString("denumire"),
            rs.getFloat("pret"),
            rs.getInt("stoc_disponibil"),
        )
    }
}