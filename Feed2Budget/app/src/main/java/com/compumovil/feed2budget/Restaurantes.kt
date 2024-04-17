package com.compumovil.feed2budget

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
            Item(R.drawable.hamburguesa, "Hamburguesa", "09:00 AM"),
            Item(R.drawable.comida, "Filete", "01:00 PM")
        )

        val listView: ListView = findViewById(R.id.listView)
        val adapter = ItemAdapter(this, items)
        listView.adapter = adapter
    }
}