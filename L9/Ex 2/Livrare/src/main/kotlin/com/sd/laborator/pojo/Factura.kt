package pojo

import java.sql.Date

data class Factura(
    var id_factura: Int,
    var id_comanda: Int,
    var data: Date?
)
