package com.example.clientbase.data

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class UserSession(
    val isLoggedIn: Boolean = false,
    val userName: String = "Sarah Jenkins",
    val userEmail: String = "sarah.jenkins@apexsolutions.com",
    val userRole: String = "Senior Director of Sales",
    val workspace: String = "Apex Solutions Inc.",
    val domain: String = "apex.clientbase.io"
)

class SessionManager(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("clientbase_session", Context.MODE_PRIVATE)

    private val _sessionState = MutableStateFlow(loadSession())
    val sessionState: StateFlow<UserSession> = _sessionState.asStateFlow()

    private fun loadSession(): UserSession {
        val isLoggedIn = prefs.getBoolean(KEY_IS_LOGGED_IN, true) // Default logged in for immediate usability, or toggleable
        val name = prefs.getString(KEY_USER_NAME, "Sarah Jenkins") ?: "Sarah Jenkins"
        val email = prefs.getString(KEY_USER_EMAIL, "sarah.jenkins@apexsolutions.com") ?: "sarah.jenkins@apexsolutions.com"
        val role = prefs.getString(KEY_USER_ROLE, "Senior Director of Sales") ?: "Senior Director of Sales"
        val workspace = prefs.getString(KEY_WORKSPACE, "Apex Solutions Inc.") ?: "Apex Solutions Inc."
        val domain = prefs.getString(KEY_DOMAIN, "apex.clientbase.io") ?: "apex.clientbase.io"
        return UserSession(isLoggedIn, name, email, role, workspace, domain)
    }

    fun login(email: String, workspaceDomain: String, remember: Boolean = true) {
        val name = if (email.contains("@")) {
            val prefix = email.substringBefore("@").replace(".", " ")
            prefix.split(" ").joinToString(" ") { it.replaceFirstChar { char -> char.uppercase() } }
        } else {
            "Enterprise User"
        }
        val cleanDomain = workspaceDomain.ifBlank { "apex.clientbase.io" }
        val workspaceName = cleanDomain.substringBefore(".").replaceFirstChar { it.uppercase() } + " Enterprise"

        prefs.edit()
            .putBoolean(KEY_IS_LOGGED_IN, true)
            .putString(KEY_USER_NAME, name)
            .putString(KEY_USER_EMAIL, email)
            .putString(KEY_WORKSPACE, workspaceName)
            .putString(KEY_DOMAIN, cleanDomain)
            .apply()

        _sessionState.value = UserSession(
            isLoggedIn = true,
            userName = name,
            userEmail = email,
            userRole = "Executive Lead",
            workspace = workspaceName,
            domain = cleanDomain
        )
    }

    fun logout() {
        prefs.edit().putBoolean(KEY_IS_LOGGED_IN, false).apply()
        _sessionState.value = _sessionState.value.copy(isLoggedIn = false)
    }

    companion object {
        private const val KEY_IS_LOGGED_IN = "is_logged_in"
        private const val KEY_USER_NAME = "user_name"
        private const val KEY_USER_EMAIL = "user_email"
        private const val KEY_USER_ROLE = "user_role"
        private const val KEY_WORKSPACE = "workspace"
        private const val KEY_DOMAIN = "domain"
    }
}
