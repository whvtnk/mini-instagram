from django.urls import path
from . import views

urlpatterns = [
    path('register/', views.register_user, name='register'),
    path('me/', views.get_my_profile, name='my-profile'),
    path('profile/<int:user_id>/', views.get_user_profile, name='user-profile'),
    path('all/', views.get_all_users, name='get-all-users'),
    
    

    # Follow логикасы (POST және DELETE қабылдайды)
    path('follow/<int:user_id>/', views.manage_follow, name='manage-follow'), 
    
    path('suggestions/', views.get_suggestions, name='suggestions'),
    path('me/update/', views.update_delete_user, name='user-update-delete'),
    
    path('<int:user_id>/followers/', views.get_followers, name='get-followers'),
    path('<int:user_id>/following/', views.get_following, name='get-following'),
]