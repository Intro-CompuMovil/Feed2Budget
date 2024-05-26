package com.compumovil.feed2budget

import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.compumovil.feed2budget.Cliente.PrincipalUser
import com.compumovil.feed2budget.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = Firebase.auth
        val currentUser = auth.currentUser
        updateUI(currentUser)
        binding.loginButton.setOnClickListener {

            auth.signInWithEmailAndPassword(
                binding.userText.text.toString(),
                binding.passwordText.text.toString()
            )
                .addOnCompleteListener(this) { task ->

                    // Sign in task
                    Log.d(ContentValues.TAG, "signInWithEmail:onComplete:" + task.isSuccessful)
                    // If sign in fails, display a message to the user. If sign in succeeds
                    // the auth state listener will be notified and logic to handle the
                    // signed in user can be handled in the listener.
                    if (!task.isSuccessful) {
                        Log.w(ContentValues.TAG, "signInWithEmail:failure", task.exception)
                        Toast.makeText(
                            this, "Authentication failed: ${task.exception?.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                        binding.userText.setText("")
                        binding.passwordText.setText("")
                    } else {
                        val user = auth.currentUser

                        updateUI(user)
                        val intent = Intent(this, PrincipalUser::class.java)
                        startActivity(intent)
                    }
                }
        }
        binding.registerButton.setOnClickListener {
            val intent = Intent(this, Register::class.java)
            startActivity(intent)
        }








    }
    private fun checkValues(){
        val username = findViewById<TextView>(R.id.userText)
        val password = findViewById<TextView>(R.id.passwordText)
        val errorMessage = findViewById<TextView>(R.id.errorMessage)

        if(username.text.toString().isEmpty() || password.text.toString().isEmpty()){
            errorMessage.text = "No puedes tener campos vacios"
        }

    }
    private fun updateUI(currentUser: FirebaseUser?) {
        if (currentUser != null) {
            val intent = Intent(this, PrincipalUser::class.java)
            intent.putExtra("user", currentUser.email)
            startActivity(intent)
        } else {
            binding.userText.setText("")
            binding.passwordText.setText("")
        }
    }
}