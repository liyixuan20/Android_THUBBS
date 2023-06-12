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
from sqlalchemy import asc, desc
from sqlalchemy import func
#from itsdangerous import TimedJSONWebSignatureSerializer as Serializer
from django.conf import settings
from django.core.mail import send_mail
from django.http import HttpResponse, JsonResponse,Http404, FileResponse
import re
import json
import uuid

img_server = './img/'
filtertree = []

myserver = 'http://82.157.198.223'

def signupfunc(request):
    print("call signupfunc")
    res = {}
    if request.method == 'POST':
        username = request.POST.get('username', '')
        user_password = request.POST.get('password', '')
        nickname = request.POST.get('name', '')
        intro = request.POST.get('intro','')
        res['message'] = 'signing up'
        
        user = User.objects.create_user(username = username, password = user_password)
        
        user_ = Users(id = str(user.id),nickname = nickname,account = username,email = username,school = intro,password = user_password)
        res['id'] = user_.id
        session.add(user_)
        session.commit()
        

        session.execute(Message.__table__.insert().values(send_id="notice_follow",receive_id=res['id'],content="欢迎!"))
        session.execute(Message.__table__.insert().values(send_id = "notice_like", receive_id = res['id'], content = "欢迎!"))
        session.execute(Message.__table__.insert().values(send_id = "notice_upgrade", receive_id = res['id'], content = "欢迎!"))
        session.execute(Message.__table__.insert().values(send_id = "notice_comment", receive_id = res['id'], content = "欢迎!"))
        session.commit()
        print("sign up message")
        return JsonResponse({'type': 1})
    


def loginfunc(request):
    print("call login")
    password = request.POST.get('password', '')
    username = request.POST.get('username', '')
    user = authenticate(username=username, password=password)
    res = {}
    accounts = session.query(Users).filter(Users.account == username).all()
    if user is not None:
        login(request, user)
    else:
        print("login fail")
    for account in accounts:
        if account.password == password:
            res['message'] = account.id
            res['type'] = 'ok'
        else:
            res['type'] = 'nok'
            res['message'] = "密码错误"
        print("login successful")
        return JsonResponse(res, safe=False)
    res['type'] = 'nok'
    res['message'] = "账号不存在"
    return JsonResponse(res,safe=False)

def change_password(request):
    print("change_password")
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
    print("get_user_ava")
    user = session.query(Users).filter(Users.id == id).one_or_none()
    return user.profile
def get_user_nickname(id):
    print("get_user_nickname")
    user = session.query(Users).filter(Users.id == id).one_or_none()
    if user == None:
        return None
    return user.nickname

#头像文件路径 'pic/' + name
def change_avatar(request):
    myfile = request.FILES.get('image','')
    user_id = request.POST.get('user_id','')
    user = session.query(Users).filter(Users.id == user_id).one_or_none()
    if myfile:
        dir = "pic/" + myfile.name
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
    if flag == None or flag == []:  # 操作记录不存在
        op = Operator(reply_id = black_id, user_id = user_id,type = 5)
        op.id = uuid.uuid1()
        session.add(op)
        session.commit()
    else:
        op = session.query(Operator).filter(Operator.type == 5, Operator.user_id==user_id, Operator.reply_id==black_id)
        op.delete()
        session.commit()
    session.commit()
    return JsonResponse("success!", safe=False)
    
def user_follow(request):
    user_id = request.POST.get('user_id', '')
    black_id = request.POST.get('follow_id', '')
    print(black_id)
    flag = Operator.objects.filter(type=6).filter(user_id=user_id).filter(reply_id=black_id).count()
    if flag == None or flag == []:  # 操作记录不存在
        op = Operator(reply_id = black_id, user_id = user_id,type = 6)
        op.id = uuid.uuid1()
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
        follow = session.query(Operator).filter(Operator.type == 6, Operator.user_id == user_id, Operator.reply_id == user.id).one_or_none()
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
        follow = session.query(Operator).filter(Operator.type == 6, Operator.user_id == user_id, Operator.reply_id == user.id).one_or_none()
        if follow:
            item["follow"] = "已关注"
        else:
            item["follow"] = "未关注"
        res.append(item)
    return JsonResponse(res, safe=False)

#todo:前端增加更多选项
def edit_user(request):
    id = request.POST.get('id', '')
    wechat_id = request.POST.get('intro', '')
    nickname = request.POST.get('name','')
    user = session.query(Users).filter(Users.id == id).one_or_none()
    if nickname != '':
        user.nickname = nickname
    if wechat_id !='':
        user.wechat_id = wechat_id
    session.commit()
    return JsonResponse("success!", safe=False)

def get_user_detail(request):
    id = request.POST.get('id', '')
    user_id = request.POST.get('user_id','')
    user = session.query(Users).filter(Users.id == id).one_or_none()
    res1 = {}
    res1['name']="已被拉黑"
    black = len(session.query(Operator).filter(Operator.type == 5, Operator.reply_id == id, Operator.user_id == user_id).all()) + len(session.query(Operator).filter(Operator.type == 5, Operator.reply_id == user_id, Operator.user_id == id).all())
    res = {}
    res["name"] = user.nickname
    res["ava"] = user.profile
    res["intro"] = user.wechat_id
    res["account"] = user.email
    res["user_id"] = user.id 
    follow = session.query(Operator).filter(Operator.user_id == user_id, Operator.reply_id == id, Operator.type == 6).one_or_none()
    if follow != None:
        res["follow"] = "已关注"
    else:
        res["follow"] = "未关注"
    if black!=0:
        print(black)  
        print(res1) 
        res1['follow']="未知" 
        return JsonResponse(res1,safe=False)
    else:
        return JsonResponse(res,safe=False)
    

