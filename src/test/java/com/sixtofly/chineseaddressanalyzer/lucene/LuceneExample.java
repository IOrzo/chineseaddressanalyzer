package com.sixtofly.chineseaddressanalyzer.lucene;

import org.apache.commons.lang3.StringUtils;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.apdplat.word.lucene.ChineseWordAnalyzer;
import org.apdplat.word.segmentation.SegmentationAlgorithm;
import org.apdplat.word.segmentation.SegmentationFactory;

import java.io.File;
import java.io.IOException;

/**
 * Lucene案例
 * @author xie yuan bing
 * @date 2020-04-16 16:43
 * @description
 */
public class LuceneExample {

    /**
     * 初始化数据
     */
    public void initData() {
        // 分词插件
        Analyzer analyzer = new ChineseWordAnalyzer(SegmentationFactory.getSegmentation(SegmentationAlgorithm.MaximumMatching));
        IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_47, analyzer);
        config.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
        IndexWriter writer = null;
        Directory directory = getDirectory();
        try {
            writer = new IndexWriter(directory, config);
        } catch (IOException e) {
            e.printStackTrace();
        }

        Document document = null;
        // 创建数据
        document = new Document();
        document.add(new TextField("name", "锦江区", Field.Store.YES));
        document.add(new TextField("alias", StringUtils.EMPTY, Field.Store.YES));
        document.add(new StringField("code", "510104", Field.Store.YES));
        document.add(new StringField("provinceName", "四川省", Field.Store.YES));
        document.add(new StringField("provinceCode", "510000", Field.Store.YES));
        document.add(new StringField("cityName", "成都市", Field.Store.YES));
        document.add(new StringField("cityCode", "510100", Field.Store.YES));
        document.add(new TextField("level", "4", Field.Store.YES));
        try {
            writer.addDocument(document);
        } catch (IOException e) {
            e.printStackTrace();
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


    public void query(){
        query("凉山", "2");
    }

    /**
     * 查询
     * @param name
     * @param level
     */
    public void query(String name, String level) {
        Directory directory = getDirectory();
        try {
            DirectoryReader reader = DirectoryReader.open(directory);
//            TermQuery query = new TermQuery(new Term("name", name));
//            MultiPhraseQuery query = new MultiPhraseQuery();
            // and 查询
            BooleanQuery query = new BooleanQuery();
            // 前缀查询
            query.add(new PrefixQuery(new Term("name", name)), BooleanClause.Occur.SHOULD);
            // 精准查询
            query.add(new TermQuery(new Term("alias", name)), BooleanClause.Occur.SHOULD);
            // 查询过滤
            QueryWrapperFilter filter = new QueryWrapperFilter(new TermQuery(new Term("level", level)));
            IndexSearcher searcher = new IndexSearcher(reader);
            // 查询结果
            TopDocs hits = searcher.search(query, filter, 5);
            System.out.println("命中：" + hits.totalHits);
            System.out.println("最高得分：" + hits.getMaxScore());
            // 输出结果
            for (ScoreDoc scoreDoc : hits.scoreDocs) {
                Document doc = searcher.doc(scoreDoc.doc);
                System.out.println(doc);
                System.out.println("得分：" + scoreDoc.score + "\tname："
                        + doc.get("name") + "\tcode：" + doc.get("code"));
            }
            reader.close();
            directory.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * 获取数据目录
     * @return
     */
    public Directory getDirectory() {
        File file = new File("F:\\temp\\lucene\\address");
        Directory directory = null;
        try {
            directory = FSDirectory.open(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return directory;
    }
}
