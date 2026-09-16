# Mini Instagram

Instagram-тың Android + Django нұсқасы.

![Home Screen](screenshots/home.png)

## Технологиялар

**Backend:**
- Python / Django REST Framework
- PostgreSQL
- JWT аутентификация
- Django Channels (WebSocket)
- Redis

**Android:**
- Kotlin
- Retrofit2 + OkHttp
- Glide
- RecyclerView
- WebSocket (Direct Messages)

## Функциялар

- ✅ Тіркелу / Кіру (JWT)
- ✅ Посттар лентасы
- ✅ Сторис
- ✅ Лайк жүйесі (анимациямен)
- ✅ Комментарийлер
- ✅ Пост қосу (галереядан)
- ✅ Профиль беті
- ✅ Іздеу
- ✅ Direct Messages (WebSocket)
- ✅ Bottom Navigation
- ✅ Pull-to-refresh
- ✅ Жарнама блогы

## 📱 Скриншоттар

| Login | Registration | Home |
|-------|-------------|------|
| ![](screenshots/login.png) | ![](screenshots/registration.png) | ![](screenshots/home.png) |

| Stories | Жарнама | Add Post |
|---------|---------|----------|
| ![](screenshots/stories.png) | ![](screenshots/reklama.png) | ![](screenshots/add_post.png) |

| Comments | Profile | Direct |
|----------|---------|--------|
| ![](screenshots/comments1.png) | ![](screenshots/profile.png) | ![](screenshots/direct.png) |

## Іске қосу

### Backend:
```bash
cd insta_project
python -m venv venv
venv\Scripts\activate
pip install -r requirements.txt
python manage.py migrate
python manage.py runserver 0.0.0.0:8000
```

### Android:

## 👤 Автор

**whvtnk** — 3-курс CS студенті, Қазақстан