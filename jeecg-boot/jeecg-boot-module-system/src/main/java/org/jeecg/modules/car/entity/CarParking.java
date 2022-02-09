package org.jeecg.modules.car.entity;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.math.BigDecimal;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.jeecg.common.aspect.annotation.Dict;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * @Description: 车位表
 * @Author: jeecg-boot
 * @Date:   2022-01-09
 * @Version: V1.0
 */
@Data
@TableName("car_parking")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="car_parking对象", description="车位表")
public class CarParking implements Serializable {
    private static final long serialVersionUID = 1L;

	/**主键*/
	@TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "主键")
    private java.lang.String id;
	/**创建人*/
    @ApiModelProperty(value = "创建人")
    private java.lang.String createBy;
	/**创建日期*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "创建日期")
    private java.util.Date createTime;
	/**更新人*/
    @ApiModelProperty(value = "更新人")
    private java.lang.String updateBy;
	/**更新日期*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "更新日期")
    private java.util.Date updateTime;
	/**车位号*/
	@Excel(name = "车位号", width = 15)
    @ApiModelProperty(value = "车位号")
    private java.lang.String parkingNumber;
	/**状态*/
	@Excel(name = "状态", width = 15, dicCode = "paking_status")
	@Dict(dicCode = "paking_status")
    @ApiModelProperty(value = "状态")
    private java.lang.String status;
	/**类型*/
	@Excel(name = "类型", width = 15, dicCode = "parking_type")
	@Dict(dicCode = "parking_type")
    @ApiModelProperty(value = "类型")
    private java.lang.String type;
    /** 绑定车位*/
    @TableField(exist = false)
    private java.lang.String bindingCarNumber;

    /** 正在停车的车子*/
    @TableField(exist = false)
    private java.lang.String stopCarNumber;
    /**车辆类型*/

	/**备注*/
	@Excel(name = "备注", width = 15)
    @ApiModelProperty(value = "备注")
    private java.lang.String remark;
}
