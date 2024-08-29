package org.similaritydetection.backend.controller;

import org.similaritydetection.backend.utils.CalculateSimilarity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

import static org.similaritydetection.backend.utils.CalculateSimilarity.calculateSimilarity;

@RestController
public class TextController {
    @PostMapping("/submitText")
    public double submitText(@RequestBody Map<String, String> payload) {
        // 处理接收到的数据
        String text = payload.get("text");
        String text2 = payload.get("text2");
        System.out.println(text);
        System.out.println(text2);
        double similarity = calculateSimilarity(text, text2);
        System.out.println(similarity);
        return similarity;
    }
}
