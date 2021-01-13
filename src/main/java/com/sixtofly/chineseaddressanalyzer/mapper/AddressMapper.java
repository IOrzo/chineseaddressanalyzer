package com.sixtofly.chineseaddressanalyzer.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sixtofly.chineseaddressanalyzer.analyzer.AddressDataSource;
import com.sixtofly.chineseaddressanalyzer.entity.Address;
import com.sixtofly.chineseaddressanalyzer.entity.dto.AddressAnalyzeParams;

import java.util.List;

/**
 * @author xie yuan bing
 * @date 2020-03-27 15:03
 * @description
 */
public interface AddressMapper extends BaseMapper<Address> {

    List<Address> findByNameLike(AddressAnalyzeParams params);

    int batchInsert(List<Address> data);
}
