from django.contrib import admin
from .models import Post, Media, Like, Comment

admin.site.register(Post)
admin.site.register(Media)
admin.site.register(Like)
admin.site.register(Comment)