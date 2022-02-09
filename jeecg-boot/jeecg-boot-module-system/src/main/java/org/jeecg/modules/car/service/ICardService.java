package org.jeecg.modules.car.service;

import org.jeecg.modules.car.entity.CarParkingCost;
import org.jeecg.modules.car.entity.Card;
import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.system.entity.SysUser;

/**
 * @Description: 会员卡
 * @Author: jeecg-boot
 * @Date:   2022-01-10
 * @Version: V1.0
 */
public interface ICardService extends IService<Card> {

    /**
     * 保存会员卡
     * @param cost
     * @param card
     * @return
     */
    public boolean saveCard(CarParkingCost cost, Card card, String orderType, SysUser user);
}
