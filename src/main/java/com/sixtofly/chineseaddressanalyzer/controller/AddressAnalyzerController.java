package com.sixtofly.chineseaddressanalyzer.controller;

import com.sixtofly.chineseaddressanalyzer.entity.dto.AddressDetailDto;
import com.sixtofly.chineseaddressanalyzer.entity.dto.ExpressAddressDto;
import com.sixtofly.chineseaddressanalyzer.service.AddressAnalyzeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author xie yuan bing
 * @date 2021-01-12 17:21
 * @description
 */
@RestController
@RequestMapping("/address/analyzer")
public class AddressAnalyzerController {

    @Autowired
    private AddressAnalyzeService addressAnalyzeService;

    /**
     * 解析地址
     * @param address
     * @return
     */
    @RequestMapping("/simple")
    public AddressDetailDto simple(String address) {
        return addressAnalyzeService.parseAddress(address);
    }

    /**
     * 解析快递地址，包含姓名，电话，地址
     * 最好的解析结果是电话在中间
     * 若姓名地址在一起, 并且地址在前姓名在后, 则无法正确解析出姓名
     * @param address
     * @return
     */
    @RequestMapping("/expressAddress")
    public ExpressAddressDto expressAddress(String address) {
        return addressAnalyzeService.parseExpressAddress(address);
    }
}
