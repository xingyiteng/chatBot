package com.iteng.chatbot.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.util.Date;
import lombok.Data;

/**
 * 
 * @TableName message
 */
@TableName(value ="message")
@Data
public class Message {
    /**
     * 消息ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 所属会话ID
     */
    private Long conversationId;

    /**
     * 消息内容
     */
    private String content;

    /**
     * 角色：user/assistant
     */
    private String role;

    /**
     * 创建时间
     */
    private Date createTime;
}