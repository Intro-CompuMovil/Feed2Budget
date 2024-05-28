package com.compumovil.feed2budget.Restaurante

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.compumovil.feed2budget.R
import com.compumovil.feed2budget.User.User
import com.compumovil.feed2budget.platosRestaurantes
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage

class PrincipalCompany : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_principal_company)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val semanal = findViewById<Button>(R.id.semanal)
        val info = findViewById<ImageButton>(R.id.usuario)
        val anadir = findViewById<Button>(R.id.anadir)
        val restaurantId = FirebaseAuth.getInstance().currentUser?.uid

        val user = FirebaseAuth.getInstance().currentUser
        val userId = FirebaseAuth.getInstance().currentUser?.uid


        val databaseReference = FirebaseDatabase.getInstance().getReference("users").child(user!!.uid)

        val title = findViewById<TextView>(R.id.username)
        databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val user = dataSnapshot.getValue(User::class.java)
                if (user != null) {
                    title.setText("Bienvenido, " + user.firstName) // Aquí se obtiene el nombre de usuario
                    // ...
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Manejar el error
            }
        })



        val firebaseUser = FirebaseAuth.getInstance().currentUser
        if (firebaseUser != null) {

            // Aquí cargamos la imagen del usuario en userImage
            val storageReference =
                FirebaseStorage.getInstance().getReference("profile_images").child(userId.toString())
            storageReference.downloadUrl.addOnSuccessListener { uri ->
                Glide.with(this@PrincipalCompany).load(uri).into(info)
            }
        }




        semanal.setOnClickListener {
            val intent = Intent(this, platosRestaurantes::class.java)
            intent.putExtra("restaurantId", restaurantId)
            startActivity(intent)
        }

        info.setOnClickListener{
            val intent = Intent(this, CompanyInfo::class.java)
            startActivity(intent)
        }

        anadir.setOnClickListener {
            val intent = Intent(this, anadirProducto::class.java)
            startActivity(intent)
        }

    }
}