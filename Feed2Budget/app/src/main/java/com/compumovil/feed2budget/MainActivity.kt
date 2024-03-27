package com.compumovil.feed2budget

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val login = findViewById<Button>(R.id.loginButton)
        val register = findViewById<Button>(R.id.registerButton)

        login.setOnClickListener{
            val intent = Intent(this, Login::class.java)
            startActivity(intent)
        }

        register.setOnClickListener{
            val intent = Intent(this, Register::class.java)
            startActivity(intent)
        }

    }
}