def get_user_home(request):
    id = request.POST.get('id', '')
    user_id = request.POST.get('user_id','')
    black = len(session.query(Operator).filter(Operator.type == 5, Operator.reply_id == id, Operator.user_id == user_id).all()) + len(session.query(Operator).filter(Operator.type == 5, Operator.reply_id == user_id, Operator.user_id == id).all())
    user = session.query(Users).filter(Users.id == id).one_or_none()
    res = {}
    if(user == None):
        print("user is none")
    res["account"] = user.email
    res["name"] = user.nickname
    res["ava"] = user.profile
    res["intro"]=user.wechat_id
    res1 = {}
    res1['name']="已被拉黑"
    follows = session.query(Operator).filter(Operator.user_id == id, Operator.type == 6).all()
    count = 0
    for follow in follows:
        count+=1
    res["follow"] = str(count)
    
    followeds = session.query(Operator).filter(Operator.reply_id == id, Operator.type == 6).all()
    count = 0
    for follow in followeds:
        count+=1
    res["followed"] = str(count)
    if black!=0:
        return JsonResponse(res1,safe=False)
    else:
        return JsonResponse(res,safe=False)   
    

def user_task(request):
    id = request.POST.get('id', '')
    user = session.query(Users).filter(Users.id == id).one_or_none()
    task = user.task
    res = {}
    res["task1"] = task & 1
    res["task2"] = int((task & 2) / 2)
    res["task3"] = int((task & 4) / 4)
    return JsonResponse({'data': res})

#--------------------------------动态操作-------------------------------------
def edit_operator(request):
    id = request.POST.get('id', '')
    user_id = request.POST.get('user_id', '')
    type = int(request.POST.get('type', ''))
    reply_type = int(request.POST.get('reply_type', ''))
    flag = len(session.query(Operator).filter(Operator.type == type, Operator.user_id ==user_id, Operator.reply_id == id).all())
    user = get_user(user_id)
    if flag == 0: #操作记录不存在
        op = Operator()
        op.id = uuid.uuid1()
        op.reply_id = id
        op.user_id = user_id
        op.type = type
        session.add(op)
        session.commit()
        if reply_type == 1:
            food = session.query(Food).filter(Food.id == id).one_or_none()
            if type == 1:
                food.likes = food.likes + 1
                if user.task & 1 == 0:
                    user.task = user.task | 1
                    user.score = user.score + 10
                    
                author = session.query(Users).filter(Users.id == food.author).one_or_none()
                author.visit = author.visit + 3
                session.commit()
            elif type == 2:
                food.stores = food.stores + 1
                author = session.query(Users).filter(Users.id == food.author).one_or_none()
                author.visit = author.visit + 3
                session.commit()
            elif type == 4:
                food.reports = food.reports + 1
                if food.reports == 5 and food.hide == 0:
                    food.hide = 1
                    message = Message()
                    message.id = uuid.uuid1()
                    message.send_id = "admin"
                    message.receive_id = food.author
                    message.content = "您的帖子 " + food.name + " 被举报过多，已被隐藏！"
                    message.looked = 0
                    session.add(message)
                    session.commit()
            session.commit()
        elif reply_type == 2:
            post = session.query(Post).filter(Post.id == id).one_or_none()
            if type == 1:
                post.thumbs = post.thumbs + 1
                message = Message()
                message.send_id = "notice_like"
                message.receive_id = post.author
                message.content = "“" + user.nickname + "”" + "点赞了您的作品<" + post.title +">"
                message.looked = 0
                message.id = uuid.uuid1()
                session.commit()
                if user.task & 1 == 0:
                    user.task = user.task | 1
                    user.score = user.score + 10
                    
                author = session.query(Users).filter(Users.id == post.author).one_or_none()
                author.visit = author.visit + 3
                session.commit()
            elif type == 2:
                post.stores = post.stores + 1
                author = session.query(Users).filter(Users.id == post.author).one_or_none()
                author.visit = author.visit + 3
                session.commit
            elif type == 4:
                post.reports = post.reports + 1
                if post.reports == 5 and post.hide == 0:
                    post.hide = 1
                    message = Message()
                    message.send_id = "admin"
                    message.receive_id = post.author
                    message.content = "您的帖子 " + post.content + " 被举报过多，已被隐藏！"
                    message.looked = 0
                    session.add(message)
                    session.commit()
            session.commit()
        elif reply_type == 3:
            course = session.query(Class).filter(Class.id == id).one_or_none()
            if type == 1:
                course.likes = course.likes + 1
                if user.task & 1 == 0:
                    user.task = user.task | 1
                    user.score = user.score + 10
                    session.commit()
                author  = session.query(Users).filter(Users.id == course.author).one_or_none()
                author.visit = author.visit + 3
                session.commit()
            elif type == 2:
                course.stores = course.stores + 1
                author = session.query(Users).filter(Users.id == course.author).one_or_none()
                author.visit = author.visit + 3
                session.commit()
            elif type == 4:
                course.reports = course.reports + 1
                if course.reports == 5 and course.hide == 0:
                    course.hide = 1
                    message = Message()
                    message.send_id = "admin"
                    message.receive_id = course.author
                    message.content = "您的帖子 " + course.name + " 被举报过多，已被隐藏！"
                    message.id = uuid.uuid1()
                    session.commit()
            session.commit()
        elif reply_type == 4:
            reply = session.query(Reply).filter(Reply.id == id).one_or_none()
            if type == 1:
                reply.thumbs = reply.thumbs + 1
                if user.task & 1 == 0:
                    user.task = user.task | 1
                    user.score = user.score + 10
                    session.commit()
                author =  session.query(Users).filter(Users.id == reply.author).one_or_none()
                author.visit = author.visit + 3
                session.commit()
            elif type == 4:
                reply.reports = reply.reports + 1
                if reply.reports == 5 and reply.hide == 0:
                    reply.hide = 1
                    message = Message()
                    message.send_id = "admin"
                    message.receive_id = reply.author
                    message.content = "您的评论 " + reply.content + " 被举报过多，已被隐藏！"
                    message.looked = 0
                    session.add(message)
                    session.commit()
            session.commit()
    elif flag == 1:
        op = session.query(Operator).filter(Operator.type ==type, Operator.user_id == user_id, Operator.reply_id == id).delete()
        session.commit()
        if reply_type == 1:
            food = session.query(Food).filter(Food.id == id).one_or_none()
            if type == 1:
                food.likes = food.likes - 1
                author = session.query(Users).filter(Users.id == food.author).one_or_none()
                author.visit = author.visit - 3
                session.commit()
            elif type == 2:
                food.stores = food.stores - 1
                author = session.query(Users).filter(Users.id == food.author).one_or_none()
                author.visit = author.visit - 3
                session.commit()
            elif type == 4:
                food.reports = food.reports - 1
            session.commit()
        elif reply_type == 2:
            post = session.query(Post).filter(Post.id == id).one_or_none()
            if type == 1:
                post.thumbs = post.thumbs - 1
                author = session.query(Users).filter(Users.id == post.author).one_or_none()
                author.visit = author.visit - 3
                session.commit()
            elif type == 2:
                post.stores = post.stores - 1
                author = session.query(Users).filter(Users.id == post.author).one_or_none()
                author.visit = author.visit - 3
                session.commit()
            elif type == 4:
                post.reports = post.reports - 1
            session.commit()
        elif reply_type == 3:
            course = session.query(Class).filter(Class.id == id).one_or_none()
            if type == 1:
                course.likes = course.likes - 1
                author = session.query(Users).filter(Users.id == course.author).one_or_none()
                author.visit = author.visit - 3
                session.commit()
            elif type == 2:
                course.stores = course.stores - 1
                author = session.query(Users).filter(Users.id == course.author).one_or_none()
                author.visit = author.visit - 3
                session.commit()
            elif type == 4:
                course.reports = course.reports - 1
            session.commit()
        elif reply_type == 4:
            reply = session.query(Reply).filter(Reply.id == id).one_or_none()
            if type == 1:
                reply.thumbs = reply.thumbs - 1
                author = session.query(Users).filter(Users.id == reply.author).one_or_none()
                author.visit = author.visit - 3
                session.commit()
            elif type == 4:
                reply.reports = reply.reports - 1
            session.commit()
    return JsonResponse("success!", safe=False)


