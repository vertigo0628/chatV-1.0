package com.university.chatapp.models

import android.os.Parcelable
import com.google.firebase.firestore.PropertyName
import kotlinx.parcelize.Parcelize

@Parcelize
data class Status(
    @PropertyName("statusId") var statusId: String = "",
    @PropertyName("userId") var userId: String = "",
    @PropertyName("userName") var userName: String = "",
    @PropertyName("userImage") var userImage: String = "",
    @PropertyName("items") var items: List<StatusItem> = emptyList(),
    @PropertyName("timestamp") var timestamp: Long = System.currentTimeMillis()
) : Parcelable

@Parcelize
data class StatusItem(
    @PropertyName("itemId") var itemId: String = "",
    @PropertyName("type") var type: StatusType = StatusType.IMAGE,
    @PropertyName("mediaUrl") var mediaUrl: String = "",
    @PropertyName("caption") var caption: String = "",
    @PropertyName("timestamp") var timestamp: Long = System.currentTimeMillis(),
    @PropertyName("viewedBy") var viewedBy: List<String> = emptyList(),
    @PropertyName("duration") var duration: Long = 24 * 60 * 60 * 1000 // 24 hours
) : Parcelable {

    fun isExpired(): Boolean {
        return System.currentTimeMillis() - timestamp > duration
    }
}

enum class StatusType {
    TEXT,
    IMAGE,
    VIDEO
}