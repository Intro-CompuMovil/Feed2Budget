package com.compumovil.feed2budget.Cliente

import android.content.Intent
import android.media.Image
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.AdapterView
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.compumovil.feed2budget.Item
import com.compumovil.feed2budget.ItemAdapter
import com.compumovil.feed2budget.R
import com.compumovil.feed2budget.User.Plato
import com.compumovil.feed2budget.User.User
import com.compumovil.feed2budget.semanalDescripcion
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import org.osmdroid.util.GeoPoint

class Semanal : AppCompatActivity() {

    private lateinit var databaseReference: DatabaseReference
    private var items = mutableListOf<Item>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_semanal)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        changeMenu()

        val semanaLunes = findViewById<Button>(R.id.semanal)
        val semanaMartes = findViewById<Button>(R.id.semanal2)
        val semanaMiercoles = findViewById<Button>(R.id.semanal3)
        val semanaJueves = findViewById<Button>(R.id.semanal4)
        val semanaViernes = findViewById<Button>(R.id.semanal5)
        val semanaSabado = findViewById<Button>(R.id.semanal6)
        val semanaDomingo = findViewById<Button>(R.id.semanal7)

        semanaLunes.setOnClickListener {
            val intent = Intent(this, semanalDescripcion::class.java)
            intent.putExtra("dia", "Lunes")
            startActivity(intent)
        }
        semanaMartes.setOnClickListener {
            val intent = Intent(this, semanalDescripcion::class.java)
            intent.putExtra("dia", "Martes")
            startActivity(intent)
        }
        semanaMiercoles.setOnClickListener {
            val intent = Intent(this, semanalDescripcion::class.java)
            intent.putExtra("dia", "Miercoles")
            startActivity(intent)
        }
        semanaJueves.setOnClickListener {
            val intent = Intent(this, semanalDescripcion::class.java)
            intent.putExtra("dia", "Jueves")
            startActivity(intent)
        }
        semanaViernes.setOnClickListener {
            val intent = Intent(this, semanalDescripcion::class.java)
            intent.putExtra("dia", "Viernes")
            startActivity(intent)
        }
        semanaSabado.setOnClickListener {
            val intent = Intent(this, semanalDescripcion::class.java)
            intent.putExtra("dia", "Sabado")
            startActivity(intent)
        }
        semanaDomingo.setOnClickListener {
            val intent = Intent(this, semanalDescripcion::class.java)
            intent.putExtra("dia", "Domingo")
            startActivity(intent)
        }
    }

    fun changeMenu(){
        Toast.makeText(this@Semanal, "buscando", Toast.LENGTH_SHORT).show()
        val listaRestaurantes = mutableListOf<String?>()

        databaseReference = FirebaseDatabase.getInstance().getReference("users")
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                items.clear()
                for (userSnapshot in dataSnapshot.children) {
                    val user = userSnapshot.getValue(User::class.java)
                    if (user != null && user.empresa) {
                        Toast.makeText(this@Semanal, "Encontrado", Toast.LENGTH_SHORT).show()
                        listaRestaurantes.add(userSnapshot.key)

                    } else {
                        Log.d("Restaurantes", "User is not a company: ${user?.firstName}")
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.e("Restaurantes", "Failed to read users", databaseError.toException())
            }
        })

        for (restaurantId in listaRestaurantes) {
            Toast.makeText(this@Semanal, restaurantId, Toast.LENGTH_SHORT).show()
            databaseReference =
                FirebaseDatabase.getInstance().getReference("users").child(restaurantId!!)
                    .child("platos")
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
                            }
                        }
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    Toast.makeText(this@Semanal, "Error", Toast.LENGTH_SHORT).show()
                    // Manejar el error
                }
            })
        }

        val desayuno = mutableListOf<Item>()
        val almuerzo = mutableListOf<Item>()
        val cena = mutableListOf<Item>()

        for (platos in items){
            if (platos.time.toInt() > 4 && platos.time.toInt() < 11){
                desayuno.add(platos)
            }
            else if(platos.time.toInt() > 11 && platos.time.toInt() < 4){
                almuerzo.add(platos)
            }
            else{
                cena.add(platos)
            }
        }

        val listaSemanal = price(desayuno, almuerzo, cena)
        val desayuno1 = findViewById<ImageView>(R.id.imageView3)
        val desayuno2 = findViewById<ImageView>(R.id.imageView5)
        val desayuno3 = findViewById<ImageView>(R.id.imageView8)
        val desayuno4 = findViewById<ImageView>(R.id.imageView11)
        val desayuno5 = findViewById<ImageView>(R.id.imageView14)
        val desayuno6 = findViewById<ImageView>(R.id.imageView17)
        val desayuno7 = findViewById<ImageView>(R.id.imageView20)

        val almuerzo1 = findViewById<ImageView>(R.id.imageView4)
        val almuerzo2 = findViewById<ImageView>(R.id.imageView6)
        val almuerzo3 = findViewById<ImageView>(R.id.imageView9)
        val almuerzo4 = findViewById<ImageView>(R.id.imageView12)
        val almuerzo5 = findViewById<ImageView>(R.id.imageView15)
        val almuerzo6 = findViewById<ImageView>(R.id.imageView18)
        val almuerzo7 = findViewById<ImageView>(R.id.imageView21)

        val cena1 = findViewById<ImageView>(R.id.imageView2)
        val cena2 = findViewById<ImageView>(R.id.imageView7)
        val cena3 = findViewById<ImageView>(R.id.imageView10)
        val cena4 = findViewById<ImageView>(R.id.imageView13)
        val cena5 = findViewById<ImageView>(R.id.imageView16)
        val cena6 = findViewById<ImageView>(R.id.imageView19)
        val cena7 = findViewById<ImageView>(R.id.imageView22)

        for (restaurantId in listaRestaurantes) {
            databaseReference =
                FirebaseDatabase.getInstance().getReference("users").child(restaurantId!!)
                    .child("platos")
            databaseReference.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    items.clear()
                    for (platoSnapshot in dataSnapshot.children) {
                        val plato = platoSnapshot.getValue(Plato::class.java)
                        if (plato != null) {
                            val platoUid = platoSnapshot.key
                            val storageReference = FirebaseStorage.getInstance().getReference("platos_images").child(platoUid!!)
                            var item = Item(Uri.EMPTY,"","",0.0,"")
                            storageReference.downloadUrl.addOnSuccessListener { uri ->
                                item = Item(uri, plato.nombre!!, plato.hora!!, plato.precio!!, platoUid)
                            }
                            if(item == listaSemanal[0]){
                                val storageReference1 = FirebaseStorage.getInstance().getReference("platos_images").child(platoUid)
                                storageReference1.downloadUrl.addOnSuccessListener { uri ->
                                    Glide.with(this@Semanal).load(uri).into(desayuno1)
                                    Glide.with(this@Semanal).load(uri).into(desayuno2)
                                    Glide.with(this@Semanal).load(uri).into(desayuno3)
                                    Glide.with(this@Semanal).load(uri).into(desayuno4)
                                    Glide.with(this@Semanal).load(uri).into(desayuno5)
                                    Glide.with(this@Semanal).load(uri).into(desayuno6)
                                    Glide.with(this@Semanal).load(uri).into(desayuno7)
                                }
                            }
                            else if(item == listaSemanal[0]){
                                val storageReference1 = FirebaseStorage.getInstance().getReference("platos_images").child(platoUid)
                                storageReference1.downloadUrl.addOnSuccessListener { uri ->
                                    Glide.with(this@Semanal).load(uri).into(almuerzo1)
                                    Glide.with(this@Semanal).load(uri).into(almuerzo2)
                                    Glide.with(this@Semanal).load(uri).into(almuerzo3)
                                    Glide.with(this@Semanal).load(uri).into(almuerzo4)
                                    Glide.with(this@Semanal).load(uri).into(almuerzo5)
                                    Glide.with(this@Semanal).load(uri).into(almuerzo6)
                                    Glide.with(this@Semanal).load(uri).into(almuerzo7)
                                }
                            }
                            else if(item == listaSemanal[0]){
                                val storageReference1 = FirebaseStorage.getInstance().getReference("platos_images").child(platoUid)
                                storageReference1.downloadUrl.addOnSuccessListener { uri ->
                                    Glide.with(this@Semanal).load(uri).into(cena1)
                                    Glide.with(this@Semanal).load(uri).into(cena2)
                                    Glide.with(this@Semanal).load(uri).into(cena3)
                                    Glide.with(this@Semanal).load(uri).into(cena4)
                                    Glide.with(this@Semanal).load(uri).into(cena5)
                                    Glide.with(this@Semanal).load(uri).into(cena6)
                                    Glide.with(this@Semanal).load(uri).into(cena7)
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

    fun price(desayuno: MutableList<Item>, almuerzo: MutableList<Item>, cena: MutableList<Item>): MutableList<Item> {
        val platoSemanal = mutableListOf<Item>()
        var presupuestoSemanal: Double? = 0.0

        val userId = FirebaseAuth.getInstance().currentUser?.uid
        val databaseReference = FirebaseDatabase.getInstance().getReference("users").child(userId!!)
        databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                presupuestoSemanal =
                    dataSnapshot.child("presupuestoSemanal").getValue(Double::class.java)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Manejar el error
            }
        })

        for(platoDesayuno in desayuno){
            for (platoAlmuerzo in almuerzo){
                for (platoCena in cena){
                    if (platoDesayuno.precio.toInt() + platoAlmuerzo.precio.toInt() + platoCena.precio.toInt() <= presupuestoSemanal!!){
                        platoSemanal.add(platoDesayuno)
                        platoSemanal.add(platoAlmuerzo)
                        platoSemanal.add(platoCena)
                        return platoSemanal
                    }
                }
            }
        }
        return platoSemanal
    }
}