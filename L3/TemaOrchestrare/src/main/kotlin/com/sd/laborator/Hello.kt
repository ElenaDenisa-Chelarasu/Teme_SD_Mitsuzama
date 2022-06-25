package com.sd.laborator
import org.springframework.beans.factory.config.ConfigurableBeanFactory
import org.springframework.boot.SpringBootConfiguration
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
open class WeatherApp
fun main(args: Array<String>) {
    val x=runApplication<WeatherApp>()
}

//S - Responsabilitate unica: NOT OK
//  WeatherForecastData - retine detaliile despre vreme
//  TimeService - furnizeaza momentul curent (data)
//  WeatherForecastService      - foloseste o metoda hardcodata pentru obtinerea vremii
//                              - obtine primul obiect stocat in JSON => nu responsabilitate unica
//  LocationSearchService       - foloseste o metoda hardcodata pentru obtinerea WOEID
//                              - transforma continutul raspunsului metodei de tip GET in JSON
//                              - cunoaste ce structura are JSON-ul si obtine WOEID din el => ...
//  WeatherAppController        - primeste un WOEID de la LocationSearchInterface si, pe baza lui, cere vremea de la WeatherForecastInterface

//O - Inchis deschis: OK
// Depinde de cum ar urma sa fie dezvoltata aplicatia.
// Ar trebui ca nici o clasa sa nu fie modificata si sa poata sa fie extinsa

//L - Substitutie Lipskov: OK
// Exista doar clase care implementeaza interfete, nu si clase care deriveaza alte clase
// Asadar nici un fiu nu se comporta intr-un mod complet diferit de parintele sau (pentru ca parintii sunt interfete care prezinta doar contracte)

//I - Segregarea interfetelor OK
// Fiecare clasa contine doar cate o metoda, ar fi imposibil ca interfetele sa fie mai segregate de atat

//D - Dependenta inversa: NOT OK
// Toate clasele depinde de abstractiuni inafara de WeatherForecastService care depinde de o clasa concreta (TimeService)