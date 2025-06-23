package com.iteng.chatbot.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.iteng.chatbot.entity.Conversation;
import com.iteng.chatbot.entity.Message;
import com.iteng.chatbot.service.ConversationService;
import com.iteng.chatbot.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.Random;

@RestController
@RequestMapping("/ai/history")
@RequiredArgsConstructor
public class ChatHistoryController {

    private final ConversationService conversationService;

    private final MessageService messageService;

    /**
     * 查询会话历史列表
     * @param type 业务类型，如：chat,service,pdf
     * @return chatId列表
     */
    @GetMapping("/{type}")
    public List<Conversation> getChatIds(@PathVariable("type") String type) {
        LambdaQueryWrapper<Conversation> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Conversation::getType, type);
        wrapper.orderByDesc(Conversation::getCreateTime);
        return conversationService.list(wrapper);
    }

    /**
     * 查询单个会话的历史记录
     * @param type 业务类型，如：chat,service,pdf
     * @param chatId 会话id
     * @return
     */
    @GetMapping("/{type}/{chatId}")
    public List<Message> getChatHistory(@PathVariable("type") String type, @PathVariable("chatId") Long chatId) {
        LambdaQueryWrapper<Message> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Message::getConversationId, chatId);
        wrapper.orderByAsc(Message::getCreateTime);
        return messageService.list(wrapper);
    }

    /**
     * 新增会话
     * @param type 业务类型，如：chat,service,pdf
     * @return chatId列表
     */
    @PostMapping("/{type}")
    public Conversation addChatId(@PathVariable("type") String type) {
        Conversation conversation = new Conversation();
        conversation.setCreateTime(new Date());
        conversation.setName("会话 "+ (new Random().nextInt(90000) + 10000));
        conversation.setType(type);
        conversationService.save(conversation);
        return conversation;
    }

}
