package org.jeecg.modules.car.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.modules.car.constants.CarConstants;
import org.jeecg.modules.car.entity.Car;
import org.jeecg.modules.car.entity.CarParking;
import org.jeecg.modules.car.entity.CarParkingLog;
import org.jeecg.modules.car.entity.Card;
import org.jeecg.modules.car.mapper.CarMapper;
import org.jeecg.modules.car.mapper.CarParkingLogMapper;
import org.jeecg.modules.car.mapper.CarParkingMapper;
import org.jeecg.modules.car.mapper.CardMapper;
import org.jeecg.modules.car.service.ICarService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

/**
 * @Description: 车辆管理
 * @Author: jeecg-boot
 * @Date:   2022-01-10
 * @Version: V1.0
 */
@Service
public class CarServiceImpl extends ServiceImpl<CarMapper, Car> implements ICarService {

    @Autowired
    private CarParkingLogMapper carParkingLogMapper;
    @Autowired
    private CarParkingMapper carParkingMapper;
    @Autowired
    private CardMapper cardMapper;
    @Override
    public String findCarStoppingParkingNumber(String $carId) {
        // 再入库记录中找车位Id
        CarParkingLog queryLog = new CarParkingLog().setCarNumner($carId).setStatus(CarConstants.CAR_INTER);
        QueryWrapper<CarParkingLog> queryWrapper = QueryGenerator.initQueryWrapper(queryLog, null);
        CarParkingLog log = this.carParkingLogMapper.selectOne(queryWrapper);
        if(log == null){
            return  "";
        }
        CarParking parking = carParkingMapper.selectById(log.getParkingNumner());
        if (parking == null){
            return  "";
        }
        return parking.getParkingNumber();
    }

    @Override
    public CarParking findCarFixingParking(String carId) {
        // 查找绑定的车位
        Card card = new Card().setCarId(carId);
        QueryWrapper<Card> queryWrapper = QueryGenerator.initQueryWrapper(card, null);
        card = this.cardMapper.selectOne(queryWrapper);
        if (card == null){
            return  null;
        }
        CarParking parking = carParkingMapper.selectById(card.getParkingId());
        if (parking == null){
            return  null;
        }
        return parking;
    }
}
