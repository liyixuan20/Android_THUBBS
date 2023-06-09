from models import *
from django.contrib.auth.models import User
import os
from models import *
import random
import threading
import datetime
import time
import os
import base64
import requests,json
import ahocorasick
session = SessionLocal()

def create_profile(user_id, username, password, nickname):
    pro = session.query(Users).filter(Users.id == user_id).one_or_none()
    if pro != None:
        return -2
    pf = Users(id=user_id, account= username, password = password, nickname = nickname)
    session.add(pf)
    session.commit()
    return 0

def change_user_pwd(user):
    user = User.objects.get(username=user.account)
    user.set_password(user.password)
    user.save()
    print("Password changed successfully.")








def random_str(randomlenth=5):
    random_string = ''
    base_str = 'QWERTYUIOPLKJHGFDSAZXCVBNMqwertyuioplkjhgfdsazxvcvbnm0123456789_'
    length = len(base_str) - 1
    for i in range(randomlenth):
        random_string += base_str[random.randint(0,length)]
    return random_string

def randomcolor():
    colorArr = ['1','2','3','4','5','6','7','8','9','A','B','C','D','E','F']
    color = ""
    for i in range(6):
        color += colorArr[random.randint(0,14)]
    return "#"+color

def translate(number): #转化数字表示
    if number >= 10000:
        t1 = number // 10000
        t2 = (number - 10000*t1) // 1000
        res = str(t1) + "." + str(t2) + "W"
    elif number >= 1000:
        t1 = number // 1000
        t2 = (number - 1000*t1) // 100
        res = str(t1) + "." + str(t2) + "k"
    else:
        res = str(number)
    return res