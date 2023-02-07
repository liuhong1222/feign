package cn.feignclient.credit_feign_web.client.service;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import main.java.cn.common.BackResult;
import main.java.cn.domain.ApiAccountInfoClientDomain;
import main.java.cn.domain.CreUserClientDomain;

@FeignClient(value = "user-provider-service-client")
public interface ApiAccountInfoClientService {

	@RequestMapping(value = "/apiAccountInfo/findByCreUserId", method = RequestMethod.POST,consumes = MediaType.APPLICATION_JSON_VALUE)
	public BackResult<ApiAccountInfoClientDomain> findByCreUserId(@RequestParam("creUserId")String creUserId);
	
	@RequestMapping(value = "/apiAccountInfo/updateApiAccountInfo", method = RequestMethod.POST,consumes = MediaType.APPLICATION_JSON_VALUE)
	public BackResult<String> updateApiAccountInfo(@RequestBody ApiAccountInfoClientDomain domain);
	
	@RequestMapping(value = "/apiAccountInfo/findCreUserBymail", method = RequestMethod.POST,consumes = MediaType.APPLICATION_JSON_VALUE)
	public BackResult<CreUserClientDomain> findCreUserBymail(@RequestParam("email")String email);
	
	@RequestMapping(value = "/apiAccountInfo/findByUserName", method = RequestMethod.POST,consumes = MediaType.APPLICATION_JSON_VALUE)
	public BackResult<ApiAccountInfoClientDomain> findByUserName(@RequestParam("userName")String userName);
}
