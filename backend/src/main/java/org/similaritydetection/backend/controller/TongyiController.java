package org.similaritydetection.backend.controller;

import com.alibaba.cloud.ai.tongyi.audio.speech.TongYiAudioSpeechModel;
import com.alibaba.cloud.ai.tongyi.audio.speech.TongYiAudioSpeechOptions;
import com.alibaba.cloud.ai.tongyi.audio.speech.api.SpeechPrompt;
import com.alibaba.cloud.ai.tongyi.audio.speech.api.SpeechResponse;
import com.alibaba.cloud.ai.tongyi.audio.transcription.TongYiAudioTranscriptionModel;
import com.alibaba.cloud.ai.tongyi.audio.transcription.api.AudioTranscriptionPrompt;
import com.alibaba.cloud.ai.tongyi.audio.transcription.api.AudioTranscriptionResponse;
import com.alibaba.cloud.ai.tongyi.chat.TongYiChatModel;
import com.alibaba.cloud.ai.tongyi.image.TongYiImagesModel;
import com.alibaba.dashscope.audio.asr.transcription.Transcription;
import com.alibaba.dashscope.audio.tts.SpeechSynthesisAudioFormat;
import com.alibaba.dashscope.utils.JsonUtils;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.similaritydetection.backend.po.JsonData;
import jakarta.annotation.Resource;
import org.springframework.ai.image.ImageOptionsBuilder;
import org.springframework.ai.image.ImagePrompt;
import org.springframework.ai.image.ImageResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.UrlResource;
import org.springframework.web.bind.annotation.*;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.UUID;

@RestController
public class TongyiController {

    @Autowired
    private TongYiChatModel chatModel;

    @PostMapping("chat")
    public JsonData chat(@RequestBody Map<String, String> request) {
        String text1 = request.get("text1");
        String text2 = request.get("text2");

        JsonData data = null;
        try {
            // 调用 Tongyi AI 的 chat 模型，明确请求相似度值
            String chatResponse = chatModel.call("请你比较这两段代码的仅限于文字相似度，并返回给我一个0到1的浮点数来表示相似度，除了这个浮点数以外不要返回其他任何内容，比如这两段代码的相似度为0.56，也不要给我推荐算法，也不要重复我的输入，直接返回结果：" + text1 + " 和 " + text2);

            // 解析返回的相似度值
            double similarity = parseSimilarity(chatResponse);

            data = JsonData.ok(similarity);
        } catch (Exception ex) {
            data = JsonData.error(200, ex.getMessage());
        }
        return data;
    }

    private double parseSimilarity(String response) {
        try {
            // 假设返回的响应是一个 JSON 字符串，其中包含相似度值
            JsonObject jsonObject = JsonUtils.parse(response).getAsJsonObject();
            return jsonObject.get("similarity").getAsDouble();
        } catch (Exception e) {
            // 如果解析失败，返回默认值或抛出异常
            throw new RuntimeException("Failed to parse similarity from response: " + response, e);
        }
    }
    @Resource
    private TongYiImagesModel imagesModel;
    @GetMapping("image")
    public JsonData image(String key){
        JsonData data=null;
        try {
            ImageResponse call = imagesModel.call(new ImagePrompt(key, ImageOptionsBuilder.builder()
                    .withModel("wanx-v1")
                    .withN(1)
                    .withHeight(512)
                    .withWidth(512)
                    .build()));

           data=JsonData.ok(call.getResult().getOutput());


        }
        catch (Exception ex) {
            ex.printStackTrace();
            data=JsonData.error(300,ex.getMessage());
        }
    return  data;
    }
    @Resource
    private TongYiAudioSpeechModel speechModel;
    @GetMapping("speech")
    public JsonData speech(String key){
        JsonData data=null;
        try{
            FileOutputStream outputStream= null;
           File parent =new File("src/main/resources/static/audio");
           if(!parent.exists()){
               parent.mkdirs();
           }
            String fileName = UUID.randomUUID().toString()+".mp3";
           SpeechResponse call= speechModel.call(new SpeechPrompt(key, TongYiAudioSpeechOptions.builder()
                    .withModel("cosyvoice-v1")
                    .withFormat(SpeechSynthesisAudioFormat.MP3)
                    .build()));
           byte[] array =call.getResult().getOutput().array();
           outputStream = new FileOutputStream(new File(parent,fileName));
           outputStream.write(array);
           data = JsonData.ok(fileName);


        }
        catch (Exception ex){
            data=JsonData.error(300,ex.getMessage());
        }
        return data;
    }
    @Resource
    private TongYiAudioTranscriptionModel transcriptionModel;
    @GetMapping("trans")
    public JsonData trans(String key)
    {
        JsonData data=null;
        try{
            String decodedKey = URLDecoder.decode(key, StandardCharsets.UTF_8);
            UrlResource resource = new UrlResource(decodedKey);
            AudioTranscriptionResponse call = transcriptionModel.call(
                    new AudioTranscriptionPrompt(resource));
            URL url=new URL(call.getResult().getOutput());
            HttpURLConnection connection =
                    (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            StringBuilder sb = new StringBuilder();
            BufferedInputStream in = new BufferedInputStream(
                    connection.getInputStream());
            byte[] buffer = new byte[1024];
            int count;
            while ((count= in.read(buffer)) != -1) {
                sb.append(new String(buffer, 0, count));
            }
            JsonObject parse = JsonUtils.parse(sb.toString());
            JsonArray transcripts = parse.getAsJsonArray("transcripts");
            JsonElement jsonElement = transcripts.get(0);
            JsonElement text = jsonElement.getAsJsonObject().get("text");
            data=JsonData.ok(text.toString());
        }
        catch (Exception ex){
            ex.printStackTrace();
            data=JsonData.error(300,ex.getMessage());
        }
        return data;
    }
}
