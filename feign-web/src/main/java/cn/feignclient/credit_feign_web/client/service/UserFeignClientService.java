package cn.feignclient.credit_feign_web.client.service;

import main.java.cn.common.BackResult;
import main.java.cn.domain.ApiAccountInfoClientDomain;
import main.java.cn.domain.CreUserClientDomain;
import main.java.cn.domain.CreUserDomain;

import java.util.Map;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(value = "user-provider-service-client")
public interface UserFeignClientService {
	
	@RequestMapping(value = "/user/findByUserName", method = RequestMethod.POST,consumes = MediaType.APPLICATION_JSON_VALUE)
	BackResult<CreUserClientDomain> findByUserName(@RequestParam("userName")String userName);
	
	@RequestMapping(value = "/user/getApiAccountInfoByUserName", method = RequestMethod.POST,consumes = MediaType.APPLICATION_JSON_VALUE)
	BackResult<ApiAccountInfoClientDomain> getApiAccountInfoByUserName(@RequestParam("userName")String userName);

	@RequestMapping(value = "/user/findById", method = RequestMethod.POST,consumes = MediaType.APPLICATION_JSON_VALUE)
	BackResult<CreUserClientDomain> findById(@RequestParam("id")Integer id);
	
	@RequestMapping(value = "/user/findCreUserInfobyUserName", method = RequestMethod.POST,consumes = MediaType.APPLICATION_JSON_VALUE)
	BackResult<Map<String,Object>> findCreUserInfobyUserName(@RequestParam("userName")String userName);
	
	@RequestMapping(value = "/user/bindCreUserMail", method = RequestMethod.POST,consumes = MediaType.APPLICATION_JSON_VALUE)
	BackResult<String> bindCreUserMail(@RequestParam("userId")String userId,@RequestParam("email")String email);
}

