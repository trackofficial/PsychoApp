package com.example.psychoapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import androidx.room.Room
import kotlinx.coroutines.launch

class LoginActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val splashScreen = installSplashScreen()

        val db = UsersDbProvider.get(this)

        val session = SessionManager(this)
        if (session.isLoggedIn) {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)

            finish()
        }

        setContentView(R.layout.login_activity)

        val btnGoToReg = findViewById<Button>(R.id.btn_go_to_reg)
        btnGoToReg.setOnClickListener {
            val intent = Intent(this, RegistrationActivity::class.java)
            startActivity(intent)
        }

        val userLogin = findViewById<EditText>(R.id.login_space)
        val userPassword = findViewById<EditText>(R.id.password_space)
        val rememberMe = findViewById<CheckBox>(R.id.cb_remember_me)

        val loginBtn: Button = findViewById(R.id.login_btn)
        loginBtn.setOnClickListener {
            val login = userLogin.text.toString().trim()
            val password = userPassword.text.toString().trim()

            if (login.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Все поля должны быть заполнены", Toast.LENGTH_LONG).show()
            }
            else {
                val userManager = UserManager(db)

                val user = User(login=login, password=password)

                lifecycleScope.launch {
                    val isLoggedIn = userManager.loginUser(user)

                    if (isLoggedIn) {
                        if (rememberMe.isChecked) {
                            session.saveSession(login)
                        }

                        Toast.makeText(this@LoginActivity, "Вы успешно вошли в систему", Toast.LENGTH_LONG).show()

                        val intent = Intent(this@LoginActivity, MainActivity::class.java)
                        intent.putExtra("USER_LOGIN", user.login) // * передача логина юзера ТОЛЬКО в MainActivity
                        startActivity(intent)
                    }
                    else {
                        Toast.makeText(this@LoginActivity, "Введены неверные данные", Toast.LENGTH_LONG).show()

                        userLogin.text.clear()
                        userPassword.text.clear()
                    }
                }
            }
        }
    }
}