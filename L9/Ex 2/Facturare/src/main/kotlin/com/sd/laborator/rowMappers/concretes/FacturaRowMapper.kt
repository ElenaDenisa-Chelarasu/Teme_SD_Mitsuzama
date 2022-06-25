package rowMappers.concretes

import pojo.Factura
import rowMappers.interfaces.IRowMapper
import java.sql.ResultSet

class FacturaRowMapper: IRowMapper<Factura> {
    override fun mapRow(rs: ResultSet): Factura {
        return Factura(
            rs.getInt("id_factura"),
            rs.getInt("id_comanda"),
            rs.getDate("data"),
        )
    }
}