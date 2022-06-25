package com.sd.laborator.presentation.config
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.crypto.factory.PasswordEncoderFactories
import org.springframework.security.crypto.password.PasswordEncoder
@Configuration
open class SecurityConfig : WebSecurityConfigurerAdapter() {
    @Bean
    open fun passwordEncoder(): PasswordEncoder {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder()
    }
    override fun configure(http: HttpSecurity) {
        http
// Disable la "cross site request forgery" pentru a permite operatiile de POST/PUT/PATCH
            .csrf().disable()
// Aici puteti autoriza anumite rute (ex. pe baza unui token).
// In cazul nostru sunt permise accesul endpoint-urilor fara autorizare
                .authorizeRequests()
            .antMatchers(HttpMethod.GET, "/**").permitAll()
            .antMatchers(HttpMethod.DELETE, "/**").permitAll()
            .antMatchers(HttpMethod.POST, "/**").permitAll()
            .antMatchers(HttpMethod.PUT, "/**").permitAll()
            .antMatchers(HttpMethod.PATCH, "/**").permitAll()
    }
}