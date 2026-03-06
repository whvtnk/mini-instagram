from rest_framework import serializers
from .models import Post, Media, Comment, Like, Note, Story

class MediaSerializer(serializers.ModelSerializer):
    class Meta:
        model = Media
        fields = ['id', 'file', 'mime_type', 'order_idx']

class CommentSerializer(serializers.ModelSerializer):
    author_username = serializers.ReadOnlyField(source='author.username')
    author_avatar = serializers.ImageField(source='author.avatar_url', read_only=True)

    class Meta:
        model = Comment
        fields = ['id', 'post', 'author', 'author_username', 'author_avatar', 'text', 'created_at']
        read_only_fields = ['author', 'post']

class PostSerializer(serializers.ModelSerializer):
    media = MediaSerializer(many=True, read_only=True)
    author_username = serializers.ReadOnlyField(source='author.username')
    author_avatar = serializers.ImageField(source='author.avatar_url', read_only=True)
    likes_count = serializers.IntegerField(source='likes.count', read_only=True)
    comments_count = serializers.IntegerField(source='comments.count', read_only=True)
    is_liked = serializers.SerializerMethodField() # Сіз бұл постқа лайк бастыңыз ба?

    class Meta:
        model = Post
        fields = [
            'id', 'author', 'author_username', 'author_avatar', 
            'caption', 'media', 'likes_count', 'is_liked', 
            'comments_count', 'created_at'
        ]
        read_only_fields = ['author']

    def get_is_liked(self, obj):
        request = self.context.get('request')
        if request and request.user.is_authenticated:
            return Like.objects.filter(user=request.user, post=obj).exists()
        return False
    
# ================= ЖАҢА: ЗАМЕТКИ ЖӘНЕ СТОРИС SERIALIZERS =================

class NoteSerializer(serializers.ModelSerializer):
    # Заметканы кім жазғанын қолмен жазбау үшін, оны автоматты түрде аламыз
    author = serializers.ReadOnlyField(source='author.username')

    class Meta:
        model = Note
        fields = ['id', 'author', 'text', 'created_at']

class StorySerializer(serializers.ModelSerializer):
    author = serializers.ReadOnlyField(source='author.username')

    class Meta:
        model = Story
        fields = ['id', 'author', 'image', 'created_at']