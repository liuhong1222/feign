package cn.feignclient.credit_feign_web.service;

import org.springframework.stereotype.Component;

@Component
public class SmsCallBackFeignServiceHiHystric implements SmsCallBackFeignService {

	@Override
	public void removeOrSave(String mobile, String status, String notifyTime) {

	}

}
