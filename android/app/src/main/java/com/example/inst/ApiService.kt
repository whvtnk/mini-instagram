package com.example.inst // Өзіңнің пакетің

import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Header
import retrofit2.http.DELETE
import retrofit2.http.PUT
import retrofit2.http.Multipart
import retrofit2.http.Part
import retrofit2.http.Query

interface ApiService {
    // Регистрацияға сұраныс
    @POST("api/users/register/")
    fun registerUser(@Body request: RegisterRequest): Call<RegisterResponse>

    // Логинге сұраныс
    @POST("api/token/")
    fun loginUser(
        @Body request: LoginRequest
    ): Call<LoginResponse>
    // Барлық посттар (лента)
    @GET("api/posts/")
    fun getPosts(
        @Header("Authorization") token: String
    ): Call<PostListResponse>

    // Барлық сторис
    @GET("api/posts/stories/")
    fun getStories(
        @Header("Authorization") token: String
    ): Call<List<Story>>

    // Лайк басу
    @POST("api/posts/{post_id}/like/")
    fun likePost(
        @Header("Authorization") token: String,
        @Path("post_id") postId: Int
    ): Call<Any>

    // Лайкты алып тастау
    @DELETE("api/posts/{post_id}/like/")
    fun unlikePost(
        @Header("Authorization") token: String,
        @Path("post_id") postId: Int
    ): Call<Any>




    @GET("api/users/me/")
    fun getMyProfile(
        @Header("Authorization") token: String
    ): Call<UserProfile>

    @Multipart
    @PUT("api/users/me/update/")
    fun updateAvatar(
        @Header("Authorization") token: String,
        @Part avatar: MultipartBody.Part
    ): Call<UserProfile>

    @GET("api/users/suggestions/")
    fun searchUsers(
        @Header("Authorization") token: String,
        @Query("search") query: String
    ): Call<List<UserProfile>>

    @Multipart
    @POST("api/posts/")
    fun createPost(
        @Header("Authorization") token: String,
        @Part("caption") caption: RequestBody,
        @Part image: MultipartBody.Part
    ): Call<Any>



    @GET("api/posts/{post_id}/comments/")
    fun getComments(
        @Header("Authorization") token: String,
        @Path("post_id") postId: Int
    ): Call<List<Comment>>

    @POST("api/posts/{post_id}/comments/")
    fun addComment(
        @Header("Authorization") token: String,
        @Path("post_id") postId: Int,
        @Body request: CommentRequest
    ): Call<Comment>

    @GET("api/chat/conversations/")
    fun getConversations(
        @Header("Authorization") token: String
    ): Call<List<Conversation>>

    @GET("api/chat/{user_id}/")
    fun getMessages(
        @Header("Authorization") token: String,
        @Path("user_id") userId: Int
    ): Call<List<ChatMessage>>

}

