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
    public static String calculateLevenshteinSimilarity(String code1, String code2) {
        // 获取 Levenshtein 距离
        int distance = calculateLevenshteinDistance(code1, code2);
        int maxLength = Math.max(code1.length(), code2.length());

        // 避免除以 0 的情况
        if (maxLength == 0) {
            return "1.0"; // 两个空字符串完全相同
        }

        // 计算相似性
        double similarity = 1.0 - (double) distance / maxLength;
        return Double.toString(similarity);
    }

    private static int calculateLevenshteinDistance(String code1, String code2) {
        int len1 = code1.length();
        int len2 = code2.length();

        // 创建动态规划表
        int[][] dp = new int[len1 + 1][len2 + 1];

        // 初始化 dp 表
        for (int i = 0; i <= len1; i++) {
            dp[i][0] = i;
        }
        for (int j = 0; j <= len2; j++) {
            dp[0][j] = j;
        }

        // 填充 dp 表
        for (int i = 1; i <= len1; i++) {
            for (int j = 1; j <= len2; j++) {
                int cost = (code1.charAt(i - 1) == code2.charAt(j - 1)) ? 0 : 1;
                dp[i][j] = Math.min(
                        Math.min(dp[i - 1][j] + 1, dp[i][j - 1] + 1), // 删除或插入
                        dp[i - 1][j - 1] + cost                       // 替换
                );
            }
        }

        return dp[len1][len2];
    }
}