def index_search(request):
    user_type = request.POST.get('user_type','')
    search = request.POST.get('search', '')
    type = request.POST.get('type', '')
    order = request.POST.get('order', '')
    user_id = request.POST.get('user_id', '')
    attention = request.POST.get('attention', '')
    mine = request.POST.get('')
    search_tag = search.split(",")
    print("index_search")
    print(user_type, search, type, order, user_id, attention)
    res = []
    if order == "1":
        posts = session.query(Post).order_by(asc(Post.thumbs)).all()
    else:
        posts = session.query(Post).order_by(asc(Post.time)).all()
    if type == "内容":
        filter_posts = []
        added_posts = set()
        for tag in search_tag:
            for post in posts:
                if post.content.ilike(f"%{tag}%") and post not in added_posts:
                    filter_posts.append(post)
                    added_posts.add(post)
        posts = filter_posts
    elif type == "标题":
        filter_posts = []
        added_posts = set()
        for tag in search_tag:
            for post in posts:
                if post.title.ilike(f"%{tag}%") and post not in added_posts:
                    filter_posts.append(post)
                    added_posts.add(post)
        posts = filter_posts
    elif type == "类型":
        filter_posts = []
        added_posts = set()
        for tag in search_tag:
            for post in posts:
                if post.type.ilike(f"%{tag}%") and post not in added_posts:
                    filter_posts.append(post)
                    added_posts.add(post)
        posts = filter_posts
    for post in posts:
        item = {}
        item["id"] = post.id
        item["time"] = post.time.strftime('%m-%d %H:%M')
        item["content"] = post.content
        item["title"] = post.title
        if post.image == None or post.image == "":
            item["image"] = False
            item["imagePath"] = ""
        else:
            temp = post.image.split(";")
            item["image"] = True
            item["imagePath"] = temp[0]
        item["type"] = post.type
        item["location"] = post.tag
        item["thumbs"] = post.thumbs
        author = post.author
        user = get_user(author)
        item["avatar"] = user.profile
        item["dep"] = user.department
        item["user_id"] = user.id
        if item["dep"] == "" or item["dep"] == None:
            item["dep"] = "未知"
        item["sender"] = user.nickname
        follow = session.query(Operator).filter(Operator.type==6, Operator.user_id == user_id, Operator.reply_id == user_id).one_or_none()
        if follow:
            item["follow"] = "已关注"
        else:
            item["follow"] = "未关注"
        thumb = len(session.query(Operator).filter(Operator.type==1, Operator.user_id == user_id, Operator.reply_id == post.id).all())
        item["thumb"] = thumb
        flag = 1
        if mine == "1":
            if item["user_id"]!=user_id:
                flag = 0
        if attention == "1":
            flag = len(session.query(Operator).filter(Operator.type==6, Operator.user_id==user_id, Operator.reply_id==item["user_id"]).all())
        if type == "用户":
            for tag in search_tag:
                if tag not in item["sender"]:
                    flag = 0
        if flag != 0:
            if user_type != 'all':
                black = len(session.query(Operator).filter(Operator.user_id == user_id,Operator.reply_id == user_type, Operator.type == 5).all())+len(session.query(Operator).filter(Operator.user_id == user_type,Operator.reply_id == user_id, Operator.type == 5).all())
                print(black)
                if black==0 and item['user_id']==user_type:
                    res.append(item)
            else:
                res.append(item)
    return JsonResponse(res, safe=False)
