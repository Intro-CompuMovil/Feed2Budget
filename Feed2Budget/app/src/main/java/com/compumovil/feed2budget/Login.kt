package com.compumovil.feed2budget

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class Login : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val login = findViewById<Button>(R.id.loginButton)

        login.setOnClickListener{
            checkValues()
        }
    }

    private fun checkValues(){
        val username = findViewById<TextView>(R.id.userText)
        val password = findViewById<TextView>(R.id.passwordText)
        val errorMessage = findViewById<TextView>(R.id.errorMessage)

        if(username.text.toString().isEmpty() || password.text.toString().isEmpty()){
            errorMessage.text = "No puedes tener campos vacios"
        }
        else if(username.text.toString() == "miguel" && password.text.toString() == "1234"){
            val intent = Intent(this, PrincipalUser::class.java)
            startActivity(intent)
        }
        else if(username.text.toString() == "parrillada" && password.text.toString() == "1234"){
            val intent = Intent(this, PrincipalCompany::class.java)
            startActivity(intent)
        }
        else{
            errorMessage.text = "Usuario y/o contraseña incorrectos"
        }
    }
}