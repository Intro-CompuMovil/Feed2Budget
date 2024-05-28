package com.compumovil.feed2budget.Cliente

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.compumovil.feed2budget.R
import com.compumovil.feed2budget.User.Plato
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage

class DetallePlato : AppCompatActivity() {
    private lateinit var databaseReference: DatabaseReference
    private lateinit var imageView: ImageView
    private lateinit var title: TextView
    private lateinit var descripcion: TextView
    private lateinit var precio: TextView
    private lateinit var restaurante: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_detalle_plato)

        imageView = findViewById(R.id.imageView3)
        title = findViewById(R.id.title)
        descripcion = findViewById(R.id.descripcion)
        precio = findViewById(R.id.precio)

        // Obtén la UID del plato y el ID del restaurante del Intent que inició esta actividad
        val platoUid = intent.getStringExtra("platoUid")
        val restaurantId = intent.getStringExtra("restaurantId")

        // Modifica la referencia de la base de datos para apuntar al plato seleccionado
        databaseReference = FirebaseDatabase.getInstance().getReference("users").child(restaurantId!!).child("platos").child(platoUid!!)

        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val plato = dataSnapshot.getValue(Plato::class.java)
                if (plato != null) {
                    title.text = plato.nombre
                    descripcion.text = plato.descripcion
                    precio.text = "Precio: $${plato.precio} COP"

                    // Aquí cargamos la imagen del plato en imageView
                    val storageReference = FirebaseStorage.getInstance().getReference("platos_images").child(platoUid)
                    storageReference.downloadUrl.addOnSuccessListener { uri ->
                        Glide.with(this@DetallePlato).load(uri).into(imageView)
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Manejar el error
            }
        })
        val btnMapa = findViewById<TextView>(R.id.btnMapa)
        btnMapa.setOnClickListener {
            Intent(this, MapsActivity::class.java).apply {
                putExtra("restaurantId", restaurantId)
                startActivity(this)
            }
        }
    }
}