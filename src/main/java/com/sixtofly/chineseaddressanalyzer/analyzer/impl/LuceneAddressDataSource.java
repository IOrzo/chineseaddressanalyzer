package com.sixtofly.chineseaddressanalyzer.analyzer.impl;

import com.sixtofly.chineseaddressanalyzer.analyzer.AddressDataSource;
import com.sixtofly.chineseaddressanalyzer.entity.Address;
import com.sixtofly.chineseaddressanalyzer.entity.dto.AddressAnalyzeParams;
import org.apache.commons.lang3.StringUtils;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

/**
 * @author xie yuan bing
 * @date 2020-04-02 17:14
 * @description
 */
@Primary
@Service
public class LuceneAddressDataSource implements AddressDataSource {

    @Override
    public List<Address> findByNameLike(AddressAnalyzeParams params) {
        List<Address> list = new LinkedList<>();
        Directory directory = getDirectory();
        try {
            DirectoryReader reader = DirectoryReader.open(directory);
            BooleanQuery query = new BooleanQuery();
            query.add(new PrefixQuery(new Term("name", params.getName())), BooleanClause.Occur.SHOULD);
            query.add(new TermQuery(new Term("alias", params.getName())), BooleanClause.Occur.SHOULD);
            QueryWrapperFilter filter = null;
            if (StringUtils.isNotBlank(params.getLevel())) {
                filter = new QueryWrapperFilter(new TermQuery(new Term("level", params.getLevel())));
            }
            IndexSearcher searcher = new IndexSearcher(reader);
            TopDocs hits = searcher.search(query, filter, 5);
            // 输出结果
            Address address = null;
            for (ScoreDoc scoreDoc : hits.scoreDocs) {
                Document doc = searcher.doc(scoreDoc.doc);
                address = new Address();
                address.setName(doc.get("name"));
                address.setCode(doc.get("code"));
                address.setProvinceName(doc.get("provinceName"));
                address.setProvinceCode(doc.get("provinceCode"));
                address.setCityName(doc.get("cityName"));
                address.setCityCode(doc.get("cityCode"));
                list.add(address);
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return list;
    }


    public Directory getDirectory() {
        String home = System.getProperty("user.home");
        File file = new File(home + "/chineseaddressanalyzer");
        if (!file.exists()) {
            if(!file.mkdir()) {
                throw new RuntimeException("创建数据文件失败");
            }
        }

        Directory directory = null;
        try {
            directory = FSDirectory.open(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return directory;
    }
}
