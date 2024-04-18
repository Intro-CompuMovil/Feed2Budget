package com.compumovil.feed2budget

import android.content.Intent
import android.os.Bundle
import android.widget.ListView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class Restaurantes : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_restaurantes)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val items = listOf(
            Item(R.drawable.cardenales, "Cardenales", "09:00 AM - 09:00 PM"),
            Item(R.drawable.chula, "La chula", "06:00 PM - 04:00 AM"),
            Item(R.drawable.giornatta, "Giornatta", "08:00 AM - 05:00 PM")
        )

        val listView: ListView = findViewById(R.id.listView)
        val adapter = ItemAdapter(this, items)
        listView.adapter = adapter

        listView.setOnItemClickListener { adapterView, view, position, id ->
            val intent = Intent(this, platosRestaurantes::class.java)

            intent.putExtra("id", position)  // O cualquier otro dato relevante

            startActivity(intent)
        }
    }
}