package com.example.inst

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class ProfileActivity : AppCompatActivity() {

    private lateinit var token: String
    private lateinit var ivAvatar: ImageView
    private val PICK_IMAGE = 100

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        val prefs = getSharedPreferences("AppPrefs", Context.MODE_PRIVATE)
        token = "Bearer ${prefs.getString("ACCESS_TOKEN", "")}"

        ivAvatar = findViewById(R.id.ivAvatar)
        val tvUsername = findViewById<TextView>(R.id.tvUsername)
        val tvBio = findViewById<TextView>(R.id.tvBio)
        val tvPosts = findViewById<TextView>(R.id.tvPostsCount)
        val tvFollowers = findViewById<TextView>(R.id.tvFollowersCount)
        val tvFollowing = findViewById<TextView>(R.id.tvFollowingCount)
        val btnEdit = findViewById<Button>(R.id.btnEditProfile)

        // Профиль мәліметтерін жүктеу
        ApiClient.apiService.getMyProfile(token)
            .enqueue(object : Callback<UserProfile> {
                override fun onResponse(
                    call: Call<UserProfile>,
                    response: Response<UserProfile>
                ) {
                    if (response.isSuccessful) {
                        val profile = response.body() ?: return
                        tvUsername.text = profile.username
                        tvBio.text = profile.bio ?: ""
                        tvPosts.text = profile.posts_count.toString()
                        tvFollowers.text = profile.followers_count.toString()
                        tvFollowing.text = profile.following_count.toString()

                        // Аватар
                        val avatarUrl = if ((profile.avatar_url ?: "").startsWith("http")) {
                            profile.avatar_url
                        } else {
                            "https://mini-instagram-30zg.onrender.com${profile.avatar_url}"
                        }
                        if (!avatarUrl.isNullOrEmpty()) {
                            Glide.with(this@ProfileActivity)
                                .load(avatarUrl)
                                .circleCrop()
                                .into(ivAvatar)
                        }
                    }
                }
                override fun onFailure(call: Call<UserProfile>, t: Throwable) {
                    Toast.makeText(this@ProfileActivity,
                        "Жүктелмеді", Toast.LENGTH_SHORT).show()
                }
            })

        // Аватарды өзгерту
        ivAvatar.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, PICK_IMAGE)
        }

        btnEdit.setOnClickListener {
            Toast.makeText(this, "Кейін қосамыз!", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE && resultCode == Activity.RESULT_OK) {
            val uri: Uri = data?.data ?: return
            // Аватарды бэкендке жіберу
            uploadAvatar(uri)
        }
    }

    private fun uploadAvatar(uri: Uri) {
        val inputStream = contentResolver.openInputStream(uri) ?: return
        val file = File(cacheDir, "avatar.jpg")
        file.outputStream().use { inputStream.copyTo(it) }

        val requestBody = file.asRequestBody("image/*".toMediaTypeOrNull())
        val part = MultipartBody.Part.createFormData("avatar_url", file.name, requestBody)

        ApiClient.apiService.updateAvatar(token, part)
            .enqueue(object : Callback<UserProfile> {
                override fun onResponse(
                    call: Call<UserProfile>,
                    response: Response<UserProfile>
                ) {
                    if (response.isSuccessful) {
                        Glide.with(this@ProfileActivity)
                            .load(uri)
                            .circleCrop()
                            .into(ivAvatar)
                        Toast.makeText(this@ProfileActivity,
                            "Аватар жаңартылды! ✅", Toast.LENGTH_SHORT).show()
                    }
                }
                override fun onFailure(call: Call<UserProfile>, t: Throwable) {}
            })
    }
}