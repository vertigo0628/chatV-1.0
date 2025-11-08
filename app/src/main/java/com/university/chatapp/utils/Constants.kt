package com.university.chatapp.utils

object Constants {

    // Firestore Collections
    const val COLLECTION_USERS = "users"
    const val COLLECTION_CHATS = "chats"
    const val COLLECTION_MESSAGES = "messages"
    const val COLLECTION_STATUS = "status"
    const val COLLECTION_GROUPS = "groups"

    // Storage Paths
    const val STORAGE_PROFILE_IMAGES = "profile_images"
    const val STORAGE_CHAT_MEDIA = "chat_media"
    const val STORAGE_STATUS_MEDIA = "status_media"

    // Realtime Database Paths
    const val DB_PRESENCE = "presence"
    const val DB_TYPING = "typing"

    // Shared Preferences
    const val PREFS_NAME = "ChatAppPrefs"
    const val PREF_USER_ID = "userId"
    const val PREF_USER_NAME = "userName"
    const val PREF_USER_IMAGE = "userImage"
    const val PREF_IS_LOGGED_IN = "isLoggedIn"
    const val PREF_FCM_TOKEN = "fcmToken"

    // Intent Extras
    const val EXTRA_USER = "extra_user"
    const val EXTRA_CHAT_ID = "extra_chat_id"
    const val EXTRA_USER_ID = "extra_user_id"
    const val EXTRA_STATUS = "extra_status"
    const val EXTRA_STATUS_INDEX = "extra_status_index"

    // Request Codes
    const val REQUEST_IMAGE_CAPTURE = 1001
    const val REQUEST_IMAGE_PICK = 1002
    const val REQUEST_VIDEO_PICK = 1003
    const val REQUEST_DOCUMENT_PICK = 1004
    const val REQUEST_AUDIO_PICK = 1005
    const val REQUEST_PERMISSIONS = 1006

    // Notification
    const val NOTIFICATION_CHANNEL_ID = "chat_messages"
    const val NOTIFICATION_CHANNEL_NAME = "Chat Messages"

    // Message Types
    const val MSG_TYPE_TEXT = "TEXT"
    const val MSG_TYPE_IMAGE = "IMAGE"
    const val MSG_TYPE_VIDEO = "VIDEO"
    const val MSG_TYPE_AUDIO = "AUDIO"
    const val MSG_TYPE_DOCUMENT = "DOCUMENT"
    const val MSG_TYPE_LOCATION = "LOCATION"
    const val MSG_TYPE_CONTACT = "CONTACT"

    // File Size Limits (in MB)
    const val MAX_IMAGE_SIZE = 5
    const val MAX_VIDEO_SIZE = 16
    const val MAX_DOCUMENT_SIZE = 20
    const val MAX_AUDIO_SIZE = 16

    // Status Duration (24 hours in milliseconds)
    const val STATUS_DURATION = 24 * 60 * 60 * 1000L

    // Pagination
    const val MESSAGES_PAGE_SIZE = 50
    const val CHATS_PAGE_SIZE = 30

    // Typing Indicator Timeout (milliseconds)
    const val TYPING_TIMEOUT = 2000L

    // Date Formats
    const val DATE_FORMAT_FULL = "MMM dd, yyyy hh:mm a"
    const val DATE_FORMAT_SHORT = "hh:mm a"
    const val DATE_FORMAT_DAY = "MMM dd"

    // Firebase Functions
    const val FUNCTION_SEND_NOTIFICATION = "sendNotification"
    const val FUNCTION_DELETE_ACCOUNT = "deleteAccount"

    // Error Messages
    const val ERROR_NETWORK = "No internet connection"
    const val ERROR_AUTHENTICATION = "Authentication failed"
    const val ERROR_PERMISSION_DENIED = "Permission denied"
    const val ERROR_FILE_TOO_LARGE = "File size exceeds limit"
    const val ERROR_UNKNOWN = "An error occurred"
}