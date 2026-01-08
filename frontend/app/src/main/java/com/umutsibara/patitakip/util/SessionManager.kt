package com.umutsibara.patitakip.util

import android.content.Context
import android.content.SharedPreferences

class SessionManager(context: Context) {
    private val prefs: SharedPreferences =
            context.getSharedPreferences(Constants.PREF_NAME, Context.MODE_PRIVATE)

    fun saveAuthToken(token: String) {
        prefs.edit().putString(Constants.KEY_TOKEN, token).apply()
    }

    fun getAuthToken(): String? {
        return prefs.getString(Constants.KEY_TOKEN, null)
    }

    fun saveUser(id: Int, username: String) {
        prefs.edit()
                .putInt(Constants.KEY_USER_ID, id)
                .putString(Constants.KEY_USER_NAME, username)
                .apply()
    }

    fun getUserId(): Int {
        return prefs.getInt(Constants.KEY_USER_ID, -1)
    }

    fun isLoggedIn(): Boolean {
        return getAuthToken() != null
    }

    fun clearSession() {
        prefs.edit().clear().apply()
    }
}
