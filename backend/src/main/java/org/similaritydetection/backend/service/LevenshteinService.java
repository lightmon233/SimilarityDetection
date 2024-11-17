package org.similaritydetection.backend.service;

import org.apache.commons.text.similarity.LevenshteinDistance;
import org.springframework.stereotype.Service;

@Service
public class LevenshteinService {
    /**
     * 使用 Levenshtein 距离计算相似度
     *
     * @param code1 第一个字符串
     * @param code2 第二个字符串
     * @return 相似度值，范围 [0, 1]
     */
    public String calculateLevenshteinSimilarity(String code1, String code2) {
        LevenshteinDistance levenshtein = new LevenshteinDistance();
        int distance = levenshtein.apply(code1, code2);
        int maxLength = Math.max(code1.length(), code2.length());
        // 避免除以 0 的情况
        if (maxLength == 0) {
            return "1.0"; // 两个空字符串完全相同
        }
        double similarity = 1.0 - (double) distance / maxLength;
        return Double.toString(similarity);
    }
}
