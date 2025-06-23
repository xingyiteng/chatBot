package com.iteng.chatbot.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.iteng.chatbot.entity.Conversation;
import org.apache.ibatis.annotations.Mapper;

/**
* @author xingyiteng
* @description 针对表【conversation】的数据库操作Mapper
* @createDate 2025-06-18 22:45:50
* @Entity com.iteng.chatbot.entity.Conversation
*/
@Mapper
public interface ConversationMapper extends BaseMapper<Conversation> {

}




