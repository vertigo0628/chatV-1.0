package com.university.chatapp.activities

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.google.android.material.tabs.TabLayout
import com.google.firebase.messaging.FirebaseMessaging
import com.university.chatapp.R
import com.university.chatapp.fragments.CallsFragment
import com.university.chatapp.fragments.ChatsFragment
import com.university.chatapp.fragments.StatusFragment
import com.university.chatapp.utils.FirebaseUtil
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var toolbar: Toolbar
    private lateinit var tabLayout: TabLayout

    private val chatsFragment = ChatsFragment()
    private val statusFragment = StatusFragment()
    private val callsFragment = CallsFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        try {
            android.util.Log.d("MainActivity", "MainActivity onCreate started")

            // Check if user is logged in
            if (!FirebaseUtil.isUserLoggedIn()) {
                android.util.Log.d("MainActivity", "User not logged in, navigating to login")
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
                return
            }

            setContentView(R.layout.activity_main)

            toolbar = findViewById(R.id.toolbar)
            tabLayout = findViewById(R.id.tabLayout)

            setSupportActionBar(toolbar)
            supportActionBar?.title = "ChatApp"

            setupTabs()
            setupFCM()
            updateUserStatus(true)

            // Test Firebase connection (remove after testing)
            testFirebaseConnection()

            android.util.Log.d("MainActivity", "MainActivity initialized successfully")
        } catch (e: Exception) {
            android.util.Log.e("MainActivity", "Error in onCreate", e)
            android.widget.Toast.makeText(this, "Error: ${e.message}", android.widget.Toast.LENGTH_LONG).show()
        }
    }

    private fun setupTabs() {
        // Set default fragment
        loadFragment(chatsFragment)

        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                when (tab?.position) {
                    0 -> loadFragment(chatsFragment)
                    1 -> loadFragment(statusFragment)
                    2 -> loadFragment(callsFragment)
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })
    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .commit()
    }

    private fun setupFCM() {
        FirebaseMessaging.getInstance().token.addOnSuccessListener { token ->
            // Update FCM token in Firestore
            if (FirebaseUtil.isUserLoggedIn()) {
                FirebaseUtil.currentUserDocument()
                    .update("fcmToken", token)
                    .addOnSuccessListener {
                        println("FCM Token updated successfully")
                    }
                    .addOnFailureListener { e ->
                        println("Failed to update FCM token: ${e.message}")
                    }
            }
        }
    }

    private fun updateUserStatus(isOnline: Boolean) {
        lifecycleScope.launch {
            try {
                FirebaseUtil.updateUserStatus(isOnline)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_profile -> {
                startActivity(Intent(this, ProfileActivity::class.java))
                true
            }
            R.id.action_settings -> {
                // Open settings activity
                true
            }
            R.id.action_logout -> {
                logout()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun logout() {
        updateUserStatus(false)
        FirebaseUtil.signOut()
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }

    override fun onResume() {
        super.onResume()
        updateUserStatus(true)
    }

    override fun onPause() {
        super.onPause()
        updateUserStatus(false)
    }

    override fun onDestroy() {
        super.onDestroy()
        updateUserStatus(false)
    }

    private fun testFirebaseConnection() {
        android.util.Log.d("MainActivity", "Testing Firebase connection...")
        try {
            val auth = com.google.firebase.auth.FirebaseAuth.getInstance()
            val firestore = com.google.firebase.firestore.FirebaseFirestore.getInstance()
            android.util.Log.d("MainActivity", "✅ Firebase connected successfully!")
            android.util.Log.d("MainActivity", "Auth: ${auth.app.name}, Firestore: ${firestore.app.name}")
        } catch (e: Exception) {
            android.util.Log.e("MainActivity", "❌ Firebase error: ${e.message}")
        }
    }
}