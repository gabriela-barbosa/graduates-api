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

    fun parseJwtToBody(jwt: String): Claims = Jwts.parser().setSigningKey(secret).parseClaimsJws(jwt).body

    fun generateJWT(issuer: String): String {

        return Jwts.builder()
            .setIssuer(issuer)
            .signWith(SignatureAlgorithm.HS512, secret).compact()
    }

    fun generateJWTWithExpiration(issuer: String, expiration: Long) =
        Jwts.builder()
            .setIssuer(issuer)
            .setExpiration(Date(System.currentTimeMillis() + expiration))
            .signWith(SignatureAlgorithm.HS512, secret).compact()


    fun generateJWTCookie(issuer: String): Cookie {
        val jwt = generateJWT(issuer)

        val cookie = Cookie("user.token", jwt)
        cookie.isHttpOnly = true
        cookie.path = "/"
        return cookie
    }
}