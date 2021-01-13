package com.sixtofly.chineseaddressanalyzer.entity.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 地址详情信息
 * @author xie yuan bing
 * @date 2020-03-23 11:04
 * @description
 */
@Data
@NoArgsConstructor
public class AddressDetailDto implements Serializable {


    private static final long serialVersionUID = 1368809736601269274L;

    /**
     * 省份名称
     */
    private String provinceName;

    /**
     * 省份代码
     */
    private String provinceNo;

    /**
     * 城市名称
     */
    private String cityName;

    /**
     * 城市代码
     */
    private String cityNo;

    /**
     * 区县名称
     */
    private String countyName;

    /**
     * 区县代码
     */
    private String countyNo;

    /**
     * 详情地址
     */
    private String detail;

    /**
     * 匹配器
     */
    private String regex;
}
