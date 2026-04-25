from django.urls import path
from . import views

urlpatterns = [
    path('conversations/', views.get_conversations, name='conversations'),
    path('<int:user_id>/', views.get_messages, name='messages'),
]