package com.university.chatapp.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.university.chatapp.R
import com.university.chatapp.models.User
import de.hdodenhof.circleimageview.CircleImageView

class ContactAdapter(
    private val context: Context,
    private val contacts: List<User>,
    private val onContactClick: (User) -> Unit
) : RecyclerView.Adapter<ContactAdapter.ContactViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_contact, parent, false)
        return ContactViewHolder(view)
    }

    override fun onBindViewHolder(holder: ContactViewHolder, position: Int) {
        holder.bind(contacts[position])
    }

    override fun getItemCount() = contacts.size

    inner class ContactViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val ivProfile: CircleImageView = itemView.findViewById(R.id.ivProfile)
        private val tvName: TextView = itemView.findViewById(R.id.tvName)
        private val tvAbout: TextView = itemView.findViewById(R.id.tvAbout)
        private val viewOnlineIndicator: View = itemView.findViewById(R.id.viewOnlineIndicator)

        fun bind(user: User) {
            tvName.text = user.name
            tvAbout.text = user.about

            // Load profile image
            if (user.profileImage.isNotEmpty()) {
                ivProfile.load(user.profileImage) {
                    placeholder(R.drawable.ic_profile_placeholder)
                    error(R.drawable.ic_profile_placeholder)
                }
            } else {
                ivProfile.setImageResource(R.drawable.ic_profile_placeholder)
            }

            // Show online indicator
            if (user.isOnline) {
                viewOnlineIndicator.visibility = View.VISIBLE
            } else {
                viewOnlineIndicator.visibility = View.GONE
            }

            // Click listener
            itemView.setOnClickListener {
                onContactClick(user)
            }
        }
    }
}