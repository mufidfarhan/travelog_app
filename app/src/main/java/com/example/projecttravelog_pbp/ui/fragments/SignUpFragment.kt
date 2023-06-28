package com.example.projecttravelog_pbp.ui.fragments

import android.os.Bundle
import android.util.Patterns
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.projecttravelog_pbp.R
import com.example.projecttravelog_pbp.databinding.FragmentSignUpBinding
import com.example.projecttravelog_pbp.ui.viewmodels.SignUpViewModel

class SignUpFragment : Fragment() {

    lateinit var binding: FragmentSignUpBinding
    private val viewModel: SignUpViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSignUpBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnSignUp.setOnClickListener {
            val email = binding.txtEmail.text.toString()
            val password = binding.txtPassword.text.toString()

            // memvalidasi jika emailkosong
            if (email.isEmpty()) {
                binding.txtEmail.error = "Email Required".toString()
                binding.txtEmail.requestFocus()
                return@setOnClickListener
            }
            // validasi jika email tidak sesuai
            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                binding.txtEmail.error = "invalid e-mail".toString()
                binding.txtEmail.requestFocus()
                return@setOnClickListener
            }
            //valdasi passowrd
            if (password.isEmpty()) {
                binding.txtPassword.error = "password is required".toString()
                binding.txtPassword.requestFocus()
                return@setOnClickListener
            }
            // validasi panjang password
            if (password.length < 6) {
                binding.txtPassword.error = "minimum 6 charachter password".toString()
                binding.txtPassword.requestFocus()
                return@setOnClickListener
            }
            if (binding.txtEmail.error == null && binding.txtPassword.error == null) {
                viewModel.registerFirebase(email, password)
            }
        }

        viewModel.register.observe(viewLifecycleOwner) {
            if (it.equals("Register Success!", true)) {
                Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
                findNavController().navigate(R.id.action_signUpFragment_to_signInFragment)
            } else {
                Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
            }
        }

        binding.signInBtn.setOnClickListener {
            findNavController().navigate(R.id.action_signUpFragment_to_signInFragment)
        }
    }
}


