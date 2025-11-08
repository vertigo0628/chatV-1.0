package com.university.chatapp.activities

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import com.university.chatapp.R
import com.university.chatapp.adapters.MessageAdapter
import com.university.chatapp.models.Chat
import com.university.chatapp.models.Message
import com.university.chatapp.models.MessageType
import com.university.chatapp.models.User
import com.university.chatapp.utils.Constants
import com.university.chatapp.utils.FirebaseUtil
import de.hdodenhof.circleimageview.CircleImageView
import java.util.*

class ChatActivity : AppCompatActivity() {

    private lateinit var toolbar: Toolbar
    private lateinit var ivUserProfile: CircleImageView
    private lateinit var tvUserName: TextView
    private lateinit var tvStatus: TextView
    private lateinit var rvMessages: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var etMessage: EditText
    private lateinit var btnSend: ImageButton
    private lateinit var ibAttach: ImageButton
    private lateinit var ibCamera: ImageButton

    private lateinit var messageAdapter: MessageAdapter
    private val messages = mutableListOf<Message>()
    private var otherUser: User? = null
    private var chatId: String = ""
    private var messageListener: ListenerRegistration? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_chat)

        // Initialize views
        toolbar = findViewById(R.id.toolbar)
        ivUserProfile = findViewById(R.id.ivUserProfile)
        tvUserName = findViewById(R.id.tvUserName)
        tvStatus = findViewById(R.id.tvStatus)
        rvMessages = findViewById(R.id.rvMessages)
        progressBar = findViewById(R.id.progressBar)
        etMessage = findViewById(R.id.etMessage)
        btnSend = findViewById(R.id.btnSend)
        ibAttach = findViewById(R.id.ibAttach)
        ibCamera = findViewById(R.id.ibCamera)

        // Get user from intent
        otherUser = intent.getParcelableExtra(Constants.EXTRA_USER)

        if (otherUser == null) {
            Toast.makeText(this, "Error loading chat", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        chatId = FirebaseUtil.generateChatId(
            FirebaseUtil.currentUserId(),
            otherUser!!.uid
        )

        setupToolbar()
        setupRecyclerView()
        setupClickListeners()
        setupTypingIndicator()
        loadMessages()
        updateChatInfo()
    }

    private fun setupToolbar() {
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = ""

        tvUserName.text = otherUser?.name

        if (otherUser?.profileImage?.isNotEmpty() == true) {
            ivUserProfile.load(otherUser?.profileImage) {
                placeholder(R.drawable.ic_profile_placeholder)
                error(R.drawable.ic_profile_placeholder)
            }
        }

        // Show online status
        updateOnlineStatus()

        toolbar.setNavigationOnClickListener {
            finish()
        }

        // Click on profile to view user details
        ivUserProfile.setOnClickListener {
            // Open user profile activity
        }
    }

    private fun setupRecyclerView() {
        messageAdapter = MessageAdapter(this, messages, FirebaseUtil.currentUserId())
        rvMessages.apply {
            layoutManager = LinearLayoutManager(this@ChatActivity).apply {
                stackFromEnd = true
            }
            adapter = messageAdapter
        }
    }

    private fun setupClickListeners() {
        btnSend.setOnClickListener {
            sendTextMessage()
        }

        ibAttach.setOnClickListener {
            showAttachmentOptions()
        }

        ibCamera.setOnClickListener {
            Toast.makeText(this, "Camera disabled (no Storage)", Toast.LENGTH_SHORT).show()
            // openCamera()  // Removed
        }
    }

    private fun setupTypingIndicator() {
        etMessage.addTextChangedListener(object : TextWatcher {
            private var timer: Timer? = null

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s?.isNotEmpty() == true) {
                    updateTypingStatus(true)

                    timer?.cancel()
                    timer = Timer()
                    timer?.schedule(object : TimerTask() {
                        override fun run() {
                            updateTypingStatus(false)
                        }
                    }, Constants.TYPING_TIMEOUT)
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun updateTypingStatus(isTyping: Boolean) {
        val typingRef = FirebaseUtil.typingIndicatorRef(chatId)
        typingRef.child(FirebaseUtil.currentUserId()).setValue(isTyping)
    }

    private fun loadMessages() {
        messageListener = FirebaseUtil.messagesCollectionForChat(chatId)
            .orderBy("timestamp", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Toast.makeText(this, "Error loading messages", Toast.LENGTH_SHORT).show()
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    messages.clear()
                    for (doc in snapshot.documents) {
                        val message = doc.toObject(Message::class.java)
                        message?.let { messages.add(it) }
                    }
                    messageAdapter.notifyDataSetChanged()
                    rvMessages.scrollToPosition(messages.size - 1)

                    // Mark messages as read
                    markMessagesAsRead()
                }
            }
    }

    private fun sendTextMessage() {
        val messageText = etMessage.text.toString().trim()

        if (messageText.isEmpty()) return

        val message = Message(
            messageId = UUID.randomUUID().toString(),
            senderId = FirebaseUtil.currentUserId(),
            receiverId = otherUser!!.uid,
            chatId = chatId,
            message = messageText,
            timestamp = System.currentTimeMillis(),
            type = MessageType.TEXT,
            isSent = true
        )

        sendMessage(message)
        etMessage.text?.clear()
    }

    private fun sendMediaMessage(uri: Uri, type: MessageType) {
        progressBar.visibility = View.VISIBLE

        // Storage disabled - send text message instead
        Toast.makeText(this, "Image sharing requires Firebase Storage (billing required)", Toast.LENGTH_LONG).show()
        progressBar.visibility = View.GONE

        /* Original code with Storage:
        val fileName = "${System.currentTimeMillis()}_${UUID.randomUUID()}"
        val storageRef = FirebaseUtil.chatMediaRef().child(chatId).child(fileName)

        storageRef.putFile(uri)
            .addOnSuccessListener {
                storageRef.downloadUrl.addOnSuccessListener { downloadUri ->
                    val message = Message(
                        messageId = UUID.randomUUID().toString(),
                        senderId = FirebaseUtil.currentUserId(),
                        receiverId = otherUser!!.uid,
                        chatId = chatId,
                        message = "",
                        timestamp = System.currentTimeMillis(),
                        type = type,
                        mediaUrl = downloadUri.toString(),
                        isSent = true
                    )

                    sendMessage(message)
                    binding.progressBar.visibility = View.GONE
                }
            }
            .addOnFailureListener {
                binding.progressBar.visibility = View.GONE
                Toast.makeText(this, "Failed to upload media", Toast.LENGTH_SHORT).show()
            }
        */
    }

    private fun sendMessage(message: Message) {
        android.util.Log.d("ChatActivity", "Sending message: ${message.messageId}")

        // Save message
        FirebaseUtil.messagesCollectionForChat(chatId)
            .document(message.messageId)
            .set(message)
            .addOnSuccessListener {
                android.util.Log.d("ChatActivity", "Message saved successfully")
                updateChatLastMessage(message)
            }
            .addOnFailureListener { e ->
                android.util.Log.e("ChatActivity", "Failed to send message", e)
                Toast.makeText(this, "Failed to send message: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun updateChatLastMessage(message: Message) {
        val chatRef = FirebaseUtil.chatDocument(chatId)

        android.util.Log.d("ChatActivity", "Updating chat: $chatId")

        chatRef.get().addOnSuccessListener { doc ->
            val chat = if (doc.exists()) {
                android.util.Log.d("ChatActivity", "Chat exists, updating")
                doc.toObject(Chat::class.java) ?: createNewChat()
            } else {
                android.util.Log.d("ChatActivity", "Chat doesn't exist, creating new")
                createNewChat()
            }

            chat.lastMessage = when (message.type) {
                MessageType.TEXT -> message.message
                MessageType.IMAGE -> "📷 Photo"
                MessageType.VIDEO -> "🎥 Video"
                MessageType.AUDIO -> "🎵 Audio"
                MessageType.DOCUMENT -> "📄 Document"
                else -> "Message"
            }
            chat.lastMessageTime = message.timestamp
            chat.lastMessageSenderId = message.senderId
            chat.lastMessageType = message.type.name

            // Update unread count
            val unreadMap = chat.unreadCount.toMutableMap()
            val currentCount = unreadMap[otherUser!!.uid] ?: 0
            unreadMap[otherUser!!.uid] = currentCount + 1
            chat.unreadCount = unreadMap

            android.util.Log.d("ChatActivity", "Saving chat with participants: ${chat.participants}")

            chatRef.set(chat)
                .addOnSuccessListener {
                    android.util.Log.d("ChatActivity", "Chat updated successfully!")
                }
                .addOnFailureListener { e ->
                    android.util.Log.e("ChatActivity", "Failed to update chat", e)
                }
        }
    }

    private fun createNewChat(): Chat {
        return Chat(
            chatId = chatId,
            participants = listOf(FirebaseUtil.currentUserId(), otherUser!!.uid),
            createdAt = System.currentTimeMillis(),
            createdBy = FirebaseUtil.currentUserId()
        )
    }

    private fun markMessagesAsRead() {
        val unreadMessages = messages.filter {
            it.receiverId == FirebaseUtil.currentUserId() && !it.isRead
        }

        unreadMessages.forEach { message ->
            FirebaseUtil.messagesCollectionForChat(chatId)
                .document(message.messageId)
                .update("isRead", true)
        }

        // Reset unread count
        if (unreadMessages.isNotEmpty()) {
            FirebaseUtil.chatDocument(chatId).get().addOnSuccessListener { doc ->
                val chat = doc.toObject(Chat::class.java)
                if (chat != null) {
                    val unreadMap = chat.unreadCount.toMutableMap()
                    unreadMap[FirebaseUtil.currentUserId()] = 0
                    FirebaseUtil.chatDocument(chatId).update("unreadCount", unreadMap)
                }
            }
        }
    }

    private fun updateOnlineStatus() {
        FirebaseUtil.userDocument(otherUser!!.uid)
            .addSnapshotListener { snapshot, _ ->
                val user = snapshot?.toObject(User::class.java)
                if (user != null) {
                    if (user.isOnline) {
                        tvStatus.text = "Online"
                        tvStatus.visibility = View.VISIBLE
                    } else {
                        tvStatus.visibility = View.GONE
                    }
                }
            }
    }

    private fun updateChatInfo() {
        // Listen to typing indicator
        FirebaseUtil.typingIndicatorRef(chatId)
            .child(otherUser!!.uid)
            .addValueEventListener(object : com.google.firebase.database.ValueEventListener {
                override fun onDataChange(snapshot: com.google.firebase.database.DataSnapshot) {
                    val isTyping = snapshot.getValue(Boolean::class.java) ?: false
                    tvStatus.text = if (isTyping) "typing..." else "Online"
                }

                override fun onCancelled(error: com.google.firebase.database.DatabaseError) {}
            })
    }

    private fun showAttachmentOptions() {
        Toast.makeText(this, "Media sharing disabled (no Storage)", Toast.LENGTH_SHORT).show()
        /* Attachment options removed
        val options = arrayOf("Image", "Video", "Document", "Audio")
        val builder = androidx.appcompat.app.AlertDialog.Builder(this)
        builder.setTitle("Choose attachment")
        builder.setItems(options) { _, which ->
            when (which) {
                0 -> pickImage()
                1 -> pickVideo()
                2 -> pickDocument()
                3 -> pickAudio()
            }
        }
        builder.show()
        */
    }

    /* All picker functions removed
    private fun pickImage() {
        ImagePicker.with(this)
            .compress(1024)
            .createIntent { intent ->
                imagePickerLauncher.launch(intent)
            }
    }

    private fun pickVideo() {
        // Implement video picker
    }

    private fun pickDocument() {
        // Implement document picker
    }

    private fun pickAudio() {
        // Implement audio picker
    }

    private fun openCamera() {
        ImagePicker.with(this)
            .cameraOnly()
            .compress(1024)
            .createIntent { intent ->
                imagePickerLauncher.launch(intent)
            }
    }
    */

    override fun onDestroy() {
        super.onDestroy()
        messageListener?.remove()
        updateTypingStatus(false)
    }
}