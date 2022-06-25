package com.sd.laborator.services

import com.maxmind.geoip2.DatabaseReader
import com.sd.laborator.exceptions.BlacklistedCountryException
import com.sd.laborator.interfaces.WeatherForecastInterface
import com.sd.laborator.pojo.abstractions.AbstractForecastData
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Service
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader
import java.net.InetAddress
import java.net.URL

@Service
class WeatherForecastProxyService: WeatherForecastInterface {
    @Autowired
    private lateinit var weatherForecastProxyServiceWeatherForecastService: WeatherForecastInterface
    override fun getForecastData(locationId: Int): AbstractForecastData {
        //BlackList-uri: ID-uri si numme
        val blackListIdList = listOf<Long>(798549, 3017382, 2510769)
        val blackListNameList = listOf("Romania","Franta","Germania")

        //Obtinere IP
        val url = URL("http://checkip.amazonaws.com/")
        val br = BufferedReader(InputStreamReader(url.openStream()))
        val stringIp = br.readLine()

        //Cautare a tarii din care face parte IP-ul
        val database = File("./GeoLite2-Country.mmdb")
        val dbReader: DatabaseReader = DatabaseReader.Builder(database).build()
        val response = dbReader.country(InetAddress.getByName(stringIp))
        val id = response.country.geoNameId

        //Daca se afla in BlackList atunci se genereaza o exceptie
        if(id in blackListIdList)
            throw BlacklistedCountryException(blackListNameList)
        else//Daca nu se returneaza prognoza
            return weatherForecastProxyServiceWeatherForecastService.getForecastData(locationId)
    }
}