package com.university.chatapp.activities

import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import coil.load
import com.university.chatapp.R
import com.university.chatapp.models.User
import com.university.chatapp.utils.FirebaseUtil
import de.hdodenhof.circleimageview.CircleImageView

class ProfileActivity : AppCompatActivity() {

    private lateinit var toolbar: Toolbar
    private lateinit var ivProfile: CircleImageView
    private lateinit var tvName: TextView
    private lateinit var tvEmail: TextView
    private lateinit var tvPhone: TextView
    private lateinit var tvAbout: TextView
    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        toolbar = findViewById(R.id.toolbar)
        ivProfile = findViewById(R.id.ivProfile)
        tvName = findViewById(R.id.tvName)
        tvEmail = findViewById(R.id.tvEmail)
        tvPhone = findViewById(R.id.tvPhone)
        tvAbout = findViewById(R.id.tvAbout)
        progressBar = findViewById(R.id.progressBar)

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "My Profile"

        toolbar.setNavigationOnClickListener {
            finish()
        }

        loadProfile()
    }

    private fun loadProfile() {
        progressBar.visibility = View.VISIBLE

        FirebaseUtil.currentUserDocument()
            .get()
            .addOnSuccessListener { doc ->
                progressBar.visibility = View.GONE

                val user = doc.toObject(User::class.java)
                if (user != null) {
                    displayProfile(user)
                } else {
                    Toast.makeText(this, "Error loading profile", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { e ->
                progressBar.visibility = View.GONE
                Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun displayProfile(user: User) {
        tvName.text = user.name
        tvEmail.text = user.email
        tvPhone.text = user.phone
        tvAbout.text = user.about

        if (user.profileImage.isNotEmpty()) {
            ivProfile.load(user.profileImage) {
                placeholder(R.drawable.ic_profile_placeholder)
                error(R.drawable.ic_profile_placeholder)
            }
        } else {
            ivProfile.setImageResource(R.drawable.ic_profile_placeholder)
        }
    }
}