package com.university.chatapp.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.university.chatapp.R
import com.university.chatapp.adapters.ContactAdapter
import com.university.chatapp.models.User
import com.university.chatapp.utils.Constants
import com.university.chatapp.utils.FirebaseUtil

class ContactsActivity : AppCompatActivity() {

    private lateinit var toolbar: Toolbar
    private lateinit var rvContacts: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var tvEmpty: TextView

    private lateinit var contactAdapter: ContactAdapter
    private val contacts = mutableListOf<User>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contacts)

        toolbar = findViewById(R.id.toolbar)
        rvContacts = findViewById(R.id.rvContacts)
        progressBar = findViewById(R.id.progressBar)
        tvEmpty = findViewById(R.id.tvEmpty)

        setSupportActionBar(toolbar)
        supportActionBar?.title = "Select Contact"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        toolbar.setNavigationOnClickListener {
            finish()
        }

        setupRecyclerView()
        loadContacts()
    }

    private fun setupRecyclerView() {
        contactAdapter = ContactAdapter(this, contacts) { user ->
            // When user clicks on a contact
            openChat(user)
        }

        rvContacts.apply {
            layoutManager = LinearLayoutManager(this@ContactsActivity)
            adapter = contactAdapter
        }
    }

    private fun loadContacts() {
        progressBar.visibility = View.VISIBLE

        val currentUserId = FirebaseUtil.currentUserId()

        FirebaseUtil.usersCollection()
            .get()
            .addOnSuccessListener { snapshot ->
                progressBar.visibility = View.GONE

                contacts.clear()
                for (doc in snapshot.documents) {
                    val user = doc.toObject(User::class.java)
                    // Don't show current user in the list
                    if (user != null && user.uid != currentUserId) {
                        contacts.add(user)
                    }
                }

                if (contacts.isEmpty()) {
                    showEmptyState()
                } else {
                    hideEmptyState()
                }

                contactAdapter.notifyDataSetChanged()
            }
            .addOnFailureListener { e ->
                progressBar.visibility = View.GONE
                android.util.Log.e("ContactsActivity", "Error loading contacts", e)
                android.widget.Toast.makeText(
                    this,
                    "Error loading contacts: ${e.message}",
                    android.widget.Toast.LENGTH_SHORT
                ).show()
                showEmptyState()
            }
    }

    private fun openChat(user: User) {
        // Open ChatActivity with the selected user
        val intent = Intent(this, ChatActivity::class.java)
        intent.putExtra(Constants.EXTRA_USER, user)
        startActivity(intent)
        finish()
    }

    private fun showEmptyState() {
        tvEmpty.visibility = View.VISIBLE
        rvContacts.visibility = View.GONE
    }

    private fun hideEmptyState() {
        tvEmpty.visibility = View.GONE
        rvContacts.visibility = View.VISIBLE
    }
}