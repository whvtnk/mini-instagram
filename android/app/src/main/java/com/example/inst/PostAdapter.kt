package com.example.inst

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class PostAdapter(
    private val posts: MutableList<Post>,
    private val token: String,
    private val onLikeClick: (Post, Int) -> Unit,
    private val onCommentClick: (Post) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        const val VIEW_TYPE_POST = 0
        const val VIEW_TYPE_AD = 1
    }

    // Жарнама деректері
    data class AdData(
        val title: String,
        val description: String,
        val imageUrl: String,
        val buttonText: String
    )

    private val ads = listOf(
        AdData("🔥 Samsung Galaxy S25", "Жаңа флагман смартфон — 20% жеңілдік!", "", "Сатып ал"),
        AdData("🍕 Dominos Pizza", "2 пицца бағасына 3 пицца!", "", "Тапсырыс бер"),
        AdData("✈️ Air Astana", "Алматы-Дубай билеті — 89,000 тг!", "", "Брондау")
    )

    // POST + AD аралас тізім индексін есептеу
    private fun getActualPosition(position: Int): Int {
        // Әр 4-позицияда жарнама, қалғандары пост
        var postCount = 0
        for (i in 0..position) {
            if ((i + 1) % 4 != 0) postCount++
            if (postCount > posts.size) return -1
        }
        return postCount - 1
    }

    override fun getItemCount(): Int {
        // Посттар + жарнамалар саны
        val adCount = posts.size / 3
        return posts.size + adCount
    }

    override fun getItemViewType(position: Int): Int {
        return if ((position + 1) % 4 == 0) VIEW_TYPE_AD else VIEW_TYPE_POST
    }

    // POST ViewHolder
    class PostViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val ivAvatar: ImageView = view.findViewById(R.id.ivPostAvatar)
        val tvUsername: TextView = view.findViewById(R.id.tvPostUsername)
        val ivImage: ImageView = view.findViewById(R.id.ivPostImage)
        val btnLike: ImageButton = view.findViewById(R.id.btnLike)
        val tvLikes: TextView = view.findViewById(R.id.tvLikesCount)
        val tvCaption: TextView = view.findViewById(R.id.tvCaption)
        val btnComment: ImageButton = view.findViewById(R.id.btnComment)
        val tvComments: TextView = view.findViewById(R.id.tvCommentsCount)
    }

    // AD ViewHolder
    class AdViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvTitle: TextView = view.findViewById(R.id.tvAdTitle)
        val tvDescription: TextView = view.findViewById(R.id.tvAdDescription)
        val btnClick: Button = view.findViewById(R.id.btnAdClick)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == VIEW_TYPE_AD) {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_ad, parent, false)
            AdViewHolder(view)
        } else {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_post, parent, false)
            PostViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is AdViewHolder) {
            // Жарнаманы көрсет
            val ad = ads[position % ads.size]
            holder.tvTitle.text = ad.title
            holder.tvDescription.text = ad.description
            holder.btnClick.text = ad.buttonText
            holder.btnClick.setOnClickListener {
                Toast.makeText(holder.itemView.context,
                    "Жарнамаға өтіп жатырмын...", Toast.LENGTH_SHORT).show()
            }
        } else if (holder is PostViewHolder) {
            // Пост индексін есептеу
            val adsBefore = position / 4
            val postIndex = position - adsBefore
            if (postIndex >= posts.size) return

            val post = posts[postIndex]

            holder.tvUsername.text = post.author_username
            holder.tvCaption.text = post.caption.trim('"', '\'', ' ')
            holder.tvLikes.text = "${post.likes_count} лайк"
            holder.tvComments.text = "${post.comments_count}"

            // Жүрек түсі
            if (post.is_liked) {
                holder.btnLike.setImageResource(R.drawable.ic_heart_filled)
            } else {
                holder.btnLike.setImageResource(R.drawable.ic_heart_outline)
            }

            // Аватар
            val avatarUrl = buildUrl(post.author_avatar)
            if (!avatarUrl.isNullOrEmpty()) {
                Glide.with(holder.itemView.context)
                    .load(avatarUrl)
                    .circleCrop()
                    .placeholder(R.mipmap.ic_launcher_round)
                    .into(holder.ivAvatar)
            } else {
                holder.ivAvatar.setImageResource(R.mipmap.ic_launcher_round)
            }

            // Пост суреті
            if (post.media.isNotEmpty()) {
                holder.ivImage.visibility = View.VISIBLE
                Glide.with(holder.itemView.context)
                    .load(buildUrl(post.media[0].file))
                    .centerCrop()
                    .placeholder(android.R.color.darker_gray)
                    .into(holder.ivImage)
            } else {
                holder.ivImage.visibility = View.GONE
            }

            // Лайк анимация
            holder.btnLike.setOnClickListener {
                holder.btnLike.animate()
                    .scaleX(1.3f).scaleY(1.3f).setDuration(100)
                    .withEndAction {
                        holder.btnLike.animate()
                            .scaleX(1.0f).scaleY(1.0f).setDuration(100).start()
                    }.start()
                onLikeClick(post, postIndex)
            }

            // Комментарий
            holder.btnComment.setOnClickListener {
                onCommentClick(post)
            }
        }
    }

    private fun buildUrl(path: String?): String? {
        if (path.isNullOrEmpty()) return null
        return if (path.startsWith("http")) path
        else "http://10.0.2.2:8000$path"
    }

    fun updateLike(position: Int, isLiked: Boolean, likesCount: Int) {
        posts[position] = posts[position].copy(
            is_liked = isLiked,
            likes_count = likesCount
        )
        // Жарнамаларды ескере отырып нақты позицияны табу
        val adsBefore = (position / 3)
        notifyItemChanged(position + adsBefore)
    }
}