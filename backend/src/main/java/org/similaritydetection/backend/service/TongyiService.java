package org.similaritydetection.backend.service;
import com.alibaba.cloud.ai.tongyi.chat.TongYiChatModel;
import org.similaritydetection.backend.utils.JsonData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TongyiService {

    @Autowired
    private TongYiChatModel chatModel;

    /**
     * 比较两段文本的相似度
     *
     * @param text1 第一段文本
     * @param text2 第二段文本
     * @return 相似度值 (0 到 1 的浮点数)
     * @throws Exception 如果调用失败
     */
    public String calculateTongyiSimilarity(String text1, String text2) {
        // 调用 Tongyi AI 的 chat 模型
        JsonData data = null;
        try {
            String chatResponse = chatModel.call("请你比较这两段代码的仅限于文字相似度，并返回给我一个0到1的浮点数来表示相似度，除了这个浮点数以外不要返回其他任何内容，比如这两段代码的相似度为0.56，也不要给我推荐算法，也不要重复我的输入，直接返回结果：" + text1 + " 和 " + text2);
            data = JsonData.ok(chatResponse);
        } catch (Exception ex) {
            data = JsonData.error(200, ex.getMessage());
        }
        return data.toString();
    }
}
