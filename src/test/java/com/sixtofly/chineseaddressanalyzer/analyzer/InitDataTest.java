package com.sixtofly.chineseaddressanalyzer.analyzer;

import cn.hutool.core.text.csv.CsvData;
import cn.hutool.core.text.csv.CsvReadConfig;
import cn.hutool.core.text.csv.CsvReader;
import cn.hutool.core.text.csv.CsvRow;
import com.sixtofly.chineseaddressanalyzer.entity.Address;
import com.sixtofly.chineseaddressanalyzer.mapper.AddressMapper;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.io.File;
import java.io.FileNotFoundException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

@SpringBootTest
class InitDataTest {

    @Resource
    private AddressMapper addressMapper;

    /**
     * 初始化数据库数据
     * @throws FileNotFoundException
     */
    @Test
    void initMysql() throws FileNotFoundException {
        File file = new File("src/main/resources/2020全国行政区划.csv");
        List<Address> lists = new ArrayList<>(3500);

        CsvReader reader = new CsvReader();

        CsvData data = reader.read(file, Charset.forName("GB2312"));
        Iterator<CsvRow> iterator = data.iterator();
        Address address = null;
        Date now = new Date();
        while (iterator.hasNext()) {
            CsvRow row = iterator.next();
            address = new Address();
            address.setId(Long.valueOf(row.get(0)));
            address.setName(row.get(1));
            address.setCode(row.get(2));
            address.setParentId(Long.valueOf(row.get(3)));
            address.setProvinceName(row.get(4));
            address.setProvinceCode(row.get(5));
            address.setCityName(row.get(6));
            address.setCityCode(row.get(7));
            address.setLevel(row.get(8));
            address.setGmtCreate(now);
            address.setGmtModified(now);
            lists.add(address);
        }
        int i = addressMapper.batchInsert(lists);
        System.out.println(i);
    }

}
