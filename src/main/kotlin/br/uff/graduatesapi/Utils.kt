package br.uff.graduatesapi

import br.uff.graduatesapi.enums.WorkHistoryStatus
import br.uff.graduatesapi.model.Institution
import br.uff.graduatesapi.model.WorkHistory
import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import org.springframework.beans.factory.annotation.Value
import java.util.*
import javax.servlet.http.Cookie

class Utils {

    companion object {

        @Value("\${jwt.secret}")
        private lateinit var secret: String

        @Value("\${token.expiration}")
        private lateinit var expiration: String
        fun getHistoryStatus(
            history: WorkHistory?,
            postDoctorate: Institution?,
            finishedDoctorateOnUFF: Boolean?
        ): WorkHistoryStatus {
            if (history != null && postDoctorate != null && finishedDoctorateOnUFF != null)
                return WorkHistoryStatus.UPDATED
            else if (history != null || postDoctorate != null || finishedDoctorateOnUFF != null)
                return WorkHistoryStatus.UPDATED_PARTIALLY
            return WorkHistoryStatus.PENDING
        }

        fun parseJwtToBody(jwt: String): Claims = Jwts.parser().setSigningKey(secret).parseClaimsJws(jwt).body

        fun generateCookie(issuer: String): Cookie {
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
}