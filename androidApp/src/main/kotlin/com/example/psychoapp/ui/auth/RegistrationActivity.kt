package com.example.psychoapp.ui.auth

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import com.example.psychoapp.R
import com.example.psychoapp.data.local.AppDbProvider
import com.example.psychoapp.data.local.users.model.User
import com.example.psychoapp.ui.main.MainActivity
import com.example.psychoapp.utils.SessionManager
import com.example.psychoapp.utils.UserManager
import kotlinx.coroutines.launch

class RegistrationActivity : ComponentActivity() {
    // * валидация пароля
    fun validatePassword(password: String, userPassword: EditText, acceptUserPassword: EditText): Boolean {
        if (password.length < 4) {
            Toast.makeText(this, "Пароль должен содержать минимум 4 символа", Toast.LENGTH_LONG).show ()

            userPassword.text.clear()
            acceptUserPassword.text.clear()

            return false
        }
        else {
            return true
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val splashScreen = installSplashScreen()

        setContentView(R.layout.registration_activity)

        val db = AppDbProvider.getDb(this)
        val userManager = UserManager(db)
        val session = SessionManager(this)

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
                if (validatePassword(password, userPassword, acceptUserPassword)) {
                    lifecycleScope.launch {
                        val user = User(login = login, password = password)
                        val isRegistered = userManager.registerUser(user)

                        if (isRegistered) {
                            session.saveSession(login)

                            val intent = Intent(this@RegistrationActivity, MainActivity::class.java)
                            intent.putExtra("USER_LOGIN", user.login) // * передача логина юзера ТОЛЬКО в MainActivity
                            startActivity(intent)
                        }
                        else {
                            Toast.makeText(this@RegistrationActivity, "Логин занят", Toast.LENGTH_LONG).show()
                        }
                    }
                }
            }
        }
    }
}