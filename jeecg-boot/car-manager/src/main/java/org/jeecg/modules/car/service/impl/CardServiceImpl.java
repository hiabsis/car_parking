package org.jeecg.modules.car.service.impl;

import org.jeecg.modules.car.entity.Card;
import org.jeecg.modules.car.mapper.CardMapper;
import org.jeecg.modules.car.service.ICardService;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

/**
 * @Description: 会员卡
 * @Author: jeecg-boot
 * @Date:   2022-01-10
 * @Version: V1.0
 */
@Service
public class CardServiceImpl extends ServiceImpl<CardMapper, Card> implements ICardService {

}
