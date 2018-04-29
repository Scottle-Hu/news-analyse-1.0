from selenium import webdriver

b2=webdriver.PhantomJS()
b2.get("https://weibo.com/u/6489739255")

while True:
	if ('W_icon icon_pf_female' in b2.page_source) or ('W_icon icon_pf_male' in b2.page_source):
		break
	else:
		b2.implicitly_wait(10)  #待解析元素没有加载出来就一直等待

# print(b.page_source)

if 'W_icon icon_pf_female' in b2.page_source:
	print("用户性别 ： 女")
else:
	print("用户性别 ： 男")
#关注量、粉丝数、微博数
src=b2.page_source[0:b2.page_source.rfind("W_f18")+10]
while src.find('W_f18')!=-1:
	index_start=src.find(">",src.find('W_f18'))+1
	index_end=src.find("<\/",index_start)
	print(src[index_start:index_end])
	src=src[src.find('W_f18')+1:]