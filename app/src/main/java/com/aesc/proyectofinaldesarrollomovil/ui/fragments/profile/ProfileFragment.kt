package com.aesc.proyectofinaldesarrollomovil.ui.fragments.profile

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.aesc.proyectofinaldesarrollomovil.R
import com.aesc.proyectofinaldesarrollomovil.databinding.ProfileFragmentBinding
import com.aesc.proyectofinaldesarrollomovil.extension.goToActivity
import com.aesc.proyectofinaldesarrollomovil.extension.goToActivityF
import com.aesc.proyectofinaldesarrollomovil.extension.loadByURL
import com.aesc.proyectofinaldesarrollomovil.provider.firebase.daos.UserDao
import com.aesc.proyectofinaldesarrollomovil.provider.firebase.models.User
import com.aesc.proyectofinaldesarrollomovil.ui.activities.AboutUsActivity
import com.aesc.proyectofinaldesarrollomovil.ui.activities.DeleteAccountActivity
import com.aesc.proyectofinaldesarrollomovil.ui.activities.LoginActivity
import com.aesc.proyectofinaldesarrollomovil.ui.activities.UpdatePasswordActivity
import com.aesc.proyectofinaldesarrollomovil.utils.Utils
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
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
    private val fileResult = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true);
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.more_options_menu, menu);
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val itemId = item.itemId
        requireActivity().goToActivity<AboutUsActivity>()
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel = ViewModelProvider(this)[ProfileViewModel::class.java]
        _binding = ProfileFragmentBinding.inflate(inflater, container, false)
        binding.btnLogout.setOnClickListener(this)
        binding.btnActualizar.setOnClickListener(this)
        binding.floatingActionButton.setOnClickListener(this)
        binding.tvUpdatePassword.setOnClickListener(this)
        binding.tvDeleteAccount.setOnClickListener(this)
        auth = Firebase.auth
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        updateUI()
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.btnLogout -> {
                auth.signOut()
                requireActivity().goToActivityF<LoginActivity>()
            }
            R.id.btnActualizar -> {
                val currentUser = auth.currentUser
                val usersDao = UserDao()
                val newUserName = binding.tieUsername.text.toString()
//                val newEmail = binding.tieEmail.text.toString()

                /*  val profileUpdates = userProfileChangeRequest {
                      photoUri = currentUser!!.photoUrl
                      displayName = newUserName
                  }
  */
                val user =
                    User(
                        currentUser!!.uid,
                        newUserName,
                        currentUser.photoUrl.toString(),
                        currentUser.email!!
                    )
                usersDao.updateUserInfo(user)
            }
            R.id.floatingActionButton -> {
                fileManager()
            }
            R.id.tvUpdatePassword -> {
                requireActivity().goToActivity<UpdatePasswordActivity>()
            }
            R.id.tvDeleteAccount -> {
                requireActivity().goToActivity<DeleteAccountActivity>()
            }
        }
    }

    private fun fileManager() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        startActivityForResult(intent, fileResult)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == fileResult) {
            if (resultCode == RESULT_OK && data != null) {
                val uri = data.data

                uri?.let { imageUpload(it) }

            }
        }
    }

    private fun imageUpload(mUri: Uri) {
        val user = auth.currentUser
        val folder: StorageReference = FirebaseStorage.getInstance().reference.child("Users")
        val fileName: StorageReference = folder.child("img" + user!!.uid)

        fileName.putFile(mUri).addOnSuccessListener {
            fileName.downloadUrl.addOnSuccessListener { uri ->

                val currentUser = auth.currentUser
                val usersDao = UserDao()
                val newUserName = binding.tieUsername.text.toString()
//                val newEmail = binding.tieEmail.text.toString()

                val user =
                    User(currentUser!!.uid, newUserName, uri.toString(), currentUser.email!!)
                usersDao.updateUserInfo(user)
                Toast.makeText(
                    requireContext(), "Se realizaron los cambios correctamente.",
                    Toast.LENGTH_SHORT
                ).show()
                updateUI()
                /*   val profileUpdates = userProfileChangeRequest {
                       photoUri = Uri.parse(uri.toString())
                   }

                   user.updateProfile(profileUpdates)
                       .addOnCompleteListener { task ->
                           if (task.isSuccessful) {
                               Toast.makeText(
                                   requireContext(), "Se realizaron los cambios correctamente.",
                                   Toast.LENGTH_SHORT
                               ).show()
                               updateUI()
                           }
                       }*/
            }
        }.addOnFailureListener {
            Log.i("TAG", "file upload error")
        }
    }

    private fun updateUI() {
        val currentUser = auth.currentUser
        val userDao = UserDao()

        GlobalScope.launch(Dispatchers.IO) {
            val user = userDao.getUserByid(currentUser!!.uid).await().toObject(User::class.java)!!
            withContext(Dispatchers.Main) {
                Utils.logsUtils("$user")
                binding.userImage.loadByURL(user.imageUrl)
                binding.tieUsername.setText(user.displayName)
            }
        }
        binding.tieUsername.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {


            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun afterTextChanged(s: Editable?) {
                binding.tvNameUser.text = s.toString()
            }
        })
    }
}