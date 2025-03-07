package it.polito.g21.crm.controllers

import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.core.Authentication
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDateTime


@RestController
@RequestMapping("/API/user")
@EnableWebSecurity
class UserController {
    @PostMapping("/data")
    fun postData(@RequestBody data: Map<String, String>, authentication: Authentication): Map<String, String?> {
        val principal = authentication.principal
        val roles = authentication.authorities
            .map { it.authority }
            .filter { it in listOf("ROLE_guest", "ROLE_manager", "ROLE_operator") }
            .map { it.removePrefix("ROLE_") }
        val username = if (principal is DefaultOidcUser) {
            principal.attributes["preferred_username"] as String?
        } else {
            null
        }
        return data.entries.associate { e -> e.key to e.value.uppercase() }
            .plus("user's roles" to roles.toString())
    }
}