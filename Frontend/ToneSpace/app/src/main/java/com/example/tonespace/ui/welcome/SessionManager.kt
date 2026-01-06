package com.example.tonespace.ui.welcome

import android.content.Context
import com.example.tonespace.network.User

class SessionManager(context: Context) {

    private val prefs = context.getSharedPreferences(
        "tonespace_session", Context.MODE_PRIVATE
    )

    companion object {
        private const val USER_ID = "user_id"
        private const val USER_NAME = "user_name"
        private const val USER_EMAIL = "user_email"
        private const val USER_PHONE = "user_phone"
        private const val USER_GENDER = "user_gender"
        private const val USER_DOB = "user_dob"
        private const val USER_IMAGE_URL = "user_image_url"
    }

    fun saveUser(user: User) {
        prefs.edit().apply {
            putInt(USER_ID, user.id)
            putString(USER_NAME, user.name)
            putString(USER_EMAIL, user.email)
            putString(USER_PHONE, user.phone)
            putString(USER_GENDER, user.gender)
            putString(USER_DOB, user.dob)
            putString(USER_IMAGE_URL, user.profileImageUrl)
            apply()
        }
    }

    fun getFullUser(): User? {
        val userId = getUserId()
        if (userId == -1) return null
        return User(
            id = userId,
            name = prefs.getString(USER_NAME, null),
            email = prefs.getString(USER_EMAIL, "")!!,
            phone = prefs.getString(USER_PHONE, null),
            gender = prefs.getString(USER_GENDER, null),
            dob = prefs.getString(USER_DOB, null),
            profileImageUrl = prefs.getString(USER_IMAGE_URL, null)
        )
    }

    fun getUserId(): Int {
        return prefs.getInt(USER_ID, -1)
    }
    
    fun clear() {
        prefs.edit().clear().apply()
    }
}
