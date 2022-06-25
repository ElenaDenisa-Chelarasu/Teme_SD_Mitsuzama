package rowMappers.interfaces

import java.sql.ResultSet

interface IRowMapper<T> {
    fun mapRow(rs: ResultSet): T
}