package com.sixtofly.chineseaddressanalyzer;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 基于Word分词插件实现的地址解析功能, 解析出地址的省市区和详细地址
 */
@SpringBootApplication
@MapperScan("com.sixtofly.chineseaddressanalyzer.mapper")
public class ChineseaddressanalyzerApplication {

    public static void main(String[] args) {
        SpringApplication.run(ChineseaddressanalyzerApplication.class, args);
    }

}
