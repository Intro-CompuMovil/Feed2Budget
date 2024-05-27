package com.compumovil.feed2budget.Cliente

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.compumovil.feed2budget.R
import com.compumovil.feed2budget.UserInfo

class PrincipalUser : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_principal_user)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val userInfo = findViewById<ImageButton>(R.id.usuario)
        val weekly = findViewById<Button>(R.id.semanal)
        val presupuesto = findViewById<Button>(R.id.presupuesto)
        val restaurantes = findViewById<Button>(R.id.restaurantes)
        val mapa = findViewById<Button>(R.id.mapa)

        userInfo.setOnClickListener{
            val intent = Intent(this, UserInfo::class.java)
            startActivity(intent)
        }

        weekly.setOnClickListener{
            val intent = Intent(this, Semanal::class.java)
            startActivity(intent)
        }

        presupuesto.setOnClickListener {
            val intent = Intent(this, Presupuesto::class.java)
            startActivity(intent)
        }

        restaurantes.setOnClickListener {
            val intent = Intent(this, Restaurantes::class.java)
            startActivity(intent)
        }

        mapa.setOnClickListener {
            val intent = Intent(this, MapsActivity::class.java)
            startActivity(intent)
        }
    }
}