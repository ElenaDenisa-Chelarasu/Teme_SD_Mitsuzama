package com.sd.laborator.services

import com.maxmind.geoip2.DatabaseReader
import com.sd.laborator.interfaces.ClientValidatorInterface
import org.springframework.stereotype.Service
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader
import java.net.InetAddress
import java.net.URL

@Service
class ClientCountryChecker:ClientValidatorInterface {
    override fun validateClient(): Boolean {
        //ID-uri
        val blackListIdList = listOf<Long>(798549, 3017382, 2510769)

        //Obtinere IP
        val url = URL("http://checkip.amazonaws.com/")
        val br = BufferedReader(InputStreamReader(url.openStream()))
        val stringIp = br.readLine()

        //Cautare a tarii din care face parte IP-ul
        val database = File("./GeoLite2-Country.mmdb")
        val dbReader: DatabaseReader = DatabaseReader.Builder(database).build()
        val response = dbReader.country(InetAddress.getByName(stringIp))
        val id = response.country.geoNameId

        return id !in blackListIdList
    }

    override fun getMessageForInvalidClient(): String {
        val blackListedCountries = listOf("Romania","Franta","Germania")
        val sb = StringBuilder("Acest serviciu este interzis in tarile ")

        blackListedCountries.forEachIndexed { index, s ->
            sb.append(s)
            if(index==blackListedCountries.lastIndex)
                return@forEachIndexed
            if(index==blackListedCountries.lastIndex-1)
                sb.append(" si ")
            else
                sb.append(", ")
        }

        return sb.append(".").toString()
    }
}