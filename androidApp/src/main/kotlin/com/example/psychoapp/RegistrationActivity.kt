package com.example.psychoapp

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import androidx.room.Room
import kotlinx.coroutines.launch

class RegistrationActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val splashScreen = installSplashScreen()

        setContentView(R.layout.registration_activity)

        val db = Room.databaseBuilder(
            applicationContext,
            UsersDb::class.java,
            "users_database"
        ).build()

        val userLogin = findViewById<EditText>(R.id.login_space)
        val userPassword = findViewById<EditText>(R.id.password_space)
        val acceptUserPassword = findViewById<EditText>(R.id.accept_password_space)
        val regBtn = findViewById<Button>(R.id.reg_btn)

        regBtn.setOnClickListener {
            val login = userLogin.text.toString().trim()
            val password = userPassword.text.toString().trim()
            val acceptedPassword = acceptUserPassword.text.toString().trim()

            if (login.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Все поля должны быть заполнены", Toast.LENGTH_LONG).show()
            }
            else if (password != acceptedPassword) {
                Toast.makeText(this, "Пароли должны совпадать", Toast.LENGTH_LONG).show()
            }
            else {
                val userManager = UserManager(db)
                lifecycleScope.launch {
                    val user = User(login=login, password=password)
                    val isRegistered = userManager.registerUser(user)

                    if (isRegistered) {
                        Toast.makeText(this@RegistrationActivity, "Регистрация прошла успешно", Toast.LENGTH_LONG).show()

                        finish()
                    }
                    else {
                        Toast.makeText(this@RegistrationActivity, "Логин занят", Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }
}