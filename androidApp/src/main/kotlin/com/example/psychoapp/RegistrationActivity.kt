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

        val userLogin = findViewById<EditText>(R.id.login_space)
        val userPassword = findViewById<EditText>(R.id.password_space)
        val acceptUserPassword = findViewById<EditText>(R.id.accept_password_space)
        val regBtn = findViewById<Button>(R.id.reg_btn)

        regBtn.setOnClickListener {
            val login = userLogin.text.toString().trim()
            val password = userPassword.text.toString().trim()
            val acceptedPassword = acceptUserPassword.text.toString().trim()

            if (login == "" || password == "") {
                Toast.makeText(this, "Все поля должны быть заполнены", Toast.LENGTH_LONG).show()
            }
            else if (password != acceptedPassword) {
                Toast.makeText(this, "Пароли должны совпадать", Toast.LENGTH_LONG).show()
            }
            else {
                val user = User(login, password)
                val db = DbHelper(this, null)

                val isRegistered = db.registerUser(user)
                if (isRegistered) {
                    Toast.makeText(this, "Регистрация прошла успешно", Toast.LENGTH_LONG).show()

                    finish()
                }
                else {
                    Toast.makeText(this, "Пользователем с таким логином уже существует. Пожалуйста, придумайте другой логин", Toast.LENGTH_LONG).show()
                }

                db.close()
            }
        }
    }
}