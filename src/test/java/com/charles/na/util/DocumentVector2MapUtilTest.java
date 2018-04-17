package com.charles.na.util;

import com.charles.na.model.DocumentVector;
import com.charles.na.utils.DocumentVector2MapUtil;
import org.junit.Test;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by huqj on 2018/4/17.
 */
public class DocumentVector2MapUtilTest {
    String testStr = "(正式:1 用水:1 滚筒洗衣机:1 占据:2 62675595:1 " +
            "公司:1 1462:1 专家:2 冰箱:2 家电产品:1 展览:1 仅为:1 惊讶:1 gowatch:1 怡:1 " +
            "智能化:1 性:1 举办:1 咨询:1 家电:2 很难:1 致辞:1 批评指正:1 销售:2 市场规模:1 " +
            "巨头:2 展出:1 整体:1 展:5 解释:1 首次:3 发布:1 表现:1 产业:1 标签:1 第一:1 " +
            "常客:1 a:1 董:1 u:2 国际化:2 梁:1 地方:1 weimi:1 欧盟:2 为例:1 溢价:1 制造:3 " +
            "通行证:1 技术:3 信息:1 抗衡:1 2000:1 增长:5 更多:1 applehomekit:2 答疑:1 家电行业:2 电子:4 " +
            "来往:1 点:1 美的:1 h8800:1 消费:5 只占:1 收缩:1 铂:1 都是:1 事实上:1 空调:4 " +
            "长虹:1 集团:1 招聘:1 空气:1 消费者:1 参展:3 认可:2 开发:1 零售额:1 事业部:1 众多:3 一代:1 " +
            "中兴:1 抢占:1 2015:2 2014:1 865:1 总量:1 动力:1 金额:1 梦想:1 下一代:1 模式:6 日前:1 提高:1 " +
            "现状:2 委员:1 qled2:1 外:1 生:3 出口:3 4461:1 早已:1 历史:1 洪:2 风向标:1 上市:1 进军:1 " +
            "laiwang:1 以往:1 贴:2 总面积:1 电:1 贵:1 服务:1 sinaenglish:1 悄然:1 三星:5 uled:1 电话:1 " +
            "镇:1 强攻:6 欧美:13 份额:1 公司版权:1 2293:1 敏:1 留言板:1 预测:1 集体:1 低价:1 000100:1 " +
            "奥:2 生产:1 这是:1 国内市场:4 浪:3 营销:1 仪式:1 联网:1 中国企业:8 市场:21 加速:1 海:8 " +
            "未来:2 提升:1 投入:1 俱乐部:1 com:1 自主:1 现场:1 观众:1 馆:2 打好:1 当年:1 重视:2 斌:2 " +
            "展区:1 首:2 www:2 快速:2 me:1 我要:1 阻碍:1 广告:1 品牌:10 协会:1 过程中:1 柏林:1 网络:1 " +
            "差距:1 摆脱:1 下降:3 注册:1 会长:1 愿望:1 新:3 放眼:1 改变:2 质量:1 更是:2 这也:1 里:1 " +
            "智能:5 量:1 超级:1 多家:1 机电产品:1 突破:1 资源:1 关注:2 日:3 难以置信:1 生在:1 门:1 " +
            "总经理:1 差:1 代表:2 称:1 壮大:1 程度:1 发现:1 时:2 亮相:2 一倍:1 清:1 万:2 师:1 董事长:1 " +
            "描述:1 350:1 厂商:1 goplay:1 去年:1 商报:3 地位:1 业:1 最外:1 创:1 打上:1 东:4 智慧:3 " +
            "国内:5 14:1 得以:1 德国:1 中:1 下滑:2 防:1 身影:1 船:6 主:1 20:1 分析:2 记者:3 牌:2 " +
            "相比之下:1 鹏:1 010:1 数量:2 还会:1 尚未:1 30:2 电子产品:1 势头:1 进出口商会:1 良性循环:1 " +
            "器:1 亿元:3 副:1 海外:2 年:4 中国家电:12 也都:1 速度:1 中国:11 北京:3 希望:1 趋势:1 微米:1 " +
            "开馆:1 云:2 本届:6 销往:2 很大:1 下半年:1 销售收入:1 走出:2 50:1)";

    @Test
    public void testD2M() {
        DocumentVector dv = new DocumentVector();
        dv.setVector(testStr);
        Map<String, Double> map = DocumentVector2MapUtil.convertDocumentVector2Map(dv);
        System.out.println(map);
    }

    @Test
    public void testFillVector() {
        Map<String, Double> map = new HashMap<String, Double>();
        map.put("你好", 1.0);
        map.put("再见", 4.2);
        map.put("hello", 3.5);
        HashSet<String> words = new HashSet<String>();
        words.add("你好");
        words.add("再见");
        words.add("hello");
        words.add("拜拜");
        DocumentVector2MapUtil.fillVector(map, words);
        System.out.println(map);
    }

    @Test
    public void testCalCenter() {
        Set<Map<String, Double>> set = new HashSet<Map<String, Double>>();
        set.add(new HashMap<String, Double>() {{
            this.put("你好", 2.0);
            this.put("再见", 1.0);
            this.put("hello", 2.0);
        }});
        set.add(new HashMap<String, Double>() {{
            this.put("你好", 1.0);
            this.put("再见", 3.5);
            this.put("hello", 1.5);
        }});
        set.add(new HashMap<String, Double>() {{
            this.put("你好", 1.0);
            this.put("再见", 3.0);
            this.put("hello", 1.5);
        }});
        Map<String, Double> res = DocumentVector2MapUtil.calCenterOfVectors(set);
        System.out.println(res);
    }

    @Test
    public void testM2DV() {
        Map<String, Double> map = new HashMap<String, Double>();
        map.put("你好", 2.0);
        map.put("再见", 1.0);
        map.put("hello", 0.0);
        map.put("test", 0.1);
        DocumentVector res = DocumentVector2MapUtil.convertMap2DocumentVector(map);
        System.out.println(res.getVector());
    }
}
