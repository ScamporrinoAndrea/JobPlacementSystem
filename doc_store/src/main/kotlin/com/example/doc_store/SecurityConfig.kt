package com.example.doc_store

import jakarta.servlet.FilterChain
import jakarta.servlet.ServletException
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.convert.converter.Converter
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter
import org.springframework.web.filter.OncePerRequestFilter
import java.io.IOException

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
class SecurityConfig {

    private fun jwtAuthenticationConverter(): JwtAuthenticationConverter {
        val converter = JwtAuthenticationConverter()
        converter.setJwtGrantedAuthoritiesConverter(KeycloakRoleConverter())
        return converter
    }

    @Bean
    fun filterChain(http: HttpSecurity): SecurityFilterChain {
        return http
            .authorizeHttpRequests {
                it.requestMatchers("/", "/actuator/prometheus").permitAll()
                it.anyRequest().authenticated()
                //it.anyRequest().hasRole("manager")

            }
            .addFilterBefore(LoggingFilter(), BasicAuthenticationFilter::class.java)
            .oauth2ResourceServer { it.jwt { jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter()) } }
            .sessionManagement { it.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }
            .csrf { it.disable() }
            .cors { it.disable() }
            .build()
    }
    class LoggingFilter : OncePerRequestFilter() {

        @Throws(ServletException::class, IOException::class)
        override fun doFilterInternal(request: HttpServletRequest, response: HttpServletResponse, filterChain: FilterChain) {
            val authentication = SecurityContextHolder.getContext().authentication
            val token = getTokenFromRequest(request)

            if (authentication != null) {
                println("User: ${authentication.name}, Roles: ${authentication.authorities}")
            } else {
                println("No authentication information available")
            }

            println("JWT Token: $token")

            filterChain.doFilter(request, response)
        }

        private fun getTokenFromRequest(request: HttpServletRequest): String? {
            val bearerToken = request.getHeader("Authorization")
            return if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
                bearerToken.substring(7)
            } else null
        }
    }

    class KeycloakRoleConverter : Converter<Jwt, Collection<GrantedAuthority>> {
        override fun convert(jwt: Jwt): Collection<GrantedAuthority> {
            val authorities = mutableListOf<GrantedAuthority>()

            val realmAccess = jwt.claims["realm_access"] as Map<*, *>?
            val roles = realmAccess?.get("roles") as? List<*>
            println(roles)
            roles?.forEach { role ->
                authorities.add(SimpleGrantedAuthority("ROLE_$role"))
            }

            return authorities
        }
    }
}