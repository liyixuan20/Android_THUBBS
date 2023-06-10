"""
URL configuration for Thu_BBC_backend project.

The `urlpatterns` list routes URLs to views. For more information please see:
    https://docs.djangoproject.com/en/4.2/topics/http/urls/
Examples:
Function views
    1. Add an import:  from my_app import views
    2. Add a URL to urlpatterns:  path('', views.home, name='home')
Class-based views
    1. Add an import:  from other_app.views import Home
    2. Add a URL to urlpatterns:  path('', Home.as_view(), name='home')
Including another URLconf
    1. Import the include() function: from django.urls import include, path
    2. Add a URL to urlpatterns:  path('blog/', include('blog.urls'))
"""
from django.contrib import admin
from django.urls import path
from . import views
urlpatterns = [
    path('admin/', admin.site.urls),
    path('user/login/', views.loginfunc),
    path('user/signup/', views.signupfunc),
    path('user/blacklist/', views.user_blacklist),
    path('user/follow/', views.user_follow),
    path('user/follows/', views.user_follows),
    path('user/followeds/', views.user_followeds),
    path('user/change_password/', views.change_password),
    path('user/avatar/',views.change_avatar),
    path('user/edit/', views.edit_user),
    path('user/task/', views.user_task),
    path('user/store/', views.user_store),
    path('user/record/', views.user_record),
    path('reply/post/', views.post_reply),
    path('reply/delete/', views.delete_reply),
    path('message/index/', views.get_message_index),
    path('message/send/', views.send_message),
    path('message/detail/', views.get_message_detail),
    path('message/delete/', views.delete_message),
    path('draft/init/', views.new_draft),
    path('draft/get/', views.get_draft),
    path('draft/delete/', views.delete_draft),
    path('draft/edit/', views.edit_draft),
    path('pic/<id>/', views.get_image),
    path('get_id/', views.get_id),
    path('judge_identy/', views.judge_identy),
    path('change_identy/',views.change_identy),
    path('get_doc/',views.get_doc),
    path('operator/edit/', views.edit_operator),
    path('operator/search/', views.index_search),
    path('user/get/home/', views.get_user_home),
    path('user/get/detail/', views.get_user_detail),
]
