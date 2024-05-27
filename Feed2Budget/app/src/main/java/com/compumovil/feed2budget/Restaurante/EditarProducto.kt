package com.compumovil.feed2budget.Restaurante

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.compumovil.feed2budget.R
import com.compumovil.feed2budget.User.Plato
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.storage.FirebaseStorage
import java.io.ByteArrayOutputStream
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class EditarProducto : AppCompatActivity() {

    private lateinit var etNombrePlato: EditText
    private lateinit var etDescripcion: EditText
    private lateinit var etPrecio: EditText
    private lateinit var ethora: EditText
    private lateinit var btnSaveChanges: Button
    private lateinit var btnDelete: ImageButton
    private lateinit var userImage: ImageButton
    private val REQUEST_IMAGE_CAPTURE = 1
    private lateinit var currentPhotoPath: String
    private lateinit var storage: FirebaseStorage


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_editar_producto)

        etNombrePlato = findViewById(R.id.nombrePlato)
        etDescripcion = findViewById(R.id.descripcion)
        etPrecio = findViewById(R.id.precio)
        ethora = findViewById(R.id.hora)
        btnSaveChanges = findViewById(R.id.saveChanges)
        btnDelete = findViewById(R.id.btnBorrar)
        userImage = findViewById(R.id.userImage)


        val platoUid = intent.getStringExtra("platoUid")
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        val databaseReference = FirebaseDatabase.getInstance().getReference("users").child(userId!!).child("platos").child(platoUid!!)

        userImage.setOnClickListener {
            askCameraPermission()
        }

        databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val plato = dataSnapshot.getValue(Plato::class.java)
                if (plato != null) {
                    etNombrePlato.setText(plato.nombre)
                    etDescripcion.setText(plato.descripcion)
                    etPrecio.setText(plato.precio.toString())
                    ethora.setText(plato.hora)

                    // Aquí cargamos la imagen del plato en userImage
                    val storageReference = FirebaseStorage.getInstance().getReference("platos_images").child(platoUid)
                    storageReference.downloadUrl.addOnSuccessListener { uri ->
                        Glide.with(this@EditarProducto).load(uri).into(userImage)
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Manejar el error
            }
        })

        btnSaveChanges.setOnClickListener {
            val nombrePlato = etNombrePlato.text.toString()
            val descripcion = etDescripcion.text.toString()
            val precio = etPrecio.text.toString()
            val hora = ethora.text.toString()

            if (nombrePlato.isNotEmpty() && descripcion.isNotEmpty() && precio.isNotEmpty() && hora.isNotEmpty()) {
                val plato = Plato()
                plato.nombre = nombrePlato
                plato.descripcion = descripcion
                plato.precio = precio.toDouble()
                plato.hora = hora

                databaseReference.setValue(plato).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        uploadProfileImageToStorage(userId, platoUid)
                        Toast.makeText(this, "Plato actualizado con éxito", Toast.LENGTH_SHORT).show()
                        finish()
                    } else {
                        Toast.makeText(this, "Error al actualizar el plato", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                Toast.makeText(this, "Complete todos los campos", Toast.LENGTH_SHORT).show()
            }
        }

        btnDelete.setOnClickListener {
            databaseReference.removeValue().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Plato eliminado con éxito", Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    Toast.makeText(this, "Error al eliminar el plato", Toast.LENGTH_SHORT).show()
                }
            }
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

    private fun uploadProfileImageToStorage(userId: String, platoUid: String) {
        val storageReference = FirebaseStorage.getInstance().getReference("platos_images").child(platoUid)
        val imageBitmap = (userImage.drawable as BitmapDrawable).bitmap

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