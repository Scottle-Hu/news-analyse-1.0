#coding:utf-8
import urllib.request
import re
from selenium import webdriver
import MySQLdb
import time
import datetime

#抓取评论
def getRemark(link):	
	#连接数据库
	db=MySQLdb.connect("122.114.206.52","root","ssgxhn$$$2018","db_news_analyse")
	cursor = db.cursor()

	driver="F:\chromedriver\chromedriver.exe"
	b=webdriver.PhantomJS()
	b.get(link)

	print()
	print("="*30+"开始抓取最热评论"+"="*30)
	print()
	#抓取最热评论
	hasHotRemark=True
	try:
		b.find_element_by_xpath("//div[@class='hot-wrap']")
	except:
		print("no hot remark!")
		hasHotRemark=False  #没有

	if hasHotRemark:
		num=1
		print()
		print("======开始抓取原始评论======")
		print()
		#原始评论
		while True:
			print("="*10+str(num)+"="*10)
			try:
				href = b.find_element_by_xpath("//div[@class='hot-wrap']/div[@class='list']/div[@class='item clearfix']["+str(num)+"]/div[@class='head']/a").get_attribute("href")
				userId = href[href.rfind("/") + 1:len(href)]
				print("用户 id : " + userId)
				content = b.find_element_by_xpath("//div[@class='hot-wrap']/div[@class='list']/div[@class='item clearfix']["+str(num)+"]/div[@class='cont']/div[@class='txt']").text
				print("评论内容 : " + content)
				time = b.find_element_by_xpath("//div[@class='hot-wrap']/div[@class='list']/div[@class='item clearfix']["+str(num)+"]/div[@class='cont']/div[@class='action']/span[@class='time']").get_attribute('date')
				print("评论时间 : " + time)
				votenum=b.find_element_by_xpath("//div[@class='hot-wrap']/div[@class='list']/div[@class='item clearfix']["+str(num)+"]/div[@class='cont']/div[@class='action']/span[@class='btns']/a[@class='vote']/span/em").text
				votenum=votenum.replace(",","")
				print("点赞数目 : "+votenum)
				#写入mysql数据库 
				#添加用户
				# sql="INSERT INTO t_user (id,url) VALUES ('"+userId+"','"+href+"')"
				# cursor.execute(sql)
				# db.commit()
				#添加评论
				# sql="INSERT INTO t_remark (id,) VALUES ()"

				num=num + 1
			except:
				print("原始评论爬取跳出，已抓取评论数目"+str(num-1))
				break

		print()
		print("======开始抓取二次评论======")
		print()

		#对评论的评论
		num=1
		while True:
			print("="*10+str(num)+"="*10)
			try:
				num2=1
				b.find_element_by_xpath("//div[@class='hot-wrap']/div[@class='list']/div[@class='dialog-list-wrap']["+str(num)+"]")
				while True:
					try:
						#内容
						content = b.find_element_by_xpath("//div[@class='hot-wrap']/div[@class='list']/div[@class='dialog-list-wrap']["+str(num)+"]/div[@class='dialog-list']/div[@class='item clearfix']["+str(num2)+"]/div[@class='cont']/div[@class='txt']").text
						print("评论内容 : " + content)
						#评论时间
						time = b.find_element_by_xpath("//div[@class='hot-wrap']/div[@class='list']/div[@class='dialog-list-wrap']["+str(num)+"]/div[@class='dialog-list']/div[@class='item clearfix']["+str(num2)+"]/div[@class='cont']/div[@class='action']/span[@class='time']").get_attribute('date')
						print("评论时间 ： "+time)
						#点赞数目
						votenum=b.find_element_by_xpath("//div[@class='hot-wrap']/div[@class='list']/div[@class='dialog-list-wrap']["+str(num)+"]/div[@class='dialog-list']/div[@class='item clearfix']["+str(num2)+"]/div[@class='cont']/div[@class='action']/span[@class='btns']/a[@class='vote']/span/em").text
						print("点赞数目 : "+votenum)

						#判断是针对原始评论的评论，还是针对二次评论的再评论
						reply2=True
						try:
							b.find_element_by_xpath("//div[@class='hot-wrap']/div[@class='list']/div[@class='dialog-list-wrap']["+str(num)+"]/div[@class='dialog-list']/div[@class='item clearfix']["+str(num2)+"]/div[@class='cont']/div[@class='txt']/span[@class='sina-comment-user-lnk-wrap'][1]")
						except:
							reply2=False

						#是针对二次评论的再评论
						if reply2:
							referenceId=b.find_element_by_xpath("//div[@class='hot-wrap']/div[@class='list']/div[@class='dialog-list-wrap']["+str(num)+"]/div[@class='dialog-list']/div[@class='item clearfix']["+str(num2)+"]/div[@class='cont']/div[@class='txt']/span[@class='sina-comment-user-lnk-wrap'][1]/a").get_attribute("href")
							referenceId=referenceId[referenceId.rfind('/')+1:len(referenceId)]
						else:  #针对原始评论的评论
							referenceId=b.find_element_by_xpath("//div[@class='hot-wrap']/div[@class='list']/div[@class='item clearfix']["+str(num)+"]/div[@class='head']/a").get_attribute("href")
							referenceId=referenceId[referenceId.rfind('/')+1:len(referenceId)]

						print("被评论者id : "+referenceId)

						userId=b.find_element_by_xpath("//div[@class='hot-wrap']/div[@class='list']/div[@class='dialog-list-wrap']["+str(num)+"]/div[@class='dialog-list']/div[@class='item clearfix']["+str(num2)+"]/div[@class='cont']/div[@class='txt']/span[@class='name ']/span[@class='sina-comment-user-lnk-wrap'][1]/a").get_attribute("href")
						userId=userId[userId.rfind('/')+1:len(userId)]
						print("评论者id ： "+userId)

						#写入mysql
						#to-do

						num2=num2+1
					except:
						print("第"+str(num)+"条二次评论集合爬取完毕，共抓取二次评论数目"+str(num2-1))
						break

				num=num+1
			except:
				print("二次评论爬取跳出，已抓取评论数目"+str(num-1))
				break
	print()
	print("="*30+"开始抓取最新评论"+"="*30)
	print()
	#抓取最新评论
	hasLatestRemark=True
	try:
		b.find_element_by_xpath("//div[@class='latest-wrap']")
	except:
		print("no latest remark!")
		hasLatestRemark=False  #没有

	if hasLatestRemark:
		num=1
		print()
		print("======开始抓取原始评论======")
		print()
		#原始评论
		while True:
			print("="*10+str(num)+"="*10)
			try:
				href = b.find_element_by_xpath("//div[@class='latest-wrap']/div[@class='list']/div[contains(@class,'sina-comment-page')]/div[@class='item clearfix']["+str(num)+"]/div[@class='head']/a").get_attribute("href")
				userId = href[href.rfind("/") + 1:len(href)]
				print("用户 id : " + userId)
				content = b.find_element_by_xpath("//div[@class='latest-wrap']/div[@class='list']/div[contains(@class,'sina-comment-page')]/div[@class='item clearfix']["+str(num)+"]/div[@class='cont']/div[@class='txt']").text
				print("评论内容 : " + content)
				time = b.find_element_by_xpath("//div[@class='latest-wrap']/div[@class='list']/div[contains(@class,'sina-comment-page')]/div[@class='item clearfix']["+str(num)+"]/div[@class='cont']/div[@class='action']/span[@class='time']").get_attribute('date')
				print("评论时间 : " + time)
				votenum=b.find_element_by_xpath("//div[@class='latest-wrap']/div[@class='list']/div[contains(@class,'sina-comment-page')]/div[@class='item clearfix']["+str(num)+"]/div[@class='cont']/div[@class='action']/span[@class='btns']/a[@class='vote']/span/em").text
				votenum=votenum.replace(",","")
				print("点赞数目 : "+votenum)
				#写入mysql数据库 
				#to-do
				
				num=num + 1
			except:
				print("原始评论爬取跳出，已抓取评论数目"+str(num-1))
				break
	#关闭模拟浏览器
	b.close()
	#关闭数据库连接
	db.close()

