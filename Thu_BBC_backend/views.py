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
    if request.method == 'PUT':
        username = request.PUT.get('username', '')
        password = request.PUT.get('password', '')
        res['message'] = 'signing up'
        try:
            user = User.objects.create_user(username = username, password = password)
            res['id'] = user.id
            create_profile(user_id = user.id)
        
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

