from django.urls import path
from . import views

urlpatterns = [
    path('', views.post_list_create, name='post-list-create'),
    path('<int:pk>/', views.post_detail, name='post-detail'),
    path('feed/', views.user_feed, name='user-feed'),
    
    # Like логикасы (POST және DELETE қабылдайды)
    path('<int:post_id>/like/', views.manage_like, name='manage-like'),
    
    path('<int:post_id>/comments/', views.comment_list_create, name='comment-list'),
    path('comments/<int:pk>/', views.comment_detail, name='comment-detail'),

  # ================= ЖАҢА: ЗАМЕТКИ ЖӘНЕ СТОРИС =================
    path('notes/', views.NoteViewSet.as_view({'get': 'list', 'post': 'create'}), name='note-list'),
    path('notes/<int:pk>/', views.NoteViewSet.as_view({
        'get': 'retrieve', 
        'put': 'update', 
        'patch': 'partial_update', 
        'delete': 'destroy'
    }), name='note-detail'),

    path('stories/', views.StoryViewSet.as_view({'get': 'list', 'post': 'create'}), name='story-list'),
    path('stories/<int:pk>/', views.StoryViewSet.as_view({
        'get': 'retrieve', 
        'put': 'update', 
        'patch': 'partial_update', 
        'delete': 'destroy'
    }), name='story-detail'),
]