package com.compumovil.feed2budget

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.compumovil.feed2budget.Cliente.PrincipalUser
import com.compumovil.feed2budget.Cliente.UserRegister
import com.compumovil.feed2budget.Restaurante.EmpresaRegister
import com.compumovil.feed2budget.Restaurante.PrincipalCompany
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage

class Register : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        val btnRegisterCliente = findViewById<Button>(R.id.registerButtonCliente)
        val btnRegisterEmpresa = findViewById<Button>(R.id.registerButtonEmpresa)

        btnRegisterCliente.setOnClickListener {
            val intent = Intent(this, UserRegister::class.java)
            startActivity(intent)

        }
        btnRegisterEmpresa.setOnClickListener {
            val intent1 = Intent(this, EmpresaRegister::class.java)
            startActivity(intent1)
        }
    }

}