#---------------------------草稿相关---------------------
def new_draft(request):#新建草稿
    print("new_draft")
    title = request.POST.get('title', '')
    content = request.POST.get('content', '')
    user_id = request.POST.get('user_id', '')
    image = request.POST.get('image','')
    draft = Draft()
    draft.image = ''
    if image!= '':
        imgList = image.split(',')
        for i in range(len(imgList)):
            image_data = base64.b64decode(imgList[i])
            pic_id = random_str(20)
            with open('pic/'+pic_id+'.png','wb') as f:
                f.write(image_data)
                f.close
            if draft.image == '':
                draft.image = img_server+pic_id+'.png'
            else:
                draft.image += (',' + img_server+pic_id+'.png')
    print("new_draft:img created")
    draft.id = uuid.uuid1()
    draft.title = check_word(title)
    draft.content = check_word(content)
    draft.author = user_id
    session.add(draft)
    session.commit()
    res = []
    drafts = session.query(Draft).filter(Draft.author==user_id).order_by(asc(Draft.time)).all()
    for draft in drafts:
        item = {}
        item["id"] = draft.id
        item["title"] = draft.title
        item["content"] = draft.content
        item["time"] = draft.time.strftime('%m-%d %H:%M')
        res.append(item)
    print("draft:", res)
    return JsonResponse(res, safe=False)

def get_draft(request):
    print("get_draft")
    print("\n")
    print("\n")
    user_id = request.POST.get('user_id', '')
    res = []
    drafts = session.query(Draft).filter(Draft.author==user_id).order_by(asc(Draft.time)).all()
    for draft in drafts:
        item = {}
        item["id"] = draft.id
        item["title"] = draft.title
        item["content"] = draft.content
        item["time"] = draft.time.strftime('%m-%d %H:%M')
        res.append(item)
    print("drafts:" ,res)
    return JsonResponse(res, safe=False)

def edit_draft(request):
    id = request.POST.get('id', '')
    title = request.POST.get('title', '')
    content = request.POST.get('content', '')
    user_id = request.POST.get('user_id', '')
    drafts = session.query(Draft).filter(Draft.id == id).all()
    for draft in drafts:
        draft.title = title
        draft.content = content
        session.commit()
    res = []
    drafts = session.query(Draft).filter(Draft.author==user_id).order_by(asc(Draft.time)).all()
    for draft in drafts:
        item = {}
        item["id"] = draft.id
        item["title"] = draft.title
        item["content"] = draft.content
        item["time"] = draft.time.strftime('%m-%d %H:%M')
        res.append(item)
    return JsonResponse(res, safe=False)


def delete_draft(request):
    id = request.POST.get('id', '')
    user_id = request.POST.get('user_id', '')
    draft = session.query(Draft).filter(Draft.id == id).delete()
    session.commit()
    res = []
    drafts = session.query(Draft).filter(Draft.author==user_id).order_by(asc(Draft.time)).all()
    for draft in drafts:
        item = {}
        item["id"] = draft.id
        item["title"] = draft.title
        item["content"] = draft.content
        item["time"] = draft.time.strftime('%m-%d %H:%M')
        res.append(item)
    return JsonResponse(res, safe=False)

#--------------------图片--------------------
def get_image(request,id):
    imagepath = "pic/" + id
    image_data = open(imagepath,"rb").read()
    if ".mp4" in imagepath:
        return HttpResponse(image_data, content_type="video/mp4")
    if ".png" in imagepath or ".jpg" in imagepath or ".jpeg" in imagepath:
        return HttpResponse(image_data, content_type="image/png")
    if ".mp3" in imagepath:
        return HttpResponse(image_data, content_type="video/mp4")
    return JsonResponse("Fail!")
#-------------------------getid----------------------------
def get_id(request):
    code = request.POST.get('code', '')
    r = requests.get('https://api.weixin.qq.com/sns/jscode2session?appid=wxb96edada600b5a49&secret=14935ef4a081f728e26abac9c3054192&js_code='+code+'&grant_type=authorization_code')
    data = json.loads(r.text)
    return JsonResponse(data)

#---------------------------判断是否在认证---------------
def judge_identy(request):
    id = request.POST.get('id', '')
    user = session.query(Users).filter(Users.id == id).one_or_none()
    identy = user.thu
    return JsonResponse({'identy': identy})
#------------------------改变认证状态-----------------
def change_identy(request):
    id = request.POST.get('id', '')
    user = session.query(Users).filter(Users.id == id).one_or_none()
    if user.thu == 0:
        user.thu = 1
    else:
        user.thu = 0
    user.save()
    return JsonResponse({'state': user.thu})
#----------------------获取文档----------------------
def get_doc(request):
    file_name = "helper.pdf"
    file_path = "doc/" + file_name
    with open(file_path, 'rb') as f:
        response = HttpResponse(f.read(), content_type="application/pdf", charset="utf-8")
        response['Content-Dispositon'] = "attachment; filename={0}".format(file_name)
        response["Access-Control-Allow-Origin"] = '*'
        response["Server"] = '*'
        return response