#判断某网址是否对应最终页面
def isFinal(str):
	match = re.search( r'/\d\d\d\d-\d\d-\d\d/doc', str, re.M|re.I)
	if match:
		return True
	else:
		return False

#从每个页面提取出新浪站内link，返回一个link列表
def getLink(link):
	response = urllib.request.urlopen(link)
	cont = response.read().decode('utf-8','ignore')
	res_link = r'<a.*?href="(.*?)"'
	allLink = re.findall(res_link,cont,re.S|re.M)
	for i in allLink:
		if ("sina.com.cn" in i) and (i not in linkCrawled):#如果是站内页面
			linkToCrawl.append(i)

def strFun(str):
	while str.find("<")!=-1 and str.find(">")!=-1:
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
	print("date : "+date[0].replace('年','-').replace('月','-').replace('日',''))
	print("source : "+strFun(source[0]))
	# print("keywords : "+keyword[0])

	# content.pop()
	# content.pop()
	_str=''
	for c in content: 
		_str=_str+c
	_str=strFun(_str)
	_str=_str.replace(" ","").replace("\n\r","").replace("\n","").replace("\t","")
	# if len(_str)<50:
	# 	print("content : "+_str)
	# else:
	# 	print("content : "+_str[:49]+"......")

	#新闻信息存入数据库
	db=MySQLdb.connect("122.114.206.52","root","ssgxhn$$$2018","db_news_analyse",charset = 'utf8')
	cursor=db.cursor()
	sql="INSERT INTO t_news (id,title,`time`,source,content,keywords,visit_num,remark_num,url,catch_time) VALUES (\"" \
	+link[link.find('doc-if')+4:link.rfind('.')]+"\",\""+title[len(title)-1]+"\",\""+date[0].replace('年','-').replace('月','-').replace('日','')+ \
	"\",\""+strFun(source[0])+"\",\""+_str+"\",\""+"空"+"\",0,0,\""+link+"\","+str(int(round(time.time() * 1000)))+");"
	# print(sql)
	cursor.execute(sql)
	db.commit()
	db.close()
	
	# getRemark(link)
	
	


linkToCrawl= ['http://news.sina.com.cn/']#该列表储存所有要爬还没爬的link
linkCrawled =[]#该列表储存已经爬取过的link，
num = 1
# print(linkToCrawl)
while linkToCrawl:
	if linkToCrawl[0] in linkCrawled:
		continue
	try:
		linkCrawled.append(linkToCrawl[0])#取出第一个元素加入已爬取
		getLink(linkToCrawl[0])
		if isFinal(linkToCrawl[0]):
			getData(linkToCrawl[0],num)
			print(num)
			num = num + 1
		else:
			linkToCrawl.append(linkToCrawl[0])
		del linkToCrawl[0]	
	except Exception as e:
		print("失败一次")
		print(e)
		pass
