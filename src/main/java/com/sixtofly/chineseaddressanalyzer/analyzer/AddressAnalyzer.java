package com.sixtofly.chineseaddressanalyzer.analyzer;

import lombok.extern.slf4j.Slf4j;
import org.apdplat.word.WordSegmenter;
import org.apdplat.word.segmentation.SegmentationAlgorithm;
import org.apdplat.word.segmentation.Word;
import org.apdplat.word.segmentation.WordRefiner;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * 中文分词处理
 * @author xie yuan bing
 * @date 2020-03-27 10:54
 * @description
 */
@Slf4j
public class AddressAnalyzer {

    /**
     * 截取地址长度
     */
    private static final int SPLIT_ADDRESS_LEN = 3;

    /**
     * 分词
     * @param address
     * @return
     */
    public static List<String> analyze(String address){
        List<String> splits = new ArrayList<>();
        List<Word> words = WordRefiner.refine(WordSegmenter.seg(address, SegmentationAlgorithm.MaximumMatching));
        for (Word word : words) {
            splits.add(word.getText());
            if (splits.size() == SPLIT_ADDRESS_LEN) {
                break;
            }
        }
        if (splits.size() < SPLIT_ADDRESS_LEN) {
            while (true) {
                splits.add("填充");
                if (splits.size() == SPLIT_ADDRESS_LEN) {
                    break;
                }
            }
        }
        return splits;
    }

    /**
     * 无长度限制分词
     * @param address
     * @return
     */
    public static List<String> unlimitedAnalyze(String address) {
        List<String> splits = new LinkedList<>();
        List<Word> words = WordRefiner.refine(WordSegmenter.seg(address, SegmentationAlgorithm.MaximumMatching));
        for (Word word : words) {
            splits.add(word.getText());
        }
        return splits;
    }

}
