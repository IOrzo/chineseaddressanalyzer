package com.sixtofly.chineseaddressanalyzer.entity.dto;

import lombok.Data;

/**
 * @author xie yuan bing
 * @date 2020-03-27 15:19
 * @description
 */
@Data
public class AddressAnalyzeParams {

    /**
     * 地址名称
     */
    private String name;

    /**
     * 地址级别
     */
    private String level;
}
