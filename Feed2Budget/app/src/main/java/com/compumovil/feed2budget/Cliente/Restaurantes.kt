package com.compumovil.feed2budget.Cliente

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ListView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.compumovil.feed2budget.Item
import com.compumovil.feed2budget.ItemAdapter
import com.compumovil.feed2budget.R
import com.compumovil.feed2budget.User.User
import com.compumovil.feed2budget.platosRestaurantes
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage

class Restaurantes : AppCompatActivity() {
    private lateinit var databaseReference: DatabaseReference
    private var items = mutableListOf<Item>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_restaurantes)

        databaseReference = FirebaseDatabase.getInstance().getReference("users")

        val listView: ListView = findViewById(R.id.listView)

        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                items.clear()
                for (userSnapshot in dataSnapshot.children) {
                    val user = userSnapshot.getValue(User::class.java)
                    if (user != null && user.empresa) {
                        val userId = userSnapshot.key
                        val storageReference = FirebaseStorage.getInstance().getReference("profile_images").child(userId!!)
                        storageReference.downloadUrl.addOnSuccessListener { uri ->
                            val item = Item(uri, user.firstName, "", 0.0, userId)
                            items.add(item)
                            val adapter = ItemAdapter(this@Restaurantes, items)
                            listView.adapter = adapter

                            listView.setOnItemClickListener { _, _, position, _ ->
                                val selectedItem = adapter.getItem(position)
                                val intent = Intent(this@Restaurantes, PlatosRestauranteCliente::class.java)
                                if (selectedItem != null) {
                                    intent.putExtra("restaurantId", selectedItem.uid)
                                }
                                startActivity(intent)
                            }



                            Log.d("Restaurantes", "Item added: $item")
                        }.addOnFailureListener { exception ->
                            Log.e("Restaurantes", "Failed to download image", exception)
                        }
                    } else {
                        Log.d("Restaurantes", "User is not a company: ${user?.firstName}")
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.e("Restaurantes", "Failed to read users", databaseError.toException())
            }
        })
    }
}