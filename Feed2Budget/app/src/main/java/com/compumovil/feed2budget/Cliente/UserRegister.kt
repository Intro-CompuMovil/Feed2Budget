package com.compumovil.feed2budget.Cliente

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.compumovil.feed2budget.R
import com.compumovil.feed2budget.Restaurante.PrincipalCompany
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.auth.User
import com.google.firebase.storage.FirebaseStorage
import java.io.ByteArrayOutputStream
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class UserRegister : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var storage: FirebaseStorage
    private lateinit var database: FirebaseDatabase
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText
    private lateinit var etFirstName: EditText
    private lateinit var ivProfileImage: ImageView
    private lateinit var etPresupuesto: EditText


    private val REQUEST_IMAGE_CAPTURE = 1
    private lateinit var currentPhotoPath: String


    val PATH_USERS = "users/"
    private lateinit var myRef: DatabaseReference
    private lateinit var uid: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_register)

        auth = FirebaseAuth.getInstance()
        storage = FirebaseStorage.getInstance()
        database = FirebaseDatabase.getInstance()
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        etEmail = findViewById(R.id.emailText)
        etPassword = findViewById(R.id.passwordText)
        etFirstName = findViewById(R.id.userText)
        ivProfileImage = findViewById(R.id.userImage)
        etPresupuesto = findViewById(R.id.etPresupuesto)

        val btnRegister = findViewById<Button>(R.id.registerButton)
        val btnTakePhoto = findViewById<Button>(R.id.btnTakePhoto)

        btnRegister.setOnClickListener {
            val email = etEmail.text.toString()
            val password = etPassword.text.toString()
            val firstName = etFirstName.text.toString()

            if (email.isNotEmpty() && password.isNotEmpty() && firstName.isNotEmpty()) {
                registerUser(
                    etEmail.text.toString(),
                    etPassword.text.toString(),
                    etFirstName.text.toString(),
                    etPresupuesto.text.toString().toDouble()


                )
            } else {
                Toast.makeText(this, "Complete todos los campos", Toast.LENGTH_SHORT).show()
            }
        }

        btnTakePhoto.setOnClickListener {
            askCameraPermission()
        }
    }

    private fun askCameraPermission() {
        when {
            ContextCompat.checkSelfPermission(
                this, Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED -> {
                openCamera()
                //Toast.makeText(this, "Gracias", Toast.LENGTH_SHORT).show()
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
                    ivProfileImage.setImageBitmap(imageBitmap)
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

    private fun registerUser(
        email: String, password: String, firstName: String, presupuesto: Double
    ) {
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val userId = auth.currentUser?.uid
                    if (userId != null) {
                        saveUserDataToDatabase(userId, firstName, presupuesto)
                        Toast.makeText(this, "Registro exitoso", Toast.LENGTH_SHORT).show()
                        uploadProfileImageToStorage(userId)
                        startActivity(Intent(this, PrincipalUser::class.java))

                    }

                    // finish()
                } else {
                    Toast.makeText(this, "Registro fallido", Toast.LENGTH_SHORT).show()
                }
            }
    }


    private fun saveUserDataToDatabase(userId: String, firstName: String, presupuesto: Double) {
        val user1 = com.compumovil.feed2budget.User.User()
        user1.firstName = firstName
        user1.presupuestoSemanal = presupuesto
        user1.Empresa = false
        user1.direccion = ""
        myRef = database.getReference(PATH_USERS + userId)

        myRef.setValue(user1)
        Toast.makeText(this, "Usuario guardado", Toast.LENGTH_SHORT).show()
    }


    private fun uploadProfileImageToStorage(userId: String) {
        val storageReference = storage.reference.child("profile_images").child(userId)
        val imageBitmap = (ivProfileImage.drawable as BitmapDrawable).bitmap

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