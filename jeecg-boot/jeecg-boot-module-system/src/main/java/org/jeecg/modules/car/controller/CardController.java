package org.jeecg.modules.car.controller;

import java.util.*;

import org.jeecg.modules.car.constants.OrderConstants;
import org.jeecg.modules.car.constants.ParkingConstants;
import org.jeecg.modules.car.entity.CarParking;
import org.jeecg.modules.car.entity.CarParkingCost;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.SecurityUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.modules.car.entity.Card;
import org.jeecg.modules.car.service.ICarParkingCostService;
import org.jeecg.modules.car.service.ICarParkingService;
import org.jeecg.modules.car.service.ICardService;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;

import org.jeecg.modules.system.entity.SysUser;
import org.jeecg.modules.system.service.ISysUserService;
import org.jeecg.common.system.base.controller.JeecgController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.jeecg.common.aspect.annotation.AutoLog;

/**
 * @Description: 会员卡
 * @Author: jeecg-boot
 * @Date: 2022-01-10
 * @Version: V1.0
 */
@Api(tags = "会员卡")
@RestController
@RequestMapping("/car/card")
@Slf4j
public class CardController extends JeecgController<Card, ICardService> {
    @Autowired
    private ICardService cardService;
    @Autowired
    private ICarParkingCostService carParkingCostService;
    @Autowired
    private ISysUserService sysUserService;

    /**
     * 分页列表查询
     *
     * @param card
     * @param pageNo
     * @param pageSize
     * @param req
     * @return
     */
    @AutoLog(value = "会员卡-分页列表查询")
    @ApiOperation(value = "会员卡-分页列表查询", notes = "会员卡-分页列表查询")
    @GetMapping(value = "/list")
    public Result<?> queryPageList(Card card,
                                   @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                   @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                   HttpServletRequest req) {
        QueryWrapper<Card> queryWrapper = QueryGenerator.initQueryWrapper(card, req.getParameterMap());
        Page<Card> page = new Page<Card>(pageNo, pageSize);
        IPage<Card> pageList = cardService.page(page, queryWrapper);
         return Result.OK(pageList);
    }

    /**
     * 分页列表查询
     * @param
     * @return
     */
    @AutoLog(value = "会员卡-分页列表查询")
    @ApiOperation(value = "会员卡-分页列表查询", notes = "会员卡-分页列表查询")
    @PostMapping(value = "/queryByCarNumber")
    public Result<?> queryByCarNumber(@RequestBody Card card) {
        QueryWrapper<Card> queryWrapper = QueryGenerator.initQueryWrapper(card, null);
        List<Card> cards = this.cardService.list(queryWrapper);
        if (cards.isEmpty()){
            return Result.OK(null);
        }else {
            return Result.OK(cards.get(0));
        }
    }





    /**
     * 添加
     * @param card
     * @return
     */
    @AutoLog(value = "会员卡-添加")
    @ApiOperation(value = "会员卡-添加", notes = "会员卡-添加")
    @PostMapping(value = "/add")
    public Result<?> add(@RequestBody Card card, @RequestParam(name = "costId") String costId) {

        // 获取用户积分
        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        SysUser user = sysUserService.getUserByName(sysUser.getUsername());
        Integer score = user.getScore();
        // 获取套餐费用
        CarParkingCost cost = this.carParkingCostService.getById(costId);
        if (score < Integer.parseInt(cost.getCost())) {
            return Result.error("积分不足！！！,你的积分"+score+" 需要积分 "+ cost.getCost());
        }
        // 设置套餐的有效时间
        String orderType = "";
        card.setStartTime(new Date());
        if (cost.getName().equals("月卡")){
            card.setEndTime(this.getEndTime(1));
            orderType = OrderConstants.MOTH_CARD_BUYING;
        }else {
            card.setEndTime(this.getEndTime(2));
            orderType = OrderConstants.YEAR_CARD_BUYING;
        }
        // 订单类型

        cardService.saveCard(cost,card,orderType,user);
        return Result.OK("添加成功！");
    }


