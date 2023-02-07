package cn.feignclient.credit_feign_web.client.service;

import java.util.Map;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import main.java.cn.common.BackResult;

@FeignClient(value = "user-provider-service-client")
public interface LoginLogService {

	@RequestMapping(value = "/loginLog/saveLoginLog", method = RequestMethod.POST,consumes = MediaType.APPLICATION_JSON_VALUE)
	public BackResult<String> saveLoginLog(@RequestBody Map<String,String> param);
}
