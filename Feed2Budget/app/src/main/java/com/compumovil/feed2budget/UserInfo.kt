package com.compumovil.feed2budget

import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import java.io.File

class UserInfo : AppCompatActivity() {

    private val CAMERA_PERMISSION = android.Manifest.permission.CAMERA

    private lateinit var captureIV : ImageButton
    private lateinit var imageUrl : Uri

    private val contract = registerForActivityResult(ActivityResultContracts.TakePicture()){
        captureIV.setImageURI(null)
        captureIV.setImageURI(imageUrl)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_user_info)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val guardar = findViewById<Button>(R.id.saveChanges)

        guardar.setOnClickListener {
            val toast = Toast.makeText(this, "Información actualizada", Toast.LENGTH_SHORT)
            toast.show()
        }

        imageUrl = createImageUri()
        captureIV = findViewById(R.id.userImage)

        captureIV.setOnClickListener{
            if (ContextCompat.checkSelfPermission(this, CAMERA_PERMISSION) == PackageManager.PERMISSION_GRANTED){
                contract.launch(imageUrl)
            }
        }
    }

    private fun createImageUri(): Uri {
        val image = File(filesDir, "camera_photos.png")
        return FileProvider.getUriForFile(this,
            "com.compumovil.feed2budget.FileProvider",
            image)
    }

}