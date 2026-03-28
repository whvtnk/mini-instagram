from rest_framework.decorators import api_view, permission_classes
from rest_framework.permissions import AllowAny, IsAuthenticated
from rest_framework.response import Response
from rest_framework import status
from .models import User, Follow
from .serializers import UserSerializer, UserProfileSerializer
from django.shortcuts import get_object_or_404

@api_view(['POST'])
@permission_classes([AllowAny])
def register_user(request):
    serializer = UserSerializer(data=request.data)
    if serializer.is_valid():
        user = serializer.save()
        user.set_password(request.data['password'])
        user.save()
        return Response(serializer.data, status=status.HTTP_201_CREATED)
    return Response(serializer.errors, status=status.HTTP_400_BAD_REQUEST)

@api_view(['GET'])
@permission_classes([IsAuthenticated])
def get_my_profile(request):
    """Қазіргі кіріп тұрған пайдаланушы (Авторизация арқылы)"""
    serializer = UserProfileSerializer(request.user)
    return Response(serializer.data)

@api_view(['GET'])
@permission_classes([IsAuthenticated])
def get_user_profile(request, user_id):
    """Басқа біреудің профилін көру"""
    user = get_object_or_404(User, id=user_id)
    serializer = UserProfileSerializer(user)
    return Response(serializer.data)

# --- ӨЗГЕРТІЛГЕН FOLLOW ЛОГИКАСЫ ---
@api_view(['POST', 'DELETE'])
@permission_classes([IsAuthenticated])
def manage_follow(request, user_id):
    """POST - Подписаться, DELETE - Отписаться"""
    target_user = get_object_or_404(User, id=user_id)

    if target_user == request.user:
        return Response({'error': 'Өзіңізге жазыла немесе өшіре алмайсыз'}, status=status.HTTP_400_BAD_REQUEST)

    # 1. ПОДПИСАТЬСЯ (POST)
    if request.method == 'POST':
        follow, created = Follow.objects.get_or_create(follower=request.user, followee=target_user)
        if created:
            return Response({'message': 'Подписка жасалды (Followed)', 'is_following': True}, status=status.HTTP_201_CREATED)
        return Response({'message': 'Сіз бұл адамға қазірдің өзінде жазылғансыз'}, status=status.HTTP_400_BAD_REQUEST)

    # 2. ОТПИСАТЬСЯ (DELETE)
    elif request.method == 'DELETE':
        deleted, _ = Follow.objects.filter(follower=request.user, followee=target_user).delete()
        if deleted:
            return Response({'message': 'Отписка жасалды (Unfollowed)', 'is_following': False}, status=status.HTTP_204_NO_CONTENT)
        return Response({'error': 'Сіз бұл адамға жазылмағансыз, өшіретін ештеңе жоқ'}, status=status.HTTP_400_BAD_REQUEST)

@api_view(['GET'])
@permission_classes([IsAuthenticated])
def get_suggestions(request):
    """Ұсыныстар: өзің емес және әзірге жазылмаған адамдар"""
    my_following = Follow.objects.filter(follower=request.user).values_list('followee_id', flat=True)
    users = User.objects.exclude(id=request.user.id).exclude(id__in=my_following).order_by('?')[:5]
    data = [{"id": u.id, "username": u.username, "avatar": u.avatar_url.url if u.avatar_url else None} for u in users]
    return Response(data)

@api_view(['PUT', 'DELETE'])
@permission_classes([IsAuthenticated])
def update_delete_user(request):
    user = request.user 
    if request.method == 'PUT':
        serializer = UserProfileSerializer(user, data=request.data, partial=True)
        if serializer.is_valid():
            serializer.save()
            return Response(serializer.data)
        return Response(serializer.errors, status=status.HTTP_400_BAD_REQUEST)

    elif request.method == 'DELETE':
        user.delete()
        return Response({'message': 'Аккаунт өшірілді'}, status=status.HTTP_204_NO_CONTENT)

@api_view(['GET'])
@permission_classes([IsAuthenticated])
def get_followers(request, user_id):
    """Подписчиктерді көру"""
    user = get_object_or_404(User, id=user_id)
    follows = Follow.objects.filter(followee=user)
    followers = [f.follower for f in follows]
    serializer = UserSerializer(followers, many=True)
    return Response(serializer.data)

@api_view(['GET'])
@permission_classes([IsAuthenticated])
def get_following(request, user_id):
    """Подпискаларды көру"""
    user = get_object_or_404(User, id=user_id)
    follows = Follow.objects.filter(follower=user)
    following = [f.followee for f in follows]
    serializer = UserSerializer(following, many=True)
    return Response(serializer.data)


# Барлық юзерлерді шығаратын жаңа функция
@api_view(['GET'])
@permission_classes([IsAuthenticated])
def get_all_users(request):
    from django.contrib.auth import get_user_model
    from .serializers import UserSerializer # Егер сенде сериализатордың аты басқаша болса (мысалы UserProfileSerializer), соған ауыстыр
    
    User = get_user_model()
    users = User.objects.all()
    serializer = UserSerializer(users, many=True)
    return Response(serializer.data)