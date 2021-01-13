package com.sixtofly.chineseaddressanalyzer.analyzer;

import com.sixtofly.chineseaddressanalyzer.entity.Address;
import com.sixtofly.chineseaddressanalyzer.entity.dto.AddressAnalyzeParams;

import java.util.List;

/**
 * 地址信息数据源
 * @author xie yuan bing
 * @date 2020-04-02 17:11
 * 使用Lucene存储数据并检索
 * @see com.sixtofly.chineseaddressanalyzer.analyzer.impl.LuceneAddressDataSource
 * 使用MySQL数据库存储并检索
 * @see com.sixtofly.chineseaddressanalyzer.analyzer.impl.MysqlAddressDataSource
 * @see com.sixtofly.chineseaddressanalyzer.mapper.AddressMapper
 */
public interface AddressDataSource {

    /**
     * 根据名称前置模糊查询省市区
     * @param params
     * @return
     */
    List<Address> findByNameLike(AddressAnalyzeParams params);
}
