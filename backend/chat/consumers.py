import json
from channels.generic.websocket import AsyncWebsocketConsumer
from channels.db import database_sync_to_async
from django.contrib.auth import get_user_model
from rest_framework_simplejwt.tokens import AccessToken
from .models import Message

User = get_user_model()

class ChatConsumer(AsyncWebsocketConsumer):
    async def connect(self):
        self.other_user_id = self.scope['url_route']['kwargs']['user_id']
        self.user = await self.get_user_from_token()

        if not self.user:
            await self.close()
            return

        # Чат бөлмесінің атын анықтау (екі user ID-ден)
        ids = sorted([str(self.user.id), str(self.other_user_id)])
        self.room_name = f"chat_{'_'.join(ids)}"

        await self.channel_layer.group_add(
            self.room_name,
            self.channel_name
        )
        await self.accept()

    async def disconnect(self, close_code):
        await self.channel_layer.group_discard(
            self.room_name,
            self.channel_name
        )

    async def receive(self, text_data):
        data = json.loads(text_data)
        text = data.get('text', '')

        # Хабарды базаға сақтау
        message = await self.save_message(text)

        # Бөлмедегі барлығына жіберу
        await self.channel_layer.group_send(
            self.room_name,
            {
                'type': 'chat_message',
                'message': text,
                'sender_id': self.user.id,
                'sender_username': self.user.username,
                'created_at': str(message.created_at),
            }
        )

    async def chat_message(self, event):
        await self.send(text_data=json.dumps({
            'message': event['message'],
            'sender_id': event['sender_id'],
            'sender_username': event['sender_username'],
            'created_at': event['created_at'],
        }))

    @database_sync_to_async
    def get_user_from_token(self):
        try:
            token = self.scope['query_string'].decode().split('token=')[1]
            access_token = AccessToken(token)
            return User.objects.get(id=access_token['user_id'])
        except Exception:
            return None

    @database_sync_to_async
    def save_message(self, text):
        return Message.objects.create(
            sender=self.user,
            receiver_id=self.other_user_id,
            text=text
        )