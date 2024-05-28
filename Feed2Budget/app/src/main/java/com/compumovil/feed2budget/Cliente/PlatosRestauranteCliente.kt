package com.compumovil.feed2budget.Cliente

import android.content.Intent
import android.os.Bundle
import android.widget.AdapterView
import android.widget.ListView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.compumovil.feed2budget.Item
import com.compumovil.feed2budget.ItemAdapter
import com.compumovil.feed2budget.R
import com.compumovil.feed2budget.Restaurante.EditarProducto
import com.compumovil.feed2budget.User.Plato
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage

class PlatosRestauranteCliente : AppCompatActivity() {

    private lateinit var databaseReference: DatabaseReference
    private var items = mutableListOf<Item>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_platos_restaurantes)

        // Obtén el ID del restaurante del Intent que inició esta actividad
        val restaurantId = intent.getStringExtra("restaurantId")

        // Modifica la referencia de la base de datos para apuntar a los platos del restaurante seleccionado
        databaseReference = FirebaseDatabase.getInstance().getReference("users").child(restaurantId!!).child("platos")

        val listView: ListView = findViewById(R.id.listView)

        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                items.clear()
                for (platoSnapshot in dataSnapshot.children) {
                    val plato = platoSnapshot.getValue(Plato::class.java)
                    if (plato != null) {
                        val platoUid = platoSnapshot.key
                        val storageReference = FirebaseStorage.getInstance().getReference("platos_images").child(platoUid!!)
                        storageReference.downloadUrl.addOnSuccessListener { uri ->
                            val item = Item(uri, plato.nombre!!, plato.hora!!, plato.precio!!, platoUid)
                            items.add(item)
                            val adapter = ItemAdapter(this@PlatosRestauranteCliente, items)
                            listView.adapter = adapter

                            listView.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
                                val selectedItem = adapter.getItem(position)
                                val intent = Intent(this@PlatosRestauranteCliente, EditarProducto::class.java)
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