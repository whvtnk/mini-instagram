package com.example.inst

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class AddPostActivity : AppCompatActivity() {

    private lateinit var token: String
    private var selectedImageUri: Uri? = null
    private val PICK_IMAGE = 200

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_post)

        val prefs = getSharedPreferences("AppPrefs", Context.MODE_PRIVATE)
        token = "Bearer ${prefs.getString("ACCESS_TOKEN", "")}"

        val ivImage = findViewById<ImageView>(R.id.ivSelectedImage)
        val btnPick = findViewById<Button>(R.id.btnPickImage)
        val etCaption = findViewById<EditText>(R.id.etCaption)
        val btnShare = findViewById<Button>(R.id.btnShare)

        // Сурет таңдау
        btnPick.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, PICK_IMAGE)
        }

        // Постты жіберу
        btnShare.setOnClickListener {
            val caption = etCaption.text.toString().trim()
            if (selectedImageUri == null) {
                Toast.makeText(this, "Сурет таңдаңыз!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            uploadPost(caption, selectedImageUri!!)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE && resultCode == Activity.RESULT_OK) {
            selectedImageUri = data?.data
            val ivImage = findViewById<ImageView>(R.id.ivSelectedImage)
            val btnPick = findViewById<Button>(R.id.btnPickImage)
            ivImage.setImageURI(selectedImageUri)
            btnPick.visibility = android.view.View.GONE
        }
    }

    private fun uploadPost(caption: String, uri: Uri) {
        val inputStream = contentResolver.openInputStream(uri) ?: return
        val file = File(cacheDir, "post_image.jpg")
        file.outputStream().use { inputStream.copyTo(it) }

        val captionBody = caption.toRequestBody("text/plain".toMediaTypeOrNull())
        val imageBody = file.asRequestBody("image/*".toMediaTypeOrNull())
        val imagePart = MultipartBody.Part.createFormData("image", file.name, imageBody)

        ApiClient.apiService.createPost(token, captionBody, imagePart)
            .enqueue(object : Callback<Any> {
                override fun onResponse(call: Call<Any>, response: Response<Any>) {
                    if (response.isSuccessful || response.code() == 201) {
                        Toast.makeText(this@AddPostActivity,
                            "Пост жарияланды! ✅", Toast.LENGTH_SHORT).show()
                        finish()
                    } else {
                        Toast.makeText(this@AddPostActivity,
                            "Қате: ${response.code()}", Toast.LENGTH_SHORT).show()
                    }
                }
                override fun onFailure(call: Call<Any>, t: Throwable) {
                    Toast.makeText(this@AddPostActivity,
                        "Интернет жоқ", Toast.LENGTH_SHORT).show()
                }
            })
    }
}