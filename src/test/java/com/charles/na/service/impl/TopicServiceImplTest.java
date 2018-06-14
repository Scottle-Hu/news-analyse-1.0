package com.charles.na.service.impl;

import com.charles.na.service.ITopicService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.*;

/**
 * @author huqj
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:applicationContext.xml"})
public class TopicServiceImplTest {

    private String text = "　　新浪手机讯3月1日上午消息，刚刚迈入2018年的第三个月，但距离苹果的“春季发布会”的脚步是越来越近。尽管去年的“春季发布会”，一款红色iPhone7和各色表带就把我们给打发了，可是对于今年却仍抱有一线希望，因为今年传说的东西太多了。现在我们一起来看看今年他们可能会推出哪些新产品吧。　　iPhone　　在手机方面，根据分析师郭明琪的预测结果来看，苹果今年可能将在秋季发布三款新iPhone：5.8英寸的新iPhoneX，6.1英寸的新iPhone（接替iPhone8），还有一款6.5英寸的iPhone，很有可能是iPhoneXPlus。　　6.1英寸的新iPhone预计将保留旧显示技术，但是屏幕将变成全面屏。液晶显示屏可以有助于降低成本，苹果还可能会把它设计成后置单摄，然后再取消3DTouch，这样的话，这款机子的价格就只有700-800美元。至于苹果会给它起一个什么名字我们现在还完全不知道，但是它可能会因为拥有全面屏、FaceID，但价格还很便宜的优势受到欢迎。　　至于iPhoneXPlus，它的屏幕分辨率可能是1241x2699，而且将会支持双卡双待。在颜色方面新款iPhone和iPhoneXPlus应该都会加入金色选项。　　iPhoneSE二代　　苹果前两年推出了iPhoneSE，之后只做过一次升级容量的更新，就再也没消息了。苹果总在春季发布价格比较低的新产品，所以关于二代到底会不会出，我们将会在今年的三月至六月知道答案。　　iPad　　苹果可能将会在3月份更新iPad阵容，但是可能仅限于“入门级”的一些型号。去年的这个时候苹果发布了一款9.7英寸的iPad，售价329美元，但在此之前没有做过任何介绍。现在根据监管部门提供的文件来看，没准过几天他们又将推出新的iPad了。文件里出现了两款新iPad的型号，但应该只是Wi-Fi和LTE版本。　　iPadPro　　说完了入门级产品，再来看看iPadPro。据外媒预测苹果将会在今年秋季的时候推出两款新型iPadPro，分别为10.5英寸和12.9英寸。将会采用全面屏设计，支持FaceID并且取消掉home键。　　但是苹果去年6月才刚刚更新完iPadPro产品线，所以不知道今年会不会出同样的更新。关于ApplePencil，苹果在今年也将作出升级。　　AppleWatch　　从短期来看，AppleWatch在春季可能推出新的表带颜色。在秋季可能会推出AppleWatchSeries4，这款新表将会在睡眠、内置扬声器和电池上做出一些更新。从第一款表到现在，他们不断的在更新着GPS、LTE和显示屏等方面的功能，在外观上没有做出什么特别的改变。　　Mac　　关于Mac，苹果可能会推出模块化系统的MacPro，他们想要出一款性能超强的MacPro，虽然目前还没有准确的发布日期，但是没准我们可以在今年6月的WWDC大会上看到它的预告片然后在12月份看到新发布的产品。　　还有传言称他们正在研发三款配有特殊处理器的新Mac还会推一个入门级别的13寸macbook。苹果在去年12月份推出了iMacPro，6月份推出了新的Macbook、MacbookPro和iMac，所以有可能下线掉已经老化的Macmini和MacbookAir。　　AirPower　　苹果将会在今年推出无线充电器，AirPower。它可以同时给三款设备进行充电，iPhone8、AppleWatchSeries3几AirPods等设备。因为目前的充电盒只能通过用Lightning线进行充电，所以苹果可能还会推出一款可用AirPower充电的AirPods盒。　　AirPods2　　根据传言称，第二代AirPods不仅会推出一款新的充电盒，耳机本身可能也会做出处理器的升级。目前这个版本的AirPods必须要双击耳机来激活Siri，新版可能会支持用语音直接来唤醒Siri。　　最近更有传闻称苹果正在研发一款新型入耳式耳机，将采用AirPods技术，而不和beats沾上边了。这些消息目前都没有得到官方的证实，而名字现在叫什么的都有，所以他们应该也会取个新的吧。　　HomePod　　苹果2月份发布的HomePod和即将推出新耳机将会让苹果音频产品更上一个台阶。他们本是打算在12月份发布HomePod，但是后来改了日子，所以今年应该是不会出新的HomePod了。和AppleTV一样，可能还要在等几年我们才能看到它的更新。（李璐）AllRightsReserved新浪公司版权所有</a>";

    @Autowired
    private ITopicService topicService;

    @Test
    public void test01() {
        System.out.println("关键词短语提取结果：\n" + topicService.abstractKeywords(text));
    }
}