#---------------------------附加模块（美食、求助）----------------
def user_store(request):
    id = request.POST.get('id', '')
    stores = session.query(Operator).filter(Operator.type == 2, Operator.user_id == id).order_by(asc(Operator.time)).all()
    res = []
    for store in stores:
        item = {}
        id = store.reply_id
        item["id"] = id
        item["hide"] = 1
        if session.query(Food).filter(Food.id == id).one_or_none() != None:
            food = session.query(Food).filter(Food.id == id).one_or_none()
            item["color"] = "green"
            item["route"] = "/pages/index/food/detail/detail?food_id=" + id
            item["text"] = food.name
            item["time"] = store.time
            item["typename"] = "美食评价"
            item["hide"] = food.hide
        elif session.query(Class).filter(Class.id == id).one_or_none() != None:
            class_ = session.query(Class).filter(Class.id == id).one_or_none()
            item["color"] = "cyan"
            item["route"] = "/pages/index/course/detail/detail?course_id=" + id
            item["text"] = class_.name
            item["time"] = store.time
            item["typename"] = "评课"
            item["hide"] = class_.hide
        elif session.query(Post).filter(Post.id == id).one_or_none() != None:
            post = session.query(Post).filter(Post.id == id).one_or_none()
            item["route"] = "/pages/index/detail/detail?post_id=" + id
            item["text"] = post.content
            item["time"] = store.time
            if post.tag == "求助":
                item["color"] = "red"
                item["typename"] = "求助"
            elif post.tag == "代取物品":
                item["color"] = "grey"
                item["typename"] = "代取物品"
            elif post.tag == "闲置物品":
                item["color"] = "purple"
                item["typename"] = "闲置物品"
            elif post.tag == "倾诉":
                item["color"] = "pink"
                item["typename"] = "倾诉"
            elif post.tag == "寻物":
                item["color"] = "blue"
                item["typename"] = "寻物"
            item["hide"] = post.hide
        if item["hide"] == 0:
            res.append(item)
    return JsonResponse({'data': res})


def user_record(request):
    id = request.POST.get('id', '')
    date_list = []
    res = []
    food_sends = session.query(Food).filter(Food.author == id, Food.hide == 0).all()
    for food in food_sends:
        date = food.time.date()
        item = {}
        item["color"] = "green"
        item["route"] = "/pages/index/food/detail/detail?food_id=" + food.id
        item["text"] = food.name
        item["time"] = food.time.time()
        item["typename"] = "美食评价"
        if date in date_list:
            for data in res:
                if data["date"] == date:
                    data["text"].append(item)
        else:
            date_list.append(date)
            temp = {}
            temp["date"] = date
            temp["text"] = []
            temp["text"].append(item)
            res.append(temp)
    post_sends = session.query(Post).filter(Post.author == id, Post.hide == 0).all()
    for post in post_sends:
        date = post.time.date()
        item = {}
        item["route"] = "/pages/index/detail/detail?post_id=" + post.id
        item["text"] = post.content
        item["time"] = post.time.time()
        if post.tag == "求助":
            item["color"] = "red"
            item["typename"] = "求助"
        elif post.tag == "代取物品":
            item["color"] = "grey"
            item["typename"] = "代取物品"
        elif post.tag == "闲置物品":
            item["color"] = "purple"
            item["typename"] = "闲置物品"
        elif post.tag == "倾诉":
            item["color"] = "pink"
            item["typename"] = "倾诉"
        elif post.tag == "寻物":
            item["color"] = "blue"
            item["typename"] = "寻物"
        if date in date_list:
            for data in res:
                if data["date"] == date:
                    data["text"].append(item)
        else:
            date_list.append(date)
            temp = {}
            temp["date"] = date
            temp["text"] = []
            temp["text"].append(item)
            res.append(temp)
        course_sends = session.query(Class).filter(Class.author == id, Class.hide == 0).all()
        for class_ in course_sends:
            date = class_.time.date()
            item = {}
            item["color"] = "cyan"
            item["route"] = "/pages/index/course/detail/detail?course_id=" + class_.id
            item["text"] = class_.name
            item["time"] = class_.time.time()
            item["typename"] = "评课"
            if date in date_list:
                for data in res:
                    if data["date"] == date:
                        data["text"].append(item)
            else:
                date_list.append(date)
                temp = {}
                temp["date"] = date
                temp["text"] = []
                temp["text"].append(item)
                res.append(temp)
    scores = session.query(Operator).filter(Operator.type == 3, Operator.user_id == id).all()
    for score in scores:
        id = score.reply_id
        date = score.time.date()
        item = {}
        if session.query(Food).filter(Food.id == id).one_or_none() != None:
            food = session.query(Food).filter(Food.id == id).one_or_none()
            item["color"] = "green"
            item["route"] = "/pages/index/food/detail/detail?food_id=" + id
            item["text"] = food.name
            item["time"] = score.time.time()
            item["typename"] = "美食评价"
            if food.hide == 0:
                continue
        elif session.query(Class).filter(Class.id == id).one_or_none() != None:
            class_ = session.query(Class).filter(Class.id == id).one_or_none()
            item["color"] = "cyan"
            item["route"] = "/pages/index/course/detail/detail?course_id=" + id
            item["text"] = class_.name
            item["time"] = score.time.time()
            item["typename"] = "评课"
            if class_.hide == 0:
                continue
        elif session.query(Post).filter(Post.id == id).one_or_none() != None:
            post = session.query(Post).filter(Post.id == id).one_or_none()
            item["route"] = "/pages/index/detail/detail?post_id=" + id
            item["text"] = post.content
            item["time"] = score.time.time()
            if post.tag == "求助":
                item["color"] = "red"
                item["typename"] = "求助"
            elif post.tag == "代取物品":
                item["color"] = "grey"
                item["typename"] = "代取物品"
            elif post.tag == "闲置物品":
                item["color"] = "purple"
                item["typename"] = "闲置物品"
            elif post.tag == "倾诉":
                item["color"] = "pink"
                item["typename"] = "倾诉"
            elif post.tag == "寻物":
                item["color"] = "blue"
                item["typename"] = "寻物"
            if post.hide == 0:
                continue
        if date in date_list:
            for data in res:
                if data["date"] == date:
                    data["text"].append(item)
        else:
            date_list.append(date)
            temp = {}
            temp["date"] = date
            temp["text"] = []
            temp["text"].append(item)
            res.append(temp)
    for item in res: #去重
        temp = []
        for i in item["text"]:
            if i in temp:
                continue
            else:
                temp.append(i)
        item["text"] = temp
    res = sorted(res, key=lambda keys: keys["date"])
    res.reverse()
    for item in res:
        item["text"] = sorted(item["text"], key=lambda keys: keys["time"])
    return JsonResponse({'data': res})

