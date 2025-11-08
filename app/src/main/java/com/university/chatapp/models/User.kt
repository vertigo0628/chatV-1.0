package com.university.chatapp.models

import android.os.Parcelable
import com.google.firebase.firestore.PropertyName
import kotlinx.parcelize.Parcelize

@Parcelize
data class User(
    @PropertyName("uid") var uid: String = "",
    @PropertyName("name") var name: String = "",
    @PropertyName("phone") var phone: String = "",
    @PropertyName("email") var email: String = "",
    @PropertyName("profileImage") var profileImage: String = "",
    @PropertyName("about") var about: String = "Hey there! I am using ChatApp",
    @PropertyName("isOnline") var isOnline: Boolean = false,
    @PropertyName("lastSeen") var lastSeen: Long = System.currentTimeMillis(),
    @PropertyName("fcmToken") var fcmToken: String = "",
    @PropertyName("createdAt") var createdAt: Long = System.currentTimeMillis(),
    @PropertyName("contacts") var contacts: List<String> = emptyList(),
    @PropertyName("blockedUsers") var blockedUsers: List<String> = emptyList()
) : Parcelable {

    fun toMap(): Map<String, Any?> {
        return hashMapOf(
            "uid" to uid,
            "name" to name,
            "phone" to phone,
            "email" to email,
            "profileImage" to profileImage,
            "about" to about,
            "isOnline" to isOnline,
            "lastSeen" to lastSeen,
            "fcmToken" to fcmToken,
            "createdAt" to createdAt,
            "contacts" to contacts,
            "blockedUsers" to blockedUsers
        )
    }
}