package org.jeecg.modules.car.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.checkerframework.checker.units.qual.C;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.modules.car.constants.ParkingConstants;
import org.jeecg.modules.car.entity.Car;
import org.jeecg.modules.car.entity.CarParking;
import org.jeecg.modules.car.entity.CarParkingLog;
import org.jeecg.modules.car.entity.Card;
import org.jeecg.modules.car.mapper.CarMapper;
import org.jeecg.modules.car.mapper.CarParkingLogMapper;
import org.jeecg.modules.car.mapper.CarParkingMapper;
import org.jeecg.modules.car.mapper.CardMapper;
import org.jeecg.modules.car.service.ICarParkingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

/**
 * @Description: 车位表
 * @Author: jeecg-boot
 * @Date:   2022-01-09
 * @Version: V1.0
 */
@Service
public class CarParkingServiceImpl extends ServiceImpl<CarParkingMapper, CarParking> implements ICarParkingService {
    @Autowired
    private CardMapper cardMapper;
    @Autowired
    private CarMapper carMapper;

    @Autowired
    private CarParkingLogMapper carParkingLogMapper;
    @Override
    public Integer[] countTotalInfo() {
        CarParking query = new CarParking();
        QueryWrapper<CarParking> countWrapper = QueryGenerator.initQueryWrapper(query,null);
        Integer total = this.baseMapper.selectCount(countWrapper);
        query.setStatus("1");
        countWrapper = QueryGenerator.initQueryWrapper(query,null);
        Integer busyingParing = this.baseMapper.selectCount(countWrapper);
        return new Integer[]{total, busyingParing};
    }

    @Override
    public Car findBindingCarInfoByParkingId(String parkingId) {
        // 从会员卡表中获取车辆Id
        Card card = new Card().setParkingId(parkingId);
        if (parkingId == null){
            return  null;
        }
        QueryWrapper<Card> query = QueryGenerator.initQueryWrapper(card,null);
        card = this.cardMapper.selectOne(query);
        if (card == null){
            return  null;
        }
        Car car = this.carMapper.selectById(card.getCarId());
        // 根据Id查询车辆信息
        return car;
    }

    @Override
    public Car findStoppingCarInfoByParkingId(String parkingId) {
        CarParkingLog log = new CarParkingLog().setParkingNumner(parkingId).setStatus("1");
        QueryWrapper<CarParkingLog> query = QueryGenerator.initQueryWrapper(log,null);
        log = this.carParkingLogMapper.selectOne(query);
        if (log == null){
            return  null;
        }
        Car car = this.carMapper.selectById(log.getCarNumner());
        return car;
    }
}
