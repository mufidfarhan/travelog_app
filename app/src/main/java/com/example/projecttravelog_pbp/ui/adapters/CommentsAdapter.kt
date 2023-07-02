package com.example.projecttravelog_pbp.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.projecttravelog_pbp.data.model.Comment
import com.example.projecttravelog_pbp.databinding.FragmentCommentBinding
import com.example.projecttravelog_pbp.databinding.MyJourneyBinding
import java.text.SimpleDateFormat
import java.util.*


class CommentsAdapter(private val comment: ArrayList<Comment>) :
    RecyclerView.Adapter<CommentsAdapter.ListViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): ListViewHolder =
        ListViewHolder(
            FragmentCommentBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) =
        holder.bind(comment[position])

    override fun getItemCount(): Int = comment.size

    inner class ListViewHolder(private val binding: FragmentCommentBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Comment) {
            binding.apply {
                name.text = getUsernameFromEmail(item.user)
                val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault())
                val formattedDate = dateFormat.format(item.timestamp)
                date.text = formattedDate
                comment.text = item.komentar
            }
        }


        private fun getUsernameFromEmail(email: String?): String {
            // Split email by '@' and return the first part as username
            var result = ""
            if (email != null) {
                result = email.split("@")[0]
            }
            return result
        }

    }
}