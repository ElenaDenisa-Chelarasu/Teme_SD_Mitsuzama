package com.sd.laborator.services

import com.maxmind.geoip2.DatabaseReader
import com.sd.laborator.interfaces.TestServiceInterface
import org.springframework.stereotype.Service
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader
import java.net.InetAddress
import java.net.URL

val countryNames = listOf("Romania", "Franta")
val countryGeoId = listOf<Long>(798549, 3017382)
@Service
class TestService1:TestServiceInterface {
    override fun doTest(): String {
        val url = URL("http://checkip.amazonaws.com/")
        val br = BufferedReader(InputStreamReader(url.openStream()))
        val stringIp = br.readLine()
        val database = File("./GeoLite2-Country.mmdb")
        val dbReader: DatabaseReader = DatabaseReader.Builder(database).build()
        val response = dbReader.country(InetAddress.getByName(stringIp))
        val id = response.country.geoNameId
        if(id in countryGeoId)
            return "Serviciu interzis in tarile $countryNames"
        else
            return "OK"
    }
}