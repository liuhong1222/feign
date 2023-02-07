package cn.feignclient.credit_feign_web.client.service;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import main.java.cn.common.BackResult;

@FeignClient(value = "user-provider-service-client")
public interface EmailService {

	@RequestMapping(value = "/email/sendMail", method = RequestMethod.POST,consumes = MediaType.APPLICATION_JSON_VALUE)
	public BackResult<String> sendMail(@RequestParam("userName") String userName,
			@RequestParam("email") String email,@RequestParam("mailCode") String mailCode,@RequestParam("userId") String userId);
}
