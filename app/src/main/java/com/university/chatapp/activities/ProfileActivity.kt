package com.university.chatapp.activities

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.university.chatapp.utils.FirebaseUtil

class ProfileActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val textView = TextView(this).apply {
            text = "Profile Activity\n\nUser ID: ${FirebaseUtil.currentUserId()}"
            textSize = 18f
            setPadding(32, 32, 32, 32)
        }

        setContentView(textView)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Profile"
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}