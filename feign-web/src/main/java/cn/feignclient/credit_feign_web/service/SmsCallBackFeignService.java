package cn.feignclient.credit_feign_web.service;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(value = "credit-provider-service",fallback = SmsCallBackFeignServiceHiHystric.class)
public interface SmsCallBackFeignService {

	@RequestMapping("/smsCallback/removeOrSave")
	public void removeOrSave(@RequestParam("mobile")String mobile,@RequestParam("status")String status,@RequestParam("notifyTime")String notifyTime);
}
