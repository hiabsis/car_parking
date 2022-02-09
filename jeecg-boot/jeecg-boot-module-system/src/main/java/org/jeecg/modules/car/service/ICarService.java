package org.jeecg.modules.car.service;

import org.jeecg.modules.car.entity.Car;
import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.car.entity.CarParking;

/**
 * @Description: 车辆管理
 * @Author: jeecg-boot
 * @Date:   2022-01-10
 * @Version: V1.0
 */
public interface ICarService extends IService<Car> {

    /**
     * 寻找车辆的停车位置
     * @return String
     */
    public String findCarStoppingParkingNumber(String $carId);

    /**
     * 寻找车辆的绑定的为车位（固定车位）
     * @param carId
     * @return
     */
    public CarParking findCarFixingParking(String carId);
}
