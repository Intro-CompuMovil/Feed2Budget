package com.compumovil.feed2budget

import android.os.Bundle
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
import com.compumovil.feed2budget.databinding.ActivityPlatosRestaurantesBinding

class platosRestaurantes : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_platos_restaurantes)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val id = intent.getIntExtra("id", 0)

        var items = mutableListOf<Item>()

        if(id == 0){
            items.clear()
            val items1 = listOf(
                Item(R.drawable.almuerzo, "Del dia", "12:00 PM - 03:00 PM"),
                Item(R.drawable.revueltos, "Huevos al gusto", "06:00 AM - 11:00 AM"),
                Item(R.drawable.paisa, "Bandeja Paisa", "12:00 PM - 03:00 PM")
            )
            items.addAll(items1)
        }
        else if(id == 1){
            items.clear()
            val items1 = listOf(
                Item(R.drawable.cena, "Burritos de cerdo", "8:00 PM - 12:00 AM"),
                Item(R.drawable.tacos, "Tacos al pastor", "8:00 PM - 10:00 PM"),
                Item(R.drawable.quesadillas, "quesadillas de birria", "9:00 PM - 11:00 PM")
            )
            items.addAll(items1)
        }
        else if(id == 2){
            items.clear()
            val items1 = listOf(
                Item(R.drawable.desayuno, "Huevos al gusto", "6:00 AM - 9:00 AM"),
                Item(R.drawable.caldo, "Caldo de costilla", "6:00 AM - 10:00 AM"),
                Item(R.drawable.bistec, "Bistec al caballo", "8:00 AM - 11:00 AM")
            )
            items.addAll(items1)
        }

        val listView: ListView = findViewById(R.id.listView)
        val adapter = ItemAdapter(this, items)
        listView.adapter = adapter
    }
}

