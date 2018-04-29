#coding=utf-8

from selenium import webdriver
import MySQLdb


def getUserInfo(href):
	# try:
		#用户信息
		userId = href[href.rfind("/") + 1:len(href)]
		print("用户 id : " + userId)
		print("用户主页 : " + href)
		b2 = webdriver.PhantomJS()
		b2.get(href)
		while True:
			if ('W_icon icon_pf_female' in b2.page_source) or ('W_icon icon_pf_male' in b2.page_source):
				break
			else:
				# print(".",end='')
				b2.implicitly_wait(10)  #待解析元素没有加载出来就一直等待
		username = b2.find_element_by_xpath("//h1[@class='username']").text
		print("用户名 ： " + username)
		if 'W_icon icon_pf_female' in b2.page_source:
			print("用户性别 ： 女")
		else:
			print("用户性别 ： 男")
		#关注量、粉丝数、微博数
		# print(b2.page_source.encode("utf-8",'ignore'))
		# b2.page_source.encode('utf-8','ignore')
		src=b2.page_source[:b2.page_source.rfind("关注<\/span>")]
		index_end=src.rfind('<\/strong')
		index_start=src[:index_end].rfind('>')+1
		print("关注量 : "+src[index_start:index_end])

		src=b2.page_source[:b2.page_source.rfind("粉丝<\/span>")]
		index_end=src.rfind('<\/strong')
		index_start=src[:index_end].rfind('>')+1
		print("粉丝量 : "+src[index_start:index_end])

		src=b2.page_source[:b2.page_source.rfind("微博<\/span>")]
		index_end=src.rfind('<\/strong')
		index_start=src[:index_end].rfind('>')+1
		print("微博量 : "+src[index_start:index_end])
		

		b2.close()
	# except:
	# 	print("==========抓取用户信息异常==========")



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
				getUserInfo(href) #获取用户信息
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
						getUserInfo(userId) #获取用户信息

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
				getUserInfo(href) #获取用户信息

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




#main
# link="http://news.sina.com.cn/o/2018-03-25/doc-ifysqvpi3319333.shtml"
# getRemark(link)
getUserInfo("https://weibo.com/u/6489739255")