package cn.feignclient.credit_feign_web.service;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import main.java.cn.common.BackResult;

@FeignClient(value = "user-provider-service",fallback = PayCallBackServiceHiHystric.class)
public interface PayCallBackService {
	
	@RequestMapping(value = "/payCallback/alipayRecharge", method = RequestMethod.GET)
	BackResult<Boolean> recharge(@RequestParam("outTrdOrder")String outTrdOrder,@RequestParam("orderStatus")String orderStatus,@RequestParam("traOrder")String traOrder);
}
