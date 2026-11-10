package com.example.psychoapp.ui.chat

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.psychoapp.R
import com.example.psychoapp.shared.chat.ChatMessageUi
import com.example.psychoapp.shared.chat.ChatStatus
import com.example.psychoapp.shared.model.ChatRole
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class ChatMessageAdapter : ListAdapter<ChatMessageUi, ChatMessageAdapter.MessageViewHolder>(
    ChatMessageDiff
) {
    companion object {
        private const val TYPE_USER = 0
        private const val TYPE_ASSISTANT = 1
    }

    override fun getItemViewType(position: Int): Int =
        when (getItem(position).role) {
            ChatRole.USER -> TYPE_USER
            ChatRole.ASSISTANT -> TYPE_ASSISTANT
            ChatRole.SYSTEM -> TYPE_ASSISTANT
        }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MessageViewHolder {
        val layoutRes = when (viewType) {
            TYPE_USER -> R.layout.item_chat_message_user
            else -> R.layout.item_chat_message_assistant
        }
        val view = LayoutInflater.from(parent.context)
            .inflate(layoutRes, parent, false)

        return MessageViewHolder(view)
    }

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        val message = getItem(position)

        val instant = Instant.ofEpochMilli(message.timestamp)
        val formattedDate = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm").withZone(ZoneId.systemDefault()).format(instant)
        holder.messageDate?.text = formattedDate

        holder.bind(message)
    }

    class MessageViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val contentView = view.findViewById<TextView>(R.id.tvMessageContent)
        private val statusView = view.findViewById<TextView?>(R.id.tvMessageStatus)
        val messageDate: TextView? = view.findViewById<TextView>(R.id.message_date)
        

        fun bind(message: ChatMessageUi) {
            contentView.text = message.content

            if (statusView != null) {
                when (message.status) {
                    ChatStatus.STREAMING -> {
                        statusView.visibility = View.VISIBLE
                        statusView.text = "…"
                    }
                    ChatStatus.ERROR -> {
                        statusView.visibility = View.VISIBLE
                        statusView.text = "не удалось"
                    }
                    ChatStatus.DONE -> {
                        statusView.visibility = View.GONE
                    }
                }
            }
        }
    }
}

private object ChatMessageDiff : DiffUtil.ItemCallback<ChatMessageUi>() {
    override fun areItemsTheSame(oldItem: ChatMessageUi, newItem: ChatMessageUi): Boolean =
        oldItem.id == newItem.id

    override fun areContentsTheSame(oldItem: ChatMessageUi, newItem: ChatMessageUi): Boolean =
        oldItem == newItem
}