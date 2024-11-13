package org.similaritydetection.backend.controller;

import com.alibaba.cloud.ai.tongyi.chat.TongYiChatModel;
import com.alibaba.dashscope.aigc.generation.Generation;
import com.alibaba.dashscope.aigc.generation.GenerationParam;
import com.alibaba.dashscope.aigc.generation.GenerationResult;
import com.alibaba.dashscope.aigc.multimodalconversation.MultiModalConversation;
import com.alibaba.dashscope.aigc.multimodalconversation.MultiModalConversationParam;
import com.alibaba.dashscope.aigc.multimodalconversation.MultiModalConversationResult;
import com.alibaba.dashscope.audio.ttsv2.SpeechSynthesisParam;
import com.alibaba.dashscope.audio.ttsv2.SpeechSynthesizer;
import com.alibaba.dashscope.audio.ttsv2.enrollment.Voice;
import com.alibaba.dashscope.audio.ttsv2.enrollment.VoiceEnrollmentService;
import com.alibaba.dashscope.common.Message;
import com.alibaba.dashscope.common.MultiModalMessage;
import com.alibaba.dashscope.common.Role;
import org.similaritydetection.backend.po.JsonData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.FileOutputStream;
import java.net.URLDecoder;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.UUID;


@RestController
public class DashScopeController {

    @Autowired
    private TongYiChatModel chatModel;

    @PostMapping("dashchat")
    public JsonData chat(@RequestBody Map<String, String> request) {
        String text1 = request.get("text1");
        String text2 = request.get("text2");

        JsonData data = null;
        try {
            Generation gen = new Generation();
            Message systemMsg = Message.builder()
                    .role(Role.SYSTEM.getValue())
                    .content("You are a helpful assistant.").build();
            Message userMsg = Message.builder()
                    .role(Role.USER.getValue())
                    .content("请你比较这两段代码的仅限于文字相似度，并返回给我一个0到1的浮点数来表示相似度，除了这个浮点数以外不要返回其他任何内容，比如这两段代码的相似度为0.56，也不要给我推荐算法，也不要重复我的输入，直接返回结果："+text1+text2)
                    .build();
            GenerationParam param = GenerationParam.builder()
                    .apiKey(System.getenv("DASHSCOPE_API_KEY"))
                    .model("qwen-max")
                    .messages(Arrays.asList(systemMsg, userMsg))
                    .resultFormat(GenerationParam.ResultFormat.MESSAGE)
                    .build();


            GenerationResult result = gen.call(param);

            // 解析返回的相似度值
            double similarity = parseSimilarity(result);

            // 构建返回数据
            data = JsonData.ok(similarity);
        } catch (Exception ex) {
            data = JsonData.error(300, ex.getMessage());
        }
        return data;
    }

    private double parseSimilarity(GenerationResult response) {
        try {
            // 假设返回的响应是一个 JSON 对象
            String content = response.getOutput().getChoices().get(0).getMessage().getContent();
            return Double.parseDouble(content.trim());
        } catch (NumberFormatException e) {
            // 如果解析失败，抛出异常
            throw new RuntimeException("Failed to parse similarity from response: " + response, e);
        }
    }


    private double parseSimilarity(String response) {
        try {
            // 假设返回的响应是一个简单的浮点数字符串
            return Double.parseDouble(response.trim());
        } catch (NumberFormatException e) {
            // 如果解析失败，抛出异常
            throw new RuntimeException("Failed to parse similarity from response: " + response, e);
        }
    }

    @GetMapping("dashimage")
    public JsonData imageDash(String key ,String url){
        JsonData data=null;
        try{
            MultiModalConversation conv = new MultiModalConversation();
            MultiModalMessage userMessage = MultiModalMessage.builder().role(Role.USER.getValue())
                    .content(Arrays.asList(
                            Collections.singletonMap("image", URLDecoder.decode(url)), Collections.singletonMap("text", URLDecoder.decode(key)))).build();
            MultiModalConversationParam param = MultiModalConversationParam.builder()
                    .apiKey(System.getenv("DASHSCOPE_API_KEY"))
                    .model("qwen-vl-max")
                    .message(userMessage)
                    .build();
            MultiModalConversationResult result = conv.call(param);
            data=JsonData.ok(result);}
        catch (Exception ex){
            data=JsonData.error(300,ex.getMessage());
        }
        return data;
    }
    @GetMapping("dashaudio")
    public JsonData dashAudio(String key ,String url){
        String apiKey = System.getenv("DASHSCOPE_API_KEY");
        String prefix = "will";
        String targetModel = "cosyvoice-clone-v1";
        JsonData data=null;
        try{
// 复刻声音
            VoiceEnrollmentService service = new VoiceEnrollmentService(apiKey);
            Voice myVoice = service.createVoice(targetModel, prefix, URLDecoder.decode(url));
            System.out.println("your voice id is " + myVoice.getVoiceId());
// 使用复刻的声音来合成文本为语音
            SpeechSynthesisParam param = SpeechSynthesisParam.builder()
                    .apiKey(apiKey)
                    .model(targetModel)
                    .voice(myVoice.getVoiceId())
                    .build();
            SpeechSynthesizer synthesizer = new SpeechSynthesizer(param, null);
            ByteBuffer audio = synthesizer.call(URLDecoder.decode(key));
// 保存合成的语音到文件
            File parent=new File("src/main/resources/static/audio");
            if(! parent.exists()){
                parent.mkdirs();
            }
            String fileName= UUID.randomUUID()+".mp3";
            File file = new File(parent,fileName);
            try (FileOutputStream fos = new FileOutputStream(file)) {
                fos.write(audio.array());
            } catch (Exception e) {
                throw new RuntimeException(e);}
            data=JsonData.ok(fileName);
        }
        catch (Exception ex){
            data=JsonData.error(300,ex.getMessage());
        }
        return data;
    }
}