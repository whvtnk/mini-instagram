package com.example.inst

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DirectActivity : AppCompatActivity() {

    private lateinit var token: String
    private var myUserId: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_direct)

        val prefs = getSharedPreferences("AppPrefs", Context.MODE_PRIVATE)
        token = "Bearer ${prefs.getString("ACCESS_TOKEN", "")}"
        myUserId = prefs.getInt("USER_ID", 0)

        val rvConversations = findViewById<RecyclerView>(R.id.rvConversations)
        rvConversations.layoutManager = LinearLayoutManager(this)

        // Жаңа чат батырмасы
        findViewById<android.widget.ImageButton>(R.id.btnNewChat).setOnClickListener {
            // Іздеу арқылы жаңа чат бастау
            startActivity(Intent(this, SearchActivity::class.java))
        }

        loadConversations(rvConversations)
    }

    private fun loadConversations(rv: RecyclerView) {
        ApiClient.apiService.getConversations(token)
            .enqueue(object : Callback<List<Conversation>> {
                override fun onResponse(
                    call: Call<List<Conversation>>,
                    response: Response<List<Conversation>>
                ) {
                    if (response.isSuccessful) {
                        val conversations = response.body() ?: emptyList()
                        rv.adapter = ConversationAdapter(conversations) { conv ->
                            val intent = Intent(this@DirectActivity, ChatActivity::class.java)
                            intent.putExtra("OTHER_USER_ID", conv.id)
                            intent.putExtra("OTHER_USERNAME", conv.username)
                            startActivity(intent)
                        }
                    }
                }
                override fun onFailure(call: Call<List<Conversation>>, t: Throwable) {}
            })
    }
}

class ConversationAdapter(
    private val items: List<Conversation>,
    private val onClick: (Conversation) -> Unit
) : RecyclerView.Adapter<ConversationAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvUsername: TextView = view.findViewById(R.id.tvUsername)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_conversation, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.tvUsername.text = item.username
        holder.itemView.setOnClickListener { onClick(item) }
    }

    override fun getItemCount() = items.size
}