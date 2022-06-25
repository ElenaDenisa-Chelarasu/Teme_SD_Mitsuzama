package com.sd.laborator.business.helpers
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.*
import org.springframework.security.crypto.scrypt.SCryptPasswordEncoder
class MyPasswordEncoderFactory {
    companion object{
        private var _encoders: Map<String, PasswordEncoder> =
            hashMapOf(
                "bcrypt" to BCryptPasswordEncoder(),
                "ldap" to LdapShaPasswordEncoder(),
                "MD4" to Md4PasswordEncoder(),
                "MD5" to MessageDigestPasswordEncoder("MD5"),
                "noop" to NoOpPasswordEncoder.getInstance(),
                "pbkdf2" to Pbkdf2PasswordEncoder(),
                "scrypt" to SCryptPasswordEncoder(),
                "SHA-1" to MessageDigestPasswordEncoder("SHA-1"),
                "SHA-256" to MessageDigestPasswordEncoder("SHA-256"),
                "sha256" to StandardPasswordEncoder(),
            )
        fun getPasswordEncodings(): Set<String>{
            return _encoders.keys
        }
        fun getEncoder(encodingId: String): PasswordEncoder?{
            return _encoders[encodingId]
        }
    }
}