package com.aesc.proyectofinaldesarrollomovil.ui.fragments.profile

import android.Manifest
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.*
import android.widget.Button
import android.widget.LinearLayout
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.aesc.proyectofinaldesarrollomovil.R
import com.aesc.proyectofinaldesarrollomovil.databinding.ProfileFragmentBinding
import com.aesc.proyectofinaldesarrollomovil.extension.goToActivity
import com.aesc.proyectofinaldesarrollomovil.extension.goToActivityF
import com.aesc.proyectofinaldesarrollomovil.extension.loadByURL
import com.aesc.proyectofinaldesarrollomovil.extension.toast
import com.aesc.proyectofinaldesarrollomovil.provider.firebase.daos.UserDao
import com.aesc.proyectofinaldesarrollomovil.provider.firebase.models.User
import com.aesc.proyectofinaldesarrollomovil.ui.activities.AboutUsActivity
import com.aesc.proyectofinaldesarrollomovil.ui.activities.DeleteAccountActivity
import com.aesc.proyectofinaldesarrollomovil.ui.activities.LoginActivity
import com.aesc.proyectofinaldesarrollomovil.ui.activities.UpdatePasswordActivity
import com.aesc.proyectofinaldesarrollomovil.utils.Utils.dialogInfo
import com.aesc.proyectofinaldesarrollomovil.utils.Utils.statusProgress
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.ByteArrayOutputStream
import java.io.File


class ProfileFragment : Fragment(), View.OnClickListener {
    private val REQUEST_CODE = 200
    private val REQUEST_CODE_CHOOSE = 1
    private lateinit var auth: FirebaseAuth
    private lateinit var viewModel: ProfileViewModel
    private var _binding: ProfileFragmentBinding? = null
    private val binding get() = _binding!!

    companion object {
        const val CAMERA_REQUEST_CODE = 1998
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true);
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.more_options_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
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
        statusProgress(true, binding.fragmentProgressBar)
        auth = Firebase.auth
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val currentUser = auth.currentUser
        val usersDao = UserDao()
        usersDao.getCurrentUser(currentUser!!.uid, {
            updateUI(it)
        }, {
//Nada
        })

    }

    fun capturePhoto() {
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(cameraIntent, REQUEST_CODE)
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.btnLogout -> {
                areYouSure()

            }
            R.id.btnActualizar -> {
                statusProgress(true, binding.fragmentProgressBar)
                val usersDao = UserDao()
                val uId = auth.currentUser!!.uid
                usersDao.getCurrentUser(uId, { user ->
                    val newUserName = binding.tieUsername.text.toString()
                    val userTemp = User(uId, newUserName, user.imageUrl, user.email)
                    usersDao.updateUserInfo(userTemp, {
                        requireActivity().toast("Informacion Acutalizada con exito")
                        statusProgress(false, binding.fragmentProgressBar)
                    }, {
                        requireActivity().toast("Error al actualizar informacion")
                        statusProgress(false, binding.fragmentProgressBar)
                    })
                }, {
                    requireActivity().toast("Error al actualizar informacion")
                    statusProgress(false, binding.fragmentProgressBar)
                })
            }
            R.id.floatingActionButton -> {
                showBottomSheetDialog()
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
        startActivityForResult(intent, REQUEST_CODE_CHOOSE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            REQUEST_CODE ->
                if (resultCode == RESULT_OK && data != null) {
                    statusProgress(true, binding.fragmentProgressBar)
                    val photo = data.extras!!.get("data") as Bitmap
                    binding.userImage.setImageBitmap(photo)
                    val file =
                        File(requireContext().cacheDir, "CUSTOM NAME") //Get Access to a local file.
                    file.delete() // Delete the File, just in Case, that there was still another File
                    file.createNewFile()
                    val fileOutputStream = file.outputStream()
                    val byteArrayOutputStream = ByteArrayOutputStream()
                    photo.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
                    val bytearray = byteArrayOutputStream.toByteArray()
                    fileOutputStream.write(bytearray)
                    fileOutputStream.flush()
                    fileOutputStream.close()
                    byteArrayOutputStream.close()

                    val photoCamera = file.toUri()
                    imageUpload(photoCamera)
                }
            REQUEST_CODE_CHOOSE ->
                if (resultCode == RESULT_OK && data != null) {
                    statusProgress(true, binding.fragmentProgressBar)
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

                val user = User(currentUser!!.uid, newUserName, uri.toString(), currentUser.email!!)
                usersDao.updateUserInfo(user, {
                    //Exitos
                    requireActivity().toast("Hola ${it.displayName}")
                    updateUI(it)
                }, {
                    //Error
                    requireActivity().toast("ERROR $it.")
                    statusProgress(false, binding.fragmentProgressBar)
                })
//                updateUI()


                /*  val profileUpdates = userProfileChangeRequest {
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
            requireActivity().toast("file upload error")
            statusProgress(false, binding.fragmentProgressBar)
        }
    }

    private fun showBottomSheetDialog() {
        val bottomSheetDialog = BottomSheetDialog(requireContext())
        bottomSheetDialog.setContentView(R.layout.bottom_sheet_dialog_image)
        val copy = bottomSheetDialog.findViewById<LinearLayout>(R.id.takePhoto)
        val share = bottomSheetDialog.findViewById<LinearLayout>(R.id.uploadFromGallery)

        copy!!.setOnClickListener {
            if (isPermissionsGranted()) {
                capturePhoto()
                bottomSheetDialog.dismiss()
            } else {
                bottomSheetDialog.dismiss()
                requestLocationPermission()
            }

        }
        share!!.setOnClickListener {
            fileManager()
            bottomSheetDialog.dismiss()
        }
        bottomSheetDialog.show()
    }

    private fun updateUI(user: User) {
        binding.userImage.loadByURL(user.imageUrl)
        binding.tieUsername.setText(user.displayName)
        binding.tvNameUser.text = user.displayName
        statusProgress(false, binding.fragmentProgressBar)
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

    private fun areYouSure() {
        var alertDialog1: AlertDialog? = null
        val dialogBuilder = AlertDialog.Builder(requireContext())
        val layoutView: View =
            LayoutInflater.from(context).inflate(R.layout.custom_dialog_logout, null)
        val mButtonSi = layoutView.findViewById<Button>(R.id.btnSi)
        val mButtonNo = layoutView.findViewById<Button>(R.id.btnNo)
        mButtonSi.setOnClickListener {
            auth.signOut()
            alertDialog1!!.dismiss()
            requireActivity().goToActivityF<LoginActivity>()
        }
        mButtonNo.setOnClickListener {
            alertDialog1!!.dismiss()
//            requireActivity().supportFragmentManager.popBackStack()
        }
        dialogBuilder.setView(layoutView)
        alertDialog1 = dialogBuilder.create()
        alertDialog1.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        alertDialog1.setCancelable(false)
        alertDialog1.show()
    }

    private fun isPermissionsGranted() = ContextCompat.checkSelfPermission(
        requireContext(),
        Manifest.permission.CAMERA
    ) == PackageManager.PERMISSION_GRANTED

    private fun requestLocationPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(
                requireActivity(),
                Manifest.permission.CAMERA
            )
        ) {
            dialogInfo(requireContext(),"Para activar la\ncamara ve a ajustes\ny acepta los permisos")
        } else {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.CAMERA),
                CAMERA_REQUEST_CODE
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            CAMERA_REQUEST_CODE -> if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                capturePhoto()
            } else {
                dialogInfo(requireContext(),"Para activar la\ncamara ve a ajustes\ny acepta los permisos")
            }
        }
    }
}