package com.example.inst

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class StoryAdapter(
    private val stories: List<Story>,
    private val onStoryClick: (Story) -> Unit
) : RecyclerView.Adapter<StoryAdapter.StoryViewHolder>() {

    class StoryViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val ivStory: ImageView = view.findViewById(R.id.ivStoryImage)
        val tvUsername: TextView = view.findViewById(R.id.tvStoryUsername)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StoryViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_story, parent, false)
        return StoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: StoryViewHolder, position: Int) {
        val filteredStories = stories.filter { !it.image.isNullOrEmpty() }
        val story = filteredStories[position]
        holder.tvUsername.text = story.author

        val imageUrl = if ((story.image ?: "").startsWith("http")) {
            story.image
        } else {
            "https://mini-instagram-30zg.onrender.com${story.image}"
        }

        Glide.with(holder.itemView.context)
            .load(imageUrl)
            .circleCrop()
            .placeholder(R.drawable.circle_border)
            .into(holder.ivStory)

        // Стористі басқанда толық экран
        holder.itemView.setOnClickListener {
            onStoryClick(story)
        }
    }

    override fun getItemCount() = stories.filter { !it.image.isNullOrEmpty() }.size
}