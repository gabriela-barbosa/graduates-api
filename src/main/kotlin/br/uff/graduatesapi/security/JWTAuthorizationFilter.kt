package br.uff.graduatesapi.security

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.web.filter.OncePerRequestFilter
import org.springframework.web.util.WebUtils
import javax.servlet.FilterChain
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class JWTAuthorizationFilter(
    private val jwtUtil: JWTUtil,
    private val userDetailsService: UserDetailsService,
) : OncePerRequestFilter() {
    override fun doFilterInternal(req: HttpServletRequest, resp: HttpServletResponse, chain: FilterChain) {
        val cookie = WebUtils.getCookie(req, "jwt")

        if (cookie != null) {
            try {
                val authorized = getAuthentication(cookie.value)
                if (authorized != null)
                    SecurityContextHolder.getContext().authentication = authorized
                else
                    SecurityContextHolder.clearContext()
            } catch (e: Exception) {
                logger.error("Erro no filtro de autenticação")
                SecurityContextHolder.clearContext()
            }
        }

        chain.doFilter(req, resp)
    }

    private fun getAuthentication(cookie: String): UsernamePasswordAuthenticationToken? {
        return try {
            val body = jwtUtil.parseJwtToBody(cookie)
            val id = body.issuer.toString()
            val user = userDetailsService.loadUserByUsername(id)
            UsernamePasswordAuthenticationToken(user, null, user.authorities)
        } catch (ex: Exception) {
            null
        }
    }

//    override fun attemptAuthentication(request: HttpServletRequest, response: HttpServletResponse?): Authentication? {
//        try {
//            val (email, password) = ObjectMapper().readValue(request.inputStream, LoginDTO::class.java)
//            val token = UsernamePasswordAuthenticationToken(email, password)
//            return authenticationManager.authenticate(token)
//        } catch (e: Exception) {
//            throw UsernameNotFoundException("")
//        }
//    }
//
//    override fun successfulAuthentication(
//        request: HttpServletRequest?,
//        response: HttpServletResponse,
//        chain: FilterChain?,
//        authResult: Authentication
//    ) {
//        val username = (authResult.principal as UserDetailsImpl).username
//        val cookie = jwtUtil.generateJWTCookie(username)
//        response.addCookie(cookie)
//    }
}