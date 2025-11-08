package com.university.chatapp.activities

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.university.chatapp.R
import com.university.chatapp.models.User
import com.university.chatapp.utils.FirebaseUtil

class RegisterActivity : AppCompatActivity() {

    private lateinit var ivProfile: ImageView
    private lateinit var etName: EditText
    private lateinit var etEmail: EditText
    private lateinit var etPhone: EditText
    private lateinit var etPassword: EditText
    private lateinit var etConfirmPassword: EditText
    private lateinit var btnRegister: Button
    private lateinit var tvLogin: TextView
    private lateinit var progressBar: ProgressBar
    private lateinit var auth: FirebaseAuth
    private var selectedImageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_register)

        // Initialize views
        ivProfile = findViewById(R.id.ivProfile)
        etName = findViewById(R.id.etName)
        etEmail = findViewById(R.id.etEmail)
        etPhone = findViewById(R.id.etPhone)
        etPassword = findViewById(R.id.etPassword)
        etConfirmPassword = findViewById(R.id.etConfirmPassword)
        btnRegister = findViewById(R.id.btnRegister)
        tvLogin = findViewById(R.id.tvLogin)
        progressBar = findViewById(R.id.progressBar)

        auth = FirebaseAuth.getInstance()

        setupClickListeners()
    }

    private fun setupClickListeners() {
        ivProfile.setOnClickListener {
            // Image picker removed
            Toast.makeText(this, "Profile image upload disabled (no Storage)", Toast.LENGTH_SHORT).show()
            // pickImage()  // Commented out
        }

        btnRegister.setOnClickListener {
            val name = etName.text.toString().trim()
            val email = etEmail.text.toString().trim()
            val phone = etPhone.text.toString().trim()
            val password = etPassword.text.toString().trim()
            val confirmPassword = etConfirmPassword.text.toString().trim()

            if (validateInputs(name, email, phone, password, confirmPassword)) {
                registerUser(name, email, phone, password)
            }
        }

        tvLogin.setOnClickListener {
            finish()
        }
    }

    private fun validateInputs(
        name: String,
        email: String,
        phone: String,
        password: String,
        confirmPassword: String
    ): Boolean {
        if (name.isEmpty()) {
            etName.error = "Name is required"
            return false
        }

        if (email.isEmpty()) {
            etEmail.error = "Email is required"
            return false
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.error = "Invalid email format"
            return false
        }

        if (phone.isEmpty()) {
            etPhone.error = "Phone number is required"
            return false
        }

        if (phone.length < 10) {
            etPhone.error = "Invalid phone number"
            return false
        }

        if (password.isEmpty()) {
            etPassword.error = "Password is required"
            return false
        }

        if (password.length < 6) {
            etPassword.error = "Password must be at least 6 characters"
            return false
        }

        if (confirmPassword.isEmpty()) {
            etConfirmPassword.error = "Please confirm password"
            return false
        }

        if (password != confirmPassword) {
            etConfirmPassword.error = "Passwords do not match"
            return false
        }

        return true
    }

    private fun registerUser(name: String, email: String, phone: String, password: String) {
        btnRegister.isEnabled = false
        progressBar.visibility = View.VISIBLE

        auth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener { authResult ->
                val user = authResult.user
                if (user != null) {
                    // Update profile
                    val profileUpdates = UserProfileChangeRequest.Builder()
                        .setDisplayName(name)
                        .build()

                    user.updateProfile(profileUpdates)
                        .addOnSuccessListener {
                            uploadProfileImageAndSaveUser(user.uid, name, email, phone)
                        }
                        .addOnFailureListener { e ->
                            handleRegistrationFailure(e.message)
                        }
                } else {
                    handleRegistrationFailure("User creation failed")
                }
            }
            .addOnFailureListener { e ->
                handleRegistrationFailure(e.message)
            }
    }

    private fun uploadProfileImageAndSaveUser(
        uid: String,
        name: String,
        email: String,
        phone: String
    ) {
        // Storage disabled - use placeholder or skip image upload
        val profileImageUrl = if (selectedImageUri != null) {
            // For now, just use a placeholder URL
            "https://ui-avatars.com/api/?name=${name.replace(" ", "+")}&size=200&background=075E54&color=fff"
        } else {
            ""
        }

        saveUserToFirestore(uid, name, email, phone, profileImageUrl)

        /* Original code with Storage (requires billing):
        if (selectedImageUri != null) {
            val imageRef = FirebaseUtil.profileImagesRef().child("$uid.jpg")

            imageRef.putFile(selectedImageUri!!)
                .addOnSuccessListener {
                    imageRef.downloadUrl.addOnSuccessListener { uri ->
                        saveUserToFirestore(uid, name, email, phone, uri.toString())
                    }
                }
                .addOnFailureListener {
                    saveUserToFirestore(uid, name, email, phone, "")
                }
        } else {
            saveUserToFirestore(uid, name, email, phone, "")
        }
        */
    }

    private fun saveUserToFirestore(
        uid: String,
        name: String,
        email: String,
        phone: String,
        profileImage: String
    ) {
        val user = User(
            uid = uid,
            name = name,
            email = email,
            phone = phone,
            profileImage = profileImage,
            createdAt = System.currentTimeMillis()
        )

        FirebaseUtil.userDocument(uid).set(user)
            .addOnSuccessListener {
                Toast.makeText(this, "Registration successful", Toast.LENGTH_SHORT).show()
                navigateToMain()
            }
            .addOnFailureListener { e ->
                handleRegistrationFailure(e.message)
            }
    }

    private fun handleRegistrationFailure(message: String?) {
        btnRegister.isEnabled = true
        progressBar.visibility = View.GONE
        Toast.makeText(this, "Registration failed: $message", Toast.LENGTH_LONG).show()
    }

    private fun navigateToMain() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}