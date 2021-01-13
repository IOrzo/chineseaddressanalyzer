package com.sixtofly.chineseaddressanalyzer.analyzer;

import com.sixtofly.chineseaddressanalyzer.service.AddressAnalyzeService;
import lombok.extern.slf4j.Slf4j;
import org.apdplat.word.WordSegmenter;
import org.apdplat.word.segmentation.SegmentationAlgorithm;
import org.apdplat.word.segmentation.WordRefiner;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.*;

/**
 * @author xie yuan bing
 * @date 2020-03-30 15:42
 * @description
 */
@Slf4j
@SpringBootTest
public class WordAddressAnalyzerTest {

    @Autowired
    private AddressAnalyzeService analyzeService;

    @Test
    public void analyzeFile() throws IOException {
        File file = new File("src/test/resources/测试地址1.txt");
        BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-8"));
        String address = null;
        while ((address = reader.readLine()) != null) {
            System.err.println(WordSegmenter.seg(address, SegmentationAlgorithm.MaximumMatching));
        }
    }
//

    @Test
    public void analyze(){
//        System.out.println(analyzeService.parseAddress("四川成都双流高新区姐儿堰路远大都市风景二期"));
//        System.out.println(analyzeService.parseAddress("新疆克拉玛依市拉玛依区油建南路雅典娜74-76"));
//        System.out.println(analyzeService.parseAddress("江西省萍乡市经济开发区西区工业园（硖石）金丰路23号江西省萍乡联友建材有限公司"));
//        System.out.println(analyzeService.parseAddress("甘肃省嘉峪关前进路车务段9号"));
//        System.out.println(analyzeService.parseAddress("四川省新津县五津镇武阳中路167号2栋1单元6楼2号"));
//        System.out.println(analyzeService.parseAddress("双流县海棠湾2栋2单元1102号"));
//        System.out.println(analyzeService.parseAddress("成都市双流区航空港康桥品上2栋1单元1704室"));
//        System.out.println(analyzeService.parseAddress("四川双流县华阳古城12组"));
//        System.out.println(analyzeService.parseAddress("新津县希望东路99号新筑巴伦西亚6-1-505"));
        System.out.println(analyzeService.parseAddress("北京市北京市丰台区北京东路118号"));
        System.out.println(analyzeService.parseAddress("上海市上海市浦东新区张江镇孙环路177弄"));
        System.out.println(analyzeService.parseAddress("澳门特别行政区澳门半岛"));
        System.out.println(analyzeService.parseAddress("成都市新都镇蜀龙大道中段619号"));
    }

    @Test
    public void multiAnalyze() throws IOException {
        File file = new File("src/test/resources/测试地址1.txt");
        BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-8"));
        String address = null;
        while ((address = reader.readLine()) != null) {
            System.out.println(analyzeService.parseAddress(address));
        }
    }

    /**
     * 测试带有姓名电话的地址
     */
    @Test
    public void parseExpressAddress() {
//        System.out.println(analyzeService.parseExpressAddress("张三13000000000四川省成都市新都区汇融广场"));
//        System.out.println(analyzeService.parseExpressAddress("张三13000000000澳门特别行政区澳门半岛"));
//        System.out.println(analyzeService.parseExpressAddress("张三13000000000北京市辖区丰台区北京东路118号"));
//        System.out.println(analyzeService.parseExpressAddress("张三呀呀13000000001新疆维吾尔自治区乌鲁木齐市天山区赵家街"));

        System.out.println(analyzeService.parseExpressAddress("四川省乐山市峨眉山市佛欣路19号蒙太奇硅藻泥艺术涂装028-88888888张三呀呀"));
        System.out.println(analyzeService.parseExpressAddress("四川省乐山市峨眉山市佛欣路19号蒙太奇硅藻泥艺术涂装(028)88888888张三呀呀"));
        System.out.println(analyzeService.parseExpressAddress("张三呀呀13000000001四川省乐山市峨眉山市佛欣路19号蒙太奇硅藻泥艺术涂装"));
        System.out.println(analyzeService.parseExpressAddress("张三呀呀四川省乐山市峨眉山市佛欣路19号蒙太奇硅藻泥艺术涂装13000000001"));
        System.out.println(analyzeService.parseExpressAddress("13000000001张三呀呀四川省乐山市峨眉山市佛欣路19号蒙太奇硅藻泥艺术涂装"));
        System.out.println(analyzeService.parseExpressAddress("13000000001四川省乐山市峨眉山市佛欣路19号蒙太奇硅藻泥艺术涂装张三呀呀"));
        System.out.println(analyzeService.parseExpressAddress("四川省乐山市峨眉山市佛欣路19号蒙太奇硅藻泥艺术涂装张三呀呀13000000001"));
        System.out.println(analyzeService.parseExpressAddress("四川省乐山市峨眉山市佛欣路19号蒙太奇硅藻泥艺术涂装13000000001张三呀呀"));
        System.out.println(analyzeService.parseExpressAddress("四川省乐山市峨眉山市佛欣路19号蒙太奇硅藻泥艺术涂装张三呀呀13000000001"));
    }

