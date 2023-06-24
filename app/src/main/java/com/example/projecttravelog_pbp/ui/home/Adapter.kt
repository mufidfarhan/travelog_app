package com.example.projecttravelog_pbp.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.projecttravelog_pbp.databinding.ItemTravelBinding

class Adapter(private val tujuan: ArrayList<Tujuan>) :
    RecyclerView.Adapter<Adapter.ListViewHolder>() {

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
        fun bind(item: Tujuan) {
            binding.apply {
                Glide.with(itemView.context).load(item.photo).into(photo)
                name.text = item.user
            }
        }
    }
}