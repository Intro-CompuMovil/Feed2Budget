package com.compumovil.feed2budget.Cliente

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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage

class PrincipalUser : AppCompatActivity() {
    private lateinit var userImage: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_principal_user)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val userInfo = findViewById<ImageButton>(R.id.usuario)
        val weekly = findViewById<Button>(R.id.semanal)
        val presupuesto = findViewById<Button>(R.id.presupuesto)
        val restaurantes = findViewById<Button>(R.id.restaurantes)
        val mapa = findViewById<Button>(R.id.mapa)

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
                Glide.with(this@PrincipalUser).load(uri).into(userInfo)
            }
        }


        userInfo.setOnClickListener{
            val intent = Intent(this, UserInfo::class.java)
            startActivity(intent)
        }

        weekly.setOnClickListener{
            val intent = Intent(this, Semanal::class.java)
            startActivity(intent)
        }

        presupuesto.setOnClickListener {
            val intent = Intent(this, Presupuesto::class.java)
            startActivity(intent)
        }

        restaurantes.setOnClickListener {
            val intent = Intent(this, Restaurantes::class.java)
            startActivity(intent)
        }

        mapa.setOnClickListener {
            val intent = Intent(this, MapsActivity::class.java)
            startActivity(intent)
        }
    }
}