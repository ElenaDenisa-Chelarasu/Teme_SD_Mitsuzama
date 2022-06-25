package com.sd.laborator.helpers

import org.apache.http.conn.ssl.NoopHostnameVerifier
import org.apache.http.conn.ssl.SSLConnectionSocketFactory
import org.apache.http.impl.client.HttpClients
import org.apache.http.ssl.SSLContexts
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory
import org.springframework.web.client.RestTemplate
import java.security.InvalidParameterException
import javax.net.ssl.SSLContext

class RestTemplateFactory {
    companion object {
        enum class RestTemplateTypes{
            WithSslVerification, WithoutSslVerification
        }
        fun getRestTemplate(option: RestTemplateTypes):RestTemplate{
            if(option==RestTemplateTypes.WithSslVerification)
                return RestTemplate()
            else if(option==RestTemplateTypes.WithoutSslVerification) {
                val requestFactory = HttpComponentsClientHttpRequestFactory()

                val sslcontext: SSLContext = SSLContexts.custom().loadTrustMaterial(null) { chain, authType -> true }.build()
                val sslsf = SSLConnectionSocketFactory(sslcontext, arrayOf("TLSv1.2"), null, NoopHostnameVerifier())
                val httpclient = HttpClients.custom().setSSLSocketFactory(sslsf).build()

                requestFactory.httpClient = httpclient
                return RestTemplate(requestFactory)
            }
            else
                throw InvalidParameterException("Optiune invalida!")
        }
    }
}