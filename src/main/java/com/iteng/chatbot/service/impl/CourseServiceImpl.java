package com.iteng.chatbot.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.iteng.chatbot.entity.Course;
import com.iteng.chatbot.mapper.CourseMapper;
import com.iteng.chatbot.service.ICourseService;
import org.springframework.stereotype.Service;

/**
 * 学科表 服务实现类
 */
@Service
public class CourseServiceImpl extends ServiceImpl<CourseMapper, Course> implements ICourseService {

}