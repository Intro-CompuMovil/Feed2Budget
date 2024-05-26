package com.compumovil.feed2budget.Cliente

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.compumovil.feed2budget.PresupuestoGlobal
import com.compumovil.feed2budget.R

class Presupuesto : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_presupuesto)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        var nuevoValor = "Su Presupuesto actual es de: " + PresupuestoGlobal.presupuestoValor.toString() + "$"

        val guardar = findViewById<Button>(R.id.saveChanges)
        val presupuesto = findViewById<EditText>(R.id.nuevoPresupuesto)
        val textoPresupuesto = findViewById<TextView>(R.id.presupuestoActual)
        val error = findViewById<TextView>(R.id.errorMessage)

        textoPresupuesto.text = nuevoValor


        guardar.setOnClickListener {
            if (presupuesto.text.toString().isEmpty()){
                error.text = "ERROR: no puede estar vacio el campo"
            }
            else if (presupuesto.text.toString().toInt() < 0){
                error.text = "ERROR: el Presupuesto no puede ser negativo"
            }
            else{
                error.text = ""
                PresupuestoGlobal.presupuestoValor = presupuesto.text.toString().toInt()
                nuevoValor = "Su Presupuesto actual es de: " + presupuesto.text.toString() + "$"
                textoPresupuesto.text = nuevoValor
                presupuesto.text.clear()
                Toast.makeText(this, "Presupuesto actualizado correctamente", Toast.LENGTH_SHORT).show()
            }
        }
    }
}