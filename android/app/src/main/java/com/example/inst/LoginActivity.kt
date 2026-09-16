package com.example.inst

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // 1. Экрандағы (XML) элементтерді ID арқылы тауып алу
        val etUsername = findViewById<EditText>(R.id.etUsernameLogin)
        val etPassword = findViewById<EditText>(R.id.etPasswordLogin)
        val btnLogin = findViewById<Button>(R.id.btnLogin)
        val tvGoToRegister = findViewById<TextView>(R.id.tvGoToRegister)

        // 2. Астындағы "Тіркелу" (Sign Up) текстін басса, қайтадан Регистрация экранына өту
        tvGoToRegister.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish() // Логин экранын жауып тастау
        }

        // 3. "Sign In" (Кіру) батырмасын басқан кездегі логика
        btnLogin.setOnClickListener {
            val username = etUsername.text.toString().trim()
            val password = etPassword.text.toString().trim()

            // Тексеру: Жолдар бос емес пе?
            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Логин мен парольді жазыңыз!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Бэкендке сұраныс жіберуге дайындау
            val request = LoginRequest(username, password)

            // Retrofit арқылы интернетке (Render-ге) жіберу
            ApiClient.apiService.loginUser(request).enqueue(object : Callback<LoginResponse> {
                override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                    if (response.isSuccessful) {
                        // Бэкендтен келген жауапты (Токендерді) алу
                        val tokens = response.body()

                        if (tokens != null) {
                            // ТОКЕНДІ ТЕЛЕФОН ЖАДЫНА САҚТАУ (SharedPreferences)
                            val sharedPreferences = getSharedPreferences("AppPrefs", Context.MODE_PRIVATE)
                            val editor = sharedPreferences.edit()
                            editor.putString("ACCESS_TOKEN", tokens.access)
                            editor.putString("REFRESH_TOKEN", tokens.refresh)
                            editor.apply() // Сақтауды растау

                            // USER_ID сақтау
                            val userId = tokens.access.let {
                                val parts = it.split(".")
                                val payload = android.util.Base64.decode(parts[1], android.util.Base64.URL_SAFE)
                                org.json.JSONObject(String(payload)).getInt("user_id")
                            }
                            editor.putInt("USER_ID", userId)

                            Toast.makeText(
                                this@LoginActivity,
                                "Қош келдіңіз! Токен сақталды 🔐",
                                Toast.LENGTH_SHORT
                            ).show()

                            // HomeActivity-ге өту
                            val intent = Intent(this@LoginActivity, HomeActivity::class.java)
                            startActivity(intent)
                            finish()
                        }
                    } else {
                        // Пароль немесе логин қате болса
                        Toast.makeText(this@LoginActivity, "Қате: Логин немесе пароль дұрыс емес", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                    Toast.makeText(this@LoginActivity, "Интернетке қосыла алмады", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }
}