#---------------------------------------------------------------------------------

def post_reply(request):
    reply = Reply()
    reply.id = uuid.uuid1()
    reply.reply_id = request.POST.get('reply_id', '')
    reply.content = check_word(request.POST.get('content', ''))
    reply.image = request.POST.get('image', '')
    reply.thumbs = 0
    author = request.POST.get('author', '')
    reply.author = author
    session.add(reply)
    session.commit()
    user = get_user(author)
    if user.task & 4 == 0:
        user.task = user.task | 4
        user.score = user.score + 50
        session.commit()
    flag = len(session.query(Food).filter(Food.id==reply.reply_id).all())
    if flag == 1:
        food = session.query(Food).filter(Food.id==reply.reply_id).one_or_none()
        author = get_user(food.author)
    else:
        flag = len(session.query(Post).filter(Post.id==reply.reply_id).all())
        if flag == 1:
            post = session.query(Post).filter(Post.id==reply.reply_id).one_or_none()
            author = get_user(post.author)
        else:
            flag = len(session.query(Class).filter(Class.id==reply.reply_id).all())
            if flag == 1:
                course = session.query(Class).filter(Class.id==reply.reply_id).one_or_none()
                author = get_user(course.author)
            else:
                reply_ = course = session.query(Reply).filter(Reply.id==reply.reply_id).one_or_none()
                author = get_user(reply_.author)
    author.visit = author.visit + 10
    session.commit()
    message = Message()
    message.id = uuid.uuid1()
    message.send_id = "notice_comment"
    message.receive_id = author.id
    message.content = "“" + user.nickname + "”" + "评论了您的作品<" + post.title +">"
    message.looked = 0
    session.add(message)
    session.commit
    return JsonResponse("success!", safe=False)

def delete_reply(request):
    id = request.POST.get('id', '')
    reply = session.query(Reply).filter(Reply.id==id).delete()
    session.commit()
    return JsonResponse("success", safe=False)

#-------------------------------------------分割线为以下消息相关

def get_message_index(request):
    """""
    async def execute_query():
        receives = session.query(Message).filter(Message.receive_id == id).order_by(asc(Message.time)).filter(Message.receiver_del == 0).all()
        return receives
    """""

    id = request.POST.get('id', '')
    print("get_message_index")
    print("get id:", id)
    res = []
    users = []
    receives = session.query(Message).filter(Message.receive_id == id).order_by(asc(Message.time)).filter(Message.receiver_del == 0).all()

    for message in receives:
        if message.send_id not in users:
            users.append(message.send_id)
            item = {}
            item["id"] = message.send_id
            item["sender"] = get_user_nickname(message.send_id)
            item["msg"] = message.content 
            item["year"] = message.time.year
            item["mon"] = message.time.month
            item["day"] = message.time.day
            item["hour"] = message.time.hour
            item["min"] = int(message.time.strftime("%M"))
            item['num'] = len(session.query(Message).filter(Message.receive_id==id, Message.send_id==message.send_id, Message.looked == 0).all())
            res.append(item)
        else:
            continue
    """""

    try:
        loop = asyncio.get_event_loop()
        receives = await loop.run_in_executor(None, execute_query)
    
        for message in receives:
            if message.send_id not in users:
                users.append(message.send_id)
                item = {}
                item["id"] = message.send_id
                item["sender"] = get_user_nickname(message.send_id)
                item["msg"] = message.content 
                item["year"] = message.time.year
                item["mon"] = message.time.month
                item["day"] = message.time.day
                item["hour"] = message.time.hour
                item["min"] = int(message.time.strftime("%M"))
                item['num'] = len(session.query(Message).filter(Message.receive_id==id, Message.send_id==message.send_id, Message.looked == 0).all())
                res.append(item)
            else:
                continue
    except Exception:
        raise Http404
    """
    print(res)
    return JsonResponse(res,safe=False)

def get_message_detail(request):
    user_id = request.POST.get('user_id', '')
    id = request.POST.get('id', '')
    res = []
    msgs = session.query(Message).filter(Message.send_id==id, Message.receive_id==user_id, Message.sender_del == 0).order_by(asc(Message.time)).all()
    
    for message in msgs:
        message.looked = 1
        message.save()
        item = {}
        item["sender"] = get_user_nickname(message.send_id)
        item["msg"] = message.content 
        item["year"] = message.time.year
        item["mon"] = message.time.month
        item["day"] = message.time.day
        item["hour"] = message.time.hour
        item["min"] = int(message.time.strftime("%M"))
        res.append(item)    
    return JsonResponse(res,safe=False)

