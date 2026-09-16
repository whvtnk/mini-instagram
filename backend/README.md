# 🔧 Backend — Django REST API

## 🚀 Іске қосу

```bash
python -m venv venv
venv\Scripts\activate
pip install -r requirements.txt
python manage.py migrate
python manage.py runserver 0.0.0.0:8000
```

## 📡 API Endpoints

### Auth
| Method | URL | Description |
|--------|-----|-------------|
| POST | `/api/users/register/` | Тіркелу |
| POST | `/api/token/` | Логин → JWT токен |

### Posts
| Method | URL | Description |
|--------|-----|-------------|
| GET | `/api/posts/` | Барлық посттар |
| POST | `/api/posts/` | Жаңа пост |
| POST | `/api/posts/{id}/like/` | Лайк |
| DELETE | `/api/posts/{id}/like/` | Лайк алу |

### Comments
| Method | URL | Description |
|--------|-----|-------------|
| GET | `/api/posts/{id}/comments/` | Комментарийлер |
| POST | `/api/posts/{id}/comments/` | Комментарий жазу |

### Stories
| Method | URL | Description |
|--------|-----|-------------|
| GET | `/api/posts/stories/` | Сторис |
| POST | `/api/posts/stories/` | Жаңа сторис |

### Direct (WebSocket)
ws://host:8000/ws/chat/{user_id}/?token=JWT_TOKEN

## 🗄️ Модельдер
User → Post → Media
↓
Comment
Like
Story
Message (WebSocket chat)