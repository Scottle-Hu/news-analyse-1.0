#coding:utf-8

import MySQLdb


db = MySQLdb.connect("122.114.206.52","root","ssgxhn$$$2018","db_news_analyse",charset = 'utf8',connect_timeout= 10)
cursor = db.cursor()
stop = open('stop_words.dict',encoding='utf8')
num=1
for w in stop:
	print(num)
	arg=(str(num),w.replace('\n',''))
	print(arg)
	num=num+1
	cursor.execute("INSERT INTO t_stop_words VALUES (%s,%s)",arg)
	db.commit()
db.close()