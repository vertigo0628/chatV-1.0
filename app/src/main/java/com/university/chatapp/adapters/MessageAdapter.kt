package com.university.chatapp.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.university.chatapp.R
import com.university.chatapp.models.Message
import com.university.chatapp.models.MessageType
import java.text.SimpleDateFormat
import java.util.*

class MessageAdapter(
    private val context: Context,
    private val messages: List<Message>,
    private val currentUserId: String
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        const val VIEW_TYPE_SENT = 1
        const val VIEW_TYPE_RECEIVED = 2
    }

    override fun getItemViewType(position: Int): Int {
        return if (messages[position].senderId == currentUserId) {
            VIEW_TYPE_SENT
        } else {
            VIEW_TYPE_RECEIVED
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == VIEW_TYPE_SENT) {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_message_sent, parent, false)
            SentMessageViewHolder(view)
        } else {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_message_received, parent, false)
            ReceivedMessageViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val message = messages[position]

        if (holder is SentMessageViewHolder) {
            holder.bind(message)
        } else if (holder is ReceivedMessageViewHolder) {
            holder.bind(message)
        }
    }

    override fun getItemCount() = messages.size

    inner class SentMessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvMessage: TextView = itemView.findViewById(R.id.tvMessage)
        private val ivImage: ImageView = itemView.findViewById(R.id.ivImage)
        private val tvTime: TextView = itemView.findViewById(R.id.tvTime)
        private val ivReadReceipt: ImageView = itemView.findViewById(R.id.ivReadReceipt)

        fun bind(message: Message) {
            when (message.type) {
                MessageType.TEXT -> {
                    tvMessage.visibility = View.VISIBLE
                    ivImage.visibility = View.GONE
                    tvMessage.text = message.message
                }
                MessageType.IMAGE -> {
                    tvMessage.visibility = View.GONE
                    ivImage.visibility = View.VISIBLE

                    ivImage.load(message.mediaUrl) {
                        placeholder(R.drawable.ic_image_placeholder)
                        error(R.drawable.ic_image_placeholder)
                    }
                }
                MessageType.VIDEO -> {
                    tvMessage.visibility = View.VISIBLE
                    ivImage.visibility = View.GONE
                    tvMessage.text = "🎥 Video"
                }
                MessageType.AUDIO -> {
                    tvMessage.visibility = View.VISIBLE
                    ivImage.visibility = View.GONE
                    tvMessage.text = "🎵 Audio (${formatDuration(message.duration)})"
                }
                MessageType.DOCUMENT -> {
                    tvMessage.visibility = View.VISIBLE
                    ivImage.visibility = View.GONE
                    tvMessage.text = "📄 ${message.fileName}"
                }
                else -> {
                    tvMessage.visibility = View.VISIBLE
                    ivImage.visibility = View.GONE
                    tvMessage.text = message.message
                }
            }

            tvTime.text = formatTime(message.timestamp)

            // Show read receipt
            if (message.isRead) {
                ivReadReceipt.setImageResource(R.drawable.ic_message_read)
            } else if (message.isDelivered) {
                ivReadReceipt.setImageResource(R.drawable.ic_message_delivered)
            } else if (message.isSent) {
                ivReadReceipt.setImageResource(R.drawable.ic_message_sent)
            } else {
                ivReadReceipt.setImageResource(R.drawable.ic_message_pending)
            }
        }
    }

    inner class ReceivedMessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvMessage: TextView = itemView.findViewById(R.id.tvMessage)
        private val ivImage: ImageView = itemView.findViewById(R.id.ivImage)
        private val tvTime: TextView = itemView.findViewById(R.id.tvTime)

        fun bind(message: Message) {
            when (message.type) {
                MessageType.TEXT -> {
                    tvMessage.visibility = View.VISIBLE
                    ivImage.visibility = View.GONE
                    tvMessage.text = message.message
                }
                MessageType.IMAGE -> {
                    tvMessage.visibility = View.GONE
                    ivImage.visibility = View.VISIBLE

                    ivImage.load(message.mediaUrl) {
                        placeholder(R.drawable.ic_image_placeholder)
                        error(R.drawable.ic_image_placeholder)
                    }
                }
                MessageType.VIDEO -> {
                    tvMessage.visibility = View.VISIBLE
                    ivImage.visibility = View.GONE
                    tvMessage.text = "🎥 Video"
                }
                MessageType.AUDIO -> {
                    tvMessage.visibility = View.VISIBLE
                    ivImage.visibility = View.GONE
                    tvMessage.text = "🎵 Audio (${formatDuration(message.duration)})"
                }
                MessageType.DOCUMENT -> {
                    tvMessage.visibility = View.VISIBLE
                    ivImage.visibility = View.GONE
                    tvMessage.text = "📄 ${message.fileName}"
                }
                else -> {
                    tvMessage.visibility = View.VISIBLE
                    ivImage.visibility = View.GONE
                    tvMessage.text = message.message
                }
            }

            tvTime.text = formatTime(message.timestamp)
        }
    }

    private fun formatTime(timestamp: Long): String {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = timestamp

        val now = Calendar.getInstance()

        return if (calendar.get(Calendar.DAY_OF_YEAR) == now.get(Calendar.DAY_OF_YEAR) &&
            calendar.get(Calendar.YEAR) == now.get(Calendar.YEAR)
        ) {
            SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date(timestamp))
        } else {
            SimpleDateFormat("MMM dd, hh:mm a", Locale.getDefault()).format(Date(timestamp))
        }
    }

    private fun formatDuration(duration: Long): String {
        val seconds = duration / 1000
        val minutes = seconds / 60
        val remainingSeconds = seconds % 60
        return String.format("%d:%02d", minutes, remainingSeconds)
    }
}