def send_message(request):
    send_id = request.POST.get('send_id', '')
    receive_id = request.POST.get('receive_id', '')
    content = request.POST.get('content', '')
    message = Message()
    message.id = uuid.uuid1()
    message.send_id = send_id
    message.receive_id = receive_id
    session.add(message)
    flag = len(session.query(Operator).filter(Operator.type == 5, Operator.user_id == receive_id, Operator.reply_id == send_id).all()) + len(session.query(Operator).filter(Operator.type == 5, Operator.user_id == send_id, Operator.reply_id == receive_id).all())
    #flag = Operator.objects.filter(type=5).filter(user_id=receive_id).filter(reply_id=send_id).count() + Operator.objects.filter(type=5).filter(user_id=send_id).filter(reply_id=receive_id).count()
    if flag == 0:
        message.content = content
    else:
        message.content = ""
        message.receiver_del = 1
    message.looked = 0
    session.commit()
    return JsonResponse("success!", safe=False)

def delete_message(request):
    user_id = request.POST.get('user_id', '')
    id = request.POST.get('id', '')
    sends = session.query(Message).filter(Message.send_id == user_id, Message.receive_id == id, Message.sender_del == 0)
    receives = session.query(Message).filter(Message.send_id == id, Message.receive_id == user_id, Message.sender_del == 0)
    #sends = Message.objects.filter(send_id=user_id).filter(receive_id=id).filter(sender_del=0)
    #receives = Message.objects.filter(send_id=id).filter(receive_id=user_id).filter(receiver_del=0)
    for send in sends:
        send.sender_del = 1
        session.commit()
    for receive in receives:
        receive.receiver_del = 1
        session.commit()
    return JsonResponse("success!", safe=False)

#-----------------------------------动态post操作-----------------------------

#-------------------------------------------分割线以下为评事相关
def get_post_index(request):
    user_id = request.POST.get('user_id','')
    tag = request.POST.get('tag', '')
    if tag == '':
        posts = session.query(Post).filter(Post.hide == 0).order_by(asc(Post.time)),all()
        #posts = Post.objects.filter(hide=0).order_by("-time")
    else:
        posts = session.query(Post).filter(Post.tag==tag, Post.hide == 0).order_by(asc(Post.time)).all()      
    List = []
    i = 0
    for post in posts:
        item = {}
        item["id"] = post.id
        item["time"] = post.time.strftime('%m-%d %H:%M')
        item["content"] = post.content
        if post.image == None or post.image == "":
            item["image"] = False
            item["imagePath"] = ""
        else:
            temp = post.image.split(",")
            item["image"] = True
            item["imagePath"] = temp[0]
        item["title"] = post.title
        item["type"] = post.type
        item["location"] = post.tag
        author = post.author
        user = get_user(author)
        item["user_id"] = user.id
        item["avatar"] = user.profile
        item["dep"] = user.department
        if item["dep"] == "" or item["dep"] == None:
            item["dep"] = "未知"
        item["sender"] = user.nickname
        follow = session.query(Operator).filter(Operator.type == 6, Operator.user_id == user_id, Operator.reply_id == user.id).one_or_none()
        #follow = Operator.objects.filter(type=6).filter(user_id=user_id).filter(reply_id=user.id)
        if follow != None:
            item["follow"] = "已关注"
        else:
            item["follow"] = "未关注"
        List.append(item)
    return JsonResponse(List,safe=False)

