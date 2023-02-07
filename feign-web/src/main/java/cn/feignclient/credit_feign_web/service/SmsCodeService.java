package cn.feignclient.credit_feign_web.service;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(value = "user-provider-service")
public interface SmsCodeService {

	@RequestMapping("/smsCode/saveSmsCode")
	public void saveSmsCode(@RequestParam("mobile")String mobile,@RequestParam("identifyCode")String identifyCode);
}
