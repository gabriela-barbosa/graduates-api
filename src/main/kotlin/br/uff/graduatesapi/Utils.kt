package br.uff.graduatesapi

import io.jsonwebtoken.Jwts

class Utils {
    companion object{
        fun parseJwtToBody(jwt: String) = Jwts.parser().setSigningKey("secret").parseClaimsJws(jwt).body
    }
}