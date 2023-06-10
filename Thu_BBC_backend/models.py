from .database import Base, SessionLocal
from datetime import datetime
from sqlalchemy import Column,String,Integer, DateTime





Users = Base.classes.User
Post = Base.classes.Post
Reply = Base.classes.Reply
Operator = Base.classes.Operator
Message = Base.classes.Message
Class = Base.classes.Class
Draft = Base.classes.Draft
Food = Base.classes.Food




# print(Base.classes.keys())
if __name__ == '__main__':
    bases.metadata.create_all()
# __all__ = [SessionLocal, Profile, Task, Single_task, Request, Receive]

# print(Task.__dict__)