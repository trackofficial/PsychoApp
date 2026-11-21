package com.example.psychoapp.ui.auth

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import com.example.psychoapp.ui.main.MainActivity
import com.example.psychoapp.R
import com.example.psychoapp.data.local.AppDbProvider
import com.example.psychoapp.utils.SessionManager
import com.example.psychoapp.data.local.users.model.User
import com.example.psychoapp.utils.UserManager
import kotlinx.coroutines.launch

class LoginActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val splashScreen = installSplashScreen()

        val db = AppDbProvider.getDb(this)
        val userManager = UserManager(db)
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

        val loginBtn: Button = findViewById(R.id.login_btn)
        loginBtn.setOnClickListener {
            val login = userLogin.text.toString().trim()
            val password = userPassword.text.toString().trim()

            if (login.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Все поля должны быть заполнены", Toast.LENGTH_LONG).show()
            }
            else {
                val user = User(login = login, password = password)

                lifecycleScope.launch {
                    val isLoggedIn = userManager.loginUser(user)

                    if (isLoggedIn) {
                        session.saveSession(login) // * автоматическое сохранение юзера в сессии

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