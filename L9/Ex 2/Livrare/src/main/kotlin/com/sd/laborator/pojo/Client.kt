package pojo

data class Client(
    var id_client: Int,
    var nume: String,
    var prenume: String,
    var email: String,
    var adresa_livrare: String,
    var adresa_facturare: String,
    var telefon: String
)
