#插入近义词表
import MySQLdb

db = MySQLdb.connect("122.114.206.52","root","ssgxhn$$$2018","db_news_analyse",charset = 'utf8',connect_timeout= 10)
cursor = db.cursor()
syno = open('tyc.txt',encoding='utf8')
num=49040
for w in syno:
	ws = w.split(" ")
	for i in range(len(ws)-1):
		print(num)
		arg=(str(num),ws[0],ws[i+1].replace('\n',''))
		# print(arg)
		num=num+1
		cursor.execute("INSERT INTO t_synonym VALUES (%s,%s,%s)",arg)
		db.commit()
db.close()