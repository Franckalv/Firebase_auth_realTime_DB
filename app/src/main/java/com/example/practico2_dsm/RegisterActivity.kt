package com.example.practico2_dsm

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class RegisterActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    private lateinit var buttonRegister: Button
    private lateinit var textViewLogin: TextView

    private lateinit var etEmail: EditText
    private lateinit var etPass: EditText

    private lateinit var authStateListener: FirebaseAuth.AuthStateListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_register)

        auth = FirebaseAuth.getInstance()

        etEmail = findViewById(R.id.txtEmail)
        etPass = findViewById(R.id.txtPass)
        buttonRegister = findViewById(R.id.btnRegister)
        textViewLogin = findViewById(R.id.textViewLogin)

        buttonRegister.setOnClickListener {
            val email = etEmail.text.toString().trim()
            val password = etPass.text.toString().trim()

            if (email.isEmpty()) {
                etEmail.error = "El correo no puede estar vacío"
                return@setOnClickListener
            }

            if (!isValidEmail(email)) {
                etEmail.error = "Formato de correo inválido"
                return@setOnClickListener
            }

            if (password.length < 6) {
                etPass.error = "La contraseña debe tener al menos 6 caracteres"
                return@setOnClickListener
            }

            register(email, password)
        }

        textViewLogin.setOnClickListener {
            goToLogin()
        }

        checkUser()
    }

    override fun onResume() {
        super.onResume()
        auth.addAuthStateListener(authStateListener)
    }

    override fun onPause() {
        super.onPause()
        auth.removeAuthStateListener(authStateListener)
    }

    private fun checkUser() {
        authStateListener = FirebaseAuth.AuthStateListener { auth ->
            if (auth.currentUser != null) {
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }
        }
    }

    private fun register(email: String?, password: String?) {
        if (email.isNullOrBlank() || password.isNullOrBlank()) {
            Toast.makeText(this, "No se puede registrar con campos vacíos", Toast.LENGTH_SHORT).show()
            return
        }

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Registro exitoso", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                } else {
                    Toast.makeText(this, "Error: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun goToLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
    }

    private fun isValidEmail(email: String): Boolean {
        val emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+".toRegex()
        return email.matches(emailPattern)
    }
}
