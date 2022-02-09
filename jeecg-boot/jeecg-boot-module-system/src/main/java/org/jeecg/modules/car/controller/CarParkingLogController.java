package org.jeecg.modules.car.controller;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.AutoLog;
import org.jeecg.common.system.base.controller.JeecgController;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.common.util.DateUtils;
import org.jeecg.modules.car.constants.CarConstants;
import org.jeecg.modules.car.constants.OrderConstants;
import org.jeecg.modules.car.constants.ParkingConstants;
import org.jeecg.modules.car.entity.*;
import org.jeecg.modules.car.service.*;
import org.jeecg.modules.system.entity.SysUser;
import org.jeecg.modules.system.service.ISysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @Description: 车辆入库记录
 * @Author: jeecg-boot
 * @Date:   2022-01-10
 * @Version: V1.0
 */
@Api(tags="车辆入库记录")
@RestController
@RequestMapping("/car/carParkingLog")
@Slf4j
public  class CarParkingLogController extends JeecgController<CarParkingLog, ICarParkingLogService> {
	@Autowired
	private ICarParkingLogService carParkingLogService;
	@Autowired
	private ICardService cardService;
	@Autowired
	private ICarService carService;
	@Autowired
	private ICarParkingService carParkingService;
	@Autowired
	private IUserOrderService userOrderService;
	@Autowired
	private ICarParkingCostService carParkingCostService;
	@Autowired
	private ISysUserService sysUserService;
	/**
	 * 分页列表查询
	 *
	 * @param carParkingLog
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	@AutoLog(value = "车辆入库记录-分页列表查询")
	@ApiOperation(value="车辆入库记录-分页列表查询", notes="车辆入库记录-分页列表查询")
	@GetMapping(value = "/list")
	public Result<?> queryPageList(CarParkingLog carParkingLog,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
		QueryWrapper<CarParkingLog> queryWrapper = QueryGenerator.initQueryWrapper(carParkingLog, req.getParameterMap());
		Page<CarParkingLog> page = new Page<CarParkingLog>(pageNo, pageSize);
		IPage<CarParkingLog> pageList = carParkingLogService.page(page, queryWrapper);
		return Result.OK(pageList);
	}

	/**
	 *   添加
	 *
	 * @param carParkingLog
	 * @return
	 */
	@AutoLog(value = "车辆入库记录-添加")
	@ApiOperation(value="车辆入库记录-添加", notes="车辆入库记录-添加")
	@PostMapping(value = "/add")
	public Result<?> add(@RequestBody CarParkingLog carParkingLog) {
		carParkingLogService.save(carParkingLog);
		return Result.OK("添加成功！");
	}

	/**
	 *  编辑
	 *
	 * @param carParkingLog
	 * @return
	 */
	@AutoLog(value = "车辆入库记录-编辑")
	@ApiOperation(value="车辆入库记录-编辑", notes="车辆入库记录-编辑")
	@PutMapping(value = "/edit")
	public Result<?> edit(@RequestBody CarParkingLog carParkingLog) {
		carParkingLogService.updateById(carParkingLog);
		return Result.OK("编辑成功!");
	}

	/**
	 *  车辆入库
	 *
	 * @param carParkingLog
	 * @return
	 */
	@AutoLog(value = "车辆入库记录-入库")
	@ApiOperation(value="车辆入库记录-入库", notes="车辆入库记录-入库")
	@PostMapping(value = "/inter")
	public Result<?> inter(@RequestBody CarParkingLog carParkingLog) {
		// 数据校验
		if (null == carParkingLog.getUserId()  ||  carParkingLog.getUserId().equals("")){
			return Result.error("无法确定用户!");
		}
		if ( null == carParkingLog.getParkingNumner() || carParkingLog.getParkingNumner().equals("")){
			return Result.error("请选择车位!");
		}
		if ( null == carParkingLog.getCarNumner() || carParkingLog.getCarNumner().equals("") ){
			return Result.error("请选择入库车辆!");
		}
		// 检查该车辆是否入库
		Car car = this.carService.getById(carParkingLog.getCarNumner());
		if (car.getStatus().equals(CarConstants.CAR_INTER)){
			return Result.error("车辆已入库,无法重复入库!");
		}
		// 检查车位是否存在车辆
		CarParking carParking = this.carParkingService.getById(carParkingLog.getParkingNumner());
		if (carParking.getStatus().equals(ParkingConstants.PARKING_BUSYING)){
			return Result.error("该车位已经有车辆!");
		}
		// 设置入库时间
		carParkingLog.setStartTime(new Date());
		// 数据持久化
		this.carParkingLogService.save(carParkingLog.setStatus(CarConstants.CAR_INTER));
		this.carService.updateById(car.setStatus(CarConstants.CAR_INTER));
		this.carParkingService.updateById(carParking.setStatus(ParkingConstants.PARKING_BUSYING));

		return Result.OK("入库成功!");
	}

