from rest_framework.decorators import api_view, permission_classes
from rest_framework.permissions import IsAuthenticated
from rest_framework.response import Response
from rest_framework import serializers
from django.db.models import Q
from .models import Message

class MessageSerializer(serializers.ModelSerializer):
    sender_username = serializers.ReadOnlyField(source='sender.username')

    class Meta:
        model = Message
        fields = ['id', 'sender', 'sender_username',
                  'receiver', 'text', 'created_at', 'is_read']

@api_view(['GET'])
@permission_classes([IsAuthenticated])
def get_messages(request, user_id):
    messages = Message.objects.filter(
        Q(sender=request.user, receiver_id=user_id) |
        Q(sender_id=user_id, receiver=request.user)
    )
    serializer = MessageSerializer(messages, many=True)
    return Response(serializer.data)

@api_view(['GET'])
@permission_classes([IsAuthenticated])
def get_conversations(request):
    """Менің барлық чаттарым"""
    from django.contrib.auth import get_user_model
    User = get_user_model()

    messages = Message.objects.filter(
        Q(sender=request.user) | Q(receiver=request.user)
    ).order_by('-created_at')

    # Бірегей пайдаланушыларды алу
    user_ids = set()
    for msg in messages:
        if msg.sender_id != request.user.id:
            user_ids.add(msg.sender_id)
        if msg.receiver_id != request.user.id:
            user_ids.add(msg.receiver_id)

    users = User.objects.filter(id__in=user_ids)
    data = [{'id': u.id, 'username': u.username} for u in users]
    return Response(data)