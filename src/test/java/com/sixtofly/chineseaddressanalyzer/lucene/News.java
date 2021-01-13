package com.sixtofly.chineseaddressanalyzer.lucene;

import lombok.Data;

/**
 * @author xie yuan bing
 * @date 2020-04-02 10:51
 * @description
 */
@Data
public class News {
    private int id;//新闻id
    private String title;//新闻标题
    private String content;//新闻内容
    private int reply;//评论数
}
