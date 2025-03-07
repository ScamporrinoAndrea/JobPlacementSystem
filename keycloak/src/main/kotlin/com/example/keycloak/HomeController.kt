package com.example.keycloak

import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser
import org.springframework.security.oauth2.core.oidc.user.OidcUser
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.web.bind.annotation.*
import java.security.Principal
import java.time.LocalDateTime

@RestController
class HomeController {

    @GetMapping("generalInfo","/generalInfo")
    fun home(auth: Principal?) : Map<String, Any?>{
        return mapOf(
            "name" to "generalInfo",
            "date" to LocalDateTime.now(),
            "principal" to auth?.name
        )
    }

    @GetMapping("/me")
    fun me(
        @CookieValue(name="XSRF-TOKEN", required = false)
        xsrf: String?,
        authentication: Authentication?
    ): Map<String,Any?>{
        val principal: OidcUser? = authentication?.principal as? OidcUser
        val name = principal?.preferredUsername ?: ""
        val email = principal?.email
        println("XSRF Token: $xsrf")
        return mapOf(
            "name" to name,
            "email" to email,
            "loginUrl" to "/oauth2/authorization/kc1client",
            "logoutUrl" to "/logout",
            "principal" to principal,
            "xsrfToken" to xsrf
        )
    }





}