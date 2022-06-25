import sun.net.www.http.HttpClient
import java.io.DataOutputStream
import java.io.File
import java.net.HttpURLConnection
import java.net.URI
import java.net.URL

fun main() {
    val url = "http://localhost:8080/"
    val data = "{\"numbers\":[" + File("numbers.txt").readText() + "]}"
    val response = khttp.post(url=url, data=data, headers = mapOf("Content-Type" to "application/json")).text
    println(response)
}