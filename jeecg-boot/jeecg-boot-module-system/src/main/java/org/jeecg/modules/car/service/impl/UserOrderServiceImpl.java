package org.jeecg.modules.car.service.impl;

import org.jeecg.modules.car.entity.UserOrder;
import org.jeecg.modules.car.mapper.UserOrderMapper;
import org.jeecg.modules.car.service.IUserOrderService;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

/**
 * @Description: 订单表
 * @Author: jeecg-boot
 * @Date:   2022-01-10
 * @Version: V1.0
 */
@Service
public class UserOrderServiceImpl extends ServiceImpl<UserOrderMapper, UserOrder> implements IUserOrderService {

}
