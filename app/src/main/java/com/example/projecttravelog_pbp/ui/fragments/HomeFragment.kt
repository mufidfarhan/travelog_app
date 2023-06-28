package com.example.projecttravelog_pbp.ui.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.projecttravelog_pbp.R
import com.example.projecttravelog_pbp.data.model.Post
import com.example.projecttravelog_pbp.databinding.FragmentHomeBinding
import com.example.projecttravelog_pbp.ui.adapters.TravelAdapter
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    private val db = FirebaseFirestore.getInstance()
    private lateinit var post: ArrayList<Post>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        post = arrayListOf()
        binding.rvTravel.layoutManager = LinearLayoutManager(requireContext())

        db.collection("posts")
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener {
                if (!it.isEmpty) {
                    for (data in it.documents) {
                        val obj = data.toObject(Post::class.java)
                        if (obj != null) {
                            post.add(obj)
                        }
                    }
                    binding.rvTravel.adapter = TravelAdapter(post)
                }
            }
            .addOnFailureListener { exception ->
                Log.e("ERROR GET PRODUCTS", exception.message!!)
            }

        binding.bottomNavigationView.setOnNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.navigation_home -> {
                    view.findNavController().navigate(R.id.action_homeFragment_to_homeFragment)
                    return@setOnNavigationItemSelectedListener true
                }
                R.id.navigation_add -> {
                    view.findNavController().navigate(R.id.action_homeFragment_to_addFragment)
                    return@setOnNavigationItemSelectedListener true
                }
                R.id.navigation_profile -> {
                    view.findNavController().navigate(R.id.action_homeFragment_to_profileFragment)
                    return@setOnNavigationItemSelectedListener true
                }
                else -> return@setOnNavigationItemSelectedListener false
            }
        }
    }
}