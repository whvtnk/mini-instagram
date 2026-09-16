package com.example.inst
// Логин кезінде жіберетін мәлімет (username, password)
data class LoginRequest(
    val username: String,
    val password: String
)

// Логиннен кейін Бэкендтен келетін жауап (Токендер)
data class LoginResponse(
    val refresh: String,
    val access: String
)

// Регистрация кезінде жіберетін мәлімет
data class RegisterRequest(
    val username: String,
    val password: String
)

// Регистрациядан кейін келетін жауап
data class RegisterResponse(
    val id: Int,
    val username: String
)

// Сторис моделі
data class Story(
    val id: Int,
    val author: String,
    val image: String?
)

// Медиа (постқа тіркелген сурет)
data class MediaItem(
    val id: Int,
    val file: String
)

// Пост моделі
data class Post(
    val id: Int,
    val author: Int,
    val author_username: String,
    val author_avatar: String?,
    val caption: String,
    val media: List<MediaItem>,
    val likes_count: Int,
    val comments_count: Int,
    val is_liked: Boolean,
    val created_at: String
)

// Пагинация жауабы
data class PostListResponse(
    val count: Int,
    val next: String?,
    val results: List<Post>
)

data class UserProfile(
    val id: Int,
    val username: String,
    val bio: String?,
    val avatar_url: String?,
    val posts_count: Int,
    val followers_count: Int,
    val following_count: Int
)

data class Comment(
    val id: Int,
    val author: Int,
    val author_username: String,
    val text: String,
    val created_at: String
)

data class CommentRequest(
    val text: String
)

data class Conversation(
    val id: Int,
    val username: String
)

data class ChatMessage(
    val id: Int,
    val sender: Int,
    val sender_username: String,
    val receiver: Int,
    val text: String,
    val created_at: String
)
