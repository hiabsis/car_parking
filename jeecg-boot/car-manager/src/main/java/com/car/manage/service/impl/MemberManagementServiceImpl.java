package com.car.manage.service.impl;

import com.car.manage.entity.MemberManagement;
import com.car.manage.mapper.MemberManagementMapper;
import com.car.manage.service.IMemberManagementService;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

/**
 * @Description: 会员管理
 * @Author: jeecg-boot
 * @Date:   2021-12-19
 * @Version: V1.0
 */
@Service
public class MemberManagementServiceImpl extends ServiceImpl<MemberManagementMapper, MemberManagement> implements IMemberManagementService {

}
