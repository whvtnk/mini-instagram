package com.example.inst // ОСЫ ЖОЛДЫ ӨЗІҢДІКІНЕ ҚАЛДЫР!

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

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 1. Экрандағы (XML) элементтерді ID арқылы тауып алу
        val etUsername = findViewById<EditText>(R.id.etUsernameRegister)
        val etPassword = findViewById<EditText>(R.id.etPasswordRegister)
        val etConfirmPassword = findViewById<EditText>(R.id.etConfirmPasswordRegister)
        val btnRegister = findViewById<Button>(R.id.btnRegister)
        val tvGoToLogin = findViewById<TextView>(R.id.tvGoToLogin)

        // 2. Астындағы "Кіру" (Sign In) текстін басса, Логин экранына өтіп кету
        tvGoToLogin.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        // 3. "Registration" батырмасын басқан кездегі логика
        btnRegister.setOnClickListener {
            val username = etUsername.text.toString().trim()
            val password = etPassword.text.toString().trim()
            val confirmPassword = etConfirmPassword.text.toString().trim()

            // Тексеру: Жолдар бос емес пе?
            if (username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                Toast.makeText(this, "Барлық жолдарды толтырыңыз!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Тексеру: Парольдер сәйкес келе ме?
            if (password != confirmPassword) {
                Toast.makeText(this, "Парольдер сәйкес келмейді!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Бэкендке сұраныс жіберуге дайындау
            val request = RegisterRequest(username, password)

            // Retrofit арқылы интернетке (Render-ге) жіберу
            ApiClient.apiService.registerUser(request).enqueue(object : Callback<RegisterResponse> {
                override fun onResponse(call: Call<RegisterResponse>, response: Response<RegisterResponse>) {
                    if (response.isSuccessful) {
                        // Сәтті тіркелсе, телефонның астынан хабарлама шығару
                        Toast.makeText(this@MainActivity, "Сәтті тіркелдіңіз!", Toast.LENGTH_SHORT).show()

                        // Тіркеліп болған соң автоматты түрде Логин экранына лақтыру
                        startActivity(Intent(this@MainActivity, LoginActivity::class.java))
                        finish() // Бұл экранды жабу
                    } else {
                        // Егер базада мұндай адам бар болса немесе қате шықса
                        Toast.makeText(this@MainActivity, "Қате: ${response.code()}", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<RegisterResponse>, t: Throwable) {
                    // Интернет жоқ болса немесе сервер құлап қалса
                    Toast.makeText(this@MainActivity, "Интернетке қосыла алмады", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }
}