def get_post_detail(request):
    id = request.POST.get('id', '')
    user_id = request.POST.get('user_id', '')
    post = session.query(Post).filter(Post.id == id).one_or_none()
    if post == None:
        print("post is none")
    else:
        print('\n')
        print("get_post_detail")
        print("post", post)
        print('\n')
    res = {}
    res["author"]=post.author
    res["sendname"] = get_user_nickname(post.author)
    res["date"] = post.time.strftime('%m-%d %H:%M')
    res["ava"] = get_user_ava(post.author)
    res["title"] = post.title
    res["text"] = post.content
    if post.image==None:
        res["pic"] = []
    else:
        res["pic"] = post.image.split(",")
    res["type"] = post.type
    res["likes"] = post.thumbs
    res["store"] = post.stores
    res["reports"] = post.reports
    res["location"] = post.tag
    res["like"] = session.query(Operator).filter(Operator.type==1,Operator.user_id ==user_id,Operator.reply_id == id).count()
    res["sto"] = session.query(Operator).filter(Operator.type==2,Operator.user_id ==user_id,Operator.reply_id == id).count()
    res["reported"] = session.query(Operator).filter(Operator.type==4,Operator.user_id ==user_id,Operator.reply_id == id).count()
    thumb_list = "点赞者:"
    if res["likes"] == 0:
      thumb_list += "无"
    else:
      peoples = session.query(Operator).filter(Operator.type==1,Operator.reply_id == id).all()
      for people in peoples:
        user = get_user(people.user_id)
        thumb_list += user.nickname + " "
    res["thumb_list"] = thumb_list
    
    #res["done"] = Reply.objects.filter(author=user_id).filter(reply_id=id).count()
    #if res["done"] > 0:
        #res["done"] = 1
    #res["icons"] = post.icons
    #res["money"] = post.money
    #res["finished"] = post.done
    response = []
    users = []
    tmp1 = []
    second_replys = []
    replys = session.query(Reply).filter(Reply.reply_id == id, Reply.hide == 0).all()

    for reply in replys:
        item = {}
        item["id"] = reply.id
        item["rank"] = 1
        item["user_id"] = reply.author
        
        item["sendname"] = get_user_nickname(reply.author)
        item["ava"] = get_user_ava(reply.author)
        item["msg"] = reply.content
        item["likes"] = reply.thumbs
        item["reports"] = reply.reports
        item["date"] = reply.time
        item["like"] = session.query(Operator).filter(Operator.type==1,Operator.user_id==user_id,Operator.reply_id==reply.id).count()
        item["reported"] = session.query(Operator).filter(Operator.type==4,Operator.user_id==user_id,Operator.reply_id==reply.id).count()
        response.append(item)
        if reply.author not in tmp1 and reply.author != res["author"]:
          users.append(item)
          tmp1.append(reply.author)
        tmp = session.query(Reply).filter(Reply.reply_id==reply.id,Reply.hide==0).all()
        for i in tmp:
            second_replys.append(i)
    
    while second_replys != []:
        next_replys = []
        for second_reply in second_replys:
            item = {}
            item["id"] = second_reply.id
            item["rank"] = 2
            item["sendname"] = get_user_nickname(second_reply.author)
            item["user_id"] = second_reply.author
            tmp_id = session.query(Reply).filter(Reply.id==second_reply.reply_id).one_or_none()
            item["recname"] = get_user_nickname(tmp_id.author)
            item["ava"] = get_user_ava(second_reply.author)
            item["msg"] = second_reply.content
            item["likes"] = second_reply.thumbs
            item["reports"] = second_reply.reports
            item["date"] = second_reply.time
            item["like"] = session.query(Operator).filter(Operator.type==1,Operator.user_id==user_id,Operator.reply_id==second_reply.id).count()
            item["reported"] = session.query(Operator).filter(Operator.type==1,Operator.user_id==user_id,Operator.reply_id==second_reply.id).count()
            response.append(item)
            if second_reply.author not in tmp1 and second_reply.author != res["author"]:
              users.append(item)
              tmp1.append(second_reply.author)
            tmp = session.query(Reply).filter(Reply.reply_id==second_reply.id,Reply.hide==0).all()
            for i in tmp:
                next_replys.append(i)
        second_replys = next_replys
    response = sorted(response, key=lambda keys: keys["date"])
    for item in response:
        item["date"] = item["date"].strftime('%m-%d %H:%M')
    res["response"] = response
    return JsonResponse(res, safe=False)


def new_post(request):#新建帖子记录

    title  = request.POST.get('title','')
    myfile = request.FILES.get('image',)
    content = request.POST.get('content', '')
    user_id = request.POST.get('user_id', '')
    post_id = request.POST.get('post_id','')
    type = request.POST.get('type','')
    location = request.POST.get('location','')
    if post_id == "init":
        post = Post()
        post.id = uuid.uuid1()
        post.content = check_word(content)
        post.author = user_id
        post.title = title
        post.tag = location
        post.type = type
        if myfile:
            dir = "pic/"  + myfile.name
            w = open(dir,'wb+')
            for chunk in myfile.chunks():   
                w.write(chunk)
            w.close()
            post.image = (myserver + "/" + dir + ";")
        session.add(post)
        session.commit()
        operators = session.query(Operator).filter(Operator.type==6,Operator.reply_id==user_id).all()
        user = get_user(user_id)
        for operator in operators:
          message = Message()
          message.send_id = "notice_upgrade"
          message.receive_id = operator.user_id
          message.content = "你关注的“" + user.nickname + "”" + "发表了作品<" + post.title +">"
          message.looked = 0
          message.id = uuid.uuid1()
          session.add(message)
          session.commit()
        return JsonResponse({'id':post.id})
    else:
        post = session.query(Post).filter(Post.id==post_id).one_or_none()
        if post == None:
            print("post is none")
            return JsonResponse({'id':''})
        if myfile:
            dir = "pic/" + myfile.name
            w = open(dir,'wb+')
            for chunk in myfile.chunks():   
                w.write(chunk)
            w.close()
            post.image += (myserver + "/" + dir + ";")
            post.save()

        print("new_post end")
        return JsonResponse({'id':post.id})
    

def finish_post(request):
    post_id = request.POST.get('post_id', '')
    user_id = request.POST.get('user_id', '')
    post = session.query(Post).filter(Post.id==post_id).one_or_none()
    if post == None:
        print("fininsh_post: post is None" )
    if get_user(user_id) == None:
        return JsonResponse("Not Exist User!", safe=False)
    user = get_user(user_id)
    user.score = user.score + post.icons
    session.commit()
    if post.done == 0:
        post.done = 1
        session.commit()
    else:
        return JsonResponse("Post was done!", safe=False)
    return JsonResponse("success!", safe=False)
def good_post(request):
    pass
#----------------------敏感词过滤----------------------
def build_actree(wordlist):
    actree = ahocorasick.Automaton()
    for index, word in enumerate(wordlist):
        actree.add_word(word, (index, word))
    actree.make_automaton()
    return actree

def check_word(content):
    global filtertree
    if filtertree == []:
        wordlist = []
        with open("key.txt", "r", encoding='utf-8') as f:
            for line in f.readlines():
                keys = line.split("|")
                for key in keys:
                    wordlist.append(key)
        print("init filter tree.")
        filtertree = build_actree(wordlist)
    res = content
    for i in filtertree.iter(content):
        res = res.replace(i[1][1], "**")
    return res

def reset_task():
    users = User.objects.all()
    for user in users:
        user.task = 0
        user.save()
        
        
    


