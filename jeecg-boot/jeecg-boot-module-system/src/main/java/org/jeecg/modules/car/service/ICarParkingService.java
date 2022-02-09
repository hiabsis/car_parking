package org.jeecg.modules.car.service;

import org.jeecg.modules.car.entity.Car;
import org.jeecg.modules.car.entity.CarParking;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * @Description: 车位表
 * @Author: jeecg-boot
 * @Date:   2022-01-09
 * @Version: V1.0
 */
public interface ICarParkingService extends IService<CarParking> {
    /**
     * 统计车位使用情况
     * @return
     */
    public Integer[] countTotalInfo();

    /**
     * 获取绑定的车辆的信息
     */
    public Car findBindingCarInfoByParkingId(String $parkingId);

    /**
     * 获取停放车辆车辆的信息
     */
    public Car findStoppingCarInfoByParkingId(String parkingId);
}
