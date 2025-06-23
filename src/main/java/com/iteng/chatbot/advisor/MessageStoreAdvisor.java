package com.iteng.chatbot.advisor;

import com.iteng.chatbot.entity.Message;
import com.iteng.chatbot.service.MessageService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.advisor.api.AdvisedRequest;
import org.springframework.ai.chat.client.advisor.api.AdvisedResponse;
import org.springframework.ai.chat.client.advisor.api.StreamAroundAdvisor;
import org.springframework.ai.chat.client.advisor.api.StreamAroundAdvisorChain;
import org.springframework.ai.chat.model.MessageAggregator;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import java.util.Date;

import static org.springframework.ai.chat.client.advisor.AbstractChatMemoryAdvisor.CHAT_MEMORY_CONVERSATION_ID_KEY;

/**
 * 保存用户、系统消息
 */
@Slf4j
@Component
public class MessageStoreAdvisor implements StreamAroundAdvisor, Ordered {

    @Resource
    private MessageService messageService;

    @Override
    public String getName() {
        return "CustomStreamAroundAdvisor";
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE; // 设置执行顺序
    }

    @Override
    public Flux<AdvisedResponse> aroundStream(AdvisedRequest advisedRequest, StreamAroundAdvisorChain chain) {
        Long conversationId = (Long)advisedRequest.advisorParams().get(CHAT_MEMORY_CONVERSATION_ID_KEY);
        String userMessage = advisedRequest.userText();
        // 保存用户消息
        saveUserMessage(conversationId, userMessage);
        // 调用下一个 Advisor 或大模型
        Flux<AdvisedResponse> responseFlux = chain.nextAroundStream(advisedRequest);
        return new MessageAggregator().aggregateAdvisedResponse(responseFlux, this::saveSystemMessage);
    }

    private void saveSystemMessage(AdvisedResponse advisedResponse) {
        Long conversationId = (Long)advisedResponse.adviseContext().get(CHAT_MEMORY_CONVERSATION_ID_KEY);
        String content = advisedResponse.response().getResult().getOutput().getText();
        Message aiMsg = new Message();
        aiMsg.setConversationId(conversationId);
        aiMsg.setContent(content);
        aiMsg.setRole("assistant");
        aiMsg.setCreateTime(new Date());
        if (messageService.save(aiMsg)) {
            log.info("保存系统消息成功");
        }else {
            log.info("保存用户消息失败");
        }
    }

    private void saveUserMessage(Long conversationId, String userMessage ){
        Message userMsg = new Message();
        userMsg.setConversationId(conversationId);
        userMsg.setContent(userMessage);
        userMsg.setRole("user");
        userMsg.setCreateTime(new Date());
        if (messageService.save(userMsg)) {
            log.info("保存用户消息成功");
        }else {
            log.info("保存用户消息失败");
        }
    }
}