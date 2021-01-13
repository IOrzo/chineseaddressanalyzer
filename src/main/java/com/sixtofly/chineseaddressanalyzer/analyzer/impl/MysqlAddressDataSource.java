package com.sixtofly.chineseaddressanalyzer.analyzer.impl;

import com.sixtofly.chineseaddressanalyzer.analyzer.AddressDataSource;
import com.sixtofly.chineseaddressanalyzer.entity.Address;
import com.sixtofly.chineseaddressanalyzer.entity.dto.AddressAnalyzeParams;
import com.sixtofly.chineseaddressanalyzer.mapper.AddressMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author xie yuan bing
 * @date 2021-01-13 12:04
 * @description
 */
@Service
public class MysqlAddressDataSource implements AddressDataSource {

    @Resource
    private AddressMapper addressMapper;

    @Override
    public List<Address> findByNameLike(AddressAnalyzeParams params) {
        return addressMapper.findByNameLike(params);
    }
}
