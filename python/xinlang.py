#coding: utf-8
import urllib.request
import re
from selenium import webdriver
import MySQLdb
import time
import datetime

#判断某网址是否对应最终页面
def isFinal(str):
	match = re.search( r'/\d\d\d\d-\d\d-\d\d/doc', str, re.M|re.I)
	if match:
		return True
	else:
		return False

#从每个页面提取出新浪站内link，返回一个link列表
def getLink(link):
	response = urllib.request.urlopen(link,timeout=20)
	cont = response.read().decode('utf-8','ignore')
	res_link = r'<a.*?href="(.*?)"'
	allLink = re.findall(res_link,cont,re.S|re.M)
	for i in allLink:
		if ("sina.com.cn" in i) and ("http" in i) and (i not in linkCrawled):#如果是站内页面
			linkToCrawl.append(i)

def strFun(str):
	while str.find("<")!=-1 and str.find(">")!=-1 and str.find(">")!=len(str)-1 and str.find('<')<str.find('>'):
		print('.',end='')
		s = str.find('<')
		e = str.find('>')
		str=str[0:s]+str[e+1:]
	str.replace('\n','')
	return str

#从每一个网页页面读取并提取所需数据写入txt
def getData(link,num):
	# f= open("D:/testNews/"+ str(num) +'.txt','w')
	response = urllib.request.urlopen(link,timeout=20).read()
	cont = response.decode("utf-8","ignore")
	#print response.getcode()

	res_title = r'<h1.*?>(.*?)</h1>'
	title = re.findall(res_title,cont,re.S|re.M)

	res_date = r'<span class="date">(.*?)</span>'
	date = re.findall(res_date,cont,re.S|re.M)

	res_source = r'<.*?class="source".*?>(.*?)</.*?>'
	source = re.findall(res_source,cont,re.S|re.M)

	res_keyword = r'<a href="http://tags.*?>(.*?)</a>'
	keyword = re.findall(res_keyword,cont,re.S|re.M)

	res_content = r'<p>(.*?)</p>'
	content = re.findall(res_content,cont,re.S|re.M)

	print("link : "+link)
	print("title : "+title[len(title)-1])
	if date:
		date_str=date[0].replace('年','-').replace('月','-').replace('日','')
	else:
		date_str="时间缺失"
	print("date : "+date_str)
	if source:
		source_str=strFun(source[0])
	else:
		source_str="来源缺失"
	print("source : "+source_str)
	# print("keywords : "+keyword[0])

	# content.pop()
	# content.pop()
	_str=''
	for c in content: 
		_str=_str+c
	_str=strFun(_str)
	_str=_str.replace(" ","").replace("\n\r","").replace("\n","").replace("\t","")
	print("content length : "+str(len(_str)))
	# if len(_str)<50:
	# 	print("content : "+_str)
	# else:
	# 	print("content : "+_str[:49]+"......")

	#新闻信息存入数据库
	db=MySQLdb.connect("122.114.206.52","root","ssgxhn$$$2018","db_news_analyse",charset = 'utf8',connect_timeout= 5)
	try:
		cursor=db.cursor()
		sql="INSERT INTO t_news (id,title,`time`,source,content,keywords,visit_num,remark_num,url,catch_time) VALUES (\"" \
		+link[link.find('doc-')+4:link.rfind('.')]+"\",\""+title[len(title)-1]+"\",\""+date_str+ \
		"\",\""+source_str+"\",\""+_str+"\",\""+"空"+"\",0,0,\""+link+"\","+str(int(round(time.time() * 1000)))+");"
		# print(sql)
		cursor.execute(sql)
		db.commit()
	except Exception as e:
		raise Exception(str(e))
	finally:
		db.close()
	

try:
	max_len=10000
	linkToCrawl= ['http://news.sina.com.cn/']#该列表储存所有要爬还没爬的link
	linkCrawled =[]#该列表储存已经爬取过的link，
	num = 1
	error_num=0
	
	while linkToCrawl:
		try:
			if linkToCrawl[0] in linkCrawled:
				continue
				
			linkCrawled.append(linkToCrawl[0])#取出第一个元素加入已爬取
			if len(linkToCrawl)<max_len:
				getLink(linkToCrawl[0])
				print("提取链接")
				print("待爬取列表长度："+str(len(linkToCrawl)))
			if isFinal(linkToCrawl[0]):
				getData(linkToCrawl[0],num)
				print(num)
				num = num + 1
			# else:
			# 	linkToCrawl.append(linkToCrawl[0])
		except Exception as e:
			error_num=error_num+1
			print("失败"+str(error_num)+"次,失败原因："+str(e))
			pass
		finally:
			del linkToCrawl[0]	
except:
	print("程序意外终止")

	


	#res_link1 = r'<a target="_blank" href="http://news.sina.com.cn/(.*?)">.*?</a>'
	#link1 = re.findall(res_link1,sourceCont,re.S|re.M)
	#res_link2 = r'<a href="http://news.sina.com.cn/(.*?)" target="_blank">.*?</a>'
	#link2 = re.findall(res_link2,sourceCont,re.S|re.M)