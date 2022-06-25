package com.sd.laborator.exceptions

class BlacklistedCountryException(public val blackListedCountries: List<String>):RuntimeException() {
    override fun toString(): String {
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