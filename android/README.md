# Android — Kotlin

## Баптау

`ApiClient.kt` ішінде URL өзгерт:

```kotlin
// Эмулятор үшін:
private const val BASE_URL = "http://10.0.2.2:8000/"

// Нақты телефон үшін:
private const val BASE_URL = "http://192.168.X.X:8000/"
```

## Негізгі файлдар

| Файл | Сипаттама |
|------|-----------|
| `HomeActivity.kt` | Басты экран (лента + сторис) |
| `LoginActivity.kt` | Кіру экраны |
| `PostAdapter.kt` | Пост тізімі + жарнама |
| `ChatActivity.kt` | WebSocket чат |
| `DirectActivity.kt` | Чаттар тізімі |
| `ProfileActivity.kt` | Профиль |
| `AddPostActivity.kt` | Пост қосу |
| `CommentsActivity.kt` | Комментарийлер |

## Тәуелділіктер

```kotlin
// Retrofit
implementation("com.squareup.retrofit2:retrofit:2.9.0")
// Glide  
implementation("com.github.bumptech.glide:glide:4.16.0")
// OkHttp WebSocket
implementation("com.squareup.okhttp3:okhttp:4.12.0")
// Material
implementation("com.google.android.material:material:1.11.0")
```