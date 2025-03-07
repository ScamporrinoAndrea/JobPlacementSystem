package com.example.keycloak

import jakarta.servlet.FilterChain
import jakarta.servlet.ServletException
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.web.csrf.CsrfToken
import org.springframework.web.filter.OncePerRequestFilter
import java.io.IOException
import java.util.logging.Logger

class CsrfCookieFilter: OncePerRequestFilter() {
    private val log: Logger = Logger.getLogger("CSrfCookieFilter")

    @Throws(ServletException::class, IOException::class)
    override fun doFilterInternal(req: HttpServletRequest, res: HttpServletResponse, filterChain: FilterChain) {
        val csrfToken = req.getAttribute("_csrf") as CsrfToken
        csrfToken.token
        try {
            filterChain.doFilter(req, res)
        } catch (e: ServletException) {
            log.info(e.message)
            when {
                e.message?.contains("HTTP response code: 401") == true -> {
                    res.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized")
                }
                e.message?.contains("HTTP response code: 403") == true -> {
                    res.sendError(HttpServletResponse.SC_FORBIDDEN, "Forbidden")
                }
                else -> {
                    res.sendError(HttpServletResponse.SC_BAD_REQUEST, "Bad Request")
                }
            }
        }
    }
}