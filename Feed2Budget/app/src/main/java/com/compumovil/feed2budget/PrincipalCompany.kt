package com.compumovil.feed2budget

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

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

        semanal.setOnClickListener {
            val intent = Intent(this, platosRestaurantes::class.java)
            intent.putExtra("id", 0)
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