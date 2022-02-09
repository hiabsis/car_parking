package org.jeecg.modules.car.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.common.util.oConvertUtils;
import org.jeecg.modules.car.entity.Car;
import org.jeecg.modules.car.entity.CarParking;
import org.jeecg.modules.car.service.ICarParkingService;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;

import org.jeecgframework.poi.excel.ExcelImportUtil;
import org.jeecgframework.poi.excel.def.NormalExcelConstants;
import org.jeecgframework.poi.excel.entity.ExportParams;
import org.jeecgframework.poi.excel.entity.ImportParams;
import org.jeecgframework.poi.excel.view.JeecgEntityExcelView;
import org.jeecg.common.system.base.controller.JeecgController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;
import com.alibaba.fastjson.JSON;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.jeecg.common.aspect.annotation.AutoLog;

 /**
 * @Description: 车位表
 * @Author: jeecg-boot
 * @Date:   2022-01-09
 * @Version: V1.0
 */
@Api(tags="车位表")
@RestController
@RequestMapping("/car/carParking")
@Slf4j
public class CarParkingController extends JeecgController<CarParking, ICarParkingService> {
	@Autowired
	private ICarParkingService carParkingService;

	 /**
	  * 统计车位使用情况
	  * @return
	  */
	 @AutoLog(value = "车位表-统计车位使用情况")
	 @ApiOperation(value="车位表-统计车位使用情况", notes="车位表-统计车位使用情况")
	 @GetMapping(value = "/total")
	public Result<?> countTotalInfo(){
		return  Result.OK(this.carParkingService.countTotalInfo());
	}
	/**
	 * 分页列表查询
	 *
	 * @param carParking
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	@AutoLog(value = "车位表-分页列表查询")
	@ApiOperation(value="车位表-分页列表查询", notes="车位表-分页列表查询")
	@GetMapping(value = "/list")
	public Result<?> queryPageList(CarParking carParking,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
		QueryWrapper<CarParking> queryWrapper = QueryGenerator.initQueryWrapper(carParking, req.getParameterMap());
		Page<CarParking> page = new Page<CarParking>(pageNo, pageSize);
		IPage<CarParking> pageList = carParkingService.page(page, queryWrapper);

		List<CarParking> cards = pageList.getRecords();
		for (int i=0;i<cards.size();i++){
			// 获取车位绑定的车辆
			Car car = this.carParkingService.findBindingCarInfoByParkingId(cards.get(i).getId());
			if (car != null){
				cards.get(i).setBindingCarNumber(car.getNumber());
			}
			// 获取车位正在停放的车辆
			 car = this.carParkingService.findStoppingCarInfoByParkingId(cards.get(i).getId());
			if (car != null){
				cards.get(i).setStopCarNumber(car.getNumber());
			}
		}
		return Result.OK(pageList);
	}
	
	/**
	 *   添加
	 *
	 * @param carParking
	 * @return
	 */
	@AutoLog(value = "车位表-添加")
	@ApiOperation(value="车位表-添加", notes="车位表-添加")
	@PostMapping(value = "/add")
	public Result<?> add(@RequestBody CarParking carParking) {
		carParkingService.save(carParking);
		return Result.OK("添加成功！");
	}
	
	/**
	 *  编辑
	 *
	 * @param carParking
	 * @return
	 */
	@AutoLog(value = "车位表-编辑")
	@ApiOperation(value="车位表-编辑", notes="车位表-编辑")
	@PutMapping(value = "/edit")
	public Result<?> edit(@RequestBody CarParking carParking) {
		carParkingService.updateById(carParking);
		return Result.OK("编辑成功!");
	}
	
	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "车位表-通过id删除")
	@ApiOperation(value="车位表-通过id删除", notes="车位表-通过id删除")
	@DeleteMapping(value = "/delete")
	public Result<?> delete(@RequestParam(name="id",required=true) String id) {
		carParkingService.removeById(id);
		return Result.OK("删除成功!");
	}
	
	/**
	 *  批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "车位表-批量删除")
	@ApiOperation(value="车位表-批量删除", notes="车位表-批量删除")
	@DeleteMapping(value = "/deleteBatch")
	public Result<?> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.carParkingService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.OK("批量删除成功!");
	}
	
	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "车位表-通过id查询")
	@ApiOperation(value="车位表-通过id查询", notes="车位表-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<?> queryById(@RequestParam(name="id",required=true) String id) {
		CarParking carParking = carParkingService.getById(id);
		if(carParking==null) {
			return Result.error("未找到对应数据");
		}
		return Result.OK(carParking);
	}

    /**
    * 导出excel
    *
    * @param request
    * @param carParking
    */
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, CarParking carParking) {
        return super.exportXls(request, carParking, CarParking.class, "车位表");
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
        return super.importExcel(request, response, CarParking.class);
    }

}
