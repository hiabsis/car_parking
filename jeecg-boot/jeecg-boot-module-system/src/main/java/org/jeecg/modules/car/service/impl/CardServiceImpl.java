package org.jeecg.modules.car.service.impl;

import org.apache.shiro.SecurityUtils;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.common.util.DateUtils;
import org.jeecg.modules.car.constants.OrderConstants;
import org.jeecg.modules.car.constants.ParkingConstants;
import org.jeecg.modules.car.entity.CarParking;
import org.jeecg.modules.car.entity.CarParkingCost;
import org.jeecg.modules.car.entity.Card;
import org.jeecg.modules.car.entity.UserOrder;
import org.jeecg.modules.car.mapper.CarParkingMapper;
import org.jeecg.modules.car.mapper.CardMapper;
import org.jeecg.modules.car.mapper.UserOrderMapper;
import org.jeecg.modules.car.service.ICardService;
import org.jeecg.modules.system.entity.SysUser;
import org.jeecg.modules.system.mapper.SysUserMapper;
import org.jeecg.modules.system.service.ISysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import java.beans.Transient;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @Description: 会员卡
 * @Author: jeecg-boot
 * @Date:   2022-01-10
 * @Version: V1.0
 */
@Service
public class CardServiceImpl extends ServiceImpl<CardMapper, Card> implements ICardService {
    @Autowired
    private SysUserMapper userMapper;

    @Autowired
    private UserOrderMapper userOrderMapper;
    @Autowired
    private CarParkingMapper carParkingMapper;


    @Override
    @Transient
    public boolean saveCard(CarParkingCost cost, Card card,String orderType,SysUser user) {
//        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();

        // 扣除用户积分
        SysUser updateUser = new SysUser().setId(user.getId()).setScore(user.getScore() - Integer.parseInt(cost.getCost()));
        this.userMapper.updateById(updateUser);
        this.baseMapper.insert(card);
        // 添加一条消费记录
        UserOrder order = new UserOrder().setOrderNumner(DateUtils.getDate("yyMMddHHmmss"))
                .setCost(cost.getCost()).setType(orderType).setStatus(OrderConstants.ORDER_STATUS_PAY)
                .setOrderTime(card.getStartTime()).setUserId(user.getId());
        userOrderMapper.insert(order);
        // 修改车位信息
        CarParking carParking = new CarParking().setType(ParkingConstants.PARKING_FIX).setId(card.getParkingId());
        carParkingMapper.updateById(carParking);
        return false;
    }

    private static String getOrderNo(){
        SimpleDateFormat sdf = new SimpleDateFormat("");
        String nowdate = sdf.format(new Date());
        return nowdate;
    }
}
