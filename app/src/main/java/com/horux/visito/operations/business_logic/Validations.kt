package com.horux.visito.operations.business_logic

import android.util.Patterns
import java.util.regex.Pattern

object Validations {
    var STRING_INVALID_EMAIL = "Invalid Email"
    var STRING_INVALID_PASSWORD =
        "Password must include a lowercase letter, a digit and one special character @,#,$,%,^,&,+,=,(,) and must be of length between 8 to 20"
    var STRING_INVALID_PHONE_NUMBER = "Phone number must start with 03 and be of length 11"
    fun isEmailValid(email: String?): Boolean {
        return try {
            Patterns.EMAIL_ADDRESS.matcher(email).matches()
        } catch (e: Exception) {
            false
        }
    }

    fun isPasswordValid(password: String?): Boolean {
        val regex = ("^(?=.*[0-9])"
                + "(?=.*[a-z])" //                + "(?=.*[A-Z])"
                + "([A-Z]?)"
                + "(?=.*[@#$%^&+=()])"
                + "(?=\\S+$).{8,20}$")
        val pattern = Pattern.compile(regex)
        val matcher = pattern.matcher(password)
        return matcher.matches()
    }

    fun isPhoneNumbeValid(phoneNumber: String?): Boolean {
        val pattern = Pattern.compile("(03)[0-9]{9}")
        val matcher = pattern.matcher(phoneNumber)
        return matcher.matches()
    }
}
