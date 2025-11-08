package com.university.chatapp.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
// import com.bumptech.glide.Glide  // Removed
import coil.load  // Added Coil
import com.university.chatapp.R
import com.university.chatapp.activities.ChatActivity
import com.university.chatapp.databinding.ItemChatBinding
import com.university.chatapp.models.Chat
import com.university.chatapp.models.User
import com.university.chatapp.utils.Constants
import com.university.chatapp.utils.FirebaseUtil
import java.text.SimpleDateFormat
import java.util.*

class ChatAdapter(
    private val context: Context,
    private val chats: MutableList<Chat>
) : RecyclerView.Adapter<ChatAdapter.ChatViewHolder>() {

    private val userCache = mutableMapOf<String, User>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        val binding = ItemChatBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ChatViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        holder.bind(chats[position])
    }

    override fun getItemCount() = chats.size

    fun updateChats(newChats: List<Chat>) {
        chats.clear()
        chats.addAll(newChats)
        notifyDataSetChanged()
    }

    inner class ChatViewHolder(
        private val binding: ItemChatBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(chat: Chat) {
            val otherUserId = chat.getOtherParticipantId(FirebaseUtil.currentUserId())

            // Check cache first
            if (userCache.containsKey(otherUserId)) {
                displayChatInfo(chat, userCache[otherUserId]!!)
            } else {
                // Load user from Firestore
                FirebaseUtil.userDocument(otherUserId).get()
                    .addOnSuccessListener { doc ->
                        val user = doc.toObject(User::class.java)
                        if (user != null) {
                            userCache[otherUserId] = user
                            displayChatInfo(chat, user)
                        }
                    }
            }

            binding.root.setOnClickListener {
                val user = userCache[otherUserId]
                if (user != null) {
                    val intent = Intent(context, ChatActivity::class.java)
                    intent.putExtra(Constants.EXTRA_USER, user)
                    context.startActivity(intent)
                }
            }
        }

        private fun displayChatInfo(chat: Chat, user: User) {
            // User name
            binding.tvName.text = if (chat.isGroup) chat.groupName else user.name

            // Profile image
            val imageUrl = if (chat.isGroup) chat.groupIcon else user.profileImage
            if (imageUrl.isNotEmpty()) {
                binding.ivProfile.load(imageUrl) {
                    placeholder(R.drawable.ic_profile_placeholder)
                    error(R.drawable.ic_profile_placeholder)
                }
            } else {
                binding.ivProfile.setImageResource(R.drawable.ic_profile_placeholder)
            }

            // Last message
            val lastMessagePrefix = if (chat.lastMessageSenderId == FirebaseUtil.currentUserId()) {
                "You: "
            } else {
                ""
            }
            binding.tvLastMessage.text = "$lastMessagePrefix${chat.lastMessage}"

            // Time
            binding.tvTime.text = formatTime(chat.lastMessageTime)

            // Unread count
            val unreadCount = chat.unreadCount[FirebaseUtil.currentUserId()] ?: 0
            if (unreadCount > 0) {
                binding.tvUnreadCount.visibility = View.VISIBLE
                binding.tvUnreadCount.text = if (unreadCount > 99) "99+" else unreadCount.toString()
            } else {
                binding.tvUnreadCount.visibility = View.GONE
            }

            // Online indicator
            if (user.isOnline && !chat.isGroup) {
                binding.viewOnlineIndicator.visibility = View.VISIBLE
            } else {
                binding.viewOnlineIndicator.visibility = View.GONE
            }
        }

        private fun formatTime(timestamp: Long): String {
            val calendar = Calendar.getInstance()
            calendar.timeInMillis = timestamp

            val now = Calendar.getInstance()

            return when {
                calendar.get(Calendar.DAY_OF_YEAR) == now.get(Calendar.DAY_OF_YEAR) &&
                        calendar.get(Calendar.YEAR) == now.get(Calendar.YEAR) -> {
                    SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date(timestamp))
                }
                calendar.get(Calendar.YEAR) == now.get(Calendar.YEAR) -> {
                    SimpleDateFormat("MMM dd", Locale.getDefault()).format(Date(timestamp))
                }
                else -> {
                    SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(Date(timestamp))
                }
            }
        }
    }
}