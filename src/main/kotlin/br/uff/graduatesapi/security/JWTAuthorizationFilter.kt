package br.uff.graduatesapi.security

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.web.filter.OncePerRequestFilter
import javax.servlet.FilterChain
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class JWTAuthorizationFilter(
    private val jwtUtil: JWTUtil,
    private val userDetailsService: UserDetailsService,
) : OncePerRequestFilter() {
    override fun doFilterInternal(req: HttpServletRequest, resp: HttpServletResponse, chain: FilterChain) {
        val token = req.getHeader("Authorization")


        if (token != null) {
            try {
                val authorized = getAuthentication(token)
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

    private fun getAuthentication(token: String): UsernamePasswordAuthenticationToken? {
        return try {
            val body = jwtUtil.parseJwtToBody(token)
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