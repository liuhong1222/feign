package cn.feignclient.credit_feign_web.service;

import java.util.Map;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import main.java.cn.common.BackResult;

@FeignClient(value = "user-provider-service")
public interface UserAuthService {

	@RequestMapping(value = "/userAuth/saveBusAuthData", method = RequestMethod.POST)
	BackResult<String> saveBusAuthData(@RequestBody Map<String, Object> param);
	
	@RequestMapping(value = "/userAuth/saveIdcardAuthData", method = RequestMethod.POST)
	BackResult<String> saveIdcardAuthData(@RequestBody Map<String, Object> param);
	
	@RequestMapping(value = "/contract/getUserContractData", method = RequestMethod.POST)
	BackResult<String> getUserContractData(@RequestParam("userId")String userId,@RequestParam("userType")String userType);
	
	@RequestMapping(value = "/userAuth/isAuthByIdentyNo", method = RequestMethod.POST)
	BackResult<Boolean> isAuthByIdentyNo(@RequestParam("identyType")String identyType,@RequestParam("identyNo")String identyNo);
}
