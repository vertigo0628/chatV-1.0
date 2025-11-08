package com.university.chatapp.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import com.university.chatapp.R
import com.university.chatapp.adapters.ChatAdapter
import com.university.chatapp.models.Chat
import com.university.chatapp.utils.FirebaseUtil

class ChatsFragment : Fragment() {

    private lateinit var rvChats: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var tvEmpty: TextView
    private lateinit var swipeRefresh: SwipeRefreshLayout
    private lateinit var fabNewChat: FloatingActionButton

    private lateinit var chatAdapter: ChatAdapter
    private val chats = mutableListOf<Chat>()
    private var chatListener: ListenerRegistration? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_chats, container, false)

        // Initialize views
        rvChats = view.findViewById(R.id.rvChats)
        progressBar = view.findViewById(R.id.progressBar)
        tvEmpty = view.findViewById(R.id.tvEmpty)
        swipeRefresh = view.findViewById(R.id.swipeRefresh)
        fabNewChat = view.findViewById(R.id.fabNewChat)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupClickListeners()
        loadChats()
    }

    private fun setupRecyclerView() {
        chatAdapter = ChatAdapter(requireContext(), chats)
        rvChats.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = chatAdapter
        }
    }

    private fun setupClickListeners() {
        fabNewChat.setOnClickListener {
            // Open contacts/new chat activity
            // startActivity(Intent(requireContext(), ContactsActivity::class.java))
        }

        swipeRefresh.setOnRefreshListener {
            loadChats()
        }
    }

    private fun loadChats() {
        progressBar.visibility = View.VISIBLE

        val currentUserId = FirebaseUtil.currentUserId()

        chatListener = FirebaseUtil.chatsCollection()
            .whereArrayContains("participants", currentUserId)
            .orderBy("lastMessageTime", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                progressBar.visibility = View.GONE
                swipeRefresh.isRefreshing = false

                if (error != null) {
                    showError("Error loading chats")
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    chats.clear()
                    for (doc in snapshot.documents) {
                        val chat = doc.toObject(Chat::class.java)
                        chat?.let { chats.add(it) }
                    }

                    if (chats.isEmpty()) {
                        showEmptyState()
                    } else {
                        hideEmptyState()
                    }

                    chatAdapter.updateChats(chats)
                }
            }
    }

    private fun showEmptyState() {
        tvEmpty.visibility = View.VISIBLE
        rvChats.visibility = View.GONE
    }

    private fun hideEmptyState() {
        tvEmpty.visibility = View.GONE
        rvChats.visibility = View.VISIBLE
    }

    private fun showError(message: String) {
        android.widget.Toast.makeText(requireContext(), message, android.widget.Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        chatListener?.remove()
    }
}