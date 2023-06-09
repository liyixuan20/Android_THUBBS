from django.urls import reverse
from django.shortcuts import render, get_object_or_404, redirect
from django.http import *
from django.utils import timezone
from django.shortcuts import render
from django.core import serializers
from django.contrib.auth.models import User
from django.contrib.auth import authenticate, logout, login
from .models import *
from .crud import *
import uuid
import random
import threading
import datetime
import time
import os
import base64
import requests,json
import ahocorasick

#from itsdangerous import TimedJSONWebSignatureSerializer as Serializer
from django.conf import settings
from django.core.mail import send_mail
from django.http import HttpResponse, JsonResponse,Http404, FileResponse
import re
import json


filtertree = []

myserver = 'http://82.157.198.223'

def signupfunc(request):
    res = {}
    if request.method == 'POST':
        username = request.POST.get('username', '')
        password = request.POST.get('password', '')
        nickname = request.POST.get('name', '')
        res['message'] = 'signing up'
        try:
            user = User.objects.create_user(username = username, password = password)
            res['id'] = user.id
            create_profile(user.id, username, password, nickname)
            
            message = Message(send_id = "notice_follow", receive_id = user.id, content = "欢迎!")
            session.add(message)
            message1 = Message(send_id = "notice_like", receive_id = user.id, content = "欢迎!")
            session.add(message1)
            message2 = Message(send_id = "notice_upgrade", receive_id = user.id, content = "欢迎!")
            session.add(message2)
            message3 = Message(send_id = "notice_comment", receive_id = user.id, content = "欢迎!")
            session.add(message3)
            session.commit()

        except Exception as e:
            res['message'] = e
        finally:
            if res['message'] == "signing up":
                res['type'] = 'ok'
                return JsonResponse(res,safe=False)
            else:
                res['type'] = 'nok'
                return JsonResponse(res,safe=False)  



def loginfunc(request):
    password = request.POST.get('password', '')
    username = request.POST.get('username', '')
    user = authenticate(username=username, password=password)
    res = {}
    if user is not None:
        login(request, user)
        res['type'] = 'ok'
        res['message'] = '登陆成功'
        return JsonResponse(res, safe=False)
    res['type'] = 'nok'
    res['message'] = "账号不存在"
    return JsonResponse(res,safe=False)

def change_password(request):
    id = request.POST.get('id', '')
    password = request.POST.get('password', '')
    user = session.query(Users).filter(Users.id == id)
    user.update({Users.password:password})
    session.commit()
    change_user_pwd(user)
    res = {}
    res['message'] = 'ok'
    return JsonResponse(res,safe=False)

def get_user_ava(id):
    user = session.query(Users).filter(Users.id == id).one_or_none()
    return user.profile
def get_user_nickname(id):
    user = session.query(Users).filter(Users.id == id).one_or_none()
    return user.nickname

#头像文件路径 username + 'pic/' + name

def change_avatar(request):
    myfile = request.FILES.get('image','')
    user_id = request.POST.get('user_id','')
    user = session.query(Users).filter(Users.id == id).one_or_none()
    if myfile:
        dir = user.account + '/' + "pic/" + myfile.name
        w = open(dir,'wb+')
        for chunk in myfile.chunks():   
            w.write(chunk)
        w.close()
        user.profile = (myserver + "/" + dir)
        session.commit()
        return JsonResponse({'message':"success"})
    else:
        return JsonResponse({'message':"fail"})
    


def user_blacklist(request):
    user_id = request.POST.get('user_id', '')
    black_id = request.POST.get('black_id', '')
    print(black_id)
    flag = session.query(Operator).filter(Operator.type ==5,Operator.user_id ==user_id, Operator.reply_id==black_id ).all()
    if flag == None or flag != []:  # 操作记录不存在
        op = Operator(reply_id = black_id, user_id = user_id,type = 5)
        session.add(op)
    else:
        op = session.query(Operator).filter(Operator.type == 5, Operator.user_id==user_id, Operator.reply_id==black_id)
        op.delete()
    session.commit()
    return JsonResponse("success!", safe=False)
    
def user_follow(request):
    user_id = request.POST.get('user_id', '')
    black_id = request.POST.get('follow_id', '')
    print(black_id)
    flag = Operator.objects.filter(type=6).filter(user_id=user_id).filter(reply_id=black_id).count()
    if flag == None or flag != []:  # 操作记录不存在
        op = Operator(reply_id = black_id, user_id = user_id,type = 6)
        session.add(op)
        session.commit()
        message = Message(send_id = "notice_follow", receive_id = black_id,content = get_user_nickname(user_id)+"关注了您！",looked = 0)
        session.add(message)
        session.commit()
    else:
        op = session.query(Operator).filter(Operator.type == 6, Operator.user_id==user_id, Operator.reply_id==black_id)
        op.delete()
        session.commit()
    return JsonResponse("success!", safe=False)
    
def user_follows(request):
    user_id = request.POST.get('id','')
    res = []
    ops = session.query(Operator).filter(Operator.type == 6, Operator.user_id == user_id).all()
    for op in ops:
        id_ = op.reply_id
        print(id_)
        user = session.query(Users).filter(Users.id == id_).one_or_none()
        item ={}
        item["ava"] = user.profile
        item["user_id"] = user.id
        item["name"] = user.nickname
        follow = session.query(Operator).filter(Operator.type == 6, Operator.user_id == user_id, Operator.reply_id == user_id).one_or_none()
        if follow:
            item["follow"] = "已关注"
        else:
            item["follow"] = "未关注"
        res.append(item)
    return JsonResponse(res, safe=False)

def user_followeds(request):
    user_id = request.POST.get('id','')
    res = []
    ops = session.query(Operator).filter(Operator.type == 6, Operator.reply_id == user_id).all()

    for op in ops:
        id_ = op.user_id
    
        user = session.query(Users).filter(Users.id == id_).one_or_none()
        item = {}
        item["ava"] = user.profile
        item["user_id"] = user.id
        item["name"] = user.nickname
        follow = session.query(Operator).filter(Operator.type == 6, Operator.user_id == user_id, Operator.reply_id == user_id).one_or_none()
        if follow:
            item["follow"] = "已关注"
        else:
            item["follow"] = "未关注"
        res.append(item)
    return JsonResponse(res, safe=False)