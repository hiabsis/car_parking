package org.jeecg.modules.car.constants;

/**
 * 订单常量
 */
public class OrderConstants {
    /**
     * 订单类型
     */
    // 月卡购买
    public static String MOTH_CARD_BUYING= "1";
    // 年卡购买
    public static String YEAR_CARD_BUYING= "2";
    // 会卡续费
    public static String RENEW_CARD_BUYING= "3";
    //充值
    public static String SCORE_ADDING= "4";
    // 普通用户出库
    public static String CAR_OUTER_COMMON= "5";
    // 会员用户出库
    public static String CAR_OUTER_VIP= "6";

    /**
     * 支付状态
     */
    // 已支付
    public static String ORDER_STATUS_NOT_PAY= "1";
    // 未支付
    public static String ORDER_STATUS_PAY= "2";
    // 审核
    public static String ORDER_STATUS_NOT_CHECKING= "3";

    /**
     * 支付方式
     */
    // 积分支付
    public static String PAY_WAY_SCORE = "1";
    // 会卡支付
    public static String PAY_WAY_CARD = "2";

    /**
     * 套餐类型
     */
    // 普通套餐
    public static String COST_COMMON = "1";
    // 会员套餐
    public static String COST_VIP = "2";

}
