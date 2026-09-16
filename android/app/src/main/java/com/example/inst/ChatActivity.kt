package com.example.inst

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import okhttp3.*
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.concurrent.TimeUnit

class ChatActivity : AppCompatActivity() {

    private lateinit var token: String
    private lateinit var accessToken: String
    private var myUserId: Int = 0
    private var otherUserId: Int = 0
    private lateinit var adapter: MessageAdapter
    private val messagesList = mutableListOf<ChatMessage>()
    private var webSocket: WebSocket? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        val prefs = getSharedPreferences("AppPrefs", Context.MODE_PRIVATE)
        accessToken = prefs.getString("ACCESS_TOKEN", "") ?: ""
        token = "Bearer $accessToken"
        myUserId = prefs.getInt("USER_ID", 0)

        otherUserId = intent.getIntExtra("OTHER_USER_ID", 0)
        val otherUsername = intent.getStringExtra("OTHER_USERNAME") ?: ""

        findViewById<TextView>(R.id.tvChatUsername).text = otherUsername
        findViewById<ImageButton>(R.id.btnBack).setOnClickListener { finish() }

        val rvMessages = findViewById<RecyclerView>(R.id.rvMessages)
        rvMessages.layoutManager = LinearLayoutManager(this).apply {
            stackFromEnd = true
        }

        adapter = MessageAdapter(messagesList, myUserId)
        rvMessages.adapter = adapter

        // Ескі хабарларды жүктеу
        loadHistory(rvMessages)

        // WebSocket қосу
        connectWebSocket()

        // Жіберу батырмасы
        findViewById<ImageButton>(R.id.btnSend).setOnClickListener {
            val etMessage = findViewById<EditText>(R.id.etMessage)
            val text = etMessage.text.toString().trim()
            if (text.isNotEmpty()) {
                sendMessage(text)
                etMessage.text.clear()
            }
        }
    }

    private fun loadHistory(rv: RecyclerView) {
        ApiClient.apiService.getMessages(token, otherUserId)
            .enqueue(object : Callback<List<ChatMessage>> {
                @SuppressLint("NotifyDataSetChanged")
                override fun onResponse(
                    call: Call<List<ChatMessage>>,
                    response: Response<List<ChatMessage>>
                ) {
                    if (response.isSuccessful) {
                        val messages = response.body() ?: emptyList()
                        messagesList.addAll(messages)
                        adapter.notifyDataSetChanged()
                        rv.scrollToPosition(messagesList.size - 1)
                    }
                }
                override fun onFailure(call: Call<List<ChatMessage>>, t: Throwable) {}
            })
    }

    private fun connectWebSocket() {
        val client = OkHttpClient.Builder()
            .readTimeout(0, TimeUnit.MILLISECONDS)
            .build()

        val wsUrl = "ws://10.0.2.2:8000/ws/chat/$otherUserId/?token=$accessToken"

        val request = Request.Builder().url(wsUrl).build()

        webSocket = client.newWebSocket(request, object : WebSocketListener() {
            override fun onOpen(webSocket: WebSocket, response: okhttp3.Response) {
                runOnUiThread {
                    Toast.makeText(this@ChatActivity,
                        "Қосылды ✅", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onMessage(webSocket: WebSocket, text: String) {
                val json = JSONObject(text)
                val message = ChatMessage(
                    id = 0,
                    sender = json.getInt("sender_id"),
                    sender_username = json.getString("sender_username"),
                    receiver = otherUserId,
                    text = json.getString("message"),
                    created_at = json.getString("created_at")
                )
                runOnUiThread {
                    messagesList.add(message)
                    adapter.notifyItemInserted(messagesList.size - 1)
                    findViewById<RecyclerView>(R.id.rvMessages)
                        .scrollToPosition(messagesList.size - 1)
                }
            }

            override fun onFailure(webSocket: WebSocket,
                                   t: Throwable, response: okhttp3.Response?) {
                runOnUiThread {
                    Toast.makeText(this@ChatActivity,
                        "Қосылу қатесі", Toast.LENGTH_SHORT).show()
                }
            }
        })
    }

    private fun sendMessage(text: String) {
        val json = JSONObject()
        json.put("text", text)
        webSocket?.send(json.toString())
    }

    override fun onDestroy() {
        super.onDestroy()
        webSocket?.close(1000, "Activity destroyed")
    }
}

class MessageAdapter(
    private val messages: MutableList<ChatMessage>,
    private val myUserId: Int
) : RecyclerView.Adapter<MessageAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvMine: TextView = view.findViewById(R.id.tvMessageMine)
        val tvOther: TextView = view.findViewById(R.id.tvMessageOther)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_message, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val msg = messages[position]
        if (msg.sender == myUserId) {
            holder.tvMine.visibility = View.VISIBLE
            holder.tvOther.visibility = View.GONE
            holder.tvMine.text = msg.text
        } else {
            holder.tvMine.visibility = View.GONE
            holder.tvOther.visibility = View.VISIBLE
            holder.tvOther.text = msg.text
        }
    }

    override fun getItemCount() = messages.size
}