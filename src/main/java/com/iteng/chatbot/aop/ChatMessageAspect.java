package com.iteng.chatbot.aop;

import com.iteng.chatbot.entity.Message;
import com.iteng.chatbot.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import java.lang.reflect.Method;
import java.util.Date;

@Aspect
@Component
@RequiredArgsConstructor
public class ChatMessageAspect {
    private final MessageService messageService;

    @Around("@annotation(com.iteng.chatbot.annotation.ChatMessage)")
    public Object aroundChatMethod(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        Object[] args = joinPoint.getArgs();
        String prompt = null;
        Long chatId = null;
        // 通过参数名查找prompt和chatId
        String[] paramNames = signature.getParameterNames();
        for (int i = 0; i < paramNames.length; i++) {
            if ("prompt".equals(paramNames[i])) {
                prompt = (String) args[i];
            } else if ("chatId".equals(paramNames[i])) {
                chatId = (Long) args[i];
            }
        }
        // 保存用户消息
        if (prompt != null && chatId != null) {
            Message userMsg = new Message();
            userMsg.setConversationId(chatId);
            userMsg.setContent(prompt);
            userMsg.setRole("user");
            userMsg.setCreateTime(new Date());
            messageService.save(userMsg);
        }
        // 执行原方法，获取Flux<String>
        Object result = joinPoint.proceed();
        if (result instanceof Flux) {
            StringBuilder fullResponse = new StringBuilder();
            Flux<String> flux = (Flux<String>) result;
            Long finalChatId = chatId;
            Flux<String> wrappedFlux = flux.doOnNext(fullResponse::append)
                    .doOnComplete(() -> {
                        if (finalChatId != null) {
                            Message aiMsg = new Message();
                            aiMsg.setConversationId(finalChatId);
                            aiMsg.setContent(fullResponse.toString());
                            aiMsg.setRole("assistant");
                            aiMsg.setCreateTime(new Date());
                            messageService.save(aiMsg);
                        }
                    });
            return wrappedFlux;
        }
        return result;
    }
} 