	/**
	 * 车辆出库
	 * @param
	 * @param
	 * @param
	 * @return
	 */
	@AutoLog(value = "车辆入库记录-出库")
	@ApiOperation(value="车辆入库记录-出库", notes="车辆入库记录-出库")
	@PutMapping(value = "/outer")
	public Result<?> outer(@RequestBody JSONObject json) {
		// 获取数据
		String carParkingLogId = json.getString("id");
		String payWay = json.getString("payWay");
		String cardId = json.getString("number");
		String cardPassword = json.getString("password");
		// 数据校验
		if (carParkingLogId.equals("")|| carParkingLogId == null){
			return Result.error("无法找到入库记录!");
		}

		// 获取停车记录 防止恶意数据篡改
		CarParkingLog carParkingLog = this.carParkingLogService.getById(carParkingLogId);
		// 检查该车辆是否出库
		Car car = this.carService.getById(carParkingLog.getCarNumner());
		if (car.getStatus().equals(CarConstants.CAR_OUTER)){
			return Result.error("车辆已出库!");
		}
		// 获取车位信息
		CarParking carParking = this.carParkingService.getById(carParkingLog.getParkingNumner());
		// 设置出库时间
		carParkingLog.setEndTime(new Date());
		// 获取用户信息
		SysUser sysUser = this.sysUserService.getById(carParkingLog.getUserId());

		/**
		 * 会员卡消费
		 */
		UserOrder order = new UserOrder().setUserId(sysUser.getId()).setOrderNumner(DateUtils.getDate("yyMMddHHmmss"))
				.setOrderTime(new Date()).setCost("0").setStatus(OrderConstants.ORDER_STATUS_PAY);
		if (payWay.equals(OrderConstants.PAY_WAY_CARD)){
			// 获取会员卡信息

			if (cardId == null){
				return Result.error("无法查找会卡信息!!!");
			}
			Card queryCard = this.cardService.getById(cardId);

			// 检查密码
			if (!queryCard.getPassword().equals(cardPassword)){
				return Result.error("支付密码错误!");
			}
			// 绑定检查
			if (queryCard.getCarId() == null || queryCard.getCarId().equals("") ){
				return Result.error("会卡未绑定车辆!");
			}
			if (queryCard.getParkingId() == null || queryCard.getParkingId().equals("") ){
				return Result.error("会卡未绑定车位!");
			}

			if (!queryCard.getParkingId().equals(carParking.getId())){
				return Result.error("会卡未绑定该车位!");
			}
			if(!queryCard.getCarId().equals(carParkingLog.getCarNumner())){
				return Result.error("会卡未绑定该车辆");
			}
			// 检查会员卡是否过期
			if (queryCard.getEndTime().getTime() < carParkingLog.getEndTime().getTime()){
				return Result.error("会员卡是否过期!");
			}
			order.setType(OrderConstants.CAR_OUTER_VIP);
		}else if (payWay.equals(OrderConstants.PAY_WAY_SCORE)){
			// 获取用户此次消费积分
			int score = getParkingScore(carParkingLog.getStartTime(),carParkingLog.getEndTime());
			if (sysUser.getScore() < score){
				return Result.error("用户积分不足!");
			}
			order.setType(OrderConstants.CAR_OUTER_COMMON);
			order.setCost(""+score);
			sysUserService.updateById(sysUser.setScore(sysUser.getScore()-score));
		}else {
			return Result.error("请选择支付方式!");
		}
		// 数据持久化
		CarParkingLog log = new CarParkingLog().setId(carParkingLog.getId()).setStatus(CarConstants.CAR_OUTER).setEndTime(carParkingLog.getEndTime());
		this.carParkingLogService.updateById(log);
		this.carService.updateById(car.setStatus(CarConstants.CAR_OUTER));
		this.carParkingService.updateById(carParking.setStatus(ParkingConstants.PARKING_EMPTY));
		this.userOrderService.save(order);

		return Result.OK("出库成功!");
	}

