package com.sixtofly.chineseaddressanalyzer.init;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.sixtofly.chineseaddressanalyzer.analyzer.impl.LuceneAddressDataSource;
import com.sixtofly.chineseaddressanalyzer.entity.Address;
import com.sixtofly.chineseaddressanalyzer.mapper.AddressMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.util.Version;
import org.apdplat.word.lucene.ChineseWordAnalyzer;
import org.apdplat.word.segmentation.SegmentationAlgorithm;
import org.apdplat.word.segmentation.SegmentationFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.List;

/**
 * @author xie yuan bing
 * @date 2020-04-02 15:10
 * @description
 */
@Slf4j
@Component
public class LuceneDatasourceInitializer implements ApplicationRunner {

    @Resource
    private AddressMapper addressMapper;

    @Resource
    private LuceneAddressDataSource luceneAddressDataSource;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        log.info("---------加载地址解析所需地址数据---------");
        init();
        log.info("---------地址解析数据加载完成---------");
    }


    /**
     * 把数据库中的文件加载到本地文件中, 建立索引
     */
    private void init() {
        Analyzer analyzer = new ChineseWordAnalyzer(SegmentationFactory.getSegmentation(SegmentationAlgorithm.MaximumMatching));
        IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_47, analyzer);
        config.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
        IndexWriter writer = null;
        Directory directory = luceneAddressDataSource.getDirectory();
        try {
            writer = new IndexWriter(directory, config);
        } catch (IOException e) {
            e.printStackTrace();
        }
        List<Address> list = addressMapper.selectList(null);
        Document document = null;
        for (Address address : list) {
            document = new Document();
            document.add(new TextField("name", address.getName(), Field.Store.YES));
            document.add(new TextField("alias", ObjectUtil.defaultIfNull(address.getAlias(), StringUtils.EMPTY), Field.Store.YES));
            document.add(new StringField("code", address.getCode(), Field.Store.YES));
            document.add(new StringField("provinceName", address.getProvinceName(), Field.Store.YES));
            document.add(new StringField("provinceCode", address.getProvinceCode(), Field.Store.YES));
            document.add(new StringField("cityName", ObjectUtil.defaultIfNull(address.getCityName(), StringUtils.EMPTY), Field.Store.YES));
            document.add(new StringField("cityCode", ObjectUtil.defaultIfNull(address.getCode(), StringUtils.EMPTY), Field.Store.YES));
            document.add(new TextField("level", address.getLevel(), Field.Store.YES));
            try {
                writer.addDocument(document);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            writer.commit();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            directory.close();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}
