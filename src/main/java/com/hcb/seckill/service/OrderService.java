package com.hcb.seckill.service;

import com.hcb.seckill.error.BussinessException;
import com.hcb.seckill.service.model.OrderModel;

public interface OrderService {
    //整合秒杀活动的思路,两种实现,推荐第一种
    //1.通过前端url上传过来秒杀活动id，然后下单接口内校验对应promoId是否属于对应商品且活动已开始
    //2.直接在下单接口内判断对应商品是否在秒杀
    OrderModel createOrder(Integer userId,Integer itemId,Integer amount,Integer promoId) throws BussinessException;
}
