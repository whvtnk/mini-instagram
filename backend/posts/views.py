from rest_framework.decorators import api_view, permission_classes
from rest_framework.permissions import IsAuthenticated, IsAuthenticatedOrReadOnly
from rest_framework.response import Response
from rest_framework import status
from rest_framework import viewsets, permissions
from django.shortcuts import get_object_or_404

from users.models import Follow 
from .models import Post, Media, Like, Comment, Note, Story
from .serializers import PostSerializer, CommentSerializer, NoteSerializer, StorySerializer
from rest_framework.pagination import PageNumberPagination

@api_view(['GET', 'POST'])
@permission_classes([IsAuthenticatedOrReadOnly])
def post_list_create(request):
    if request.method == 'GET':
        posts = Post.objects.all()
        paginator = PageNumberPagination()
        paginator.page_size = 5 # Бір бетте 5 пост шығады
        paginated_posts = paginator.paginate_queryset(posts, request)
        
        serializer = PostSerializer(posts, many=True, context={'request': request})
        return paginator.get_paginated_response(serializer.data)

    elif request.method == 'POST':
        if not request.user.is_authenticated:
            return Response({'error': 'Пост жазу үшін жүйеге кіріңіз'}, status=status.HTTP_401_UNAUTHORIZED)

        serializer = PostSerializer(data=request.data)
        if serializer.is_valid():
            post = serializer.save(author=request.user)
            images = request.FILES.getlist('image') 
            for index, image in enumerate(images):
                Media.objects.create(
                    post=post,
                    file=image,
                    mime_type=image.content_type,
                    order_idx=index
                )
            return Response(PostSerializer(post).data, status=status.HTTP_201_CREATED)
        return Response(serializer.errors, status=status.HTTP_400_BAD_REQUEST)

@api_view(['GET', 'PUT', 'DELETE'])
@permission_classes([IsAuthenticatedOrReadOnly])
def post_detail(request, pk):
    post = get_object_or_404(Post, pk=pk)

    if request.method == 'GET':
        serializer = PostSerializer(post, context={'request': request})
        return Response(serializer.data)

    if post.author != request.user:
        return Response(
            {'error': 'Сіз бұл посттың авторы емессіз!'}, 
            status=status.HTTP_403_FORBIDDEN
        )

    if request.method == 'PUT':
        serializer = PostSerializer(post, data=request.data, partial=True) 
        if serializer.is_valid():
            serializer.save()
            return Response(serializer.data)
        return Response(serializer.errors, status=status.HTTP_400_BAD_REQUEST)

    elif request.method == 'DELETE':
        post.delete()
        return Response({'message': 'Пост сәтті өшірілді'}, status=status.HTTP_204_NO_CONTENT)

# --- ӨЗГЕРТІЛГЕН ЛАЙК ЛОГИКАСЫ ---
@api_view(['POST', 'DELETE'])
@permission_classes([IsAuthenticated])
def manage_like(request, post_id):
    """POST - Лайк басу, DELETE - Лайкты алып тастау"""
    post = get_object_or_404(Post, id=post_id)
    
    # 1. ЛАЙК БАСУ (POST)
    if request.method == 'POST':
        like, created = Like.objects.get_or_create(user=request.user, post=post)
        if created:
            return Response({'message': 'Liked', 'likes_count': post.likes.count()}, status=status.HTTP_201_CREATED)
        return Response({'message': 'Сіз бұл постқа лайк басып қойғансыз'}, status=status.HTTP_400_BAD_REQUEST)

    # 2. ЛАЙКТЫ ҚАЙТАРЫП АЛУ (DELETE)
    elif request.method == 'DELETE':
        deleted, _ = Like.objects.filter(user=request.user, post=post).delete()
        if deleted:
            return Response({'message': 'Unliked', 'likes_count': post.likes.count()}, status=status.HTTP_204_NO_CONTENT)
        return Response({'error': 'Сіз бұл постқа лайк баспағансыз'}, status=status.HTTP_400_BAD_REQUEST)

@api_view(['GET', 'POST'])
@permission_classes([IsAuthenticatedOrReadOnly])
def comment_list_create(request, post_id):
    post = get_object_or_404(Post, id=post_id)

    if request.method == 'GET':
        comments = post.comments.all()
        serializer = CommentSerializer(comments, many=True)
        return Response(serializer.data)

    elif request.method == 'POST':
        serializer = CommentSerializer(data=request.data)
        if serializer.is_valid():
            serializer.save(author=request.user, post=post)
            return Response(serializer.data, status=status.HTTP_201_CREATED)
        return Response(serializer.errors, status=status.HTTP_400_BAD_REQUEST)

@api_view(['PUT', 'DELETE'])
@permission_classes([IsAuthenticated])
def comment_detail(request, pk):
    comment = get_object_or_404(Comment, pk=pk)

    if comment.author != request.user:
        return Response({'error': 'Бұл сіздің пікіріңіз емес!'}, status=status.HTTP_403_FORBIDDEN)

    if request.method == 'PUT':
        serializer = CommentSerializer(comment, data=request.data, partial=True)
        if serializer.is_valid():
            serializer.save()
            return Response(serializer.data)
        return Response(serializer.errors, status=status.HTTP_400_BAD_REQUEST)

    elif request.method == 'DELETE':
        comment.delete()
        return Response({'message': 'Пікір өшірілді'}, status=status.HTTP_204_NO_CONTENT)

@api_view(['GET'])
@permission_classes([IsAuthenticated])
def user_feed(request):
    following_ids = Follow.objects.filter(follower=request.user).values_list('followee_id', flat=True)
    posts = Post.objects.filter(author_id__in=following_ids)
    serializer = PostSerializer(posts, many=True, context={'request': request})
    return Response(serializer.data)

# ================= ЖАҢА: ЗАМЕТКИ ЖӘНЕ СТОРИС VIEWS =================

class NoteViewSet(viewsets.ModelViewSet):
    queryset = Note.objects.all().order_by('-created_at')
    serializer_class = NoteSerializer
    permission_classes = [permissions.IsAuthenticatedOrReadOnly] # Тек тіркелгендер жаза алады, басқалар тек оқиды

    def perform_create(self, serializer):
        # Заметканы сақтаған кезде, авторы ретінде қазіргі юзерді автоматты түрде тіркейміз
        serializer.save(author=self.request.user)

class StoryViewSet(viewsets.ModelViewSet):
    queryset = Story.objects.all().order_by('-created_at')
    serializer_class = StorySerializer
    permission_classes = [permissions.IsAuthenticatedOrReadOnly]

    def perform_create(self, serializer):
        serializer.save(author=self.request.user)