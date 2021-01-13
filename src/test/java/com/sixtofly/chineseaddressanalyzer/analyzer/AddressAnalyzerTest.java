package com.sixtofly.chineseaddressanalyzer.analyzer;

import com.sixtofly.chineseaddressanalyzer.service.AddressAnalyzeService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.*;

/**
 * @author xie yuan bing
 * @date 2020-03-27 11:07
 * @description
 */
@Slf4j
@SpringBootTest
class AddressAnalyzerTest {

    @Autowired
    private AddressAnalyzeService analyzeService;

    /**
     * 分词测试
     */
    @Test
    public void splits(){
        System.out.println(AddressAnalyzer.analyze("四川成都双流高新区姐儿堰路远大都市风景二期"));
        System.out.println(AddressAnalyzer.analyze("成都市温江永文路南端1688号西贵堂"));
        System.out.println(AddressAnalyzer.analyze("四川省巴中市北云台水韵名城2-21-5"));
        System.out.println(AddressAnalyzer.analyze("青海省南宁市城北区金帝花园"));
    }

    /**
     * 地址解析测试
     */
    @Test
    public void analyze(){
        System.out.println(analyzeService.parseAddress("四川成都双流高新区姐儿堰路远大都市风景二期"));
        System.out.println(analyzeService.parseAddress("新疆克拉玛依市拉玛依区油建南路雅典娜74-76"));
        System.out.println(analyzeService.parseAddress("江西省萍乡市经济开发区西区工业园（硖石）金丰路23号江西省萍乡联友建材有限公司"));
        System.out.println(analyzeService.parseAddress("甘肃省嘉峪关前进路车务段9号"));
        System.out.println(analyzeService.parseAddress("四川省新津县五津镇武阳中路167号2栋1单元6楼2号"));
        System.out.println(analyzeService.parseAddress("双流县海棠湾2栋2单元1102号"));
        System.out.println(analyzeService.parseAddress("成都市双流区航空港康桥品上2栋1单元1704室"));
        System.out.println(analyzeService.parseAddress("重庆市江北区南桥寺光华南桥人家2-3-1-1"));
    }

    /**
     * 地址解析测试
     * @throws IOException
     */
    @Test
    public void multiAnalyze() throws IOException {

        // 测试地址数据
        for (int i = 1; i < 4; i++) {
            parseAddress("src/test/resources/测试地址"+ i +".txt");
        }

        // 测试错误数据
        parseAddress("src/test/resources/error.txt");
    }

    private void parseAddress(String filePath) throws IOException {
        File file = new File(filePath);
        BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-8"));
        String address = null;
        while ((address = reader.readLine()) != null) {
            System.out.println(analyzeService.parseAddress(address));
        }
    }
}