package com.iteng.chatbot.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class InMemoryChatHistoryRepository implements ChatHistoryRepository{

    private final Map<String, List<String>> chatHistoryList = new HashMap<>();;

    @Override
    public void save(String type, String chatId) {
        if (!chatHistoryList.containsKey(type)) {
            chatHistoryList.put(type, new ArrayList<>());
        }
        List<String> chatIds = chatHistoryList.get(type);
        chatIds.add(chatId);
    }

    @Override
    public List<String> getChatIds(String type) {
        return chatHistoryList.getOrDefault(type, List.of());
    }
}
