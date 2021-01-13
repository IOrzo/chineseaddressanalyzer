package com.sixtofly.chineseaddressanalyzer.entity.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * 快递地址,包括名字和电话
 * @author xie yuan bing
 * @date 2020-05-14 08:49
 * @description
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ExpressAddressDto extends AddressDetailDto implements Serializable {

    private static final long serialVersionUID = -723332262053289879L;

    /**
     * 姓名
     */
    private String name;

    /**
     * 电话
     */
    private String phone;

    /**
     * 地址信息
     */
    private String address;
}
