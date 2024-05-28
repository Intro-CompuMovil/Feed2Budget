package com.compumovil.feed2budget

import android.content.Intent
import android.os.Bundle
import android.widget.AdapterView
import android.widget.ListView
import androidx.activity.enableEdgeToEdge
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import com.compumovil.feed2budget.Restaurante.EditarProducto
import com.compumovil.feed2budget.User.Plato
import com.compumovil.feed2budget.databinding.ActivityPlatosRestaurantesBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage

class platosRestaurantes : AppCompatActivity() {

    private lateinit var databaseReference: DatabaseReference
    private var items = mutableListOf<Item>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_platos_restaurantes)
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        databaseReference =
            FirebaseDatabase.getInstance().getReference("users").child(userId!!).child("platos")

        val listView: ListView = findViewById(R.id.listView)


        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                items.clear()
                for (platoSnapshot in dataSnapshot.children) {
                    val plato = platoSnapshot.getValue(Plato::class.java)
                    if (plato != null) {
                        val platoUid = platoSnapshot.key
                        val storageReference =
                            FirebaseStorage.getInstance().getReference("platos_images")
                                .child(platoUid!!)
                        storageReference.downloadUrl.addOnSuccessListener { uri ->
                            val item =
                                Item(uri, plato.nombre!!, plato.hora!!, plato.precio!!, platoUid)
                            items.add(item)
                            val adapter = ItemAdapter(this@platosRestaurantes, items)
                            listView.adapter = adapter

                            // Aquí agregas el onItemClickListener a tu ListView
                            listView.onItemClickListener =
                                AdapterView.OnItemClickListener { _, _, position, _ ->
                                    val selectedItem = adapter.getItem(position)

                                    val intent =
                                        Intent(this@platosRestaurantes, EditarProducto::class.java)
                                    if (selectedItem != null) {
                                        intent.putExtra("platoUid", selectedItem.uid)
                                    }
                                    startActivity(intent)
                                }
                        }
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Manejar el error
            }
        })
    }
}

