package com.iteng.chatbot.config;

import com.iteng.chatbot.advisor.MessageStoreAdvisor;
import com.iteng.chatbot.constants.SystemConstants;
import com.iteng.chatbot.tools.CourseTools;
import io.micrometer.observation.ObservationRegistry;
import jakarta.annotation.Resource;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.QuestionAnswerAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.InMemoryChatMemory;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiEmbeddingModel;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static com.iteng.chatbot.constants.SystemConstants.CUSTOMER_SERVICE_SYSTEM;

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

    @Bean
    public VectorStore vectorStore(OpenAiEmbeddingModel embeddingModel) {
        return SimpleVectorStore.builder(embeddingModel).build();
    }

    @Bean("chatClient")
    public ChatClient chatClient(OpenAiChatModel openAiChatModel, CustomInMemoryChatMemory chatMemory) {
        return ChatClient.builder(openAiChatModel, ObservationRegistry.create(), null)
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

    @Bean("serviceChatClient")
    public ChatClient serviceChatClient(
            OpenAiChatModel model,
            CustomInMemoryChatMemory chatMemory,
            CourseTools courseTools) {
        return ChatClient.builder(model)
                .defaultSystem(CUSTOMER_SERVICE_SYSTEM)
                .defaultAdvisors(
                        new MessageChatMemoryAdvisor(chatMemory), // CHAT MEMORY
                        new SimpleLoggerAdvisor())
                .defaultTools(courseTools)
                .build();
    }

    @Bean("pdfChatClient")
    public ChatClient pdfChatClient(
            OpenAiChatModel model,
            InMemoryChatMemory chatMemory,
            VectorStore vectorStore) {
        return ChatClient.builder(model)
                .defaultSystem("请根据提供的上下文回答问题，不要自己猜测。")
                .defaultAdvisors(
                        new MessageChatMemoryAdvisor(chatMemory), // CHAT MEMORY
                        new SimpleLoggerAdvisor(),
                        new QuestionAnswerAdvisor(
                                vectorStore, // 向量库
                                SearchRequest.builder() // 向量检索的请求参数
                                        .similarityThreshold(0.5d) // 相似度阈值
                                        .topK(2) // 返回的文档片段数量
                                        .build()
                        )
                )
                .build();
    }
}
