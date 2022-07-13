package br.uff.graduatesapi.security

import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.util.*
import javax.servlet.http.Cookie

@Component
class JWTUtil {

    @Value("\${jwt.secret}")
    private lateinit var secret: String

    @Value("\${token.expiration}")
    private lateinit var expiration: String

    fun parseJwtToBody(jwt: String): Claims = Jwts.parser().setSigningKey(secret).parseClaimsJws(jwt).body

    fun generateJWTCookie(issuer: String): Cookie {
        val exp = expiration.toLong()

        val jwt = Jwts.builder()
            .setIssuer(issuer)
            .setExpiration(Date(System.currentTimeMillis() + exp))
            .signWith(SignatureAlgorithm.HS512, secret).compact()

        val cookie = Cookie("jwt", jwt)
        cookie.isHttpOnly = true
        return cookie
    }
}