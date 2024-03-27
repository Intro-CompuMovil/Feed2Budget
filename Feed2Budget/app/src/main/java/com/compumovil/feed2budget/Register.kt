package com.compumovil.feed2budget

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class Register : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_register)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val register = findViewById<Button>(R.id.registerButton)

        register.setOnClickListener {
            checkValues()
        }
    }

    private fun checkValues() {
        val username = findViewById<TextView>(R.id.userText)
        val password = findViewById<TextView>(R.id.passwordText)
        val email = findViewById<TextView>(R.id.emailText)
        val errorMessage = findViewById<TextView>(R.id.errorMessage)

        if(username.text.toString().isEmpty() || password.text.toString().isEmpty() || email.text.toString().isEmpty()){
            errorMessage.text = "No puedes tener campos vacios"
        }
        else if(username.text.toString() == "miguel"){
            errorMessage.text = "El usuario ya existe"
        }
        else{
            val intent = Intent(this, Login::class.java)
            startActivity(intent)
        }
    }
}