package com.compumovil.feed2budget.Cliente

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.compumovil.feed2budget.MainActivity
import com.compumovil.feed2budget.R
import com.compumovil.feed2budget.User.Plato
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import java.io.ByteArrayOutputStream
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

import com.google.firebase.appcheck.FirebaseAppCheck
import com.google.firebase.appcheck.safetynet.SafetyNetAppCheckProviderFactory
import com.compumovil.feed2budget.User.User
import com.google.firebase.Firebase
import com.google.firebase.auth.auth


class UserInfo : AppCompatActivity() {
    private lateinit var etUsername: EditText
    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText
    private lateinit var btnSaveChanges: Button
    private lateinit var userImage: ImageButton
    private val REQUEST_IMAGE_CAPTURE = 1
    private lateinit var currentPhotoPath: String
    private lateinit var storage: FirebaseStorage
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_info)
        val firebaseAppCheck = FirebaseAppCheck.getInstance()
        firebaseAppCheck.installAppCheckProviderFactory(SafetyNetAppCheckProviderFactory.getInstance())

        etUsername = findViewById(R.id.username)
        etEmail = findViewById(R.id.email)
        etPassword = findViewById(R.id.password)
        btnSaveChanges = findViewById(R.id.saveChanges)
        userImage = findViewById(R.id.userImage)
        auth = Firebase.auth

        val userId = FirebaseAuth.getInstance().currentUser?.uid
        val databaseReference = FirebaseDatabase.getInstance().getReference("users").child(userId!!)


        val firebaseUser = FirebaseAuth.getInstance().currentUser
        if (firebaseUser != null) {
            etEmail.setText(firebaseUser.email)
            // Firebase Authentication does not provide a way to get the user's password

            // Aquí cargamos la imagen del usuario en userImage
            val storageReference =
                FirebaseStorage.getInstance().getReference("profile_images").child(userId)
            storageReference.downloadUrl.addOnSuccessListener { uri ->
                Glide.with(this@UserInfo).load(uri).into(userImage)
            }
        }
        databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val user = dataSnapshot.getValue(User::class.java)
                if (user != null) {
                    etUsername.setText(user.firstName) // Aquí se obtiene el nombre de usuario
                    // ...
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Manejar el error
            }
        })

        userImage.setOnClickListener {
            askCameraPermission()
        }

        //logout
        val btnLogout = findViewById<Button>(R.id.btnLogout)
        btnLogout.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            auth.signOut()
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)

        }

        btnSaveChanges.setOnClickListener {
            val user = FirebaseAuth.getInstance().currentUser
            val newPassword = etPassword.text.toString()
            val newUsername = etUsername.text.toString()

            // Actualizar contraseña
            user!!.updatePassword(newPassword).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Contraseña actualizada", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Error al actualizar la contraseña", Toast.LENGTH_SHORT).show()
                }
            }

            // Actualizar nombre de usuario
            val databaseReference = FirebaseDatabase.getInstance().getReference("users").child(user!!.uid)
            val updatedUser = mapOf("firstName" to newUsername)
            databaseReference.updateChildren(updatedUser).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Nombre de usuario actualizado", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Error al actualizar el nombre de usuario", Toast.LENGTH_SHORT).show()
                }
            }

            // Actualizar imagen
            uploadProfileImageToStorage(user.uid)
        }

    }


    private fun askCameraPermission() {
        when {
            ContextCompat.checkSelfPermission(
                this, Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED -> {
                openCamera()
            }

            ActivityCompat.shouldShowRequestPermissionRationale(
                this, Manifest.permission.CAMERA
            ) -> {
                Toast.makeText(
                    this, "Se necesita permiso para acceder a la cámara", Toast.LENGTH_LONG
                ).show()
                requestCameraPermission()
            }

            else -> {
                requestCameraPermission()
            }
        }
    }

    private fun requestCameraPermission() {
        ActivityCompat.requestPermissions(
            this, arrayOf(Manifest.permission.CAMERA), MY_PERMISSION_REQUEST_CAMERA
        )
    }

    private fun openCamera() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (takePictureIntent.resolveActivity(packageManager) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                REQUEST_IMAGE_CAPTURE -> {
                    val imageBitmap = data?.extras?.get("data") as Bitmap
                    userImage.setImageBitmap(imageBitmap)
                    createImageFile()
                }
            }
        }
    }

    private fun createImageFile() {
        try {
            val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(
                Date()
            )
            val storageDir: File? = getExternalFilesDir("Pictures")
            val imageFile = File.createTempFile("JPEG_${timeStamp}_", ".jpg", storageDir)
            currentPhotoPath = imageFile.absolutePath
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

    private fun uploadProfileImageToStorage(userId: String) {
        val storageReference =
            FirebaseStorage.getInstance().getReference("profile_images").child(userId)
        val imageBitmap = (userImage.drawable as BitmapDrawable).bitmap

        val baos = ByteArrayOutputStream()
        imageBitmap.compress(Bitmap.CompressFormat.PNG, 100, baos)
        val data = baos.toByteArray()

        storageReference.putBytes(data).addOnSuccessListener {
            // Subida exitosa
            Toast.makeText(this, "Imagen subida exitosamente", Toast.LENGTH_LONG).show()
        }.addOnFailureListener {
            Toast.makeText(this, "Error al subir la imagen", Toast.LENGTH_LONG).show()
            // Manejar el error en caso de falla
        }
    }

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1001
        private const val CAMERA_PERMISSION_REQUEST_CODE = 1002
        val MY_PERMISSION_REQUEST_CAMERA = 103
    }


}