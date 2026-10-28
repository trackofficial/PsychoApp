package com.example.psychoapp

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.ComponentActivity

class RegistrationActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.registration_activity)

        val userLogin: EditText = findViewById(R.id.login_space)
        val userPassword: EditText = findViewById(R.id.password_space)
        val acceptUserPassword: EditText = findViewById(R.id.accept_password_space)
        val regBtn: Button = findViewById(R.id.reg_btn)

        regBtn.setOnClickListener {
            val login = userLogin.text.toString().trim()
            val password = userPassword.text.toString().trim()
            val acceptedPassword = acceptUserPassword.text.toString().trim()

            if (login == "" || password == "") {
                Toast.makeText(this, "There must be no empty fields", Toast.LENGTH_LONG).show()
            }
            else if (password != acceptedPassword) {
                Toast.makeText(this, "Passwords must be equal", Toast.LENGTH_LONG).show()
            }
            else {
                val user = User(login, password)
                val db = DbHelper(this, null)

                val isRegistered = db.registerUser(user)
                if (isRegistered) {
                    Toast.makeText(this, "User $login was registered with password $password", Toast.LENGTH_LONG).show()

                    finish()
                }
                else {
                    Toast.makeText(this, "User with this login already exists!", Toast.LENGTH_LONG).show()
                }

                db.close()
            }
        }
    }
}