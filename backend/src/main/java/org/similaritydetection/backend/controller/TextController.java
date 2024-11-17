package org.similaritydetection.backend.controller;

import org.similaritydetection.backend.utils.CalculateSimilarity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

import org.similaritydetection.backend.utils.CalculateSimilarity;

@RestController
public class TextController {
    @Autowired
    private CalculateSimilarity calculateSimilarity;

    @PostMapping("/submitText")
    public String submitText(@RequestBody Map<String, String> payload) {
        // 处理接收到的数据
        String text = payload.get("text");
        String text2 = payload.get("text2");
        String method = payload.get("method");
        System.out.println(text);
        System.out.println(text2);
        System.out.println(method);
        String similarity = calculateSimilarity.calculateSimilarity(text, text2, method);
        System.out.println(similarity);
        return similarity;
    }
}
