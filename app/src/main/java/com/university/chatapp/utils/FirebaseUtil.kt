package com.university.chatapp.utils

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
// import com.google.firebase.storage.FirebaseStorage  // Commented out - no billing
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.messaging.FirebaseMessaging
import com.university.chatapp.models.User

object FirebaseUtil {

    // Firebase instances
    val auth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }
    val firestore: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }
    // val storage: FirebaseStorage by lazy { FirebaseStorage.getInstance() }  // Disabled
    val database: FirebaseDatabase by lazy { FirebaseDatabase.getInstance() }
    val messaging: FirebaseMessaging by lazy { FirebaseMessaging.getInstance() }

    // Current user ID
    fun currentUserId(): String {
        return auth.currentUser?.uid ?: ""
    }

    // Check if user is logged in
    fun isUserLoggedIn(): Boolean {
        return auth.currentUser != null
    }

    // Firestore collections
    fun usersCollection() = firestore.collection(Constants.COLLECTION_USERS)
    fun chatsCollection() = firestore.collection(Constants.COLLECTION_CHATS)
    fun messagesCollection() = firestore.collection(Constants.COLLECTION_MESSAGES)
    fun statusCollection() = firestore.collection(Constants.COLLECTION_STATUS)

    // User document reference
    fun currentUserDocument() = usersCollection().document(currentUserId())

    // Get user document by ID
    fun userDocument(userId: String) = usersCollection().document(userId)

    // Chat document reference
    fun chatDocument(chatId: String) = chatsCollection().document(chatId)

    // Messages collection for a chat
    fun messagesCollectionForChat(chatId: String) =
        chatDocument(chatId).collection(Constants.COLLECTION_MESSAGES)

    // Generate chat ID between two users
    fun generateChatId(userId1: String, userId2: String): String {
        return if (userId1 < userId2) {
            "${userId1}_${userId2}"
        } else {
            "${userId2}_${userId1}"
        }
    }

    // Storage references - COMMENTED OUT (no billing plan)
    // fun profileImagesRef() = storage.reference.child(Constants.STORAGE_PROFILE_IMAGES)
    // fun chatMediaRef() = storage.reference.child(Constants.STORAGE_CHAT_MEDIA)
    // fun statusMediaRef() = storage.reference.child(Constants.STORAGE_STATUS_MEDIA)

    // Alternative: Use placeholder URLs for now
    fun getPlaceholderProfileImage() = "https://ui-avatars.com/api/?name=User&size=200"

    // Realtime Database references
    fun userPresenceRef(userId: String) =
        database.getReference(Constants.DB_PRESENCE).child(userId)

    fun typingIndicatorRef(chatId: String) =
        database.getReference(Constants.DB_TYPING).child(chatId)

    // Update user online status
    suspend fun updateUserStatus(isOnline: Boolean) {
        if (!isUserLoggedIn()) return

        val updates = hashMapOf<String, Any>(
            "isOnline" to isOnline,
            "lastSeen" to System.currentTimeMillis()
        )

        currentUserDocument().update(updates)
        userPresenceRef(currentUserId()).setValue(isOnline)
    }

    // Sign out
    fun signOut() {
        auth.signOut()
    }
}