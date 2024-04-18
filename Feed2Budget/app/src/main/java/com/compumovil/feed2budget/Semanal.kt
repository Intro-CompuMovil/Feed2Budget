package com.compumovil.feed2budget

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class Semanal : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_semanal)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

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
}