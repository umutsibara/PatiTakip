package com.umutsibara.patitakip.utils

import android.content.Context
import android.content.SharedPreferences
import com.umutsibara.patitakip.utils.Constants.KEY_IS_LOGGED_IN
import com.umutsibara.patitakip.utils.Constants.KEY_TOKEN
import com.umutsibara.patitakip.utils.Constants.KEY_USER_ID
import com.umutsibara.patitakip.utils.Constants.KEY_USER_NAME
import com.umutsibara.patitakip.utils.Constants.PREF_NAME

class PreferencesManager(context: Context) {
    
    private val prefs: SharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    
    fun saveToken(token: String) {
        prefs.edit().putString(KEY_TOKEN, token).apply()
    }
    
    fun getToken(): String? {
        return prefs.getString(KEY_TOKEN, null)
    }
    
    fun saveUserId(userId: Int) {
        prefs.edit().putInt(KEY_USER_ID, userId).apply()
    }
    
    fun getUserId(): Int {
        return prefs.getInt(KEY_USER_ID, -1)
    }
    
    fun saveUserName(userName: String) {
        prefs.edit().putString(KEY_USER_NAME, userName).apply()
    }
    
    fun getUserName(): String? {
        return prefs.getString(KEY_USER_NAME, null)
    }
    
    fun setLoggedIn(isLoggedIn: Boolean) {
        prefs.edit().putBoolean(KEY_IS_LOGGED_IN, isLoggedIn).apply()
    }
    
    fun isLoggedIn(): Boolean {
        return prefs.getBoolean(KEY_IS_LOGGED_IN, false) && !getToken().isNullOrEmpty()
    }
    
    fun clearAll() {
        prefs.edit().clear().apply()
    }
}
