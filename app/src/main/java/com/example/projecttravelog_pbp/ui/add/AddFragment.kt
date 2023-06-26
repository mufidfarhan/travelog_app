package com.example.projecttravelog_pbp.ui.add

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.FragmentManager
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.example.projecttravelog_pbp.R
import com.example.projecttravelog_pbp.databinding.FragmentAddBinding
import com.example.projecttravelog_pbp.databinding.FragmentHomeBinding
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

class AddFragment : Fragment() {

    private lateinit var binding: FragmentAddBinding
    private val db = Firebase.firestore
    private var firebaseStore: FirebaseStorage? = null
    private var storageReference: StorageReference? = null
    private val PICK_IMAGE_REQUEST = 71
    private var filePath: Uri? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAddBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        firebaseStore = FirebaseStorage.getInstance()
        storageReference = FirebaseStorage.getInstance().reference
        with(binding) {
            etTglMulai.setOnClickListener {
                showDatePicker(etTglMulai)
            }
            etTglAkhir.setOnClickListener {
                showDatePicker(etTglAkhir)
            }
            image.setOnClickListener {
                val intent = Intent()
                intent.type = "image/*"
                intent.action = Intent.ACTION_GET_CONTENT
                startActivityForResult(
                    Intent.createChooser(intent, "Select Picture"),
                    PICK_IMAGE_REQUEST
                )
            }
            btnUpload.setOnClickListener {
                add()
            }
        }

        binding.bottomNavigationView.setOnNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.navigation_home -> {
                    view.findNavController().navigate(R.id.action_addFragment_to_homeFragment)
                    return@setOnNavigationItemSelectedListener true
                }
                R.id.navigation_add -> {
                    view.findNavController().navigate(R.id.action_addFragment_to_addFragment)
                    return@setOnNavigationItemSelectedListener true
                }
                R.id.navigation_profile -> {
                    view.findNavController().navigate(R.id.action_addFragment_to_profileFragment)
                    return@setOnNavigationItemSelectedListener true
                }
                else -> return@setOnNavigationItemSelectedListener false
            }
        }
    }

    private fun add() {
        with(binding) {
            val tujuan = etTujuan.text.toString().trim()
            val tglawal = etTglMulai.text.toString().trim()
            val tglakhir = etTglAkhir.text.toString().trim()
            val desc = etDescription.text.toString().trim()
            if (filePath != null && tujuan.isNotEmpty() && tglawal.isNotEmpty() && tglakhir.isNotEmpty() && desc.isNotEmpty()) {
                val ref = storageReference?.child("uploads/" + UUID.randomUUID().toString())
                val uploadTask = ref?.putFile(filePath!!)
                uploadTask?.continueWithTask(Continuation<UploadTask.TaskSnapshot, Task<Uri>> { task ->
                    if (!task.isSuccessful) {
                        task.exception?.let {
                            throw it
                        }
                    }
                    return@Continuation ref.downloadUrl
                })?.addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val downloadUri = task.result
                        val tujuanMap = hashMapOf(
                            "photo" to downloadUri,
                            "tujuan" to tujuan,
                            "tglMulai" to tglawal,
                            "tglAkhir" to tglakhir,
                            "desc" to desc,
                            "user" to Firebase.auth.currentUser?.email,
                            "timestamp" to FieldValue.serverTimestamp(),
                        )
                        db.collection("tujuan")
                            .add(tujuanMap)
                            .addOnSuccessListener {
                                Toast.makeText(
                                    requireContext(),
                                    "Berhasil menambahkan!",
                                    Toast.LENGTH_SHORT
                                ).show()
                                findNavController().navigate(R.id.homeFragment)
                            }.addOnFailureListener { e ->
                                Toast.makeText(
                                    requireContext(),
                                    e.message.toString(),
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                    } else {
                        Toast.makeText(
                            requireContext(),
                            task.exception?.message.toString(),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }?.addOnFailureListener {
                    Toast.makeText(
                        requireContext(),
                        it.message.toString(),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } else {
                Toast.makeText(
                    requireContext(),
                    "Field harus terisi!",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK) {
            if (data == null || data.data == null) {
                return
            }

            filePath = data.data
            try {
                val bitmap =
                    MediaStore.Images.Media.getBitmap(requireContext().contentResolver, filePath)
                binding.image.setImageBitmap(bitmap)
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    private fun showDatePicker(textInputEditText: TextInputEditText) {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        val datePicker =
            MaterialDatePicker.Builder.datePicker()
                .setTitleText("Select date")
                .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                .setTextInputFormat(dateFormat)
                .build()
        datePicker.show(parentFragmentManager, "DATE_DIALOG")
        datePicker.addOnPositiveButtonClickListener {
            textInputEditText.setText(dateFormat.format(Date(it)))
        }
        datePicker.addOnNegativeButtonClickListener {
            datePicker.dismiss()
        }
        datePicker.addOnCancelListener {
            datePicker.dismiss()
        }
    }
}