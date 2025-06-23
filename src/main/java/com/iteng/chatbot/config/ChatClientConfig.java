package com.iteng.chatbot.config;

import com.iteng.chatbot.advisor.MessageStoreAdvisor;
import com.iteng.chatbot.constants.SystemConstants;
import io.micrometer.observation.ObservationRegistry;
import jakarta.annotation.Resource;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.InMemoryChatMemory;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ChatClientConfig {

    @Resource
    private MessageStoreAdvisor customStreamAroundAdvisor;
    @Bean
    public CustomInMemoryChatMemory chatMemory() {
        return new CustomInMemoryChatMemory();
    }
    @Bean
    public InMemoryChatMemory InMemorychatMemory() {
        return new InMemoryChatMemory();
    }

    @Bean("chatClient")
    public ChatClient chatClient(OpenAiChatModel openAiChatModel, CustomInMemoryChatMemory chatMemory) {
        return ChatClient.builder(openAiChatModel,ObservationRegistry.create(),null)
                .defaultSystem("您是一名聊天助手，你的名字叫小黑。请以愉快的方式解答各种问题。")
                // .defaultAdvisors(customStreamAroundAdvisor)
                .defaultAdvisors(new MessageChatMemoryAdvisor(chatMemory))
                .build();
    }

    @Bean("gameChatClient")
    public ChatClient gameChatClient(OpenAiChatModel model, InMemoryChatMemory chatMemory) {
        return ChatClient
                .builder(model)
                .defaultSystem(SystemConstants.GAME_SYSTEM_PROMPT)
                .defaultAdvisors(
                        new SimpleLoggerAdvisor(),
                        new MessageChatMemoryAdvisor(chatMemory)
                )
                .build();
    }
}
