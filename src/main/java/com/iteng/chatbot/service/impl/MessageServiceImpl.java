package com.iteng.chatbot.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.iteng.chatbot.entity.Message;
import com.iteng.chatbot.mapper.MessageMapper;
import com.iteng.chatbot.service.MessageService;
import org.springframework.stereotype.Service;


@Service
public class MessageServiceImpl extends ServiceImpl<MessageMapper, Message>
    implements MessageService {

}




