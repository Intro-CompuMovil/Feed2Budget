package com.compumovil.feed2budget

import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.widget.Button
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
import java.io.File

class anadirProducto : AppCompatActivity() {
    private val CAMERA_PERMISSION = android.Manifest.permission.CAMERA
    private val MY_PERMISSION_REQUEST_CAMERA = 0

    private lateinit var captureIV : ImageButton
    private lateinit var imageUrl : Uri

    private val contract = registerForActivityResult(ActivityResultContracts.TakePicture()){
        captureIV.setImageURI(null)
        captureIV.setImageURI(imageUrl)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_anadir_producto)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val save = findViewById<Button>(R.id.saveChanges)

        save.setOnClickListener {
            finish()
        }

        imageUrl = createImageUri()
        captureIV = findViewById(R.id.userImage)

        captureIV.setOnClickListener{
            when {
                ContextCompat.checkSelfPermission(
                    this, android.Manifest.permission.CAMERA
                ) == PackageManager.PERMISSION_GRANTED -> {
                    contract.launch(imageUrl)
                }

                ActivityCompat.shouldShowRequestPermissionRationale(
                    this, android.Manifest.permission.CAMERA
                ) -> {
                    // In an educational UI, explain to the user why your app requires this
                    // permission for a specific feature to behave as expected, and what
                    // features are disabled if it's declined.
                    //showInContextUI(...)
                    showInContextUI()
                    Toast.makeText(this, "Necesita permiso de camara para cambiar la foto", Toast.LENGTH_SHORT).show()

                }

                else -> {
                    // You can directly ask for the permission.
                    requestPermissions(
                        arrayOf(android.Manifest.permission.CAMERA),
                        MY_PERMISSION_REQUEST_CAMERA
                    )
                }
            }
            //if (ContextCompat.checkSelfPermission(this, CAMERA_PERMISSION) == PackageManager.PERMISSION_GRANTED){
            //contract.launch(imageUrl)
            //}
        }
    }

    private fun showInContextUI() {
        val alertDialog = AlertDialog.Builder(this)
        alertDialog.setTitle("Permiso de Cámara Necesario")
        alertDialog.setMessage("Esta aplicación necesita acceso a tu cámara para tomar fotos. Sin este permiso, no podrás utilizar las funciones de captura de imágenes.")
        alertDialog.setPositiveButton("Ok") { dialog, which ->
            // Solicitar el permiso después de que el usuario haya entendido por qué es necesario
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.CAMERA),
                MY_PERMISSION_REQUEST_CAMERA
            )
        }
        alertDialog.setNegativeButton("Cancelar") { dialog, which ->
            dialog.dismiss()
        }
        alertDialog.show()
    }

    private fun createImageUri(): Uri {
        val image = File(filesDir, "camera_photos.png")
        return FileProvider.getUriForFile(this,
            "com.compumovil.feed2budget.FileProvider",
            image)
    }
}