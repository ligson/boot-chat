package com.yonyougov.bootchat.gpt.controller;

import com.yonyougov.bootchat.fw.context.SessionContext;
import com.yonyougov.bootchat.gpt.dto.WxChatMessage;
import com.yonyougov.bootchat.minio.file.FileMsg;
import com.yonyougov.bootchat.minio.file.FileMsgService;
import com.yonyougov.bootchat.minio.util.MinioUtil;



import com.yonyougov.bootchat.fw.web.vo.WebResult;
import com.yonyougov.bootchat.gpt.dto.ChatMessage2;
import com.yonyougov.bootchat.gpt.service.GptChatService;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.image.ImagePrompt;
import org.springframework.ai.zhipuai.ZhiPuAiImageModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import java.util.HashMap;



@RequestMapping("/api/gptchat")
@RestController
public class GptChatController {

    private final ChatClient chatClient;
    private final GptChatService gptChatService;
    private final ZhiPuAiImageModel zhiPuAiImageModel;
    private final SessionContext sessionContext;
    @Autowired
    private MinioUtil minioUtil;

    @Autowired
    private FileMsgService fileMsgService;

    public GptChatController(ChatClient chatClient, GptChatService gptChatService, ZhiPuAiImageModel zhiPuAiImageModel, SessionContext sessionContext) {
        this.chatClient = chatClient;
        this.gptChatService = gptChatService;
        this.zhiPuAiImageModel = zhiPuAiImageModel;
        this.sessionContext = sessionContext;
    }

    @GetMapping("/ai/generate")
    public ChatResponse generate(@RequestParam(value = "message", defaultValue = "Tell me a joke") String message) {
        String userId = sessionContext.getCurrentUser().getId();
        return gptChatService.generate(userId, message);
    }

    @PostMapping("/ai/saveAllWiki")
    public WebResult saveAllWiki(@RequestBody String tooken) throws Exception {
        gptChatService.saveFile(tooken);
        return WebResult.newSuccessInstance();
    }

    @GetMapping("/ai/test")
    public void test() {
        gptChatService.AddVectorStore();
    }

    @PostMapping("/ai/image")
    public WebResult getImage(@RequestBody ChatMessage2 messages) {
        String image = zhiPuAiImageModel.call(
                new ImagePrompt(messages.getProblem())
        ).getResult().getOutput().getUrl();
        //将图片存入minio，数据库
        String fileName = minioUtil.uploadImageFromUrl(image,messages);
        //从数据库中查寻图片信息并返回数据
        if (null != fileName) {
            FileMsg fileMsg = fileMsgService.findByFileName(fileName);
            if (null == fileMsg) {
                throw new RuntimeException("通过fileName查询不到图片信息");
            }
            WebResult webResult = WebResult.newSuccessInstance();

            HashMap<String, Object> result = new HashMap<>();
            result.put("uri", fileMsg.getLocalDirectory());
            result.put("type", "image");
//            result.put("type", fileMsg.getFileType());
            result.put("name", fileMsg.getFileName());
            result.put("imageId", fileMsg.getId());
            result.put("size", fileMsg.getFileSize());
            webResult.setData(result);

            return webResult;
        }
        return WebResult.newErrorInstance("图片上传失败");
//            return image;
    }


    @PostMapping("/ai/generateStream")
    public Flux<ChatResponse> generateStream(@RequestBody ChatMessage2 messages) {
        String userId = sessionContext.getCurrentUser().getId();
        return gptChatService.stream(userId, messages);
    }
    @PostMapping("/ai/generateStreamwx")
    public String generateStreamwx(@RequestBody WxChatMessage messages) {
        return gptChatService.call(messages);
    }
}