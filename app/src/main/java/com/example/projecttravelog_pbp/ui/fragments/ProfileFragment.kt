package com.example.projecttravelog_pbp.ui.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.projecttravelog_pbp.databinding.FragmentProfileBinding
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.projecttravelog_pbp.R
import com.example.projecttravelog_pbp.data.model.Post
import com.example.projecttravelog_pbp.data.model.User
import com.example.projecttravelog_pbp.ui.adapters.MyJourneyAdapter
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.ktx.Firebase

class ProfileFragment : Fragment() {

    private lateinit var binding: FragmentProfileBinding
    private val db = FirebaseFirestore.getInstance()
    private lateinit var post: ArrayList<Post>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        post = arrayListOf()
        binding.rvTravel.layoutManager = LinearLayoutManager(requireContext())


        db.collection("users")
            .whereEqualTo("email", Firebase.auth.currentUser?.email)
            .get()
            .addOnSuccessListener { querySnapshot ->
                if (!querySnapshot.isEmpty) {
                    val document = querySnapshot.documents[0]
                    val user = document.toObject(User::class.java)
                    binding.name.text = user?.username
                    if (user?.jumlah_postingan == null) {
                        binding.totalPost.text = "0"
                    } else {
                        binding.totalPost.text = user?.jumlah_postingan.toString()
                    }

                }
            }
            .addOnFailureListener { exception ->
                Log.e("ERROR GET USER", exception.message!!)
            }
        
        
        db.collection("posts")
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener {
                if (!it.isEmpty) {
                    for (data in it.documents) {
                        val obj = data.toObject(Post::class.java)
                        if (obj?.user == Firebase.auth.currentUser?.email) {
                            if (obj != null) {
                                if (obj.user == Firebase.auth.currentUser?.email) {
                                    post.add(obj)
                                }
                            }
                        }
                    }
                    binding.rvTravel.adapter = MyJourneyAdapter(post)
                }
            }
            .addOnFailureListener { exception ->
                Log.e("ERROR GET PRODUCTS", exception.message!!)
            }

//        binding.btnAdd.setOnClickListener {
//            it.findNavController().navigate(R.id.action_homeFragment_to_addFragment)
//        }

        binding.btnLogout.setOnClickListener {
            Firebase.auth.signOut()
            it.findNavController().navigate(R.id.signInFragment)
        }

        binding.bottomNavigationView.setOnNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.navigation_home -> {
                    view.findNavController().navigate(R.id.action_profileFragment_to_homeFragment)
                    return@setOnNavigationItemSelectedListener true
                }
                R.id.navigation_add -> {
                    view.findNavController().navigate(R.id.action_profileFragment_to_addFragment)
                    return@setOnNavigationItemSelectedListener true
                }
                R.id.navigation_profile -> {
                    view.findNavController().navigate(R.id.action_profileFragment_to_profileFragment)
                    return@setOnNavigationItemSelectedListener true
                }
                else -> return@setOnNavigationItemSelectedListener false
            }
        }
    }
}