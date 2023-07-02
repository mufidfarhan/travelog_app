package com.example.projecttravelog_pbp.ui.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.projecttravelog_pbp.data.model.Comment
import com.example.projecttravelog_pbp.data.model.Post
import com.example.projecttravelog_pbp.data.model.User
import com.example.projecttravelog_pbp.databinding.FragmentDetailBinding
import com.example.projecttravelog_pbp.ui.adapters.CommentsAdapter
import com.example.projecttravelog_pbp.ui.adapters.MyPostsAdapter
import com.example.projecttravelog_pbp.ui.adapters.PostsAdapter
import com.google.firebase.Timestamp
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.util.*


class DetailFragment : Fragment() {
    private lateinit var binding: FragmentDetailBinding
    private val db = FirebaseFirestore.getInstance()
    private var postId: String? = null
    private lateinit var comment: ArrayList<Comment>


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        comment = arrayListOf()
        postId = arguments?.getString("postId")
        binding.rvComment.layoutManager = LinearLayoutManager(requireContext())

        if (postId != null) {
            Log.d("Clicked Post ID", postId!!)
        }

        if (postId != null) {
            db.collection("posts")
                .document(postId!!)
                .get()
                .addOnSuccessListener { querySnapshot ->
                    if (querySnapshot.exists()) {
                        val item = querySnapshot.toObject(Post::class.java)
                        if (item != null) {
                            Glide.with(requireContext()).load(item?.gambar).into(binding.photo)
                            binding.name.text = getUsernameFromEmail(item?.user)
                            binding.location.text = item?.tujuan
                            binding.date.text =
                                mergeDateRange(item?.tanggal_mulai, item?.tanggal_akhir)
                            binding.caption.text = item?.deskripsi
                        }
                    }
                }
                .addOnFailureListener { exception ->
                    Log.e("ERROR GET POST", exception.message!!)
                }


            db.collection("comments")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener {
                    if (!it.isEmpty) {
                        for (data in it.documents) {
                            val obj = data.toObject(Comment::class.java)
                            if (obj?.id_perjalanan == postId) {
                                if (obj != null) {
                                    comment.add(obj)
                                }
                            }
                        }
                        binding.rvComment.adapter = CommentsAdapter(comment)
                    }
                }
                .addOnFailureListener { exception ->
                    Log.e("ERROR GET PRODUCTS", exception.message!!)
                }

        } else {
            Log.e("ERROR", "postId is null")
        }

        binding.postComment.setOnClickListener {
            addComment()
        }
    }

    private fun addComment() {
        val commentText = binding.textComment.text.toString()

        if (postId != null) {
            db.collection("posts")
                .document(postId!!)
                .get()
                .addOnSuccessListener { querySnapshot ->
                    if (querySnapshot.exists()) {
                        val timestamp = Timestamp.now().toDate()
                        val item = querySnapshot.toObject(Post::class.java)
                        if (item != null) {
                            val comment = Comment(
                                user = Firebase.auth.currentUser?.email,
                                id_perjalanan = postId,
                                timestamp = timestamp,
                                komentar = commentText
                            )

                            db.collection("comments")
                                .add(comment)
                                .addOnSuccessListener { documentReference ->
                                    Log.d("TAG", "Data added successfully with ID: ${documentReference.id}")

                                    // Reset input teks komentar
                                    binding.textComment.text = null

                                    // Memperbarui komentar
                                    updateComments()
                                }
                                .addOnFailureListener { e ->
                                    Log.e("TAG", "Error adding data to Firestore", e)
                                }
                        }
                    }
                }
                .addOnFailureListener { exception ->
                    Log.e("ERROR GET POST", exception.message!!)
                }
        } else {
            Log.e("ERROR", "postId is null")
        }
    }

    private fun updateComments() {
        comment.clear() // Menghapus komentar yang ada sebelumnya

        db.collection("comments")
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { querySnapshot ->
                if (!querySnapshot.isEmpty) {
                    for (data in querySnapshot.documents) {
                        val obj = data.toObject(Comment::class.java)
                        if (obj?.id_perjalanan == postId) {
                            if (obj != null) {
                                comment.add(obj)
                            }
                        }
                    }
                    binding.rvComment.adapter?.notifyDataSetChanged()
                }
            }
            .addOnFailureListener { exception ->
                Log.e("ERROR GET COMMENTS", exception.message!!)
            }
    }



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

    private fun getUsernameFromEmail(email: String?): String {
        // Split email by '@' and return the first part as username
        var result = ""
        if (email != null) {
            result = email.split("@")[0]
        }
        return result
    }

}