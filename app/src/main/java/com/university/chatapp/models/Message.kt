package com.university.chatapp.models

import android.os.Parcelable
import com.google.firebase.firestore.PropertyName
import kotlinx.parcelize.Parcelize

@Parcelize
data class Message(
    @PropertyName("messageId") var messageId: String = "",
    @PropertyName("senderId") var senderId: String = "",
    @PropertyName("receiverId") var receiverId: String = "",
    @PropertyName("chatId") var chatId: String = "",
    @PropertyName("message") var message: String = "",
    @PropertyName("timestamp") var timestamp: Long = System.currentTimeMillis(),
    @PropertyName("type") var type: MessageType = MessageType.TEXT,
    @PropertyName("mediaUrl") var mediaUrl: String = "",
    @PropertyName("thumbnailUrl") var thumbnailUrl: String = "",
    @PropertyName("isRead") var isRead: Boolean = false,
    @PropertyName("isDelivered") var isDelivered: Boolean = false,
    @PropertyName("isSent") var isSent: Boolean = false,
    @PropertyName("isDeleted") var isDeleted: Boolean = false,
    @PropertyName("replyTo") var replyTo: String? = null,
    @PropertyName("duration") var duration: Long = 0,
    @PropertyName("fileName") var fileName: String = "",
    @PropertyName("fileSize") var fileSize: Long = 0
) : Parcelable {

    fun toMap(): Map<String, Any?> {
        return hashMapOf(
            "messageId" to messageId,
            "senderId" to senderId,
            "receiverId" to receiverId,
            "chatId" to chatId,
            "message" to message,
            "timestamp" to timestamp,
            "type" to type.name,
            "mediaUrl" to mediaUrl,
            "thumbnailUrl" to thumbnailUrl,
            "isRead" to isRead,
            "isDelivered" to isDelivered,
            "isSent" to isSent,
            "isDeleted" to isDeleted,
            "replyTo" to replyTo,
            "duration" to duration,
            "fileName" to fileName,
            "fileSize" to fileSize
        )
    }
}

enum class MessageType {
    TEXT,
    IMAGE,
    VIDEO,
    AUDIO,
    DOCUMENT,
    LOCATION,
    CONTACT
}