    protected Date getEndTime(Integer type){
        Date date = new Date();
        Calendar calendar = Calendar.getInstance();
        if (type == 1){
            calendar.add(Calendar.MONTH, 1);//当前时间减去一个月，即一个月前的时间
        }else {
            calendar.add(Calendar.YEAR, 1);
        }
        return  calendar.getTime();
    }
    /**
     * 密码重置
     *
     * @param card
     * @return
     */
    @AutoLog(value = "会员卡-密码重置")
    @ApiOperation(value = "会员卡-密码重置", notes = "会员卡-密码重置")
    @PutMapping(value = "/resetPassword")
    public Result<?> edit(@RequestBody Card card) {
        // 获取用户
        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        // 查看是否为当前用户的卡
        Card queryCard = new Card().setUserId(sysUser.getId()).setNumber(card.getNumber());
        QueryWrapper<Card> queryWrapper = QueryGenerator.initQueryWrapper(queryCard,null);
        if (this.cardService.count(queryWrapper)<1){
             Result.error("非法操作!");
        }
        queryCard = new Card().setCarId(card.getCarId()).setPassword(card.getPassword());
        cardService.updateById(queryCard);
        return Result.OK("编辑成功!");
    }

    /**
     * 编辑
     * @param card
     * @return
     */
    @AutoLog(value = "会员卡-编辑")
    @ApiOperation(value = "会员卡-编辑", notes = "会员卡-编辑")
    @PutMapping(value = "/edit")
    public Result<?> resetPassword(@RequestBody Card card) {
        // 修改旧车位的类型变成临时
        CarParking carParking = new CarParking().setId(card.getOlderParkingId()).setType(ParkingConstants.PARKING_TEM);
        this.carParkingService.updateById(carParking);
        // 新车位修改状态
        carParking = new CarParking().setId(card.getParkingId()).setType(ParkingConstants.PARKING_FIX);
        this.carParkingService.updateById(carParking);
        // 修改会卡的内容
        cardService.updateById(card);

        return Result.OK("编辑成功!");
    }
    @Autowired
    private ICarParkingService carParkingService;
    /**
     * 通过id删除
     *
     * @param id
     * @return
     */
    @AutoLog(value = "会员卡-通过id删除")
    @ApiOperation(value = "会员卡-通过id删除", notes = "会员卡-通过id删除")
    @DeleteMapping(value = "/delete")
    public Result<?> delete(@RequestParam(name = "id", required = true) String id) {

        Card card = cardService.getById(id);
        // 把车位状态变成临时
        CarParking carParking = new CarParking().setType(ParkingConstants.PARKING_TEM).setId(card.getParkingId());
        this.carParkingService.updateById(carParking);
        this.cardService.removeById(id);
        return Result.OK("删除成功!");
    }

    /**
     * 批量删除
     *
     * @param ids
     * @return
     */
    @AutoLog(value = "会员卡-批量删除")
    @ApiOperation(value = "会员卡-批量删除", notes = "会员卡-批量删除")
    @DeleteMapping(value = "/deleteBatch")
    public Result<?> deleteBatch(@RequestParam(name = "ids", required = true) String ids) {
        this.cardService.removeByIds(Arrays.asList(ids.split(",")));
        return Result.OK("批量删除成功!");
    }

    /**
     * 通过id查询
     *
     * @param id
     * @return
     */
    @AutoLog(value = "会员卡-通过id查询")
    @ApiOperation(value = "会员卡-通过id查询", notes = "会员卡-通过id查询")
    @GetMapping(value = "/queryById")
    public Result<?> queryById(@RequestParam(name = "id", required = true) String id) {
        Card card = cardService.getById(id);
        if (card == null) {
            return Result.error("未找到对应数据");
        }
        return Result.OK(card);
    }


    /**
     * 导出excel
     *
     * @param request
     * @param card
     */
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, Card card) {
        return super.exportXls(request, card, Card.class, "会员卡");
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
        return super.importExcel(request, response, Card.class);
    }

}
