from database import Base, SessionLocal, bases
from datetime import datetime
from sqlalchemy import Column,String,Integer, DateTime



Profile = Base.classes.Profile





# print(Base.classes.keys())
if __name__ == '__main__':
    bases.metadata.create_all()
# __all__ = [SessionLocal, Profile, Task, Single_task, Request, Receive]

# print(Task.__dict__)