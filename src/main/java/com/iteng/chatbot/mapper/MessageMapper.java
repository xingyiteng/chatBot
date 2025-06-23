package com.iteng.chatbot.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.iteng.chatbot.entity.Message;
import org.apache.ibatis.annotations.Mapper;

/**
* @author xingyiteng
* @description 针对表【message】的数据库操作Mapper
* @createDate 2025-06-18 22:48:57
* @Entity com.iteng.chatbot.entity.Message
*/
@Mapper
public interface MessageMapper extends BaseMapper<Message> {

}




