package com.iteng.chatbot.config;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.iteng.chatbot.service.MessageService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Component
@Slf4j
public class CustomInMemoryChatMemory implements ChatMemory {

	@Resource
	private MessageService messageService;

	// 使用 Caffeine 缓存：key 为 conversationId，value 为聊天记录列表
	private final Cache<Long, List<Message>> chatHistoryCache = Caffeine.newBuilder()
			.expireAfterWrite(1, TimeUnit.HOURS) // 缓存写入后1小时过期
			.maximumSize(1000) // 最大缓存1000个会话
			.build();

	@Override
	public void add(String conversationId, List<Message> messages) {
		// 将消息添加到缓存中
		List<Message> existingMessages = chatHistoryCache.getIfPresent(Long.valueOf(conversationId));
		if (existingMessages == null) {
			existingMessages = new ArrayList<>();
		}
		existingMessages.addAll(messages);
		while (existingMessages.size() > 60) {
			existingMessages.remove(0);
		}
		chatHistoryCache.put(Long.valueOf(conversationId), existingMessages);
		//保存用户消息到数据库
		if ("user".equals(messages.get(0).getMessageType().getValue())) {
			saveUserMessage(Long.valueOf(conversationId), messages.get(0).getText());
		}else if ("assistant".equals(messages.get(0).getMessageType().getValue())){
			saveSystemMessage(Long.valueOf(conversationId), messages.get(0).getText());
		}
	}

	@Override
	public List<Message> get(String conversationId, int lastN) {
		Long key = Long.valueOf(conversationId);

		// 先从缓存中尝试获取
		List<Message> cachedMessages = chatHistoryCache.getIfPresent(key);
		if (cachedMessages != null) {
			return cachedMessages;
		}

		// 如果缓存未命中，从数据库加载最近30条聊天记录
		List<com.iteng.chatbot.entity.Message> chatHistories = messageService.list(
				new LambdaQueryWrapper<com.iteng.chatbot.entity.Message>()
						.eq(com.iteng.chatbot.entity.Message::getConversationId, key)
						.orderByAsc(com.iteng.chatbot.entity.Message::getCreateTime)
						.last("limit 30")
		);

		// 将查询结果转换为 Message 对象
		List<Message> messages = chatHistories.stream()
				.map(history -> {
					if (history.getRole().equals("user")) {
						return new UserMessage(history.getContent());
					} else {
						return new SystemMessage(history.getContent());
					}
				})
				.collect(Collectors.toList());

		// 将查询结果放入 Caffeine 缓存
		chatHistoryCache.put(key, messages);

		return messages;
	}

	@Override
	public void clear(String conversationId) {
		// 从缓存中移除会话记录
		chatHistoryCache.invalidate(Long.valueOf(conversationId));
	}

	private void saveSystemMessage(Long conversationId, String message) {
		com.iteng.chatbot.entity.Message aiMsg = new com.iteng.chatbot.entity.Message();
		aiMsg.setConversationId(conversationId);
		aiMsg.setContent(message);
		aiMsg.setRole("assistant");
		aiMsg.setCreateTime(new Date());
		if (messageService.save(aiMsg)) {
			log.info("保存系统消息成功");
		}else {
			log.info("保存用户消息失败");
		}
	}

	private void saveUserMessage(Long conversationId, String userMessage){
		com.iteng.chatbot.entity.Message userMsg = new com.iteng.chatbot.entity.Message();
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
