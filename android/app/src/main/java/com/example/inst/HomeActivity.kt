package com.example.inst

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.bottomnavigation.BottomNavigationView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeActivity : AppCompatActivity() {

    private lateinit var storyAdapter: StoryAdapter
    private lateinit var postAdapter: PostAdapter
    private lateinit var token: String
    private lateinit var swipeRefresh: SwipeRefreshLayout
    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        val prefs = getSharedPreferences("AppPrefs", Context.MODE_PRIVATE)
        val accessToken = prefs.getString("ACCESS_TOKEN", "") ?: ""
        token = "Bearer $accessToken"

        swipeRefresh = findViewById(R.id.swipeRefresh)
        progressBar = findViewById(R.id.progressBar)

        // Жоғарыдағы профиль батырмасы
        findViewById<ImageButton>(R.id.btnProfile).setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java))
        }

        // Pull-to-refresh
        swipeRefresh.setColorSchemeColors(
            android.graphics.Color.RED,
            android.graphics.Color.MAGENTA
        )
        swipeRefresh.setOnRefreshListener {
            setupStories()
            setupPosts()
        }

        // Bottom Navigation
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNav)
        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    setupStories()
                    setupPosts()
                    true
                }
                R.id.nav_search -> {
                    startActivity(Intent(this, SearchActivity::class.java))
                    true
                }
                R.id.nav_add -> {
                    startActivity(Intent(this, AddPostActivity::class.java))
                    true
                }
                R.id.nav_reels -> {
                    startActivity(Intent(this, DirectActivity::class.java))
                    true
                }
                R.id.nav_profile -> {
                    startActivity(Intent(this, ProfileActivity::class.java))
                    true
                }
                else -> false
            }
        }

        // Home белсенді
        bottomNav.selectedItemId = R.id.nav_home

        setupStories()
        setupPosts()
    }

    private fun setupStories() {
        val rvStories = findViewById<RecyclerView>(R.id.rvStories)
        rvStories.layoutManager = LinearLayoutManager(
            this, LinearLayoutManager.HORIZONTAL, false
        )

        ApiClient.apiService.getStories(token)
            .enqueue(object : Callback<List<Story>> {
                override fun onResponse(
                    call: Call<List<Story>>,
                    response: Response<List<Story>>
                ) {
                    if (response.isSuccessful) {
                        val stories = response.body() ?: emptyList()
                        storyAdapter = StoryAdapter(stories) { story ->
                            showStoryFullScreen(story)
                        }
                        rvStories.adapter = storyAdapter
                    }
                }
                override fun onFailure(call: Call<List<Story>>, t: Throwable) { }
            })
    }

    private fun setupPosts() {
        val rvPosts = findViewById<RecyclerView>(R.id.rvPosts)
        rvPosts.layoutManager = LinearLayoutManager(this)

        progressBar.visibility = View.VISIBLE

        ApiClient.apiService.getPosts(token)
            .enqueue(object : Callback<PostListResponse> {
                override fun onResponse(
                    call: Call<PostListResponse>,
                    response: Response<PostListResponse>
                ) {
                    progressBar.visibility = View.GONE
                    swipeRefresh.isRefreshing = false

                    if (response.isSuccessful) {
                        val posts = response.body()?.results
                            ?.toMutableList() ?: mutableListOf()

                        postAdapter = PostAdapter(
                            posts, token,
                            onLikeClick = { post, position -> handleLike(post, position) },
                            onCommentClick = { post ->
                                val intent = Intent(this@HomeActivity, CommentsActivity::class.java)
                                intent.putExtra("POST_ID", post.id)
                                startActivity(intent)
                            }
                        )
                        rvPosts.adapter = postAdapter
                    } else {
                        Toast.makeText(
                            this@HomeActivity,
                            "Қате: ${response.code()}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(call: Call<PostListResponse>, t: Throwable) {
                    progressBar.visibility = View.GONE
                    swipeRefresh.isRefreshing = false
                    Toast.makeText(
                        this@HomeActivity,
                        "Интернет жоқ",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
    }

    private fun handleLike(post: Post, position: Int) {
        if (post.is_liked) {
            ApiClient.apiService.unlikePost(token, post.id)
                .enqueue(object : Callback<Any> {
                    override fun onResponse(
                        call: Call<Any>,
                        response: Response<Any>
                    ) {
                        if (response.isSuccessful || response.code() == 204) {
                            postAdapter.updateLike(
                                position, false, post.likes_count - 1
                            )
                        }
                    }
                    override fun onFailure(call: Call<Any>, t: Throwable) { }
                })
        } else {
            ApiClient.apiService.likePost(token, post.id)
                .enqueue(object : Callback<Any> {
                    override fun onResponse(
                        call: Call<Any>,
                        response: Response<Any>
                    ) {
                        if (response.isSuccessful || response.code() == 201) {
                            postAdapter.updateLike(
                                position, true, post.likes_count + 1
                            )
                        }
                    }
                    override fun onFailure(call: Call<Any>, t: Throwable) { }
                })
        }
    }

    private fun showStoryFullScreen(story: Story) {
        val dialog = android.app.Dialog(
            this, android.R.style.Theme_Black_NoTitleBar_Fullscreen
        )
        dialog.setContentView(R.layout.dialog_story_fullscreen)

        val ivFullStory = dialog.findViewById<android.widget.ImageView>(R.id.ivFullStory)
        val tvStoryUser = dialog.findViewById<android.widget.TextView>(R.id.tvStoryUser)

        tvStoryUser.text = story.author

        val imageUrl = if ((story.image ?: "").startsWith("http")) {
            story.image
        } else {
            "http://10.0.2.2:8000${story.image}"
        }

        com.bumptech.glide.Glide.with(this)
            .load(imageUrl)
            .centerCrop()
            .into(ivFullStory)

        ivFullStory.setOnClickListener { dialog.dismiss() }
        dialog.show()
    }
}