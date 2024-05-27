package com.compumovil.feed2budget.Restaurante

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
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.compumovil.feed2budget.R
import com.compumovil.feed2budget.User.Plato
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import java.io.File

import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.storage.FirebaseStorage
import java.io.ByteArrayOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class anadirProducto : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var storage: FirebaseStorage
    private lateinit var database: FirebaseDatabase
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private lateinit var etNombrePlato: EditText
    private lateinit var etDescripcion: EditText
    private lateinit var etPrecio: EditText
    private lateinit var ethora: EditText
    private lateinit var ivProfileImage: ImageView


    private val REQUEST_IMAGE_CAPTURE = 1
    private lateinit var currentPhotoPath: String


    val PATH_USERS = "users/"
    private lateinit var myRef: DatabaseReference
    private lateinit var uid: String


    private lateinit var imageUrl: Uri


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_anadir_producto)
        auth = FirebaseAuth.getInstance()
        storage = FirebaseStorage.getInstance()
        database = FirebaseDatabase.getInstance()
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        etNombrePlato = findViewById(R.id.nombrePlato)
        etDescripcion = findViewById(R.id.descripcion)
        etPrecio = findViewById(R.id.precio)
        ethora = findViewById(R.id.hora)
        ivProfileImage = findViewById(R.id.userImage)


        ivProfileImage.setOnClickListener {
            askCameraPermission()
        }


        val save = findViewById<Button>(R.id.saveChanges)
        save.setOnClickListener {
            val nombrePlato = etNombrePlato.text.toString()
            val descripcion = etDescripcion.text.toString()
            val precio = etPrecio.text.toString()
            val hora = ethora.text.toString()



            if (nombrePlato.isNotEmpty() && descripcion.isNotEmpty() && precio.isNotEmpty() && hora.isNotEmpty()) {
                registerPlato(
                    etNombrePlato.text.toString(),
                    etDescripcion.text.toString(),
                    etPrecio.text.toString(),
                    ethora.text.toString()
                )
            } else {
                Toast.makeText(this, "Complete todos los campos", Toast.LENGTH_SHORT).show()

            }
        }

    }

    private fun registerPlato(nombre: String, descripcion: String, precio: String, hora: String) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        val databaseReference = FirebaseDatabase.getInstance().getReference("users").child(userId!!)

        // Crear un nuevo plato
        val plato = Plato()
        plato.nombre = nombre
        plato.descripcion = descripcion
        plato.precio = precio.toDouble()
        plato.hora = hora

        // Agregar el plato a la lista de platos del usuario
        val newPlatoReference = databaseReference.child("platos").push()
        newPlatoReference.setValue(plato).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                // Aquí obtenemos el UID del nuevo plato
                val platoUid = newPlatoReference.key
                uploadProfileImageToStorage(userId, platoUid!!)
                Toast.makeText(this, "Plato agregado con éxito", Toast.LENGTH_SHORT).show()
                finish()
            } else {
                Toast.makeText(this, "Error al agregar el plato", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun createImageUri(): Uri {
        val image = File(filesDir, "camera_photos.png")
        return FileProvider.getUriForFile(
            this, "com.compumovil.feed2budget.FileProvider", image
        )
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

    private fun uploadProfileImageToStorage(userId: String, platoUid: String) {
        val storageReference = storage.reference.child("platos_images").child(platoUid)
        val imageBitmap = (ivProfileImage.drawable as BitmapDrawable).bitmap

        val baos = ByteArrayOutputStream()
        imageBitmap.compress(Bitmap.CompressFormat.PNG, 100, baos)
        val data = baos.toByteArray()

        storageReference.putBytes(data)
            .addOnSuccessListener {
                // Subida exitosa
                Toast.makeText(this, "Imagen subida exitosamente", Toast.LENGTH_LONG).show()
            }
            .addOnFailureListener {
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

