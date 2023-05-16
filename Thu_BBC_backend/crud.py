from models import *
from django.contrib.auth.models import User
import os
from models import *
from django.contrib.auth.models import User
session = SessionLocal()

def create_profile(user_id):
    pro = session.query(Profile).filter(Profile.user_id == user_id).one_or_none()
    if pro != None:
        return -2
    pf = Profile(user_id=user_id, credits = 0)
    session.add(pf)
    session.commit()
    return 0