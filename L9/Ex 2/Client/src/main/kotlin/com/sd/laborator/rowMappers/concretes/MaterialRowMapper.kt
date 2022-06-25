package rowMappers.concretes

import pojo.Material
import rowMappers.interfaces.IRowMapper
import java.sql.ResultSet

class MaterialRowMapper: IRowMapper<Material> {
    override fun mapRow(rs: ResultSet): Material {
        return Material(
            rs.getInt("id_material"),
            rs.getString("denumire"),
            rs.getFloat("pret"),
            rs.getInt("id_produs"),
            rs.getInt("stoc_disponibil"),
        )
    }
}