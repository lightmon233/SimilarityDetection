package org.similaritydetection.backend.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.similaritydetection.backend.service.LevenshteinService;
import org.similaritydetection.backend.service.TongyiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CalculateSimilarity {
    @Autowired
    private LevenshteinService levenshteinService;;
    @Autowired
    private TongyiService tongyiService;

    public String calculateSimilarity(String code1, String code2, String method) {
        if (method.equals("levenshtein")) {
            return levenshteinService.calculateLevenshteinSimilarity(code1, code2);
        } else {
            return tongyiService.calculateTongyiSimilarity(code1, code2);
        }
    }
}