    public static void main(String[] args) {
//        System.out.println(WordSegmenter.seg("四川成都双流高新区姐儿堰路远大都市风景二期"));
        System.err.println(WordSegmenter.seg("成都市温江永文路南端1688号西贵堂", SegmentationAlgorithm.MaximumMatching));
        System.err.println(WordSegmenter.seg("成都市新都镇蜀龙大道中段619号", SegmentationAlgorithm.MaximumMatching));
        System.err.println(WordSegmenter.seg("四川省巴中市北云台水韵名城2-21-5"));
        System.err.println(WordSegmenter.seg("青海省南宁市城北区金帝花园"));
        System.err.println(WordRefiner.refine(WordSegmenter.seg("上海市浦东新区张江镇孙环路177弄")));
        System.err.println(WordSegmenter.seg("上海市上海市崇明区张江镇孙环路177弄"));
        System.err.println(WordSegmenter.seg("上海市上海市松江区张江镇孙环路177弄"));
        System.err.println(WordSegmenter.seg("上海市上海市静安区张江镇孙环路177弄"));
        System.err.println(WordSegmenter.seg("上海市上海市徐汇区张江镇孙环路177弄"));
        System.err.println(WordSegmenter.seg("上海市上海市闵行区张江镇孙环路177弄"));
        System.err.println(WordSegmenter.seg("上海市上海市嘉定区张江镇孙环路177弄"));
        System.err.println(WordSegmenter.seg("上海市上海市宝山区张江镇孙环路177弄"));
        System.err.println(WordSegmenter.seg("上海市上海市杨浦区张江镇孙环路177弄"));

//        青浦区普陀区金山区奉贤区长宁区黄浦区虹口区
    }

    public void demo(){
        // 默认使用方式
        System.out.println(WordSegmenter.seg("江西省萍乡市经济开发区西区工业园"));
        /**
         * 指定分词算法
         * SegmentationAlgorithm的可选类型为：
         * 正向最大匹配算法：MaximumMatching
         * 逆向最大匹配算法：ReverseMaximumMatching
         * 正向最小匹配算法：MinimumMatching
         * 逆向最小匹配算法：ReverseMinimumMatching
         * 双向最大匹配算法：BidirectionalMaximumMatching
         * 双向最小匹配算法：BidirectionalMinimumMatching
         * 双向最大最小匹配算法：BidirectionalMaximumMinimumMatching
         * 全切分算法：FullSegmentation
         * 最少分词算法：MinimalWordCount
         * 最大Ngram分值算法：MaxNgramScore
         */
        System.out.println(WordSegmenter.seg("青海省南宁市城北区金帝花园", SegmentationAlgorithm.MaximumMatching));

        /**
         * 编写word_refine.txt配置文件
         * 对分词过后的词进行拆分或者合并
         */
        System.out.println(WordRefiner.refine(WordSegmenter.seg("上海市浦东新区张江镇孙环路")));

    }
}
