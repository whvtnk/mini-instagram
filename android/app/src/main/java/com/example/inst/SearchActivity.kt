package com.example.inst

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SearchActivity : AppCompatActivity() {

    private lateinit var token: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        val prefs = getSharedPreferences("AppPrefs", Context.MODE_PRIVATE)
        token = "Bearer ${prefs.getString("ACCESS_TOKEN", "")}"

        val etSearch = findViewById<EditText>(R.id.etSearch)
        val rvResults = findViewById<RecyclerView>(R.id.rvSearchResults)
        rvResults.layoutManager = LinearLayoutManager(this)

        // Іздеу батырмасын басқанда
        etSearch.setOnKeyListener { _, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_ENTER &&
                event.action == KeyEvent.ACTION_DOWN) {
                val query = etSearch.text.toString().trim()
                if (query.isNotEmpty()) searchUsers(query, rvResults)
                true
            } else false
        }
    }

    private fun searchUsers(query: String, rv: RecyclerView) {
        ApiClient.apiService.searchUsers(token, query)
            .enqueue(object : Callback<List<UserProfile>> {
                override fun onResponse(
                    call: Call<List<UserProfile>>,
                    response: Response<List<UserProfile>>
                ) {
                    if (response.isSuccessful) {
                        val users = response.body() ?: emptyList()
                        rv.adapter = SearchAdapter(users)
                    }
                }
                override fun onFailure(call: Call<List<UserProfile>>, t: Throwable) {}
            })
    }
}

// Search Adapter
class SearchAdapter(private val users: List<UserProfile>) :
    RecyclerView.Adapter<SearchAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val ivAvatar: ImageView = view.findViewById(R.id.ivUserAvatar)
        val tvUsername: TextView = view.findViewById(R.id.tvUsername)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_search_result, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val user = users[position]
        holder.tvUsername.text = user.username

        val avatarUrl = if ((user.avatar_url ?: "").startsWith("http")) {
            user.avatar_url
        } else {
            "https://mini-instagram-30zg.onrender.com${user.avatar_url}"
        }

        holder.itemView.setOnClickListener {
            val intent = Intent(holder.itemView.context, ChatActivity::class.java)
            intent.putExtra("OTHER_USER_ID", user.id)
            intent.putExtra("OTHER_USERNAME", user.username)
            holder.itemView.context.startActivity(intent)
        }

        Glide.with(holder.itemView.context)
            .load(avatarUrl)
            .circleCrop()
            .placeholder(R.mipmap.ic_launcher_round)
            .into(holder.ivAvatar)
    }

    override fun getItemCount() = users.size
}