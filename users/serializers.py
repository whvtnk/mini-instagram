from rest_framework import serializers
from .models import User, Follow

class UserSerializer(serializers.ModelSerializer):
    password = serializers.CharField(write_only=True) # Парольді тек жазуға болады, оқуға болмайды

    class Meta:
        model = User
        fields = ['id', 'username', 'email', 'password', 'bio', 'avatar_url', 'created_at']

class UserProfileSerializer(serializers.ModelSerializer):
    posts_count = serializers.IntegerField(source='posts.count', read_only=True)
    followers_count = serializers.SerializerMethodField()
    following_count = serializers.SerializerMethodField()
    is_following = serializers.SerializerMethodField() # Қазіргі қолданушы бұл адамға жазылған ба?

    class Meta:
        model = User
        fields = [
            'id', 'username', 'email', 'bio', 'avatar_url', 
            'posts_count', 'followers_count', 'following_count', 
            'is_following', 'created_at'
        ]

    def get_followers_count(self, obj):
        return obj.followers.count()

    def get_following_count(self, obj):
        return obj.following.count()

    def get_is_following(self, obj):
        request = self.context.get('request')
        if request and request.user.is_authenticated:
            return Follow.objects.filter(follower=request.user, followee=obj).exists()
        return False