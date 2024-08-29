package org.similaritydetection.backend.utils;

import org.apache.commons.text.similarity.LevenshteinDistance;

public class CalculateSimilarity {
    public static double calculateSimilarity(String code1, String code2) {
        LevenshteinDistance levenshtein = new LevenshteinDistance();
        int distance = levenshtein.apply(code1, code2);
        int maxLength = Math.max(code1.length(), code2.length());
        // 相似度定义为 1 - 距离/两个字符串的最大长度
        return 1.0 - (double) distance / maxLength;
    }
}
