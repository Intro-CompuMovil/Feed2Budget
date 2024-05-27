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
import com.compumovil.feed2budget.User.PresupuestoGlobal
import com.compumovil.feed2budget.R
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.auth.FirebaseAuth


class Presupuesto : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_presupuesto)

        val textoPresupuesto =
            findViewById<TextView>(R.id.presupuestoActual) // Asegúrate de tener un TextView con este id en tu layout
        var nuevoValor: String

        val userId = FirebaseAuth.getInstance().currentUser?.uid
        val databaseReference = FirebaseDatabase.getInstance().getReference("users").child(userId!!)
        databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val presupuestoSemanal =
                    dataSnapshot.child("presupuestoSemanal").getValue(Double::class.java)
                if (presupuestoSemanal != null) {
                    // Actualizar el valor del presupuesto en la UI
                    nuevoValor = "Tu Presupuesto semanal actual es: $presupuestoSemanal$"
                    textoPresupuesto.text = nuevoValor
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Manejar el error
            }
        })
        // Obtener el EditText y el botón
        val nuevoPresupuesto = findViewById<EditText>(R.id.nuevoPresupuesto)
        val saveChanges = findViewById<Button>(R.id.saveChanges)

        // Agregar un OnClickListener al botón
        saveChanges.setOnClickListener {
            val nuevoValorPresupuesto = nuevoPresupuesto.text.toString().toDouble()

            // Actualizar el valor en la base de datos de Firebase
            databaseReference.child("presupuestoSemanal").setValue(nuevoValorPresupuesto)

            // Actualizar el valor del presupuesto en la UI
            nuevoValor = "Tu Presupuesto semanal actual es: $nuevoValorPresupuesto$"
            textoPresupuesto.text = nuevoValor
        }
    }
}