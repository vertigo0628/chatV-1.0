package com.university.chatapp.models

import android.os.Parcelable
import com.google.firebase.firestore.PropertyName
import kotlinx.parcelize.Parcelize

@Parcelize
data class Chat(
    @PropertyName("chatId") var chatId: String = "",
    @PropertyName("participants") var participants: List<String> = emptyList(),
    @PropertyName("isGroup") var isGroup: Boolean = false,
    @PropertyName("groupName") var groupName: String = "",
    @PropertyName("groupIcon") var groupIcon: String = "",
    @PropertyName("groupAdmin") var groupAdmin: String = "",
    @PropertyName("lastMessage") var lastMessage: String = "",
    @PropertyName("lastMessageType") var lastMessageType: String = "TEXT",
    @PropertyName("lastMessageTime") var lastMessageTime: Long = System.currentTimeMillis(),
    @PropertyName("lastMessageSenderId") var lastMessageSenderId: String = "",
    @PropertyName("unreadCount") var unreadCount: Map<String, Int> = emptyMap(),
    @PropertyName("createdAt") var createdAt: Long = System.currentTimeMillis(),
    @PropertyName("createdBy") var createdBy: String = "",
    @PropertyName("typingUsers") var typingUsers: List<String> = emptyList(),
    @PropertyName("muteNotifications") var muteNotifications: Map<String, Boolean> = emptyMap()
) : Parcelable {

    fun toMap(): Map<String, Any?> {
        return hashMapOf(
            "chatId" to chatId,
            "participants" to participants,
            "isGroup" to isGroup,
            "groupName" to groupName,
            "groupIcon" to groupIcon,
            "groupAdmin" to groupAdmin,
            "lastMessage" to lastMessage,
            "lastMessageType" to lastMessageType,
            "lastMessageTime" to lastMessageTime,
            "lastMessageSenderId" to lastMessageSenderId,
            "unreadCount" to unreadCount,
            "createdAt" to createdAt,
            "createdBy" to createdBy,
            "typingUsers" to typingUsers,
            "muteNotifications" to muteNotifications
        )
    }

    fun getOtherParticipantId(currentUserId: String): String {
        return participants.firstOrNull { it != currentUserId } ?: ""
    }
}