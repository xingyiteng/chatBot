package com.iteng.chatbot.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.util.Date;
import lombok.Data;

/**
 * 
 * @TableName conversation
 */
@TableName(value ="conversation")
@Data
public class Conversation {
    /**
     * 会话ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 会话名称
     */
    private String name;

    /**
     * 类型（chat、pdf、game、service）
     */
    private String type;
}