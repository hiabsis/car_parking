package org.jeecg.modules.car.controller;

import java.util.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.modules.car.constants.CarConstants;
import org.jeecg.modules.car.constants.ParkingConstants;
import org.jeecg.modules.car.entity.*;
import org.jeecg.modules.car.service.*;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;

import org.jeecg.common.system.base.controller.JeecgController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.jeecg.common.aspect.annotation.AutoLog;

 /**
 * @Description: 车辆管理
 * @Author: jeecg-boot
 * @Date:   2022-01-10
 * @Version: V1.0
 */
@Api(tags="车辆管理")
@RestController
@RequestMapping("/car/car")
@Slf4j
public class CarController extends JeecgController<Car, ICarService> {
	@Autowired
	private ICarService carService;

	 @Autowired
	 private ICarParkingService carParkingService;
	/**
	 * 分页列表查询
	 *
	 * @param car
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	@AutoLog(value = "车辆管理-分页列表查询")
	@ApiOperation(value="车辆管理-分页列表查询", notes="车辆管理-分页列表查询")
	@GetMapping(value = "/list")
	public Result<?> queryPageList(Car car,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
		QueryWrapper<Car> queryWrapper = QueryGenerator.initQueryWrapper(car, req.getParameterMap());
		Page<Car> page = new Page<Car>(pageNo, pageSize);
		IPage<Car> pageList = carService.page(page, queryWrapper);
		List<Car> cars = pageList.getRecords();
		for (int i=0;i<cars.size();i++){
			// 查询车辆入库所在车位
			String parkingNumber = carService.findCarStoppingParkingNumber(cars.get(i).getId());
			cars.get(i).setParkingNumber(parkingNumber);
			// 如果有绑定会卡 返回绑定的车位
			CarParking parking = this.carService.findCarFixingParking(cars.get(i).getId());
			if (parking != null){
				cars.get(i).setFixParkingNumber(parking.getParkingNumber());
				cars.get(i).setFixParkingId(parking.getId());
			}

		}
		return Result.OK(pageList);
	}

	 /**
	  * 分页列表查询
	  * @param car
	  * @param pageNo
	  * @param pageSize
	  * @param req
	  * @return
	  */
	 @AutoLog(value = "车辆管理-分页列表查询")
	 @ApiOperation(value="车辆管理-分页列表查询", notes="车辆管理-分页列表查询")
	 @GetMapping(value = "/user/list")
	 public Result<?> queryPageListByUser(Car car,
									@RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
									@RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
									HttpServletRequest req) {
		 QueryWrapper<Car> queryWrapper = QueryGenerator.initQueryWrapper(car, req.getParameterMap());
		 Page<Car> page = new Page<Car>(pageNo, pageSize);
		 IPage<Car> pageList = carService.page(page, queryWrapper);

		 return Result.OK(pageList);
	 }



	/**
	 *   添加
	 *
	 * @param car
	 * @return
	 */
	@AutoLog(value = "车辆管理-添加")
	@ApiOperation(value="车辆管理-添加", notes="车辆管理-添加")
	@PostMapping(value = "/add")
	public Result<?> add(@RequestBody Car car) {
		// 检查车辆是否超出绑定数量
		Card card = new Card().setUserId(car.getUserId());
		QueryWrapper<Card> queryWrapper = QueryGenerator.initQueryWrapper(card, null);
		int total = cardService.count(queryWrapper);
		if (total > CarConstants.CAR_MAX_BINDING){
			return Result.error("超过用户最大绑定车辆数!!!!");
		}
		car.setStatus(CarConstants.CAR_OUTER);
		carService.save(car);
		return Result.OK("添加成功！");
	}
	
	/**
	 *  编辑
	 *
	 * @param car
	 * @return
	 */
	@AutoLog(value = "车辆管理-编辑")
	@ApiOperation(value="车辆管理-编辑", notes="车辆管理-编辑")
	@PutMapping(value = "/edit")
	public Result<?> edit(@RequestBody Car car) {
		carService.updateById(car);
		return Result.OK("编辑成功!");
	}


	@Autowired
	private ICardService cardService;
	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "车辆管理-通过id删除")
	@ApiOperation(value="车辆管理-通过id删除", notes="车辆管理-通过id删除")
	@DeleteMapping(value = "/delete")
	public Result<?> delete(@RequestParam(name="id",required=true) String id) {
		// 如果车辆未出库禁止操作
		Car car = this.carService.getById(id);
		if (car.getStatus().equals(CarConstants.CAR_INTER) ){
			return Result.error("车辆未出库!");
		}
		// 绑定车辆的会卡 清除记录
		Card card = new Card().setCarId(id);
		QueryWrapper<Card> queryWrapper = QueryGenerator.initQueryWrapper(card, null);
		card  = this.cardService.getOne(queryWrapper);

		if (card != null){
			card.setCarId("");
			this.cardService.updateById(card);
			//把车位状态变成临时
			this.carParkingService.updateById(new CarParking().setId(card.getParkingId()).setType(ParkingConstants.PARKING_TEM));
		}
		// 修改车位状态 临时

		carService.removeById(id);
		return Result.OK("删除成功!");
	}
	
	/**
	 *  批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "车辆管理-批量删除")
	@ApiOperation(value="车辆管理-批量删除", notes="车辆管理-批量删除")
	@DeleteMapping(value = "/deleteBatch")
	public Result<?> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.carService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.OK("批量删除成功!");
	}
	
	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "车辆管理-通过id查询")
	@ApiOperation(value="车辆管理-通过id查询", notes="车辆管理-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<?> queryById(@RequestParam(name="id",required=true) String id) {
		Car car = carService.getById(id);
		if(car==null) {
			return Result.error("未找到对应数据");
		}
		return Result.OK(car);
	}

    /**
    * 导出excel
    *
    * @param request
    * @param car
    */
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, Car car) {
        return super.exportXls(request, car, Car.class, "车辆管理");
    }

    /**
      * 通过excel导入数据
    *
    * @param request
    * @param response
    * @return
    */
    @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
        return super.importExcel(request, response, Car.class);
    }

}
