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
import com.example.projecttravelog_pbp.data.model.Tujuan
import com.example.projecttravelog_pbp.ui.adapters.MyJourneyAdapter
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.ktx.Firebase

class ProfileFragment : Fragment() {

    private lateinit var binding: FragmentProfileBinding
    private val db = FirebaseFirestore.getInstance()
    private lateinit var tujuan: ArrayList<Tujuan>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        tujuan = arrayListOf()
        binding.rvTravel.layoutManager = LinearLayoutManager(requireContext())

        db.collection("tujuan")
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener {
                if (!it.isEmpty) {
                    for (data in it.documents) {
                        val obj = data.toObject(Tujuan::class.java)
                        if (obj != null) {
                            tujuan.add(obj)
                        }
                    }
                    binding.rvTravel.adapter = MyJourneyAdapter(tujuan)
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