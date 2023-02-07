package cn.feignclient.credit_feign_web.service;

import org.springframework.stereotype.Component;

import main.java.cn.common.BackResult;
import main.java.cn.common.ResultCode;

@Component
public class PayCallBackServiceHiHystric implements PayCallBackService {

	@Override
	public BackResult<Boolean> recharge(String outTrdOrder, String orderStatus, String traOrder) {
		return new BackResult<Boolean>(ResultCode.RESULT_FAILED, "user-provider-service服务recharge出现异常");
	}

}
