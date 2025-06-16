package com.iteng.chatbot.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ChatClientConfig {
    @Bean
    public ChatClient createChatClient(OpenAiChatModel openAiChatModel) {
        return ChatClient.builder(openAiChatModel)
                .defaultSystem("您是一家名为“黑马程序员”的职业教育公司的客户聊天助手，" +
                        "你的名字叫小黑。请以友好、乐于助人和愉快的方式解答学生的各种问题。")
                .defaultAdvisors(new SimpleLoggerAdvisor())
                .build();
    }
}
