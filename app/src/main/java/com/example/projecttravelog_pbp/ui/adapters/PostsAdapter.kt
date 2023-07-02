package com.example.projecttravelog_pbp.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.projecttravelog_pbp.data.model.Post
import com.example.projecttravelog_pbp.databinding.ItemTravelBinding
import java.text.SimpleDateFormat
import java.util.*


class PostsAdapter(private val posts: ArrayList<Post>, private val itemClickListener: ItemClickListener) :
    RecyclerView.Adapter<PostsAdapter.ListViewHolder>() {


    // Antarmuka untuk penanganan klik item
    interface ItemClickListener {
        fun onItemClick(postId: String)
    }
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): ListViewHolder =
        ListViewHolder(
            ItemTravelBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) =
        holder.bind(posts[position])

    override fun getItemCount(): Int = posts.size

    inner class ListViewHolder(private val binding: ItemTravelBinding) :
        RecyclerView.ViewHolder(binding.root) {

        private fun mergeDateRange(startDate: String?, endDate: String?): String {
            if (startDate == null || endDate == null) {
                return ""
            }

            val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val outputFormat = SimpleDateFormat("d MMMM yyyy", Locale.getDefault())

            val start = inputFormat.parse(startDate)
            val end = inputFormat.parse(endDate)

            val dayOfMonthStart = outputFormat.format(start)
            val dayOfMonthEnd = outputFormat.format(end)

            val calendarStart = Calendar.getInstance().apply { time = start }
            val calendarEnd = Calendar.getInstance().apply { time = end }

            val monthStart = calendarStart.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault())
            val monthEnd = calendarEnd.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault())

            val yearStart = calendarStart.get(Calendar.YEAR)
            val yearEnd = calendarEnd.get(Calendar.YEAR)

            val result: String

            val dayStart = dayOfMonthStart.substringBefore(' ')
            val dayEnd = dayOfMonthEnd.substringBefore(' ')

            if (yearStart == yearEnd && monthStart == monthEnd) {
                result = "$dayStart - $dayEnd $monthStart $yearStart"
            } else if (yearStart == yearEnd) {
                result = "$dayStart $monthStart - $dayEnd $monthEnd $yearStart"
            } else {
                result = "$dayStart $monthStart $yearStart - $dayEnd $monthEnd $yearEnd"
            }

            return result
        }



        fun bind(item: Post) {
            binding.apply {
                Glide.with(itemView.context).load(item.gambar).into(photo)
                name.text = getUsernameFromEmail(item.user)
                location.text = item.tujuan
                date.text = mergeDateRange(item.tanggal_mulai, item.tanggal_akhir)
                caption.text = item.deskripsi

                itemView.setOnClickListener {
                    val position = adapterPosition
                    if (position != RecyclerView.NO_POSITION) {
                        val post = posts[position]
                        post.id?.let { it1 -> itemClickListener.onItemClick(it1) }
                    }
                }
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