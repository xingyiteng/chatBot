package com.iteng.chatbot.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.iteng.chatbot.entity.School;
import com.iteng.chatbot.mapper.SchoolMapper;
import com.iteng.chatbot.service.ISchoolService;
import org.springframework.stereotype.Service;

/**
 * 校区表 服务实现类
 */
@Service
public class SchoolServiceImpl extends ServiceImpl<SchoolMapper, School> implements ISchoolService {

}