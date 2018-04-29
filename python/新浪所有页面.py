#coding: utf-8
import urllib2
import re

#判断某网址是否对应最终页面
def isFinal(str):
	match = re.search( r'/\d\d\d\d-\d\d-\d\d/doc', str, re.M|re.I)
	if match:
		return True
	else:
		return False

#从每个页面提取出新浪站内link，返回一个link列表
def getLink(link):
	response = urllib2.urlopen(link)
	cont = response.read()
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
	f= open("E:/testNews/"+ str(num) +'.txt','w')
	response = urllib2.urlopen(link)
	cont = response.read()
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

	f.write('link:'+link+'\n')
	for t in title: 
		f.write('title:'+t+'\n')
	for d in date:
		f.write('date:'+d.replace('年','-').replace('月','-').replace('日','')+'\n')
	for s in source: 
		f.write('source:'+strFun(s)+'\n')
	for k in keyword: 
		f.write('keywords:'+k+'\n')	
	content.pop()
	content.pop()
	_str=''
	for c in content: 
		_str=_str+c
	f.write('content:'+'\n'+strFun(_str) +'\n')
	f.close()


linkToCrawl= ['http://news.sina.com.cn/']#该列表储存所有要爬还没爬的link
linkCrawled =[]#该列表储存已经爬取过的link，
num = 1

while linkToCrawl:
	linkCrawled.append(linkToCrawl[0])#取出第一个元素加入已爬取
	getLink(linkToCrawl[0])
	if isFinal(linkToCrawl[0]):
		getData(linkToCrawl[0],num)
		print num
		num = num + 1
	else:
		linkToCrawl.append(linkToCrawl[0])
	del linkToCrawl[0]	



	#res_link1 = r'<a target="_blank" href="http://news.sina.com.cn/(.*?)">.*?</a>'
	#link1 = re.findall(res_link1,sourceCont,re.S|re.M)
	#res_link2 = r'<a href="http://news.sina.com.cn/(.*?)" target="_blank">.*?</a>'
	#link2 = re.findall(res_link2,sourceCont,re.S|re.M)