	/**
	 * 获取车辆信息
	 * 	入库时间
	 * 	截至目前所要的积分
	 * @param carId
	 * @return
	 */
	@AutoLog(value = "车辆入库记录-车辆信息")
	@ApiOperation(value="车辆入库记录-车辆信息", notes="车辆入库记录-车辆信息")
	@GetMapping(value = "/carInfo")
	public Result<?> getCarInfo(@RequestParam(defaultValue = "") String carId){
		if (carId.equals("") ){
			return Result.error("车辆信息为空，无法获取车辆入库信息!");
		}
		CarParkingLog log = new CarParkingLog().setStatus(CarConstants.CAR_INTER).setCarNumner(carId);
		List<CarParkingLog> carParkingLogs = this.carParkingLogService.list(QueryGenerator.initQueryWrapper(log,null));
		if (carParkingLogs.isEmpty()){
			return Result.error("无法获取车辆入库信息，请联系管理员");
		}
		log = carParkingLogs.get(0);
		log.setEndTime(new Date());
		int score = getParkingScore(log.getStartTime(),log.getEndTime());
		// 获取车位信息
		CarParking carParking = this.carParkingService.getById(log.getParkingNumner());

		String strDateFormat = "yyyy-MM-dd HH:mm:ss";
		SimpleDateFormat sdf = new SimpleDateFormat(strDateFormat);
		Map<String,Object> map = new HashMap();
		map.put("score",score);
		map.put("startTime",sdf.format(log.getStartTime()));
		map.put("log",log.getId());
		map.put("parkingNumber",carParking.getParkingNumber());
		return Result.OK(map);
	}
	public Integer getParkingScore(Date startDate, Date endDate){
		// 停车的时间 单位秒
		int time = Integer.parseInt(""+(startDate.getTime()-endDate.getTime())/1000);
		//
		int score = -1;
		// 获取普通套餐
		CarParkingCost cost = new CarParkingCost().setType(OrderConstants.COST_COMMON);
		QueryWrapper<CarParkingCost> costQueryWrapper = QueryGenerator.initQueryWrapper(cost,null);
		List<CarParkingCost> costs = this.carParkingCostService.list(costQueryWrapper);
		if (costs.size() == 0){
			return score;
		}
		// 通过停车时间排序
		costs.sort(new Comparator<CarParkingCost>() {
			@Override
			public int compare(CarParkingCost o1, CarParkingCost o2) {
				return o1.getTime() - o2.getTime();
			}
		});
		for (int i=0;i<costs.size();i++){
			if (costs.get(i).getTime() > time){
				score = Integer.parseInt(costs.get(i).getCost()) ;
				break;
			}
		}
		//超过一天停车时间 按照5积分/H收费
		return  score == -1?score = time/3600 * 5:score;
	}
	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "车辆入库记录-通过id删除")
	@ApiOperation(value="车辆入库记录-通过id删除", notes="车辆入库记录-通过id删除")
	@DeleteMapping(value = "/delete")
	public Result<?> delete(@RequestParam(name="id",required=true) String id) {
		carParkingLogService.removeById(id);
		return Result.OK("删除成功!");
	}

	/**
	 *  批量删除
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "车辆入库记录-批量删除")
	@ApiOperation(value="车辆入库记录-批量删除", notes="车辆入库记录-批量删除")
	@DeleteMapping(value = "/deleteBatch")
	public Result<?> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.carParkingLogService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.OK("批量删除成功!");
	}


	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "车辆入库记录-通过id查询")
	@ApiOperation(value="车辆入库记录-通过id查询", notes="车辆入库记录-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<?> queryById(@RequestParam(name="id",required=true) String id) {
		CarParkingLog carParkingLog = carParkingLogService.getById(id);
		if(carParkingLog==null) {
			return Result.error("未找到对应数据");
		}
		return Result.OK(carParkingLog);
	}

	/**
	 * 导出excel
	 *
	 * @param request
	 * @param carParkingLog
	 */
	@RequestMapping(value = "/exportXls")
	public ModelAndView exportXls(HttpServletRequest request, CarParkingLog carParkingLog) {
		return super.exportXls(request, carParkingLog, CarParkingLog.class, "车辆入库记录");
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
		return super.importExcel(request, response, CarParkingLog.class);
	}

}
