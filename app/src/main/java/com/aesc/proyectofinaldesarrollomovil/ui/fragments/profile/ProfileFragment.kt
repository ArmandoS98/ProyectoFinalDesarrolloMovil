package com.aesc.proyectofinaldesarrollomovil.ui.fragments.profile

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.aesc.proyectofinaldesarrollomovil.R
import com.aesc.proyectofinaldesarrollomovil.databinding.HistoryFragmentBinding
import com.aesc.proyectofinaldesarrollomovil.databinding.ProfileFragmentBinding
import com.aesc.proyectofinaldesarrollomovil.extension.goToActivityF
import com.aesc.proyectofinaldesarrollomovil.extension.loadByURL
import com.aesc.proyectofinaldesarrollomovil.provider.firebase.daos.UserDao
import com.aesc.proyectofinaldesarrollomovil.provider.firebase.models.User
import com.aesc.proyectofinaldesarrollomovil.ui.activities.LoginActivity
import com.aesc.proyectofinaldesarrollomovil.ui.fragments.history.HistoryViewModel
import com.aesc.proyectofinaldesarrollomovil.utils.Utils
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class ProfileFragment : Fragment(), View.OnClickListener {
    private lateinit var auth: FirebaseAuth
    private lateinit var viewModel: ProfileViewModel
    private var _binding: ProfileFragmentBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel = ViewModelProvider(this)[ProfileViewModel::class.java]
        _binding = ProfileFragmentBinding.inflate(inflater, container, false)
        binding.btnLogout.setOnClickListener(this)
        auth = Firebase.auth
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val currentUser = auth.currentUser
        val userDao = UserDao()

        GlobalScope.launch(Dispatchers.IO) {
            val user = userDao.getUserByid(currentUser!!.uid).await().toObject(User::class.java)!!
            withContext(Dispatchers.Main) {
                Utils.logsUtils("$user")
                binding.userImage.loadByURL(user.imageUrl)
                binding.tieEmail.setText(user.email)
                binding.tieUsername.setText(user.displayName)
                binding.tvNameUser.text = user.displayName
            }
        }
    }

    override fun onClick(v: View?) {
        auth.signOut()
        requireActivity().goToActivityF<LoginActivity>()
    }

}