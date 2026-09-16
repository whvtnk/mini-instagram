package com.example.inst

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CommentsActivity : AppCompatActivity() {

    private lateinit var token: String
    private lateinit var adapter: CommentsAdapter
    private val commentsList = mutableListOf<Comment>()
    private var postId: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_comments)

        val prefs = getSharedPreferences("AppPrefs", Context.MODE_PRIVATE)
        token = "Bearer ${prefs.getString("ACCESS_TOKEN", "")}"

        // PostId-ді Intent-тен алу
        postId = intent.getIntExtra("POST_ID", 0)

        val rvComments = findViewById<RecyclerView>(R.id.rvComments)
        val etComment = findViewById<EditText>(R.id.etComment)
        val btnSend = findViewById<ImageButton>(R.id.btnSend)
        val btnBack = findViewById<ImageButton>(R.id.btnBack)

        // Артқа қайту
        btnBack.setOnClickListener { finish() }

        // Adapter
        adapter = CommentsAdapter(commentsList)
        rvComments.layoutManager = LinearLayoutManager(this)
        rvComments.adapter = adapter

        // Комментарийлерді жүктеу
        loadComments()

        // Жіберу батырмасы
        btnSend.setOnClickListener {
            val text = etComment.text.toString().trim()
            if (text.isEmpty()) {
                Toast.makeText(this, "Бір нәрсе жаз!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            sendComment(text, etComment)
        }
    }

    private fun loadComments() {
        ApiClient.apiService.getComments(token, postId)
            .enqueue(object : Callback<List<Comment>> {
                override fun onResponse(
                    call: Call<List<Comment>>,
                    response: Response<List<Comment>>
                ) {
                    if (response.isSuccessful) {
                        val comments = response.body() ?: emptyList()
                        commentsList.clear()
                        commentsList.addAll(comments)
                        adapter.notifyDataSetChanged()
                    }
                }
                override fun onFailure(call: Call<List<Comment>>, t: Throwable) {
                    Toast.makeText(this@CommentsActivity,
                        "Жүктелмеді", Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun sendComment(text: String, etComment: EditText) {
        val request = CommentRequest(text)

        ApiClient.apiService.addComment(token, postId, request)
            .enqueue(object : Callback<Comment> {
                override fun onResponse(
                    call: Call<Comment>,
                    response: Response<Comment>
                ) {
                    if (response.isSuccessful) {
                        val comment = response.body() ?: return
                        commentsList.add(comment)
                        adapter.notifyItemInserted(commentsList.size - 1)
                        etComment.text.clear()
                        // Ең төменге scroll
                        findViewById<RecyclerView>(R.id.rvComments)
                            .scrollToPosition(commentsList.size - 1)
                    }
                }
                override fun onFailure(call: Call<Comment>, t: Throwable) {}
            })
    }
}

// Comments Adapter
class CommentsAdapter(private val comments: MutableList<Comment>) :
    RecyclerView.Adapter<CommentsAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvUsername: TextView = view.findViewById(R.id.tvCommentUsername)
        val tvText: TextView = view.findViewById(R.id.tvCommentText)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_comment, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val comment = comments[position]
        holder.tvUsername.text = comment.author_username
        holder.tvText.text = comment.text
    }

    override fun getItemCount() = comments.size
}