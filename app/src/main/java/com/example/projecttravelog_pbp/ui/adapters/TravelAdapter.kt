package com.example.projecttravelog_pbp.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.projecttravelog_pbp.databinding.ItemTravelBinding
import com.example.projecttravelog_pbp.data.model.Tujuan
import java.text.SimpleDateFormat
import java.util.*


class TravelAdapter(private val tujuan: ArrayList<Tujuan>) :
    RecyclerView.Adapter<TravelAdapter.ListViewHolder>() {

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
        holder.bind(tujuan[position])

    override fun getItemCount(): Int = tujuan.size

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

            val dayStart = dayOfMonthStart.substring(0, 2)
            val dayEnd = dayOfMonthEnd.substring(0, 2)

            if (yearStart == yearEnd && monthStart == monthEnd) {
                result = "$dayStart-$dayEnd $monthStart $yearStart"
            } else if (yearStart == yearEnd) {
                result = "$dayStart $monthStart - $dayEnd $monthEnd $yearStart"
            } else {
                result = "$dayStart $monthStart $yearStart - $dayEnd $monthEnd $yearEnd"
            }

            return result
        }



        fun bind(item: Tujuan) {
            binding.apply {
                Glide.with(itemView.context).load(item.photo).into(photo)
                name.text = item.user
                location.text = item.tujuan
                date.text = mergeDateRange(item.tglMulai, item.tglAkhir)
                caption.text = item.desc
            }
        }
    }
}